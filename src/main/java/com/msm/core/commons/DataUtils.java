package com.msm.core.commons;

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

    // -------------------------------------------------------------------------
    // Grouping - Generic Key
    // -------------------------------------------------------------------------

    /**
     * Groups a list of entities into a {@link Map} using a generic key type.
     *
     * <p>Each entity is mapped to a key using {@code keyExtractor} and to a
     * value using {@code valueMapper}.</p>
     *
     * <p>
     * <b>Warning:</b> This method does not support duplicate keys. If multiple
     * elements produce the same key, an {@link IllegalStateException} is thrown.
     * Use the overloaded version that accepts a {@link BinaryOperator} merge
     * function when duplicate keys need to be handled explicitly.
     * </p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Map<String, User> result = Utils.D.groupBy(
     *     users,
     *     User::getId,
     *     Function.identity()
     * );
     * }</pre>
     *
     * @param <T>          the type of the input elements
     * @param <K>          the type of the grouping key
     * @param <R>          the type of the target values
     * @param dataList     the input list of entities
     * @param keyExtractor the function to extract the grouping key
     * @param valueMapper  the function to map each entity to the target value
     * @return a map containing the grouped and mapped data
     * @throws IllegalStateException if multiple elements produce the same key
     */
    public <T, K, R> Map<K, R> groupBy(
            List<T> dataList,
            Function<T, K> keyExtractor,
            Function<T, R> valueMapper) {

        return Utils.CL.emptyIfNull(dataList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueMapper
                ));
    }

    /**
     * Groups a list of entities into a {@link Map} using a generic key type
     * and an explicit merge strategy for duplicate keys.
     *
     * <p>If multiple entities produce the same key, {@code mergeFunction}
     * determines which value should be retained or how the values should
     * be combined.</p>
     *
     * <p><b>Example - keep the existing value:</b></p>
     * <pre>{@code
     * Map<String, Product> result = Utils.D.groupBy(
     *     products,
     *     Product::getId,
     *     Function.identity(),
     *     (existing, replacement) -> existing
     * );
     * }</pre>
     *
     * @param <T>            the type of the input elements
     * @param <K>            the type of the grouping key
     * @param <R>            the type of the target values
     * @param dataList       the input list of entities
     * @param keyExtractor   the function to extract the grouping key
     * @param valueMapper    the function to map each entity to the target value
     * @param mergeFunction  the function used to resolve duplicate keys
     * @return a map containing the grouped and mapped data
     */
    public <T, K, R> Map<K, R> groupBy(
            List<T> dataList,
            Function<T, K> keyExtractor,
            Function<T, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return Utils.CL.emptyIfNull(dataList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        keyExtractor,
                        valueMapper,
                        mergeFunction
                ));
    }

    // -------------------------------------------------------------------------
    // Grouping - Compound Key
    // -------------------------------------------------------------------------

    /**
     * Groups a list of entities into a {@link Map} using a compound string key
     * extracted from multiple grouping criteria.
     *
     * <p>Each grouping criterion is passed together with the current entity
     * to {@code keyExtractor}. The extracted values are combined into a single
     * string key using {@code Utils.KEYS.getKey}.</p>
     *
     * <p>
     * <b>Warning:</b> This method does not support duplicate keys. If multiple
     * elements produce the same compound key, an
     * {@link IllegalStateException} may be thrown depending on the underlying
     * map implementation.
     * </p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Map<String, DataRecord> result = Utils.D.groupEntityList(
     *     records,
     *     List.of(Attributes.AGE, Attributes.GENDER),
     *     (record, attribute) -> record.get(attribute),
     *     Function.identity()
     * );
     * }</pre>
     *
     * @param <T>          the type of the input elements
     * @param <G>          the type of the grouping criteria
     * @param <R>          the type of the target values
     * @param dataList     the input list of entities
     * @param groupBys     the criteria used to build the compound key
     * @param keyExtractor the function used to extract each key component
     * @param valueMapper  the function used to map each entity to the target value
     * @return a map containing the grouped and mapped data
     * @throws IllegalStateException if duplicate compound keys are encountered
     */
    public <T, G, R> Map<String, R> groupEntityList(
            List<T> dataList,
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

    /**
     * Groups a list of entities into a {@link Map} using a compound string key
     * and an explicit merge strategy for duplicate keys.
     *
     * <p>Each grouping criterion is passed together with the current entity
     * to {@code keyExtractor}. Duplicate compound keys are resolved using
     * {@code mergeFunction}.</p>
     *
     * <p><b>Example - keep the existing value:</b></p>
     * <pre>{@code
     * Map<String, Product> result = Utils.D.groupEntityList(
     *     products,
     *     List.of(Attributes.PRODUCT_ID),
     *     (product, attribute) -> product.getId(),
     *     Function.identity(),
     *     (existing, replacement) -> existing
     * );
     * }</pre>
     *
     * @param <T>            the type of the input elements
     * @param <G>            the type of the grouping criteria
     * @param <R>            the type of the target values
     * @param dataList       the input list of entities
     * @param groupBys       the criteria used to build the compound key
     * @param keyExtractor   the function used to extract each key component
     * @param valueMapper    the function used to map each entity to the target value
     * @param mergeFunction  the function used to resolve duplicate keys
     * @return a map containing the grouped and mapped data
     */
    public <T, G, R> Map<String, R> groupEntityList(
            List<T> dataList,
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

    /**
     * Groups a list of maps into a {@link Map} using a compound string key
     * generated from the specified map keys.
     *
     * <p>
     * <b>Warning:</b> This method does not support duplicate keys. If multiple
     * maps produce the same compound key, an
     * {@link IllegalStateException} is thrown.
     * </p>
     *
     * <p><b>Example:</b></p>
     * <pre>{@code
     * Map<String, Map<String, Object>> result = Utils.D.groupMapList(
     *     mapList,
     *     List.of("id", "type"),
     *     Function.identity()
     * );
     * }</pre>
     *
     * @param <K>          the type of keys maintained by the input maps
     * @param <V>          the type of values maintained by the input maps
     * @param <R>          the type of target values
     * @param mapList      the list of maps to be grouped
     * @param groupBys     the map keys used to build the compound key
     * @param valueMapper  the function used to map each input map to the target value
     * @return a map containing the grouped and mapped data
     * @throws IllegalStateException if duplicate compound keys are encountered
     */
    public <K, V, R> Map<String, R> groupMapList(
            List<Map<K, V>> mapList,
            List<K> groupBys,
            Function<Map<K, V>, R> valueMapper) {

        return Utils.CL.emptyIfNull(mapList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        map -> buildCompoundKey(
                                groupBys,
                                map::get
                        ),
                        valueMapper
                ));
    }

    /**
     * Groups a list of maps into a {@link Map} using a compound string key
     * and an explicit merge strategy for duplicate keys.
     *
     * @param <K>            the type of keys maintained by the input maps
     * @param <V>            the type of values maintained by the input maps
     * @param <R>            the type of target values
     * @param mapList        the list of maps to be grouped
     * @param groupBys       the map keys used to build the compound key
     * @param valueMapper    the function used to map each input map to the target value
     * @param mergeFunction  the function used to resolve duplicate keys
     * @return a map containing the grouped and mapped data
     */
    public <K, V, R> Map<String, R> groupMapList(
            List<Map<K, V>> mapList,
            List<K> groupBys,
            Function<Map<K, V>, R> valueMapper,
            BinaryOperator<R> mergeFunction) {

        return Utils.CL.emptyIfNull(mapList).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        map -> buildCompoundKey(
                                groupBys,
                                map::get
                        ),
                        valueMapper,
                        mergeFunction
                ));
    }

    /**
     * Groups a list of maps into a multi-map, accumulating all maps with the
     * same compound key into a list.
     *
     * <p>This method does not lose data when duplicate keys occur.</p>
     *
     * @param <K>      the type of keys maintained by the input maps
     * @param <V>      the type of values maintained by the input maps
     * @param mapList  the list of maps to be grouped
     * @param groupBys the map keys used to build the compound key
     * @return a map where each compound key maps to all matching input maps
     */
    public <K, V> Map<String, List<Map<K, V>>> groupMapListToMultiMap(
            List<Map<K, V>> mapList,
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

    // -------------------------------------------------------------------------
    // Transformation
    // -------------------------------------------------------------------------

    /**
     * Transforms a list of entities into a list of maps using a custom extractor.
     *
     * <p>Null elements in the input list are filtered out.</p>
     *
     * @param <T>          the type of input elements
     * @param <K>          the type of keys in the resulting maps
     * @param <V>          the type of values in the resulting maps
     * @param dataRecords  the input list of entities
     * @param mapExtractor the function used to convert each entity into a map
     * @return a list of transformed maps, or an empty list if input is null
     */
    public <T, K, V> List<Map<K, V>> toMapList(
            List<T> dataRecords,
            Function<T, Map<K, V>> mapExtractor) {

        return Utils.CL.emptyIfNull(dataRecords).stream()
                .filter(Objects::nonNull)
                .map(mapExtractor)
                .collect(Collectors.toList());
    }

    /**
     * Transforms a collection of entities into a list of maps.
     *
     * <p>Null elements in the input collection are filtered out.</p>
     *
     * @param <T>           the type of input elements
     * @param <K>           the type of keys in the resulting maps
     * @param <V>           the type of values in the resulting maps
     * @param dataRecords   the input collection of entities
     * @param mapExtractor  the function used to extract a map from each entity
     * @return a list of transformed maps, or an empty list if input is null
     */
    public <T, K, V> List<Map<K, V>> toList(
            Collection<T> dataRecords,
            Function<T, Map<K, V>> mapExtractor) {

        return Utils.CL.emptyIfNull(dataRecords).stream()
                .filter(Objects::nonNull)
                .map(mapExtractor)
                .collect(Collectors.toList());
    }

    /**
     * Transforms a list of maps into a list of target objects.
     *
     * <p>Null elements in the input list are filtered out.</p>
     *
     * @param <K>         the type of keys in the input maps
     * @param <V>         the type of values in the input maps
     * @param <R>         the type of target objects
     * @param mapList     the input list of maps
     * @param creatorFunc the function used to create a target object from each map
     * @return a list of constructed target objects
     */
    public <K, V, R> List<R> toEntityList(
            List<Map<K, V>> mapList,
            Function<Map<K, V>, R> creatorFunc) {

        return Utils.CL.emptyIfNull(mapList).stream()
                .filter(Objects::nonNull)
                .map(creatorFunc)
                .collect(Collectors.toList());
    }

    // -------------------------------------------------------------------------
    // Aggregation
    // -------------------------------------------------------------------------

    /**
     * Calculates the sum of a BigDecimal property extracted from a list of entities.
     *
     * <p>Null extracted values are treated as {@link BigDecimal#ZERO}.</p>
     *
     * @param <T>            the type of input elements
     * @param dataRecords    the input list of entities
     * @param valueExtractor the function used to extract the BigDecimal value
     * @return the total sum, or {@link BigDecimal#ZERO} if the input is null or empty
     */
    public <T> BigDecimal sum(
            List<T> dataRecords,
            Function<T, BigDecimal> valueExtractor) {

        return Utils.CL.emptyIfNull(dataRecords).stream()
                .filter(Objects::nonNull)
                .map(item -> Optional.ofNullable(valueExtractor.apply(item))
                        .orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Groups entities by a single generic key and calculates the sum of a
     * BigDecimal property for each group.
     *
     * <p>Null extracted values are treated as {@link BigDecimal#ZERO}.</p>
     *
     * @param <T>            the type of input elements
     * @param <K>            the type of the grouping key
     * @param dataRecords    the input list of entities
     * @param keyExtractor   the function used to extract the grouping key
     * @param valueExtractor the function used to extract the BigDecimal value
     * @return a map containing the sum for each group
     */
    public <T, K> Map<K, BigDecimal> groupByAndSum(
            List<T> dataRecords,
            Function<T, K> keyExtractor,
            Function<T, BigDecimal> valueExtractor) {

        return Utils.CL.emptyIfNull(dataRecords).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        keyExtractor,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> Optional.ofNullable(valueExtractor.apply(item))
                                        .orElse(BigDecimal.ZERO),
                                BigDecimal::add
                        )
                ));
    }

    /**
     * Groups entities by a compound string key and calculates the combined sum
     * of multiple BigDecimal attributes for each group.
     *
     * <p>For each entity, all values extracted from {@code sumAttributes} are
     * added together. The resulting value is then aggregated across all
     * entities belonging to the same group.</p>
     *
     * @param <T>                 the type of input elements
     * @param <G>                 the type of grouping criteria
     * @param <S>                 the type of summation criteria
     * @param dataRecords         the input list of entities
     * @param groupBys            the criteria used to build the compound key
     * @param groupKeyExtractor   the function used to extract each key component
     * @param sumAttributes       the attributes whose values are summed
     * @param sumValueExtractor   the function used to extract each BigDecimal value
     * @return a map containing the aggregated sums for each compound key
     */
    public <T, G, S> Map<String, BigDecimal> groupByAndSum(
            List<T> dataRecords,
            List<G> groupBys,
            BiFunction<T, G, Object> groupKeyExtractor,
            List<S> sumAttributes,
            BiFunction<T, S, BigDecimal> sumValueExtractor) {

        return Utils.CL.emptyIfNull(dataRecords).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(
                        item -> buildCompoundKey(
                                groupBys,
                                groupBy -> groupKeyExtractor.apply(item, groupBy)
                        ),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                item -> sumAttributes.stream()
                                        .map(attr -> sumValueExtractor.apply(item, attr))
                                        .filter(Objects::nonNull)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                                BigDecimal::add
                        )
                ));
    }

    // -------------------------------------------------------------------------
    // Numeric Utilities
    // -------------------------------------------------------------------------

    /**
     * Calculates a new value by adding a percentage to a base value.
     *
     * <p>Formula:
     * {@code result = baseValue * (1 + percentage / 100)}</p>
     *
     * @param baseValue  the baseline value
     * @param percentage the percentage to add
     * @return the value adjusted by the specified percentage
     */
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

    /**
     * Builds a compound string key from multiple key components.
     *
     * @param <G>             the type of grouping criteria
     * @param groupBys        the grouping criteria
     * @param valueExtractor  the function used to extract each key component
     * @return the generated compound key
     */
    private <G> String buildCompoundKey(
            List<G> groupBys,
            Function<G, Object> valueExtractor) {

        return Utils.KEYS.getKey(
                groupBys.stream()
                        .map(valueExtractor)
                        .collect(Collectors.toList())
        );
    }
}
