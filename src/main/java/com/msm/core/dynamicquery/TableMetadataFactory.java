package com.msm.core.dynamicquery;

import org.jooq.Table;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TableMetadataFactory {

    private static final String PACKAGE = "com.msm.core.objects.dsl";
    private static final Map<String, Table<?>> CACHE = new ConcurrentHashMap<>();

    public static Table<?> getTable(String tableName) {
        return CACHE.get(tableName);
    }

    public static void register(Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();

        for (Field f : fields) {
            try {
                if (!Modifier.isStatic(f.getModifiers())) continue;
                if (!Table.class.isAssignableFrom(f.getType())) continue;
                f.setAccessible(true);
                Table<?> table = (Table<?>) f.get(null);
                if (table == null) continue;
                CACHE.put(table.getName(), table);
            } catch (Exception e) {
                throw new RuntimeException("Failed to register: " + f.getName(), e);
            }
        }
    }
}
