package com.msm.core.commons;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.jooq.JSON;
import org.jooq.JSONB;
import org.jooq.types.DayToSecond;
import org.jooq.types.YearToMonth;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class GenericTypeResolver {
    GenericTypeResolver() {}
    private final TypeFactory tf = TypeFactory.defaultInstance();
    private final Map<String, JavaType> cache = new ConcurrentHashMap<>();

    private final Map<String, String> aliases = new ConcurrentHashMap<>();
    private final Set<String> imports = new CopyOnWriteArraySet<>(Arrays.asList(
            "java.lang.*", "java.util.*", "java.math.*", "java.text.*", "java.time.*"
    ));


    private static final Map<String, Class<?>> SIMPLE_TYPES = new ConcurrentHashMap<>();

    static {
        // primitives
        SIMPLE_TYPES.put("int", int.class);
        SIMPLE_TYPES.put("long", long.class);
        SIMPLE_TYPES.put("double", double.class);
        SIMPLE_TYPES.put("float", float.class);
        SIMPLE_TYPES.put("boolean", boolean.class);
        SIMPLE_TYPES.put("char", char.class);
        SIMPLE_TYPES.put("byte", byte.class);
        SIMPLE_TYPES.put("short", short.class);

        // date and datetime
        SIMPLE_TYPES.put("Instant", Instant.class);
        SIMPLE_TYPES.put("LocalDate", LocalDate.class);
        SIMPLE_TYPES.put("LocalTime", LocalTime.class);
        SIMPLE_TYPES.put("LocalDateTime", LocalDateTime.class);
        SIMPLE_TYPES.put("OffsetTime", OffsetTime.class);
        SIMPLE_TYPES.put("OffsetDateTime", OffsetDateTime.class);
        SIMPLE_TYPES.put("YearToMonth", YearToMonth.class);
        SIMPLE_TYPES.put("DayToSecond", DayToSecond.class);

        //Number
        SIMPLE_TYPES.put("Byte", Byte.class);
        SIMPLE_TYPES.put("Short", Short.class);
        SIMPLE_TYPES.put("BigDecimal", BigDecimal.class);
        SIMPLE_TYPES.put("Integer", Integer.class);
        SIMPLE_TYPES.put("Long", Long.class);
        SIMPLE_TYPES.put("Double", Double.class);
        SIMPLE_TYPES.put("Float", Float.class);
        SIMPLE_TYPES.put("BigInteger", BigInteger.class);
        SIMPLE_TYPES.put("Number", Number.class);

        //String
        SIMPLE_TYPES.put("String", String.class);
        SIMPLE_TYPES.put("byte[]", byte[].class);

        // wrappers + common
        SIMPLE_TYPES.put("Boolean", Boolean.class);
        SIMPLE_TYPES.put("Character", Character.class);
        SIMPLE_TYPES.put("UUID", UUID.class);
        SIMPLE_TYPES.put("Object", Object.class);

        // collections
        SIMPLE_TYPES.put("List", List.class);
        SIMPLE_TYPES.put("Set", Set.class);
        SIMPLE_TYPES.put("Map", Map.class);
        SIMPLE_TYPES.put("JSON", JSON.class);
        SIMPLE_TYPES.put("JSONB", JSONB.class);
    }

    public void addImport(String pkg) {
        imports.add(pkg);
    }

    public void addAlias(String simple, String fullyQualifiedClassName) {
        aliases.putIfAbsent(simple, fullyQualifiedClassName);
    }

    public JavaType parse(String input) {
        return cache.computeIfAbsent(input, this::parseInternal);
    }

    private JavaType parseInternal(String input) {
        return parseType(input.trim());
    }

    private JavaType parseType(String str) {
        // array
        if (str.endsWith("[]")) {
            return tf.constructArrayType(parseType(str.substring(0, str.length() - 2)));
        }

        // generics
        int lt = str.indexOf('<');
        if (lt != -1) {
            String raw = str.substring(0, lt).trim();
            String inner = str.substring(lt + 1, str.lastIndexOf('>'));

            Class<?> rawClass = resolveClass(raw);

            List<String> parts = split(inner);
            JavaType[] params = parts.stream()
                    .map(p -> parseType(p.trim()))
                    .toArray(JavaType[]::new);

            return tf.constructParametricType(rawClass, params);
        }

        return tf.constructType(resolveClass(str));
    }

    Class<?> resolveClass(String name) {
        // alias
        if (aliases.containsKey(name)) {
            return loadClass(aliases.get(name));
        }

        // simple map
        if (SIMPLE_TYPES.containsKey(name)) {
            return SIMPLE_TYPES.get(name);
        }

        // fully qualified
        try {
            return loadClass(name);
        } catch (Exception ignored) {}

        // using imports to parse fully qualified class name
        for (String imp : imports) {
            String fullyQualifiedName = getFullyQualifiedName(imp, name);
            if (fullyQualifiedName != null) {
                try {
                    Class<?> clazz = Class.forName(fullyQualifiedName);
                    SIMPLE_TYPES.put(name, clazz);
                    SIMPLE_TYPES.put(fullyQualifiedName, clazz);
                    return clazz;
                } catch (ClassNotFoundException ignored) {}
            }
        }

        throw new RuntimeException("Cannot resolve type: " + name);
    }

    private String getFullyQualifiedName(String imp, String name) {
        return imp.endsWith(".*")
                ? imp.substring(0, imp.length() - 1) + name
                : (imp.endsWith("." + name) ? imp : null);
    }

    private Class<?> loadClass(String name) {
        return SIMPLE_TYPES.computeIfAbsent(name, fq -> {
            try {
                return Class.forName(fq);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Class not found: " + fq);
            }
        });
    }

    private List<String> split(String input) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '<') depth++;
            if (c == '>') depth--;

            if (c == ',' && depth == 0) {
                result.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        result.add(current.toString().trim());
        return result;
    }

    JavaType construct(Class<?> rawClass, JavaType... params) {
        if (params == null || params.length == 0) {
            return tf.constructType(rawClass);
        }

        return tf.constructParametricType(rawClass, params);
    }

    JavaType constructType(Class<?> clazz) {
        return tf.constructType(clazz);
    }

}