package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;

import java.util.List;
import java.util.Map;

/**
 * Dynamic insert operations for metadata-driven entities.
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
     * Inserts multiple records.
     *
     * @param meta entity metadata
     * @param values list of field-value maps
     * @return affected rows for each insert statement
     */
    int[] insert(ObjectMetadata meta, List<Map<String, Object>> values);

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
}
