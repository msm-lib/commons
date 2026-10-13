package com.msm.core.dynamicquery.command;

import com.msm.core.exceptions.FieldNotFoundException;
import com.msm.core.exceptions.FieldValueRequiredException;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultDynamicInsertTest {

    private static final UUID ID = UUID.fromString("00000000-0000-0000-0000-000000000001");

    /** SQL of each executed statement, followed by its bind values (one list per row for a JDBC batch). */
    private final List<String> executed = new ArrayList<>();

    private final DSLContext dsl = DSL.using(new MockConnection(ctx -> {
        Object[][] batch = ctx.batchBindings();
        executed.add(ctx.sql() + " " + (batch.length > 1 ? Arrays.deepToString(batch) : Arrays.toString(ctx.bindings())));
        // statements with RETURNING read a result set; an empty one is enough here
        return new MockResult[]{ctx.sql().contains(" returning ")
                ? new MockResult(1, DSL.using(SQLDialect.POSTGRES).newResult())
                : new MockResult(1, null)};
    }), SQLDialect.POSTGRES);

    private final DefaultDynamicInsert insert = new DefaultDynamicInsert(dsl);

    private static final ObjectMetadata META = ObjectMetadata.builder()
            .name("product")
            .tableName("product")
            .attributes(List.of(
                    attribute("id", "id", "UUID"),
                    attribute("code", "code", "String"),
                    attribute("note", "note", "String"),
                    attribute("orderId", "order_id", "UUID"),
                    attribute("createdAt", "created_at", "Instant")
            ))
            .build();

    private static Attribute attribute(String fieldName, String columnName, String fieldType) {
        return Attribute.builder().fieldName(fieldName).columnName(columnName).fieldType(fieldType).build();
    }

    private static Map<String, Object> row(Object... keyValues) {
        Map<String, Object> row = new HashMap<>();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    @Test
    void batchInsertKeepsFieldsOnlyLaterRowsCarry() {
        int[] counts = insert.batchInsert(META, List.of(row("code", "A"), row("code", "B", "note", "keep me"), row("code", "C")));

        assertThat(executed).containsExactly(
                "insert into \"product\" (\"code\") values (?) [[A], [C]]",
                "insert into \"product\" (\"code\", \"note\") values (?, ?) [B, keep me]");
        assertThat(counts).hasSize(3);
    }

    @Test
    void batchInsertDoesNotWriteNullForFieldsEarlierRowsCarry() {
        insert.insertBatchIgnoreDuplicate(META, List.of(row("id", ID, "code", "A"), row("code", "B")));

        assertThat(executed).containsExactly(
                "insert into \"product\" (\"id\", \"code\") values (cast(? as uuid), ?) on conflict do nothing [" + ID + ", A]",
                "insert into \"product\" (\"code\") values (?) on conflict do nothing [B]");
    }

    @Test
    void sameFieldsGiveOneStatement() {
        insert.insertReturning(META, List.of(row("code", "A", "note", "x"), row("code", "B", "note", "y")), List.of("id"));

        assertThat(executed).containsExactly(
                "insert into \"product\" (\"code\", \"note\") values (?, ?), (?, ?) returning \"id\" as \"id\" [A, x, B, y]");
    }

    @Test
    void emptyBatchInsertDoesNothing() {
        assertThat(insert.batchInsert(META, List.of())).isEmpty();
        assertThat(executed).isEmpty();
    }

    @Test
    void upsertDoesNotUpdatePrimaryKeyNorCreationAudit() {
        insert.upsert(META, row("id", ID, "code", "A", "note", "n", "createdAt", Instant.EPOCH), "uk_product_code");

        assertThat(executed.getFirst()).contains(
                "on conflict on constraint \"uk_product_code\" do update set \"code\" = excluded.\"code\", \"note\" = excluded.\"note\" [");
    }

    @Test
    void upsertWithOnlyNonUpdatableFieldsIsANoOpUpdate() {
        insert.upsertReturning(META, List.of(row("id", ID)), "product_pkey", List.of("id"));

        assertThat(executed.getFirst()).contains("do update set \"id\" = \"product\".\"id\" returning");
    }

    @Test
    void upsertClearsAFieldGivenAsNull() {
        insert.upsert(META, row("code", "A", "note", null), "uk_product_code");

        assertThat(executed).containsExactly("insert into \"product\" (\"code\", \"note\") values (?, ?) "
                + "on conflict on constraint \"uk_product_code\" do update set \"code\" = excluded.\"code\", \"note\" = excluded.\"note\" [A, null]");
    }

    @Test
    void upsertKeepsAFieldNotInThePayload() {
        insert.upsertReturning(META, List.of(row("code", "A", "note", null), row("code", "B")), "uk_product_code", List.of("id"));

        assertThat(executed).hasSize(2);
        assertThat(executed.get(1)).contains("(\"code\") values (?) on conflict on constraint \"uk_product_code\" do update set \"code\" = excluded.\"code\" returning");
    }

    @Test
    void insertSkipsAFieldGivenAsNull() {
        insert.batchInsert(META, List.of(row("code", "A", "note", null)));

        assertThat(executed).containsExactly("insert into \"product\" (\"code\") values (?) [A]");
    }

    @Test
    void upsertBatchGroupsRowsInsteadOfFailing() {
        int count = insert.upsertBatch(META, List.of(row("code", "A"), row("code", "B", "note", "n")), List.of("uk_product_code"));

        assertThat(count).isEqualTo(2);
        assertThat(executed).hasSize(2);
        assertThat(executed.get(1)).contains("do update set \"code\" = excluded.\"code\", \"note\" = excluded.\"note\"");
    }

    @Test
    void upsertConflictFieldsAreColumnsOfTheMetadata() {
        insert.upsert(META, row("code", "A", "orderId", ID), List.of("orderId", "code"), DSL.condition("is_deleted IS NOT TRUE"));
        insert.upsert(META, row("code", "A"), List.of("order_id"), null);

        assertThat(executed.get(0)).contains("on conflict (\"order_id\", \"code\") where (is_deleted IS NOT TRUE) do update");
        assertThat(executed.get(1)).contains("on conflict (\"order_id\") do update");
    }

    @Test
    void unknownConflictFieldIsRejected() {
        assertThatThrownBy(() -> insert.upsertReturning(META, List.of(row("code", "A")), List.of("unknownField")))
                .isInstanceOf(FieldNotFoundException.class);
        assertThat(executed).isEmpty();
    }

    @Test
    void rowWithoutKnownFieldIsRejectedBeforeAnySql() {
        Map<String, Object> unknown = row("order_code", "A");

        assertThatThrownBy(() -> insert.insert(META, unknown))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("Row 0 of object product has no field to write: unknown keys [order_code]");
        assertThatThrownBy(() -> insert.insertReturning(META, unknown)).isInstanceOf(FieldValueRequiredException.class);
        assertThatThrownBy(() -> insert.insertReturningInsertedRow(META, unknown, "uk_product_code")).isInstanceOf(FieldValueRequiredException.class);
        assertThatThrownBy(() -> insert.upsert(META, unknown, "uk_product_code")).isInstanceOf(FieldValueRequiredException.class);
        assertThatThrownBy(() -> insert.upsert(META, unknown, List.of("code"), null)).isInstanceOf(FieldValueRequiredException.class);
        assertThatThrownBy(() -> insert.batchInsert(META, List.of(row("code", "A"), unknown)))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("Row 1 ");
        assertThatThrownBy(() -> insert.upsertBatch(META, List.of(row("code", "A"), unknown), List.of("uk_product_code")))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("Row 1 ");
        assertThat(executed).isEmpty();
    }

    @Test
    void insertWithOnlyNullKnownFieldsIsRejected() {
        assertThatThrownBy(() -> insert.insert(META, row("code", null, "foo", 1)))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("nothing to write in [code], unknown keys [foo]");
    }

    @Test
    void upsertWithOnlyNullKnownFieldsClearsThem() {
        insert.upsert(META, row("code", "A"), "uk_product_code");
        insert.upsert(META, row("note", null), "uk_product_code");

        assertThat(executed).hasSize(2);
    }
}
