package com.msm.core.dynamicquery.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.dynamicquery.mapping.converter.GenericPostgresArrayConverter;
import com.msm.core.dynamicquery.mapping.converter.GenericPostgresJsonConverter;
import org.jooq.Converter;
import org.jooq.DataType;
import org.jooq.impl.SQLDataType;

import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class JavaTypeMappingFactory {

    private static final Map<String, DataType<?>> DATA_TYPE_MAPPING = new LinkedHashMap<>();
    private static final Map<String, DataType<?>> CUSTOM_DATA_TYPE_MAPPING = new ConcurrentHashMap<>();

    static {

        register("int", SQLDataType.INTEGER);
        register("long", SQLDataType.BIGINT);
        register("double", SQLDataType.DOUBLE);
        register("float", SQLDataType.REAL);
        register("boolean", SQLDataType.BOOLEAN);
        register("char", SQLDataType.CHAR);
        register("byte", SQLDataType.TINYINT);
        register("short", SQLDataType.SMALLINT);


        register("LocalDate", SQLDataType.LOCALDATE);
        register("LocalTime", SQLDataType.LOCALTIME);
        register("LocalDateTime", SQLDataType.LOCALDATETIME);
        register("OffsetTime", SQLDataType.OFFSETTIME);
        register("OffsetDateTime", SQLDataType.OFFSETDATETIME);
        register("Instant", SQLDataType.INSTANT);
        register("YearToMonth", SQLDataType.INTERVALYEARTOMONTH);
        register("DayToSecond", SQLDataType.INTERVALDAYTOSECOND);


        register("Byte", SQLDataType.TINYINT);
        register("Short", SQLDataType.SMALLINT);
        register("Integer", SQLDataType.INTEGER);
        register("Long", SQLDataType.BIGINT);
        register("Float", SQLDataType.REAL);
        register("Double", SQLDataType.DOUBLE);
        register("BigDecimal", SQLDataType.DECIMAL);
        register("BigInteger", SQLDataType.DECIMAL_INTEGER);
        register("Number", SQLDataType.NUMERIC);


        register("String", SQLDataType.VARCHAR);
        register("Character", SQLDataType.CHAR);
        register("byte[]", SQLDataType.VARBINARY);

        
        register("UUID", SQLDataType.UUID);
        register("JSON", SQLDataType.JSON);
        register("JSONB", SQLDataType.JSONB);
        register("Boolean", SQLDataType.BOOLEAN);

    }

    public static void register(String type, DataType<?> dataType) {
        DATA_TYPE_MAPPING.put(type, dataType);
    }

    public static <T> DataType<T> getDataType(String type) {
        if (type == null) return (DataType<T>) SQLDataType.OTHER;

        // 1. Check simple data type
        if (DATA_TYPE_MAPPING.containsKey(type)) {
            return (DataType<T>) DATA_TYPE_MAPPING.get(type);
        }

        // 2. Check is List or Set or Collection or Map
        if(CUSTOM_DATA_TYPE_MAPPING.containsKey(type)) {
            return (DataType<T>) CUSTOM_DATA_TYPE_MAPPING.get(type);
        }

        // 3. Check supper class (isAssignableFrom)
        for (Map.Entry<String, DataType<?>> entry : DATA_TYPE_MAPPING.entrySet()) {
            JavaType targetType = GenericTypeResolverFactory.resolve(entry.getKey());
            JavaType superType = targetType.getSuperClass();
            if (superType != null && type.equals(superType.getTypeName())) {
                return (DataType<T>) entry.getValue();
            }
        }

        return getOrRegisterCustomConverter(type);
    }

    private static <T> DataType<T> getOrRegisterCustomConverter(String typeName) {
        JavaType targetType = GenericTypeResolverFactory.resolve(typeName);
        Converter<Object, ?> customConverter =  new GenericPostgresJsonConverter<>(targetType.getRawClass(), targetType);
        if (targetType.isMapLikeType()) {
            DataType<T> dataType = (DataType<T>) SQLDataType.JSONB.asConvertedDataType(customConverter);
            CUSTOM_DATA_TYPE_MAPPING.put(typeName, dataType);
            return dataType;
        }

        if (targetType.isCollectionLikeType() || targetType.isArrayType()) {
            //Check for base type: UUID, String, Integer,...
            //If that is base type then create convert from list to array
            //Exp: List<String>, List<UUID>
            Class<T> elementType = (Class<T>) targetType.getContentType().getRawClass();
            DataType<T> baseDataType = getBaseDataType(elementType.getSimpleName());
            if(baseDataType != null) {
                Class<T[]> arrayClass = (Class<T[]>) Array.newInstance(elementType, 0).getClass();
                DataType<T> dataType = (DataType<T>) baseDataType
                        .getArrayDataType()
                        .asConvertedDataType(new GenericPostgresArrayConverter<>(arrayClass, elementType));
                CUSTOM_DATA_TYPE_MAPPING.put(typeName, dataType);
                return dataType;
            }
        }

        //For json array, json
        //Exp: List<Map<String,Object>>, List<User>, User, List<Map<String,com.data.User>>
        DataType<T> dataType = (DataType<T>) SQLDataType.JSONB.asConvertedDataType(customConverter);
        CUSTOM_DATA_TYPE_MAPPING.put(typeName, dataType);

        return dataType;
    }

    private static <T> DataType<T> getBaseDataType(String typeName) {
        return (DataType<T>) DATA_TYPE_MAPPING.get(typeName);
    }

    public static boolean isJson(String typeName) {
        JavaType targetType = GenericTypeResolverFactory.resolve(typeName);
        if (targetType.isMapLikeType()) {
            return true;
        }

        return !DATA_TYPE_MAPPING.containsKey(typeName);
    }

//    public static void registerArrayType(String typeName) {
//        JavaType javaType = GenericTypeResolverFactory.resolve(typeName);
////        register(javaType.getRawClass(), resolveJooqDataType(typeName));
//    }
//    public static DataType<?> resolveJooqDataType(String typeName) {
//        if (typeName == null) return SQLDataType.OTHER;
//
//        String type = typeName.toLowerCase().trim();
//        boolean isArray = type.contains("[]") || type.contains("list") || type.contains("set") || type.startsWith("_");
//
//        if (type.contains("uuid")) {
//            return isArray ? SQLDataType.UUID.array() : SQLDataType.UUID;
//        }
//
//        if (type.contains("string") || type.contains("text") || type.contains("varchar")) {
//            return isArray ? SQLDataType.VARCHAR.array() : SQLDataType.VARCHAR;
//        }
//
//        if (type.contains("int") || type.contains("integer")) {
//            return isArray ? SQLDataType.INTEGER.array() : SQLDataType.INTEGER;
//        }
//
//        if (type.contains("long") || type.contains("bigint")) {
//            return isArray ? SQLDataType.BIGINT.array() : SQLDataType.BIGINT;
//        }
//
//        if (type.contains("boolean") || type.contains("bool")) {
//            return isArray ? SQLDataType.BOOLEAN.array() : SQLDataType.BOOLEAN;
//        }
//
//        if (type.contains("instant") || type.contains("timestamp")) {
//            return isArray ? SQLDataType.INSTANT.array() : SQLDataType.INSTANT;
//        }
//
//        if (type.contains("localdate") || type.contains("date")) {
//            return isArray ? SQLDataType.LOCALDATE.array() : SQLDataType.LOCALDATE;
//        }
//
//        if (type.contains("decimal") || type.contains("numeric")) {
//            return isArray ? SQLDataType.NUMERIC.array() : SQLDataType.NUMERIC;
//        }
//
//        if (type.contains("double") || type.contains("float8")) {
//            return isArray ? SQLDataType.DOUBLE.array() : SQLDataType.DOUBLE;
//        }
//
//        return isArray ? SQLDataType.OTHER.array() : SQLDataType.OTHER;
//    }
}
