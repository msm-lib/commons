package com.msm.core.dynamicquery.command;

import com.msm.core.exceptions.FieldValueInvalidException;
import com.msm.core.exceptions.FieldValueRequiredException;
import com.msm.core.exceptions.ObjectNotFoundException;
import com.msm.core.exceptions.OptimisticLockingFailureException;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.tools.jdbc.MockConnection;
import org.jooq.tools.jdbc.MockResult;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultDynamicUpdateTest {

    private static final UUID X = UUID.fromString("00000000-0000-0000-0000-00000000000a");
    private static final UUID Y = UUID.fromString("00000000-0000-0000-0000-00000000000b");
    private static final Field<Object> TEAM_ID = DSL.field(DSL.name("team_id"));

    private final List<String> executed = new ArrayList<>();
    // rows each executed statement reports as updated
    private int affectedRows = 1;
    // when set: rows updated by each statement of a batch, in order
    private int[] batchAffectedRows;
    // ids returned by an UPDATE ... RETURNING
    private List<UUID> returnedIds = List.of();

    private final DSLContext dsl = DSL.using(new MockConnection(ctx -> {
        String[] batch = ctx.batchSQL();
        if (batch.length > 1) {
            executed.addAll(Arrays.asList(batch));
            return IntStream.range(0, batch.length)
                    .mapToObj(i -> new MockResult(batchAffectedRows != null ? batchAffectedRows[i] : affectedRows, null))
                    .toArray(MockResult[]::new);
        }
        executed.add(ctx.sql() + " " + Arrays.toString(ctx.bindings()));
        if (ctx.sql().contains(" returning ") || ctx.sql().startsWith("select")) {
            DSLContext create = DSL.using(SQLDialect.POSTGRES);
            Field<UUID> id = DSL.field(DSL.name("id"), UUID.class);
            Result<Record1<UUID>> result = create.newResult(id);
            returnedIds.forEach(value -> result.add(create.newRecord(id).values(value)));
            return new MockResult[]{new MockResult(result.size(), result)};
        }
        return new MockResult[]{new MockResult(affectedRows, null)};
    }), SQLDialect.POSTGRES);

    private final DefaultDynamicUpdate update = new DefaultDynamicUpdate(dsl);

    private static final ObjectMetadata VERSIONED = metadata("product", true);
    private static final ObjectMetadata PLAIN = metadata("tag", false);

    private static ObjectMetadata metadata(String name, boolean versioned) {
        List<Attribute> attributes = new ArrayList<>(List.of(
                attribute("id", "id", "UUID"),
                attribute("code", "code", "String"),
                attribute("teamId", "team_id", "UUID")));
        if (versioned) {
            attributes.add(attribute("version", "version", "Long"));
        }
        return ObjectMetadata.builder().name(name).tableName(name).attributes(attributes).build();
    }

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
    void updateByConditionIncrementsEachRowVersionWithoutVersionCheck() {
        update.update(VERSIONED, row("code", "N", "version", 3), TEAM_ID.eq(X));

        assertThat(executed).containsExactly(
                "update \"product\" set \"code\" = ?, \"version\" = (\"version\" + ?) where \"team_id\" = cast(? as uuid) [N, 1, " + X + "]");
    }

    @Test
    void updateByConditionDoesNotRequireVersion() {
        // the mock returns no row, so the call ends as not found; only the SQL matters here
        assertThatThrownBy(() -> update.updateReturning(VERSIONED, row("code", "N"), TEAM_ID.eq(X), List.of("id")))
                .isInstanceOf(ObjectNotFoundException.class);

        assertThat(executed.getFirst()).startsWith(
                "update \"product\" set \"code\" = ?, \"version\" = (\"version\" + ?) where \"team_id\" = cast(? as uuid) returning");
    }

    @Test
    void updateByIdChecksVersion() {
        update.updateById(VERSIONED, X, row("code", "N", "version", 3));

        // SET follows the payload's (hash) order
        assertThat(executed).hasSize(1);
        assertThat(executed.getFirst())
                .contains("\"version\" = ?", "\"code\" = ?", "where (\"id\" = cast(? as uuid) and \"version\" = ?)")
                .endsWith(X + ", 3]")
                .contains("4");
    }

    @Test
    void updateByIdNeverChangesThePrimaryKey() {
        update.updateById(PLAIN, X, row("id", Y, "code", "N"));

        assertThat(executed).containsExactly("update \"tag\" set \"code\" = ? where \"id\" = cast(? as uuid) [N, " + X + "]");
    }

    @Test
    void batchRowWithoutIdIsRejectedBeforeAnySql() {
        assertThatThrownBy(() -> update.batchUpdate(PLAIN, List.of(row("id", X, "code", "A"), row("code", "B"))))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("Row 1 of object tag has no id");
        assertThat(executed).isEmpty();
    }

    @Test
    void rowIndexCountsAcrossChunks() {
        List<Map<String, Object>> items = new ArrayList<>(IntStream.range(0, 1000).mapToObj(i -> row("id", UUID.randomUUID(), "code", "C" + i)).toList());
        items.add(row("code", "no id"));

        assertThatThrownBy(() -> update.updateReturning(PLAIN, items))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("Row 1000 ");
    }

    @Test
    void noRowUpdatedIsNotFoundWithoutVersion() {
        affectedRows = 0;

        assertThatThrownBy(() -> update.updateById(PLAIN, X, row("code", "N"))).isInstanceOf(ObjectNotFoundException.class);
        assertThatThrownBy(() -> update.batchUpdate(PLAIN, List.of(row("id", X, "code", "N"), row("id", Y, "code", "M"))))
                .isInstanceOf(ObjectNotFoundException.class);
        assertThatThrownBy(() -> update.update(VERSIONED, row("code", "N"), TEAM_ID.eq(X))).isInstanceOf(ObjectNotFoundException.class);
    }

    @Test
    void noRowUpdatedByIdIsVersionConflictWithVersion() {
        affectedRows = 0;

        assertThatThrownBy(() -> update.updateById(VERSIONED, X, row("code", "N", "version", 3)))
                .isInstanceOf(OptimisticLockingFailureException.class);
    }

    @Test
    void rowWithNothingToWriteIsRejectedBeforeAnySql() {
        assertThatThrownBy(() -> update.updateById(PLAIN, X, row("foo", 1)))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("unknown keys [foo]");
        assertThatThrownBy(() -> update.updateById(PLAIN, X, row("id", X)))
                .isInstanceOf(FieldValueRequiredException.class)
                .hasMessageContaining("nothing to write in [id]");
        assertThatThrownBy(() -> update.updateById(PLAIN, null, row("code", "N")))
                .isInstanceOf(FieldValueRequiredException.class);
        assertThat(executed).isEmpty();
    }

    @Test
    void updateWithExpressionsIncrementsVersionAndKeepsExpressions() {
        Field<Object> code = DSL.field(DSL.name("code"));
        update.updateWithExpressions(VERSIONED, row("code", DSL.concat(code, DSL.inline("!"))), TEAM_ID.eq(X));

        assertThat(executed.getFirst()).contains("\"version\" = (\"version\" + ?)").contains("where \"team_id\" = cast(? as uuid)");
    }

    @Test
    void batchUpdateWithExpressionsSetsVersionAndGuardPerRow() {
        batchAffectedRows = new int[]{1, 0};
        Field<Object> code = DSL.field(DSL.name("code"));

        int[] affected = update.batchUpdateWithExpressions(VERSIONED, List.of(
                ExpressionUpdate.of(X, row("code", DSL.concat(code, DSL.inline("!")), "id", Y, "version", 9)),
                new ExpressionUpdate(Y, row("code", "plain"), code.ne("locked"))));

        assertThat(affected).containsExactly(1, 0);
        assertThat(executed).hasSize(2);
        // the id and version in the values are ignored: the row is chosen by its id, the version moves by one
        assertThat(executed.get(0)).contains("\"code\" = (cast(\"code\" as varchar) || '!')")
                .contains("\"version\" = (\"version\" + 1)")
                .contains("where \"id\" = cast('00000000-0000-0000-0000-00000000000a' as uuid)")
                .doesNotContain("00000000-0000-0000-0000-00000000000b");
        assertThat(executed.get(1)).contains("\"code\" <> 'locked'");
    }

    @Test
    void batchUpdateWithExpressionsOfNoRowRunsNothing() {
        assertThat(update.batchUpdateWithExpressions(VERSIONED, List.of())).isEmpty();
        assertThat(executed).isEmpty();
    }

    @Test
    void batchUpdateWithExpressionsNeedsAnId() {
        assertThatThrownBy(() -> update.batchUpdateWithExpressions(VERSIONED, List.of(ExpressionUpdate.of(null, row("code", "x")))))
                .hasMessageContaining("requires an id");
    }

    @Test
    void batchReportsEveryRowNotUpdated() {
        batchAffectedRows = new int[]{1, 0, 1, 0};
        List<Map<String, Object>> items = IntStream.range(0, 4).mapToObj(i -> row("id", UUID.randomUUID(), "code", "C" + i)).toList();

        assertThatThrownBy(() -> update.batchUpdate(PLAIN, items))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("rows [1, 3]");
        assertThat(executed).hasSize(4);
    }

    @Test
    void updateReturningListRunsEveryChunkThenReportsGlobalRows() {
        List<Map<String, Object>> items = IntStream.range(0, 1002).mapToObj(i -> row("id", UUID.randomUUID(), "code", "C" + i)).toList();
        batchAffectedRows = new int[1000];
        Arrays.fill(batchAffectedRows, 1);
        batchAffectedRows[5] = 0;
        // second chunk: 2 rows, the 1st one (row 1000) not updated
        DefaultDynamicUpdate chunked = new DefaultDynamicUpdate(DSL.using(new MockConnection(ctx -> {
            String[] batch = ctx.batchSQL();
            if (batch.length > 1) {
                return IntStream.range(0, batch.length)
                        .mapToObj(i -> new MockResult(batch.length == 2 ? (i == 0 ? 0 : 1) : batchAffectedRows[i], null))
                        .toArray(MockResult[]::new);
            }
            return new MockResult[]{new MockResult(0, DSL.using(SQLDialect.POSTGRES).newResult())};
        }), SQLDialect.POSTGRES));

        assertThatThrownBy(() -> chunked.updateReturning(PLAIN, items))
                .isInstanceOf(ObjectNotFoundException.class)
                .hasMessageContaining("rows [5, 1000]");
    }

    @Test
    void manyFailedRowsAreTruncatedInTheMessage() {
        batchAffectedRows = new int[25];
        List<Map<String, Object>> items = IntStream.range(0, 25).mapToObj(i -> row("id", UUID.randomUUID(), "code", "C" + i)).toList();

        assertThatThrownBy(() -> update.batchUpdate(PLAIN, items))
                .hasMessageContaining("... (25 rows)");
    }

    @Test
    void updateReturningByConditionReturnsTheOneRow() {
        returnedIds = List.of(X);

        assertThat(update.updateReturning(PLAIN, row("code", "N"), TEAM_ID.eq(X), List.of("id"))).containsEntry("id", X);
    }

    @Test
    void updateReturningByConditionRejectsSeveralRows() {
        returnedIds = List.of(X, Y);

        assertThatThrownBy(() -> update.updateReturning(PLAIN, row("code", "N"), TEAM_ID.eq(X), List.of("id")))
                .isInstanceOf(FieldValueInvalidException.class)
                .hasMessageContaining("Condition matched 2 rows of object tag, expected one");
    }

    @Test
    void updateAllReturningReturnsEveryRow() {
        returnedIds = List.of(X, Y);

        assertThat(update.updateAllReturning(PLAIN, row("code", "N"), TEAM_ID.eq(X), List.of("id"))).hasSize(2);
        assertThat(executed.getFirst()).startsWith("update \"tag\" set \"code\" = ? where \"team_id\" = cast(? as uuid) returning");
    }

    @Test
    void updateAllReturningWithoutMatchIsNotFound() {
        assertThatThrownBy(() -> update.updateAllReturning(PLAIN, row("code", "N"), TEAM_ID.eq(X)))
                .isInstanceOf(ObjectNotFoundException.class);
    }
}
