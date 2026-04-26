package com.msm.core.dynamicquery.mapping.converter;

import com.msm.core.commons.Utils;
import org.jooq.impl.AbstractConverter;

import java.util.List;


public class GenericPostgresArrayConverter<T> extends AbstractConverter<T[], List<T>> {

    private final Class<T> type;

    @SuppressWarnings("unchecked")
    public GenericPostgresArrayConverter(Class<T[]> arrayClass, Class<T> type) {
        super(arrayClass, (Class<List<T>>) (Class<?>) List.class);
        this.type = type;
    }

    @Override
    public List<T> from(T[] databaseObject) {
        // Postgres Array (T[]) -> List<T>
        return databaseObject == null ? null : Utils.CL.newArrayList(databaseObject);
    }

    @Override
    public T[] to(List<T> userObject) {
        // List<T> -> Postgres Array (T[])
        if (userObject == null) return null;
        return Utils.O.convertListToArray(userObject, type);
    }
}
