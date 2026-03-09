package com.msm.core.commons.object;

public class TypeConverter {

    public static Object convert(Object value, Class<?> targetType) {

        if (value == null) return null;

        if (targetType.isAssignableFrom(value.getClass()))
            return value;

        if (targetType == Integer.class || targetType == int.class)
            return Integer.parseInt(value.toString());

        if (targetType == Long.class || targetType == long.class)
            return Long.parseLong(value.toString());

        if (targetType == Double.class || targetType == double.class)
            return Double.parseDouble(value.toString());

        if (targetType == Float.class || targetType == float.class)
            return Float.parseFloat(value.toString());

        if (targetType == Short.class || targetType == short.class)
            return Short.parseShort(value.toString());

        if (targetType == Byte.class || targetType == byte.class)
            return Byte.parseByte(value.toString());

        if (targetType == Boolean.class || targetType == boolean.class)
            return Boolean.parseBoolean(value.toString());

        if (targetType == String.class)
            return value.toString();

        return value;
    }

}
