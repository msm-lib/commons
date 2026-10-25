package com.msm.core.dynamicquery.command;

import com.msm.core.exceptions.FieldValueInvalidException;
import com.msm.core.exceptions.FieldValueRequiredException;
import com.msm.core.exceptions.ObjectNotFoundException;
import com.msm.core.exceptions.OptimisticLockingFailureException;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * {@link DefaultDynamicInsert} and {@link DefaultDynamicUpdate} against a real PostgreSQL.
 * Skipped unless {@code MSM_TEST_DB_URL} is set (e.g. {@code jdbc:postgresql://localhost:5432/msm_test?user=x}).
 * The tables are dropped and recreated before each test.
 */
class DynamicCommandsPostgresIT {

    private static final String DB_URL = System.getenv("MSM_TEST_DB_URL");

    private static Connection connection;
    private static DSLContext dsl;
    private static DefaultDynamicInsert insert;
    private static DefaultDynamicUpdate update;

    // product: versioned, audited, unique code; tag: no version; item: partial unique index
    private static final ObjectMetadata PRODUCT = metadata("product",
            attribute("id", "id", "UUID"),
            attribute("code", "code", "String"),
            attribute("note", "note", "String"),
            attribute("teamId", "team_id", "UUID"),
            attribute("isDeleted", "is_deleted", "Boolean"),
            attribute("version", "version", "Long"),
            attribute("createdAt", "created_at", "Instant"),
            attribute("createdBy", "created_by", "String"));
    private static final ObjectMetadata TAG = metadata("tag",
            attribute("id", "id", "UUID"),
            attribute("code", "code", "String"),
            attribute("teamId", "team_id", "UUID"),
            attribute("hits", "hits", "Long"));
    private static final ObjectMetadata ITEM = metadata("item",
            attribute("id", "id", "UUID"),
            attribute("orderId", "order_id", "UUID"),
            attribute("soLine", "so_line", "Long"),
            attribute("note", "note", "String"),
            attribute("isDeleted", "is_deleted", "Boolean"));

    // no NOT NULL column besides the primary key
    private static final ObjectMetadata MARKER = metadata("marker",
            attribute("id", "id", "UUID"),
            attribute("label", "label", "String"),
            attribute("createdBy", "created_by", "String"));

    private static final UUID TEAM_A = UUID.randomUUID();
    private static final UUID TEAM_B = UUID.randomUUID();
    private static final Field<Object> TEAM_ID = DSL.field(DSL.name("team_id"));

    @BeforeAll
    static void connect() throws Exception {
        assumeTrue(DB_URL != null, "MSM_TEST_DB_URL not set");
        connection = DriverManager.getConnection(DB_URL);
        dsl = DSL.using(connection, SQLDialect.POSTGRES);
        insert = new DefaultDynamicInsert(dsl);
        update = new DefaultDynamicUpdate(dsl);
    }

    @AfterAll
    static void close() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    void schema() {
        dsl.execute("drop table if exists product, tag, item, marker");
        dsl.execute("create table marker (id uuid primary key, label text, created_by text)");
        dsl.execute("""
                create table product (
                    id uuid primary key default gen_random_uuid(),
                    code text not null constraint uk_product_code unique,
                    note text,
                    team_id uuid,
                    is_deleted boolean not null default false,
                    version bigint not null default 0,
                    created_at timestamptz not null default now(),
                    created_by text default 'system')""");
        dsl.execute("""
                create table tag (
                    id uuid primary key default gen_random_uuid(),
                    code text not null constraint uk_tag_code unique,
                    team_id uuid,
                    hits bigint not null default 0)""");
        dsl.execute("""
                create table item (
                    id uuid primary key default gen_random_uuid(),
                    order_id uuid not null,
                    so_line bigint not null,
                    note text,
                    is_deleted boolean not null default false)""");
        dsl.execute("create unique index uk_item_line on item (order_id, so_line) where is_deleted is not true");
    }

    // ------------------------------------------------------------------ insert

    @Nested
    class Insert {

        @Test
        void batchInsertKeepsEveryRowsOwnFieldsAndDefaults() {
            int[] counts = insert.batchInsert(PRODUCT, List.of(
                    row("code", "A"),
                    row("code", "B", "note", "keep me", "isDeleted", true),
                    row("code", "C", "note", null)));

            assertThat(counts).containsExactly(1, 1, 1);
            assertThat(product("B")).containsEntry("note", "keep me").containsEntry("is_deleted", true);
            // rows without the column get the column default, not NULL
            assertThat(product("A")).containsEntry("is_deleted", false).containsEntry("created_by", "system");
            assertThat(product("A").get("id")).isNotNull();
            assertThat(product("C")).containsEntry("note", null);
        }

        @Test
        void emptyBatchInsertWritesNothing() {
            assertThat(insert.batchInsert(PRODUCT, List.of())).isEmpty();
            assertThat(count("product")).isZero();
        }

        @Test
        void insertBatchIgnoreDuplicateSkipsExistingRows() {
            insert.insert(PRODUCT, row("code", "A", "note", "original"));

            int[] counts = insert.insertBatchIgnoreDuplicate(PRODUCT, List.of(
                    row("code", "A", "note", "duplicate"),
                    row("code", "B")));

            assertThat(counts).containsExactly(0, 1);
            assertThat(product("A")).containsEntry("note", "original");
            assertThat(count("product")).isEqualTo(2);
        }

        @Test
        void insertReturningListReturnsRowsOfEveryColumnSet() {
            List<Map<String, Object>> rows = insert.insertReturning(PRODUCT,
                    List.of(row("code", "A"), row("code", "B", "note", "n")), List.of("id", "code", "note"));

            assertThat(rows).extracting(r -> r.get("code")).containsExactlyInAnyOrder("A", "B");
            assertThat(rows).allSatisfy(r -> assertThat(r.get("id")).isNotNull());
        }

        @Test
        void insertReturningInsertedRowsSkipsConflicts() {
            insert.insert(PRODUCT, row("code", "A"));

            List<Map<String, Object>> rows = insert.insertReturningInsertedRows(PRODUCT,
                    List.of(row("code", "A"), row("code", "B", "note", "n")), "uk_product_code");

            assertThat(rows).extracting(r -> r.get("code")).containsExactly("B");
        }

        @Test
        void rowWithOnlyUnknownKeysWritesNothing() {
            assertThatThrownBy(() -> insert.batchInsert(PRODUCT, List.of(row("code", "A"), row("product_code", "B"))))
                    .isInstanceOf(FieldValueRequiredException.class)
                    .hasMessageContaining("Row 1 of object product has no field to write: unknown keys [product_code]");
            assertThat(count("product")).isZero();
        }
    }

    // ------------------------------------------------------------------ upsert

    @Nested
    class Upsert {

        private UUID existingId;
        private OffsetDateTime existingCreatedAt;

        @BeforeEach
        void existingRow() {
            insert.insert(PRODUCT, row("code", "A", "note", "old", "teamId", TEAM_A, "createdBy", "alice"));
            Map<String, Object> row = product("A");
            existingId = (UUID) row.get("id");
            existingCreatedAt = (OffsetDateTime) row.get("created_at");
        }

        @Test
        void upsertKeepsPrimaryKeyAndCreationAudit() {
            insert.upsert(PRODUCT, row("id", UUID.randomUUID(), "code", "A", "note", "new",
                    "createdAt", java.time.Instant.EPOCH, "createdBy", "bob"), "uk_product_code");

            Map<String, Object> row = product("A");
            assertThat(row.get("id")).isEqualTo(existingId);
            assertThat(row.get("created_at")).isEqualTo(existingCreatedAt);
            assertThat(row).containsEntry("created_by", "alice").containsEntry("note", "new");
        }

        @Test
        void upsertNullClearsAndAbsentKeyKeeps() {
            insert.upsert(PRODUCT, row("code", "A", "note", null), "uk_product_code");

            assertThat(product("A")).containsEntry("note", null).containsEntry("team_id", TEAM_A);
        }

        @Test
        void upsertInsertsANewRow() {
            assertThat(insert.upsert(PRODUCT, row("code", "B", "note", "n"), "uk_product_code")).isEqualTo(1);

            assertThat(product("B")).containsEntry("note", "n").containsEntry("is_deleted", false);
        }

        @Test
        void upsertOnConflictFieldsWithPartialIndexPredicate() {
            UUID order = UUID.randomUUID();
            var predicate = DSL.condition("is_deleted is not true");
            insert.upsert(ITEM, row("orderId", order, "soLine", 1, "note", "first"), List.of("orderId", "soLine"), predicate);
            insert.upsert(ITEM, row("orderId", order, "soLine", 1, "note", "second"), List.of("order_id", "so_line"), predicate);

            assertThat(count("item")).isEqualTo(1);
            assertThat(dsl.fetchValue("select note from item")).isEqualTo("second");
        }

        @Test
        void upsertBatchWithDifferentFieldsPerRow() {
            int count = insert.upsertBatch(PRODUCT, List.of(
                    row("code", "A", "note", "updated"),
                    row("code", "B"),
                    row("code", "C", "teamId", TEAM_B)), List.of("uk_product_code"));

            assertThat(count).isEqualTo(3);
            assertThat(product("A")).containsEntry("note", "updated").containsEntry("team_id", TEAM_A);
            assertThat(product("C")).containsEntry("team_id", TEAM_B);
        }

        @Test
        void upsertReturningByFieldsReturnsInsertedAndUpdatedRows() {
            List<Map<String, Object>> rows = insert.upsertReturning(PRODUCT,
                    List.of(row("code", "A", "note", "u"), row("code", "B")), List.of("code"), List.of("id", "code", "note"));

            assertThat(rows).hasSize(2);
            assertThat(rows).filteredOn(r -> "A".equals(r.get("code"))).singleElement()
                    .satisfies(r -> assertThat(r).containsEntry("id", existingId).containsEntry("note", "u"));
        }

        @Test
        void upsertReturningWithOnlyNonUpdatableFieldsStillReturnsTheRow() {
            UUID id = UUID.randomUUID();
            insert.insert(MARKER, row("id", id, "label", "kept", "createdBy", "alice"));

            List<Map<String, Object>> rows = insert.upsertReturning(MARKER,
                    List.of(row("id", id, "createdBy", "bob")), "marker_pkey", List.of("id", "label", "createdBy"));

            assertThat(rows).singleElement().satisfies(r -> assertThat(r)
                    .containsEntry("label", "kept").containsEntry("createdBy", "alice"));
        }

        @Test
        void upsertPayloadMustCarryNotNullColumnsEvenForAnExistingRow() {
            // PostgreSQL checks NOT NULL on the proposed row before the conflict is detected
            assertThatThrownBy(() -> insert.upsert(PRODUCT, row("id", existingId, "note", "n"), "product_pkey"))
                    .isInstanceOf(org.jooq.exception.DataAccessException.class)
                    .hasMessageContaining("null value in column \"code\"");
        }
    }

    // ------------------------------------------------------------------ update

    @Nested
    class Update {

        private UUID a;
        private UUID b;

        @BeforeEach
        void rows() {
            insert.batchInsert(PRODUCT, List.of(
                    row("code", "A", "teamId", TEAM_A, "version", 3),
                    row("code", "B", "teamId", TEAM_A, "version", 7),
                    row("code", "C", "teamId", TEAM_B)));
            a = (UUID) product("A").get("id");
            b = (UUID) product("B").get("id");
        }

        @Test
        void updateByIdIncrementsVersion() {
            update.updateById(PRODUCT, a, row("note", "n", "version", 3));

            assertThat(product("A")).containsEntry("note", "n").containsEntry("version", 4L);
        }

        @Test
        void updateByIdWithStaleVersionFailsAndWritesNothing() {
            assertThatThrownBy(() -> update.updateById(PRODUCT, a, row("note", "n", "version", 2)))
                    .isInstanceOf(OptimisticLockingFailureException.class);

            assertThat(product("A")).containsEntry("note", null).containsEntry("version", 3L);
        }

        @Test
        void updateByIdNeverChangesThePrimaryKey() {
            update.updateById(PRODUCT, a, row("id", UUID.randomUUID(), "note", "n", "version", 3));

            assertThat(product("A")).containsEntry("id", a).containsEntry("note", "n");
        }

        @Test
        void updateByConditionUpdatesEveryRowAndItsOwnVersion() {
            int updated = update.update(PRODUCT, row("note", "team", "version", 3), TEAM_ID.eq(TEAM_A));

            assertThat(updated).isEqualTo(2);
            assertThat(product("A")).containsEntry("note", "team").containsEntry("version", 4L);
            assertThat(product("B")).containsEntry("note", "team").containsEntry("version", 8L);
            assertThat(product("C")).containsEntry("note", null);
        }

        @Test
        void softDeleteByCondition() {
            new DefaultDynamicDelete(dsl, update).delete(PRODUCT, row("isDeleted", true), TEAM_ID.eq(TEAM_A));

            assertThat(product("A")).containsEntry("is_deleted", true);
            assertThat(product("B")).containsEntry("is_deleted", true);
            assertThat(product("C")).containsEntry("is_deleted", false);
        }

        @Test
        void batchUpdateReturnsJdbcCountsInOrder() {
            int[] counts = update.batchUpdate(PRODUCT, List.of(
                    row("id", a, "note", "x", "version", 3),
                    row("id", b, "version", 7, "teamId", TEAM_B)));

            assertThat(counts).containsExactly(1, 1);
            assertThat(product("B")).containsEntry("team_id", TEAM_B).containsEntry("version", 8L);
        }

        @Test
        void batchUpdateReportsEveryStaleRow() {
            assertThatThrownBy(() -> update.batchUpdate(PRODUCT, List.of(
                    row("id", a, "note", "x", "version", 1),
                    row("id", b, "note", "y", "version", 7),
                    row("id", UUID.randomUUID(), "note", "z", "version", 0))))
                    .isInstanceOf(OptimisticLockingFailureException.class)
                    .hasMessageContaining("rows [0, 2]");
            // no transaction: the valid row was written
            assertThat(product("B")).containsEntry("note", "y");
        }

        @Test
        void batchUpdateInATransactionIsAllOrNothing() {
            assertThatThrownBy(() -> dsl.transaction(cfg -> new DefaultDynamicUpdate(cfg.dsl()).batchUpdate(PRODUCT, List.of(
                    row("id", a, "note", "x", "version", 1),
                    row("id", b, "note", "y", "version", 7)))))
                    .isInstanceOf(OptimisticLockingFailureException.class);

            assertThat(product("B")).containsEntry("note", null).containsEntry("version", 7L);
        }

        @Test
        void updateReturningListAcrossChunks() {
            List<Map<String, Object>> tags = IntStream.range(0, 1005).mapToObj(i -> row("code", "T" + i)).toList();
            insert.batchInsert(TAG, tags);
            List<Map<String, Object>> items = dsl.fetch("select id, code from tag").stream()
                    .map(r -> row("id", r.get("id"), "teamId", TEAM_B))
                    .toList();

            assertThat(update.updateReturning(TAG, items, List.of("id", "teamId"))).hasSize(1005)
                    .allSatisfy(r -> assertThat(r).containsEntry("teamId", TEAM_B));
        }

        @Test
        void updateReturningByConditionReturnsTheOneRow() {
            Map<String, Object> row = update.updateReturning(PRODUCT, row("note", "only"), TEAM_ID.eq(TEAM_B), List.of("code", "version"));

            assertThat(row).containsEntry("code", "C").containsEntry("version", 1L);
        }

        @Test
        void updateReturningByConditionMatchingSeveralRowsRollsBackInATransaction() {
            assertThatThrownBy(() -> dsl.transaction(cfg -> new DefaultDynamicUpdate(cfg.dsl())
                    .updateReturning(PRODUCT, row("note", "many"), TEAM_ID.eq(TEAM_A))))
                    .isInstanceOf(FieldValueInvalidException.class)
                    .hasMessageContaining("Condition matched 2 rows of object product");

            assertThat(product("A")).containsEntry("note", null);
        }

        @Test
        void updateAllReturningReturnsEveryRow() {
            List<Map<String, Object>> rows = update.updateAllReturning(PRODUCT, row("note", "all"), TEAM_ID.eq(TEAM_A), List.of("code"));

            assertThat(rows).extracting(r -> r.get("code")).containsExactlyInAnyOrder("A", "B");
        }

        @Test
        void noMatchingRowIsNotFound() {
            assertThatThrownBy(() -> update.updateById(TAG, UUID.randomUUID(), row("code", "x")))
                    .isInstanceOf(ObjectNotFoundException.class);
            assertThatThrownBy(() -> update.update(PRODUCT, row("note", "x"), TEAM_ID.eq(UUID.randomUUID())))
                    .isInstanceOf(ObjectNotFoundException.class);
        }

        @Test
        void updateWithExpressionsIncrementsCounters() {
            insert.insert(TAG, row("code", "T", "teamId", TEAM_A));
            Field<Long> hits = DSL.field(DSL.name("hits"), Long.class);

            update.updateWithExpressions(TAG, row("hits", hits.plus(5)), TEAM_ID.eq(TEAM_A));
            update.updateWithExpressions(TAG, row("hits", hits.plus(2)), TEAM_ID.eq(TEAM_A));

            assertThat(dsl.fetchValue("select hits from tag where code = 'T'")).isEqualTo(7L);
        }

        @Test
        void batchUpdateWithExpressionsAppliesEachRowsOwnExpressionAndGuard() {
            Field<String> note = DSL.field(DSL.name("note"), String.class);
            Field<String> code = DSL.field(DSL.name("code"), String.class);

            int[] affected = update.batchUpdateWithExpressions(PRODUCT, List.of(
                    ExpressionUpdate.of(a, row("note", DSL.concat(code, DSL.inline("-1")))),
                    new ExpressionUpdate(b, row("note", "blocked"), code.eq("not B")),
                    ExpressionUpdate.of(UUID.randomUUID(), row("note", "nobody")),
                    ExpressionUpdate.of(a, row("note", DSL.concat(note, DSL.inline("-2"))))));

            assertThat(affected).containsExactly(1, 0, 0, 1);
            // same id twice: the second runs on the result of the first; each update moves the version
            assertThat(product("A")).containsEntry("note", "A-1-2").containsEntry("version", 5L);
            assertThat(product("B")).containsEntry("note", null).containsEntry("version", 7L);
        }
    }

    // ------------------------------------------------------------------ helpers

    private static ObjectMetadata metadata(String name, Attribute... attributes) {
        return ObjectMetadata.builder().name(name).tableName(name).attributes(new ArrayList<>(List.of(attributes))).build();
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

    private static Map<String, Object> product(String code) {
        Record record = dsl.fetchOne("select * from product where code = ?", code);
        assertThat(record).as("product " + code).isNotNull();
        return record.intoMap();
    }

    private static int count(String table) {
        return dsl.fetchCount(DSL.table(DSL.name(table)));
    }
}
