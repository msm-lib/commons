package com.msm.core.dynamicquery.command;

import com.msm.core.metadata.ObjectMetadata;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.UpdatableRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecordBuilder {
    public static List<UpdatableRecord<?>> build(
            DSLContext dsl,
            ObjectMetadata meta,
            List<Map<Field<?>, Object>> items) {

        List<UpdatableRecord<?>> records = new ArrayList<>();
        for (Map<Field<?>, Object> item : items) {
            UpdatableRecord<?> record = (UpdatableRecord<?>) dsl.newRecord(meta.getTable());
            item.forEach((key, value) -> {
                record.set((Field<Object>)key, value);
            });
            records.add(record);
        }

        return records;
    }
}
