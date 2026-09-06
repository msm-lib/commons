package com.msm.core.commons;

import com.msm.core.metadata.typesafe.DataRecord;
import com.msm.core.metadata.typesafe.TypedAttribute;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utility class providing generic methods for grouping, transforming, and
 * mapping collections of data.
 *
 * <p>Grouping methods support both generic single keys and compound string keys.
 * Duplicate keys can either be rejected, explicitly merged, or accumulated
 * into a multi-map depending on the method used.</p>
 */
public class DataUtils {

    DataUtils() {
    }

    public <T, K, R> Map<K, R> groupBy(
            Collection<T> dataList,
            Function<T, K> keyExtractor,
            Function<T, R> valueMapper) {

        return Utils.CL.emptyIfNull(dataList)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueMapper
                ));
    }

    public <T, K, R> Map<K, R> groupBy(
            Collection<T> dataList,
            Function<T, K> keyExtractor,
            Function<T, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return Utils.CL.emptyIfNull(dataList)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueMapper,
                        mergeFunction
                ));
    }

    public <T, G, R> Map<String, R> groupBy(
            Collection<T> dataList,
            List<G> groupBys,
            BiFunction<T, G, Object> keyExtractor,
            Function<T, R> valueMapper) {

        return Utils.CL.emptyIfNull(dataList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        item -> buildCompoundKey(
                                groupBys,
                                groupBy -> keyExtractor.apply(item, groupBy)
                        ),
                        valueMapper
                ));
    }

    public <T, G, R> Map<String, R> groupBy(
            Collection<T> dataList,
            List<G> groupBys,
            BiFunction<T, G, Object> keyExtractor,
            Function<T, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return Utils.CL.emptyIfNull(dataList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        item -> buildCompoundKey(
                                groupBys,
                                groupBy -> keyExtractor.apply(item, groupBy)
                        ),
                        valueMapper,
                        mergeFunction
                ));
    }


    public <R> Map<String, R> groupBy(
            Collection<DataRecord> dataList,
            List<TypedAttribute<?>> groupBys,
            Function<DataRecord, R> valueMapper) {

        return groupBy(
                dataList,
                groupBys,
                DataRecord::get,
                valueMapper
        );
    }

    public <R> Map<String, R> groupBy(
            Collection<DataRecord> dataList,
            List<TypedAttribute<?>> groupBys,
            Function<DataRecord, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return groupBy(
                dataList,
                groupBys,
                DataRecord::get,
                valueMapper,
                mergeFunction
        );
    }


    public <T, R> Map<T, R> groupBy(
            Collection<DataRecord> dataRecordList,
            TypedAttribute<T> groupBy,
            Function<DataRecord, R> valueMapper) {

        return groupBy(dataRecordList, k -> k.get(groupBy), valueMapper);
    }


    public <T, R> Map<T, R> groupBy(
            Collection<DataRecord> dataRecordList,
            TypedAttribute<T> groupBy,
            Function<DataRecord, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return groupBy(dataRecordList, k -> k.get(groupBy), valueMapper, mergeFunction);
    }

    public <K, V> Map<String, List<Map<K, V>>> groupMapListToMultiMap(
            Collection<Map<K, V>> mapList,
            List<K> groupBys) {

        return Utils.CL.emptyIfNull(mapList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        map -> buildCompoundKey(
                                groupBys,
                                map::get
                        )
                ));
    }

    public <K, V, R> List<R> toEntityList(
            Collection<Map<K, V>> mapList,
            Function<Map<K, V>, R> creatorFunc) {

        return Utils.CL.emptyIfNull(mapList).stream()
                .filter(Objects::nonNull)
                .map(creatorFunc)
                .collect(Collectors.toList());
    }

    public <T> BigDecimal sum(
            Collection<T> dataRecords,
            Function<T, BigDecimal> valueExtractor) {

        return Utils.CL.emptyIfNull(dataRecords)
                .stream()
                .filter(Objects::nonNull)
                .map(item -> Optional.ofNullable(valueExtractor.apply(item))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public <T, K> Map<K, BigDecimal> groupByAndSum(
            Collection<T> dataRecords,
            Function<T, K> keyExtractor,
            Function<T, BigDecimal> valueExtractor) {

        return Utils.CL.emptyIfNull(dataRecords)
                .stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        keyExtractor,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> Optional.ofNullable(valueExtractor.apply(item))
                                        .orElse(BigDecimal.ZERO),
                                Utils.N::add
                        )
                ));
    }

    public <T, G, S> Map<String, BigDecimal> groupByAndSum(
            Collection<T> dataRecords,
            List<G> groupBys,
            BiFunction<T, G, Object> groupKeyExtractor,
            List<S> sumAttributes,
            BiFunction<T, S, BigDecimal> sumValueExtractor) {

        return groupBy(
                dataRecords,
                groupBys,
                groupKeyExtractor,
                item -> sumAttributes.stream()
                        .map(attr -> sumValueExtractor.apply(item, attr))
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                Utils.N::add
        );
    }


    public Map<String, BigDecimal> groupByAndSum(
            Collection<DataRecord> dataRecords,
            List<TypedAttribute<?>> groupBys,
            List<TypedAttribute<BigDecimal>> sumAttributes) {

        return groupBy(
                dataRecords,
                groupBys,
                DataRecord::get,
                item -> sumAttributes
                        .stream()
                        .map(item::get)
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                Utils.N::add
        );
    }


    public <G> Map<G, BigDecimal> groupByAndSum(
            Collection<DataRecord> dataRecords,
            TypedAttribute<G> typedAttribute,
            TypedAttribute<BigDecimal> sumAttribute) {

        return groupByAndSum(
                dataRecords,
                dataRecord -> dataRecord.get(typedAttribute),
                dataRecord -> dataRecord.get(sumAttribute)
        );
    }


    public BigDecimal addPercentage(
            BigDecimal baseValue,
            BigDecimal percentage) {

        BigDecimal ratio = Utils.N.divide(
                percentage,
                BigDecimal.valueOf(100),
                2
        );

        return Utils.N.multiply(
                baseValue,
                Utils.N.add(BigDecimal.ONE, ratio)
        );
    }


    private <G> String buildCompoundKey(
            Collection<G> groupBys,
            Function<G, Object> valueExtractor) {

        return Utils.KEYS.getKey(
                groupBys.stream()
                        .map(valueExtractor)
                        .collect(Collectors.toList())
        );
    }





    public <T, R> List<R> toList(
            Collection<T> dataList,
            Function<T, R> valueExtractor) {

        return Utils.CL.emptyIfNull(dataList)
                .stream()
                .filter(Objects::nonNull)
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public <T, K, V> List<Map<K, V>> toMapList(
            Collection<T> dataRecords,
            Function<T, Map<K, V>> mapExtractor) {

        return toList(dataRecords, mapExtractor);
    }

    public <K, V> List<V> toList(Collection<Map<K, V>> mapList, K key) {

        return Utils.CL.emptyIfNull(mapList)
                .stream()
                .filter(Objects::nonNull)
                .map(map -> map.get(key))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public <V> List<V> toList(Collection<DataRecord> mapList, TypedAttribute<V> key) {
        return toList(
                mapList,
                dataRecord -> dataRecord.get(key)
        );
    }

    public List<Object> toObjectList(Collection<DataRecord> mapList, TypedAttribute<?> key) {
        return toList(
                mapList,
                dataRecord -> dataRecord.get(key)
        );
    }
}
