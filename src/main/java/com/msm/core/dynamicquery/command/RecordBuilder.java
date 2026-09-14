package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.TableRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordBuilder {
    public static List<TableRecord<?>> build(
            DSLContext dsl,
            ObjectMetadata meta,
            List<Map<Field<?>, Object>> items) {

        List<TableRecord<?>> records = new ArrayList<>();
        for (Map<Field<?>, Object> item : items) {
            TableRecord<?> record = (TableRecord<?>) dsl.newRecord(meta.getTable());
            for (Map.Entry<Field<?>, Object> entry : item.entrySet()) {
                setRecordValue(record, entry.getKey(), entry.getValue());
            }
            records.add(record);
        }

        return records;
    }

    @SuppressWarnings("unchecked")
    private static <T> void setRecordValue(TableRecord<?> record, Field<T> field, Object value) {
        record.set(field, (T) value);
    }
}
