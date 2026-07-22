package com.msm.core.commons;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Stream;

public final class ArrayUtils {

    ArrayUtils() {
    }

    public boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public boolean isNotEmpty(Object[] array) {
        return !isEmpty(array);
    }

    public int length(Object[] array) {
        return array == null ? 0 : array.length;
    }

    public <T> T[] concat(T[]... arrays) {
        return Arrays.stream(arrays)
                .filter(Objects::nonNull)
                .flatMap(Arrays::stream)
                .toArray(size -> Arrays.copyOf(arrays[0], size));
    }

    public <T> T[] add(T[] array, T element) {
        if (array == null) {
            return (T[]) new Object[]{element};
        }

        T[] result = Arrays.copyOf(array, array.length + 1);
        result[array.length] = element;

        return result;
    }

    public <T> T[] add(T[] array, int index, T element) {

        if (array == null) {
            return add(null, element);
        }

        if (index < 0 || index > array.length) {
            throw new IndexOutOfBoundsException("Index: " + index);
        }

        T[] result = Arrays.copyOf(array, array.length + 1);

        System.arraycopy(
                result,
                index,
                result,
                index + 1,
                array.length - index
        );

        result[index] = element;

        return result;
    }


    public <T> T[] remove(T[] array, int index) {

        if (array == null || index < 0 || index >= array.length) {
            return array;
        }

        T[] result = Arrays.copyOf(array, array.length - 1);

        System.arraycopy(
                array,
                index + 1,
                result,
                index,
                array.length - index - 1
        );

        return result;
    }

    public <T> boolean contains(T[] array, T value) {

        if (array == null) {
            return false;
        }

        return Arrays.asList(array).contains(value);
    }

    public String join(Object[] array, String separator) {

        if (isEmpty(array)) {
            return "";
        }

        return Arrays.stream(array)
                .map(String::valueOf)
                .collect(java.util.stream.Collectors.joining(separator));
    }

    public <T> T[] fromCollection(Collection<T> collection, Class<T> type) {

        if (collection == null) {
            return (T[]) Array.newInstance(type, 0);
        }

        return collection.toArray(size -> (T[]) Array.newInstance(type, size));
    }

    public <T> T first(T[] array) {

        if (isEmpty(array)) {
            return null;
        }

        return array[0];
    }

    public <T> T last(T[] array) {

        if (isEmpty(array)) {
            return null;
        }

        return array[array.length - 1];
    }

    public <T> T[] subArray(T[] array, int from, int to) {

        if (array == null) {
            return null;
        }

        return Arrays.copyOfRange(
                array,
                from,
                to
        );
    }

    public <T> Stream<T> stream(T[] array) {

        return array == null
                ? Stream.empty()
                : Arrays.stream(array);
    }
}