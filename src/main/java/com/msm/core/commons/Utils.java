package com.msm.core.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.querydsl.core.util.ArrayUtils;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class Utils {
    public static final int DEFAULT_LENGTH_CODE = 7;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final ObjectMapper MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    public static final StringUtils STR = new StringUtils();
    public static final NumberUtils NUMBER = new NumberUtils();
    public static final DateUtils DATES = new DateUtils();
    public static final CollectionUtils CL = new CollectionUtils();

    public static <T> T convertObject(Object object, Class<T> clazz) {
        if (Objects.isNull(object)) {
            return null;
        }

        return MAPPER.convertValue(object,clazz);
    }

    public static <T> T toObject(Map<String, Object> attributeMap, Class<T> clazz) {
        if (Objects.isNull(attributeMap)) {
            return null;
        }
        return MAPPER.convertValue(attributeMap, clazz);
    }

    public static <K, V> Map<K, V> toMap(Object object) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }

        return MAPPER.convertValue(object, Map.class);
    }

    public static <T> T toObject(String object, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        return MAPPER.readValue(object, clazz);
    }

    public static <T> String toJsonString(T value) throws JsonProcessingException {
        if (Objects.isNull(value)) {
            return null;
        }
        return MAPPER.writeValueAsString(value);
    }

    public static <T> List<T> toListObject(String object, Class<T> clazz) throws JsonProcessingException {
        if (Objects.isNull(object)) {
            return null;
        }
        CollectionType collectionType = MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
        return MAPPER.readValue(object, collectionType);
    }

    public static <T> String toJsonString(List<T> list) throws JsonProcessingException {
        if (Objects.isNull(list)) {
            return null;
        }
        return MAPPER.writeValueAsString(list);
    }

    public static String toCodeGenerator(String prefix, int length) {
        if (length <= 0) {
            length = DEFAULT_LENGTH_CODE;
        }
        StringBuilder stringBuilderCode = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(ALPHA_NUMERIC_STRING.charAt(ThreadLocalRandom.current().nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public static Integer getStartIndex(int page, int size) {
        return (page - 1) * size + 1;
    }

    public static int getEndIndex(int page, int size) {

        return getStartIndex(page, size) + size - 1;
    }


    public static <T> T getSupplier(final Supplier<T> supplier) {
        return Objects.isNull(supplier) ? null : supplier.get();
    }

    public static <T> T defaultIfNull(T value, Supplier<T> defaultSupplier) {
        if (Objects.isNull(value)) {
            return getSupplier(defaultSupplier);
        }
        return value;
    }

    public static final class StringUtils {

        public int length(final String input) {
            return Objects.isNull(input) ? 0 : input.length();
        }

        public boolean isEmpty(final String input) {
            return Objects.isNull(input) || input.isEmpty();
        }

        public String trim(final String input) {
            return Objects.isNull(input) ? null : input.trim();
        }

        public String replace(final String input, final String replacement, final String target) {
            if (isBlank(input)) {
                return input;
            }

            return input.replace(replacement, target);
        }


        public boolean isBlank(final String input) {
            return Objects.isNull(input) || input.isBlank();
        }

        public boolean isNotBlank(final String input) {
            return !isBlank(input);
        }

        public String defaultIfEmpty(final String value, final Supplier<String> defaultSupplier) {
            return isEmpty(value) ? getSupplier(defaultSupplier) : value;
        }

        public String defaultIfBlank(final String value, final Supplier<String> defaultSupplier) {
            return isBlank(value) ? getSupplier(defaultSupplier) : value;
        }

        public String toString(ByteBuffer buffer, int offset, int len) {
            StringBuilder stringBuilder = new StringBuilder();
            int end = offset + len;
            for (int i = offset; i < end; ++i) {
                stringBuilder.append((char) buffer.get(i));
            }
            return stringBuilder.toString();
        }

        public String toString(ByteBuffer buffer) {
            return toString(buffer, 0, buffer.limit());
        }

        public String toString(byte[] a) {
            if (a == null) {
                return "";
            }
            StringBuilder b = new StringBuilder();
            for (byte value : a) {
                b.append((char) value);
            }
            return b.toString();
        }

        public String format(String message, Object... msgArgs) {
            if (Objects.isNull(message) || Objects.isNull(msgArgs)) {
                return message;
            }
            return MessageFormat.format(message, msgArgs);
        }

        public String freeText(final String input) {
            Pattern pattern = Pattern.compile("\\s+");
            return Utils.STR.isBlank(input) ? null : pattern.matcher(input.trim().toLowerCase()).replaceAll(" ");
        }

        public String uncapitalize(final String str) {
            final int strLen = length(str);
            if (strLen == 0) {
                return str;
            }
            final int firstCodePoint = str.codePointAt(0);
            final int newCodePoint = Character.toLowerCase(firstCodePoint);
            if (firstCodePoint == newCodePoint) {
                return str;
            }
            final int[] newCodePoints = str.codePoints().toArray();
            newCodePoints[0] = newCodePoint;
            return new String(newCodePoints, 0, newCodePoints.length);
        }

        public String capitalize(final String str) {
            if (isEmpty(str)) {
                return str;
            }
            final int firstCodepoint = str.codePointAt(0);
            final int newCodePoint = Character.toTitleCase(firstCodepoint);
            if (firstCodepoint == newCodePoint) {
                return str;
            }
            final int[] newCodePoints = str.codePoints().toArray();
            newCodePoints[0] = newCodePoint;
            return new String(newCodePoints, 0, newCodePoints.length);
        }

        private Set<Integer> toDelimiterSet(final char[] delimiters) {
            final Set<Integer> delimiterHashSet = new HashSet<>();
            delimiterHashSet.add(Character.codePointAt(new char[]{' '}, 0));
            if (Utils.CL.isEmpty(delimiters)) {
                return delimiterHashSet;
            }

            for (int index = 0; index < delimiters.length; index++) {
                delimiterHashSet.add(Character.codePointAt(delimiters, index));
            }
            return delimiterHashSet;
        }

        public String toCamelCase(String str, final boolean capitalizeFirstLetter, final char... delimiters) {
            if (isEmpty(str)) {
                return str;
            }
            str = str.toLowerCase();
            final int strLen = str.length();
            final int[] newCodePoints = new int[strLen];
            int outOffset = 0;
            final Set<Integer> delimiterSet = toDelimiterSet(delimiters);
            boolean capitalizeNext = capitalizeFirstLetter;
            for (int index = 0; index < strLen; ) {
                final int codePoint = str.codePointAt(index);

                if (delimiterSet.contains(codePoint)) {
                    capitalizeNext = outOffset != 0;
                    index += Character.charCount(codePoint);
                } else if (capitalizeNext || outOffset == 0 && capitalizeFirstLetter) {
                    final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                    newCodePoints[outOffset++] = titleCaseCodePoint;
                    index += Character.charCount(titleCaseCodePoint);
                    capitalizeNext = false;
                } else {
                    newCodePoints[outOffset++] = codePoint;
                    index += Character.charCount(codePoint);
                }
            }

            return new String(newCodePoints, 0, outOffset);
        }

        public String toCamelCaseUnderscore(String str) {
            return toCamelCase(str, false, '_');
        }

        private StringUtils() {
        }
    }

    public static final class NumberUtils {
        private static final String DEFAULT_FORMAT_PATTERN = "#,###";
        private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.getDefault());

        public String format(BigDecimal input, String pattern) {
            DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');  // Not needed in this case since we don't want decimals
            DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator('.');

            DecimalFormat formatter = new DecimalFormat(Utils.defaultIfNull(pattern, () -> DEFAULT_FORMAT_PATTERN));
            formatter.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);

            return formatter.format(input);
        }

        public String format(BigDecimal input) {
            return format(input, DEFAULT_FORMAT_PATTERN);
        }

        private NumberUtils() {
        }
    }

    public static final class CollectionUtils {

        public static <T> Collection<T> emptyCollection() {
            return Collections.emptyList();
        }

        public <T> Collection<T> defaultIfEmpty(final Collection<T> input, final Supplier<Collection<T>> defaultSupplier) {
            Objects.requireNonNull(defaultSupplier);
            return isEmpty(input) ? defaultSupplier.get() : input;
        }

        public <T> Collection<T> emptyIfNull(final Collection<T> input) {
            return Objects.isNull(input) ? emptyCollection() : input;
        }

        public <T> List<T> emptyIfNull(final List<T> input) {
            return Objects.isNull(input) ? Collections.emptyList() : input;
        }

        public <T> Set<T> emptyIfNull(final Set<T> input) {
            return Objects.isNull(input) ? Collections.emptySet() : input;
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

        private CollectionUtils() {
        }
    }

    public static final class DateUtils {

        public boolean lessThan(LocalDateTime d1, LocalDateTime d2) {
            long diffTime = ChronoUnit.SECONDS.between(d1, d2);
            return (diffTime > 0);
        }

        public boolean lessThanOrEqual(LocalDate d1, LocalDate d2) {
            long diffTime = ChronoUnit.DAYS.between(d1, d2);
            return (diffTime >= 0);
        }

        public boolean moreThanOrEqual(LocalDate date1, LocalDate date2) {
            long diffTime = ChronoUnit.DAYS.between(date1, date2);
            return (diffTime <= 0);
        }

        private DateUtils() {
        }
    }

    private Utils() {
    }
}
