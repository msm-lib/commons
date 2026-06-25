package com.msm.core.metadata.ref;

import com.msm.core.commons.object.FieldAccess;
import com.msm.core.commons.object.PropertyToken;
import com.msm.core.commons.object.TypeConverter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class ObjectRefUtils {

    private ObjectRefUtils() {
    }

    public static Object getProperty(
            Object root,
            String path) {

        if (root == null) {
            return null;
        }

        List<PropertyToken> tokens =
                PropertyPathCache.parse(path);

        Object current = root;

        for (PropertyToken token : tokens) {

            current = FieldAccess.get(
                    current,
                    token.getName());

            if (current == null) {
                return null;
            }

            if (token.getIndex() != null) {

                List<?> list = (List<?>) current;

                if (token.getIndex() >= list.size()) {
                    return null;
                }

                current = list.get(token.getIndex());

                if (current == null) {
                    return null;
                }
            }

            if (token.getKey() != null) {

                current = ((Map<?, ?>) current)
                        .get(token.getKey());

                if (current == null) {
                    return null;
                }
            }
        }

        return current;
    }

    public static void setProperty(
            Object root,
            String path,
            Object value) {

        setProperty(
                root,
                path,
                value,
                MissingFieldStrategy.IGNORE
        );
    }

    public static void setProperty(
            Object root,
            String path,
            Object value,
            MissingFieldStrategy strategy) {

        List<PropertyToken> tokens =
                PropertyPathCache.parse(path);

        Object current = root;

        for (int i = 0; i < tokens.size() - 1; i++) {

            PropertyToken token = tokens.get(i);

            current = ensureNode(
                    current,
                    token,
                    strategy);

            if (current == null) {
                return;
            }
        }

        PropertyToken last = tokens.get(tokens.size() - 1);

        Field field =
                ReflectionCache.findField(
                        current.getClass(),
                        last.getName());

        if (field == null) {

            if (strategy == MissingFieldStrategy.THROW_EXCEPTION) {
                throw new IllegalArgumentException(
                        "Field not found: "
                                + last.getName()
                                + " in "
                                + current.getClass());
            }

            return;
        }

        Object converted = value;

        try {
            converted =
                    TypeConverter.convert(
                            value,
                            field.getType());
        } catch (Exception ignored) {
        }

        FieldAccess.set(
                current,
                last.getName(),
                converted);
    }

    private static Object ensureNode(
            Object parent,
            PropertyToken token,
            MissingFieldStrategy strategy) {

        Field field =
                ReflectionCache.findField(
                        parent.getClass(),
                        token.getName());

        if (field == null) {

            if (strategy == MissingFieldStrategy.THROW_EXCEPTION) {
                throw new IllegalArgumentException(
                        "Field not found: "
                                + token.getName()
                                + " in "
                                + parent.getClass());
            }

            return null;
        }

        Object value =
                FieldAccess.get(
                        parent,
                        token.getName());

        if (value == null) {

            value = instantiateField(field);

            FieldAccess.set(
                    parent,
                    token.getName(),
                    value);
        }

        if (token.getIndex() != null) {

            List<Object> list =
                    (List<Object>) value;

            while (list.size() <= token.getIndex()) {
                list.add(null);
            }

            Object element =
                    list.get(token.getIndex());

            if (element == null) {

                Class<?> elementType =
                        ReflectionCache
                                .getListElementType(field);

                try {

                    element =
                            elementType
                                    .getDeclaredConstructor()
                                    .newInstance();

                    list.set(
                            token.getIndex(),
                            element);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            value = element;
        }

        if (token.getKey() != null) {

            Map<Object, Object> map =
                    (Map<Object, Object>) value;

            Object child =
                    map.get(token.getKey());

            if (child == null) {

                child =
                        new LinkedHashMap<>();

                map.put(
                        token.getKey(),
                        child);
            }

            value = child;
        }

        return value;
    }

    private static Object instantiateField(
            Field field) {

        try {

            Class<?> type = field.getType();

            if (List.class.isAssignableFrom(type)) {
                return new ArrayList<>();
            }

            if (Set.class.isAssignableFrom(type)) {
                return new LinkedHashSet<>();
            }

            if (Map.class.isAssignableFrom(type)) {
                return new LinkedHashMap<>();
            }

            return type
                    .getDeclaredConstructor()
                    .newInstance();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Cannot instantiate "
                            + field.getType(),
                    e);
        }
    }
}
