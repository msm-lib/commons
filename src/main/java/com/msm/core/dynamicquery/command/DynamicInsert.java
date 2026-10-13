package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Condition;

import java.util.List;
import java.util.Map;

/**
 * Dynamic insert operations for metadata-driven entities.
 *
 * <p>Keys unknown to the metadata are not written. An insert skips null values (the column default applies); an
 * upsert writes them, so a null clears the value of an existing row. Rows of a multi-row call may carry different
 * fields. Upserts never update the primary key nor the creation audit fields of an existing row.
 *
 * <p>Supports:
 * <ul>
 *     <li>Single row insert</li>
 *     <li>Batch insert</li>
 *     <li>Insert with RETURNING clause</li>
 *     <li>Insert with conflict handling (ON CONFLICT DO NOTHING)</li>
 * </ul>
 */
public interface DynamicInsert {

    /**
     * Inserts a single record.
     *
     * @param meta entity metadata
     * @param values field-value map
     * @return number of affected rows
     */
    int insert(ObjectMetadata meta, Map<String, Object> values);

    /**
     * Inserts multiple records with JDBC batches.
     *
     * @param meta entity metadata
     * @param values list of field-value maps
     * @return affected rows of each record, in input order
     */
    int[] batchInsert(ObjectMetadata meta, List<Map<String, Object>> values);

    /**
     * @deprecated renamed to {@link #batchInsert}
     */
    @Deprecated
    default int[] insert(ObjectMetadata meta, List<Map<String, Object>> values) {
        return batchInsert(meta, values);
    }

    /**
     * Inserts a record and returns all fields of the inserted row.
     *
     * @param meta entity metadata
     * @param values field-value map
     * @return inserted row
     */
    Map<String, Object> insertReturning(ObjectMetadata meta, Map<String, Object> values);

    /**
     * Inserts a record and returns selected fields.
     *
     * @param meta entity metadata
     * @param values field-value map
     * @param returnFields fields to return
     * @return inserted row
     */
    Map<String, Object> insertReturning(
            ObjectMetadata meta,
            Map<String, Object> values,
            List<String> returnFields);

    /**
     * Inserts multiple records and returns all fields.
     *
     * @param meta entity metadata
     * @param items records to insert
     * @return inserted rows
     */
    List<Map<String, Object>> insertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items);

    /**
     * Inserts multiple records and returns selected fields.
     *
     * @param meta entity metadata
     * @param items records to insert
     * @param returnFields fields to return
     * @return inserted rows
     */
    List<Map<String, Object>> insertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> returnFields);

    /**
     * Inserts multiple records using
     * {@code ON CONFLICT DO NOTHING}.
     *
     * <p>Only successfully inserted rows are returned.
     *
     * @param meta entity metadata
     * @param items records to insert
     * @param conflictOnConstraintName unique constraint name
     * @return inserted rows
     */
    List<Map<String, Object>> insertReturningInsertedRows(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            String conflictOnConstraintName);

    /**
     * Inserts a record using
     * {@code ON CONFLICT DO NOTHING}.
     *
     * <p>Returns the inserted row if insertion succeeds,
     * otherwise returns {@code null}.
     *
     * @param objectMetadata entity metadata
     * @param values record values
     * @param conflictOnConstraintName unique constraint name
     * @return inserted row or {@code null}
     */
    Map<String, Object> insertReturningInsertedRow(
            ObjectMetadata objectMetadata,
            Map<String, Object> values,
            String conflictOnConstraintName);

    int upsert(ObjectMetadata meta, Map<String, Object> values, String conflictOnConstraintName);

    /**
     * Inserts a record, or updates it on conflict over the given columns.
     *
     * @param meta entity metadata
     * @param values record values
     * @param conflictFieldNames conflict target: field names (or column names) of a unique index
     * @param condition predicate of a partial unique index ({@code ON CONFLICT (...) WHERE ...});
     *                  it must match that index, it does not filter which row is updated. {@code null}: none
     * @return number of affected rows
     */
    int upsert(
            ObjectMetadata meta,
            Map<String, Object> values,
            List<String> conflictFieldNames,
            Condition condition
    );

    List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName);

    List<Map<String, Object>> upsertReturning(ObjectMetadata meta, List<Map<String, Object>> items, String conflictOnConstraintName, List<String> returnFields);

    List<Map<String, Object>> upsertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> conflictFields);

    List<Map<String, Object>> upsertReturning(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            List<String> conflictFields,
            List<String> returnFields);

    int[] insertBatchIgnoreDuplicate(
            ObjectMetadata objectMetadata,
            List<Map<String, Object>> values
    );
}
