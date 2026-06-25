package com.msm.core.commons;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Supplier;

public final class CollectionUtils {

    public <T> Collection<T> emptyCollection() {
        return Collections.emptyList();
    }

    @SafeVarargs
    public final <T> List<T> newLinkedList(final T... elements) {
        if(Objects.isNull(elements)) {
            return new LinkedList<>();
        }
        List<T> returnList = new LinkedList<>();
        Collections.addAll(returnList, elements);
        return returnList;
    }

    @SafeVarargs
    public final <T> List<T> newArrayList(final T... elements) {
        if(Objects.isNull(elements)) {
            return new ArrayList<>();
        }
        List<T> returnList = new ArrayList<>(elements.length);
        Collections.addAll(returnList, elements);
        return returnList;
    }

    public <T> List<T> newArrayList(final Collection<T> elements) {
        if(Objects.isNull(elements)) {
            return new ArrayList<>();
        }
        List<T> returnList = new ArrayList<>(elements.size());
        returnList.addAll(elements);
        return returnList;
    }

    public <T> Collection<T> defaultIfEmpty(final Collection<T> input, final Supplier<Collection<T>> defaultSupplier) {
        Objects.requireNonNull(defaultSupplier);
        return isEmpty(input) ? defaultSupplier.get() : input;
    }

    public <T> Collection<T> emptyIfNull(final Collection<T> input) {
        return Objects.isNull(input) ? emptyCollection() : input;
    }

    public <T> List<T> emptyIfNull(final List<T> input) {
        return Objects.isNull(input) ? newArrayList() : input;
    }

    public <T> Set<T> emptyIfNull(final Set<T> input) {
        return Objects.isNull(input) ? newHashSet() : input;
    }

    public <K, V> Map<K, V> emptyIfNull(final Map<K, V> input) {
        return Objects.isNull(input) ? newHashMap() : input;
    }

    @SafeVarargs
    public final <T> Set<T> newHashSet(final T... elements) {
        if (Objects.isNull(elements)) {
            return Collections.emptySet();
        }
        Set<T> returnSet = new HashSet<>(elements.length);
        Collections.addAll(returnSet, elements);
        return returnSet;
    }

    public <T> boolean isEmpty(Collection<T> input) {
        return Objects.isNull(input) || input.isEmpty();
    }

    public <T> int size(Collection<T> input) {
        return Objects.isNull(input) ? 0 : input.size();
    }

    public boolean isNotEmpty(Collection<?> input) {
        return !isEmpty(input);
    }
    public boolean isEmpty(char[] array) {
        return array == null || array.length == 0;
    }
    public <K, V> boolean isEmpty(Map<K, V> input) {
        return Objects.isNull(input) || input.isEmpty();
    }

    public <K, V> boolean isNotEmpty(Map<K, V> input) {
        return !isEmpty(input);
    }

    public <K, V> Map<K, V> defaultIfEmpty(final Map<K, V> input, final Supplier<Map<K, V>> defaultSupplier) {
        Objects.requireNonNull(defaultSupplier);
        return isEmpty(input) ? defaultSupplier.get() : input;
    }

    public <O, K, V> Map<K, V> toMap(List<O> input, Function<O, K> keyMapper, Function<O, V> valueMapper) {
        Map<K, V> map = new HashMap<>();
        input.forEach(o -> {
            map.put(keyMapper.apply(o), valueMapper.apply(o));
        });
        return map;
    }

    public <K, V> Map<K, V> newHashMap() {
        return new HashMap<>();
    }

    public <K, V> Map<K, V> newConcurrentHashMap() {
        return new ConcurrentHashMap<>();
    }

    public <K, V> Map<K, V> newHashMap(K key, V value) {
        return new HashMap<>() {{
            put(key, value);
        }};
    }

    public <K, V> Map<K, V> newConcurrentHashMap(K key, V value) {
        return new ConcurrentHashMap<>() {{
            put(key, value);
        }};
    }

    public <X> X getFirst(List<X> input) {
        if(Utils.CL.isNotEmpty(input)) {
            return input.getFirst();
        }
        return null;
    }

//    public Map<String, Map<String, Object>> toMap(List<Map<String, Object>> input) {
//        if(Utils.CL.isNotEmpty(input)) {
//            return newHashMap();
//        }
//        return input
//                .stream()
//                .collect(Collectors.toMap(data -> {
//                    return data.get(objectMetadata.getIdAttribute().getFieldName());
//                }, Function.identity()));
//
//    }

    CollectionUtils() {}
}