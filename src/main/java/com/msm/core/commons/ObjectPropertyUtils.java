package com.msm.core.commons;

import com.msm.core.commons.object.FieldAccess;
import com.msm.core.commons.object.PropertyPathParser;
import com.msm.core.commons.object.PropertyToken;
import com.msm.core.commons.object.TypeConverter;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ObjectPropertyUtils {
    ObjectPropertyUtils() {}

    public Object getProperty(Object root, String path) {
        List<PropertyToken> tokens = PropertyPathParser.parse(path);
        Object current = root;

        for (PropertyToken token : tokens) {
            current = FieldAccess.get(current, token.getName());
            if (Objects.isNull(current)) return null;
            if (Objects.nonNull(token.getIndex())) {
                current = ((List<?>) current).get(token.getIndex());
            }
            if (Objects.nonNull(token.getKey())) {
                current = ((Map<?, ?>) current).get(token.getKey());
            }
        }

        return current;
    }

    public void setProperty(Object root, String path, Object value) {

        List<PropertyToken> tokens = PropertyPathParser.parse(path);
        Object current = root;

        for (int i = 0; i < tokens.size() - 1; i++) {

            PropertyToken token = tokens.get(i);
            current = newInstance(current, token);

            if (Objects.nonNull(token.getIndex())) {
                List list = (List) current;
                while (list.size() <= token.getIndex()) {
                    list.add(null);
                }
                Object element = list.get(token.getIndex());
                if (Objects.isNull(element)) {
                    try {
                        Field field = root.getClass().getDeclaredField(token.getName());
                        Class<?> genericType = (Class<?>) ((java.lang.reflect.ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        element = genericType.getDeclaredConstructor().newInstance();
                        list.set(token.getIndex(), element);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }

                current = element;
            }

            if (token.getKey() != null) {
                current = ((Map) current).get(token.getKey());
            }
        }

        PropertyToken last = tokens.getLast();
        Object converted = value;

        try {
            Field field = current.getClass().getDeclaredField(last.getName());
            converted = TypeConverter.convert(value, field.getType());
        } catch (Exception ignored) {
        }

        FieldAccess.set(current, last.getName(), converted);
    }

    private Object newInstance(Object current, PropertyToken token) {
        Object next = FieldAccess.get(current, token.getName());
        if (Objects.isNull(next)) {
            try {
                Field field = current.getClass().getDeclaredField(token.getName());
                field.setAccessible(true);
                next = field.getType().getDeclaredConstructor().newInstance();
                FieldAccess.set(current, token.getName(), next);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return next;
    }
}