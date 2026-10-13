package com.msm.core.dynamicquery.command;

import com.google.common.collect.Lists;
import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.ConditionUtils;
import com.msm.core.dynamicquery.SelectBuilder;
import com.msm.core.dynamicquery.locking.OptimisticLocks;
import com.msm.core.dynamicquery.mapping.DynamicQueryFieldValueMapper;
import com.msm.core.exceptions.CommonErrors;
import com.msm.core.exceptions.GenericBaseException;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Updates never write the primary key: an {@code id} in the values is ignored (by id updates use it to find the row).
 * <p>
 * Optimistic locking, for an object with a {@code version} field:
 * <ul>
 *   <li>Update by id ({@code updateById*}, {@code batchUpdate}, {@code update(List)}, {@code updateReturning(List)}):
 *       the values must carry the current {@code version}; the row is updated only if it still has that version,
 *       and gets {@code version + 1}. A row not updated is a {@code OptimisticLockingFailureException}.</li>
 *   <li>Update by condition ({@code update}, {@code updateReturning}, {@code updateWithExpressions} with a
 *       {@link Condition}): no version check, a {@code version} in the values is ignored, and every matched row gets
 *       its own {@code version + 1}.</li>
 * </ul>
 * Otherwise, no row updated is an {@code ObjectNotFoundException}.
 */
@RequiredArgsConstructor
public class DefaultDynamicUpdate implements DynamicUpdate{
    private static final int BATCH_SIZE = 1000;
    private static final int MAX_REPORTED_ROWS = 20;

    private final DSLContext dsl;

    @Override
    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items) {
        return updateReturning(meta, items, meta.getFieldNames());
    }

    public List<Map<String, Object>> updateReturning(ObjectMetadata meta, List<Map<String, Object>> items, List<String> returnFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        List<Integer> failedRows = new ArrayList<>();
        List<List<Map<String, Object>>> chunks = Lists.partition(Utils.CL.emptyIfNull(items), BATCH_SIZE);
        for (int chunk = 0; chunk < chunks.size(); chunk++) {
            result.addAll(bulkUpdateReturningChunk(meta, chunks.get(chunk), chunk * BATCH_SIZE, returnFields, failedRows));
        }
        requireAllUpdated(meta, failedRows);
        return result;
    }

    private List<Map<String, Object>> bulkUpdateReturningChunk(
            ObjectMetadata meta,
            List<Map<String, Object>> items,
            int firstRowIndex,
            List<String> returnFields,
            List<Integer> failedRows
    ) {
        Attribute idAttr = meta.getIdAttribute();
        List<Object> ids = new ArrayList<>();
        List<Query> queries = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Object id = requireId(meta, items.get(i), firstRowIndex + i);
            ids.add(id);
            queries.add(updateByIdQuery(meta, id, items.get(i), firstRowIndex + i));
        }

        executeBatch(queries, firstRowIndex, failedRows);
        return dsl.select(SelectBuilder.buildFields(meta, returnFields))
                .from(meta.getTable())
                .where(idAttr.getField().in(ids))
                .fetchMaps();
    }

    public int updateById(ObjectMetadata meta, Object id, Map<String, Object> values) {
        Object idValue = castId(meta, id);
        Map<Field<?>, Object> map = updateSet(meta, values, 0);
        Condition versionCondition = OptimisticLocks.apply(meta, map);

        int affected = dsl.update(meta.getTable())
                .set(map)
                .where(idCondition(meta, idValue))
                .and(versionCondition)
                .execute();

        if (affected == 0) {
            throw noRowUpdated(meta, true, "id " + idValue);
        }
        return affected;
    }

    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values) {
        return updateByIdReturning(meta, id, values, meta.getFieldNames());
    }

    public Map<String, Object> updateByIdReturning(ObjectMetadata meta, Object id, Map<String, Object> values, List<String> returnFields) {
        Object idValue = castId(meta, id);
        Map<Field<?>, Object> map = updateSet(meta, values, 0);
        Condition versionCondition = OptimisticLocks.apply(meta, map);

        Map<String, Object> affected = dsl.update(meta.getTable())
                .set(map)
                .where(idCondition(meta, idValue))
                .and(versionCondition)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchOneMap();

        if (Utils.CL.isEmpty(affected)) {
            throw noRowUpdated(meta, true, "id " + idValue);
        }
        return affected;
    }

    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        return updateReturning(meta, values, where, meta.getFieldNames());
    }

    public Map<String, Object> updateReturning(ObjectMetadata meta, Map<String, Object> values, Condition where, List<String> returnFields) {
        List<Map<String, Object>> rows = updateAllReturning(meta, values, where, returnFields);
        if (rows.size() > 1) {
            // already updated: undone only if the caller's transaction rolls back
            throw CommonErrors.invalid("where", "Condition matched " + rows.size() + " rows of object "
                    + meta.getName() + ", expected one; use updateAllReturning to update several rows");
        }
        return rows.getFirst();
    }

    @Override
    public List<Map<String, Object>> updateAllReturning(ObjectMetadata meta, Map<String, Object> values, Condition where, List<String> returnFields) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = updateSet(meta, values, 0);
        incrementVersion(meta, map);

        List<Map<String, Object>> rows = dsl.update(meta.getTable())
                .set(map)
                .where(where)
                .returning(SelectBuilder.buildFields(meta, returnFields))
                .fetchMaps();

        if (rows.isEmpty()) {
            throw noRowUpdated(meta, false, "condition");
        }
        return rows;
    }

    public int update(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = updateSet(meta, values, 0);
        incrementVersion(meta, map);

        int affected = dsl.update(meta.getTable())
                .set(map)
                .where(where)
                .execute();

        if (affected == 0) {
            throw noRowUpdated(meta, false, "condition");
        }
        return affected;
    }

    @Override
    public int updateWithExpressions(ObjectMetadata meta, Map<String, Object> values, Condition where) {
        ConditionUtils.requireWhereCondition(where);
        Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMapWithExpressions(meta, values);
        removePrimaryKey(meta, map);
        WriteRows.requireColumns(meta, values, map, 0);
        incrementVersion(meta, map);

        int affected = dsl.update(meta.getTable()).set(map).where(where).execute();
        if (affected == 0) {
            throw noRowUpdated(meta, false, "condition");
        }
        return affected;
    }

    public int[] batchUpdate(ObjectMetadata meta, List<Map<String, Object>> items) {
        if (Utils.CL.isEmpty(items)) {
            return new int[]{};
        }

        List<Query> queries = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            Object id = requireId(meta, items.get(i), i);
            queries.add(updateByIdQuery(meta, id, items.get(i), i));
        }
        List<Integer> failedRows = new ArrayList<>();
        int[] affected = executeBatch(queries, 0, failedRows);
        requireAllUpdated(meta, failedRows);
        return affected;
    }

    @Override
    public int update(ObjectMetadata meta, List<Map<String, Object>> items) {
        int[] affected = batchUpdate(meta, items);
        return Arrays.stream(affected).sum();
    }

    private Query updateByIdQuery(ObjectMetadata meta, Object id, Map<String, Object> item, int rowIndex) {
        Map<Field<?>, Object> map = updateSet(meta, item, rowIndex);
        Condition versionCondition = OptimisticLocks.apply(meta, map);
        return dsl.update(meta.getTable())
                .set(map)
                .where(idCondition(meta, id))
                .and(versionCondition);
    }

    /** Runs the batch and adds the (input) index of every row not updated to {@code failedRows}. */
    private int[] executeBatch(List<Query> queries, int firstRowIndex, List<Integer> failedRows) {
        int[] affected = dsl.batch(queries).execute();
        for (int i = 0; i < affected.length; i++) {
            if (affected[i] == 0) {
                failedRows.add(firstRowIndex + i);
            }
        }
        return affected;
    }

    /**
     * Fails with every row not updated. The batch has already run: the other rows are written unless the caller's
     * transaction rolls back.
     */
    private static void requireAllUpdated(ObjectMetadata meta, List<Integer> failedRows) {
        if (failedRows.isEmpty()) {
            return;
        }
        String rows = failedRows.size() <= MAX_REPORTED_ROWS
                ? failedRows.toString()
                : failedRows.subList(0, MAX_REPORTED_ROWS) + "... (" + failedRows.size() + " rows)";
        throw noRowUpdated(meta, true, "rows " + rows);
    }

    /** Columns to set: the known values, without the primary key. */
    private static Map<Field<?>, Object> updateSet(ObjectMetadata meta, Map<String, Object> values, int rowIndex) {
        Map<Field<?>, Object> map = DynamicQueryFieldValueMapper.toUpdateMap(meta, Utils.CL.emptyIfNull(values));
        removePrimaryKey(meta, map);
        return WriteRows.requireColumns(meta, values, map, rowIndex);
    }

    private static void removePrimaryKey(ObjectMetadata meta, Map<Field<?>, Object> map) {
        Attribute idAttr = meta.getIdAttribute();
        if (idAttr != null) {
            map.remove(idAttr.getField());
        }
    }

    /** Update by condition: each matched row gets its own {@code version + 1}; a version in the values is ignored. */
    private static void incrementVersion(ObjectMetadata meta, Map<Field<?>, Object> map) {
        Attribute versionAttr = meta.getVersionAttribute();
        if (versionAttr != null) {
            Field<Object> versionField = (Field<Object>) versionAttr.getField();
            map.remove(versionField);
            map.put(versionField, versionField.plus(1));
        }
    }

    private static Object requireId(ObjectMetadata meta, Map<String, Object> item, int rowIndex) {
        Attribute idAttr = meta.getIdAttribute();
        Object id = Utils.CL.emptyIfNull(item).get(idAttr.getFieldName());
        if (id == null) {
            throw CommonErrors.required(idAttr.getFieldName(),
                    "Row " + rowIndex + " of object " + meta.getName() + " has no " + idAttr.getFieldName());
        }
        return idAttr.cast(id);
    }

    private static Object castId(ObjectMetadata meta, Object id) {
        Attribute idAttr = meta.getIdAttribute();
        if (id == null) {
            throw CommonErrors.required(idAttr.getFieldName(), "Update of object " + meta.getName() + " requires an id");
        }
        return idAttr.cast(id);
    }

    private static Condition idCondition(ObjectMetadata meta, Object id) {
        return ((Field<Object>) meta.getIdAttribute().getField()).eq(id);
    }

    /** By id on a versioned object: version conflict (or deleted row). Otherwise: not found. */
    private static GenericBaseException noRowUpdated(ObjectMetadata meta, boolean byId, String target) {
        if (byId && meta.getVersionAttribute() != null) {
            return CommonErrors.optimisticLockingFailureException(meta.getName(),
                    "Version conflict or record not found: " + target);
        }
        return CommonErrors.objectNotFound(meta.getName(), "No " + meta.getName() + " updated for " + target);
    }
}
