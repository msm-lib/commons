package com.msm.core.commons;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GenericTypeResolver {
    GenericTypeResolver() {}
    private final TypeFactory tf = TypeFactory.defaultInstance();
    private final Map<String, JavaType> cache = new ConcurrentHashMap<>();

    private final Map<String, String> aliases = new HashMap<>();
    private final List<String> imports = new ArrayList<>() {{
        add("java.lang.*");
        add("java.util.*");
    }};

    private static final Map<String, Class<?>> SIMPLE_TYPES = new HashMap<>();

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

        // wrappers + common
        SIMPLE_TYPES.put("Integer", Integer.class);
        SIMPLE_TYPES.put("Long", Long.class);
        SIMPLE_TYPES.put("Double", Double.class);
        SIMPLE_TYPES.put("Float", Float.class);
        SIMPLE_TYPES.put("BigDecimal", BigDecimal.class);
        SIMPLE_TYPES.put("Boolean", Boolean.class);
        SIMPLE_TYPES.put("Character", Character.class);
        SIMPLE_TYPES.put("Byte", Byte.class);
        SIMPLE_TYPES.put("Short", Short.class);

        SIMPLE_TYPES.put("String", String.class);
        SIMPLE_TYPES.put("Object", Object.class);

        // collections
        SIMPLE_TYPES.put("List", List.class);
        SIMPLE_TYPES.put("Set", Set.class);
        SIMPLE_TYPES.put("Map", Map.class);
    }

    public GenericTypeResolver addImport(String pkg) {
        imports.add(pkg);
        return this;
    }

    public GenericTypeResolver addAlias(String simple, String fqcn) {
        aliases.put(simple, fqcn);
        return this;
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

    private Class<?> resolveClass(String name) {
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

        // imports
        for (String imp : imports) {
            if (imp.endsWith(".*")) {
                String fqcn = imp.replace(".*", "." + name);
                try {
                    return loadClass(fqcn);
                } catch (Exception ignored) {}
            } else if (imp.endsWith("." + name)) {
                return loadClass(imp);
            }
        }

        throw new RuntimeException("Cannot resolve type: " + name);
    }

    private Class<?> loadClass(String fqcn) {
        try {
            return Class.forName(fqcn);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + fqcn);
        }
    }

    private List<String> split(String input) {
        List<String> result = new ArrayList<>();
        int depth = 0;
        StringBuilder current = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '<') depth++;
            if (c == '>') depth--;

            if (c == ',' && depth == 0) {
                result.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        result.add(current.toString());
        return result;
    }
}