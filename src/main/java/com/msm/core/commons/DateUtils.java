package com.msm.core.commons;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unchecked")
public class DateUtils {

    private static final ZoneId DEFAULT_ZONE_ID = ZoneOffset.UTC;
    private static final String INSTANT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS][XXX]";
    private static final String LOCAL_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss[.SSS]";
    private static final String LOCAL_DATE_PATTERN = "yyyy-MM-dd";
    private static final String LOCAL_TIME_PATTERN = "HH:mm:ss";
    private static final Map<String, DateTimeFormatter> FORMATTERS = new ConcurrentHashMap<>();

    // =========================================================
    // Comparison - LocalDateTime
    // =========================================================

    public boolean lessThan(LocalDateTime d1, LocalDateTime d2) {
        return d1 != null && d2 != null && d1.isBefore(d2);
    }

    public boolean lessThanOrEqual(LocalDateTime d1, LocalDateTime d2) {
        return d1 != null && d2 != null && !d1.isAfter(d2);
    }

    public boolean moreThan(LocalDateTime d1, LocalDateTime d2) {
        return d1 != null && d2 != null && d1.isAfter(d2);
    }

    public boolean moreThanOrEqual(LocalDateTime d1, LocalDateTime d2) {
        return d1 != null && d2 != null && !d1.isBefore(d2);
    }

    public boolean equal(LocalDateTime d1, LocalDateTime d2) {
        if (d1 == d2) {
            return true;
        }

        return d1 != null && d1.isEqual(d2);
    }

    // =========================================================
    // Comparison - LocalDate
    // =========================================================

    public boolean lessThan(LocalDate d1, LocalDate d2) {
        return d1 != null && d2 != null && d1.isBefore(d2);
    }

    public boolean lessThanOrEqual(LocalDate d1, LocalDate d2) {
        return d1 != null && d2 != null && !d1.isAfter(d2);
    }

    public boolean moreThan(LocalDate d1, LocalDate d2) {
        return d1 != null && d2 != null && d1.isAfter(d2);
    }

    public boolean moreThanOrEqual(LocalDate d1, LocalDate d2) {
        return d1 != null && d2 != null && !d1.isBefore(d2);
    }

    public boolean equal(LocalDate d1, LocalDate d2) {
        if (d1 == d2) {
            return true;
        }

        return d1 != null && d1.isEqual(d2);
    }

    // =========================================================
    // Comparison - Instant
    // =========================================================

    public boolean lessThan(Instant d1, Instant d2) {
        return d1 != null && d2 != null && d1.isBefore(d2);
    }

    public boolean lessThanOrEqual(Instant d1, Instant d2) {
        return d1 != null && d2 != null && !d1.isAfter(d2);
    }

    public boolean moreThan(Instant d1, Instant d2) {
        return d1 != null && d2 != null && d1.isAfter(d2);
    }

    public boolean moreThanOrEqual(Instant d1, Instant d2) {
        return d1 != null && d2 != null && !d1.isBefore(d2);
    }

    public boolean equal(Instant d1, Instant d2) {
        if (d1 == d2) {
            return true;
        }

        return d1 != null && d1.equals(d2);
    }

    // =========================================================
    // Comparison - OffsetDateTime
    // =========================================================

    public boolean lessThan(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && d1.isBefore(d2);
    }

    public boolean lessThanOrEqual(
            OffsetDateTime d1,
            OffsetDateTime d2
    ) {
        return d1 != null && d2 != null && !d1.isAfter(d2);
    }

    public boolean moreThan(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && d1.isAfter(d2);
    }

    public boolean moreThanOrEqual(
            OffsetDateTime d1,
            OffsetDateTime d2
    ) {
        return d1 != null && d2 != null && !d1.isBefore(d2);
    }

    public boolean equal(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null
                && d2 != null
                && d1.isEqual(d2);
    }

    // =========================================================
    // Between - Instant
    // =========================================================

    public boolean isBetween(
            Instant target,
            Instant start,
            Instant end
    ) {
        if (target == null || start == null || end == null) {
            return false;
        }

        return !target.isBefore(start)
                && target.isBefore(end);
    }

    public boolean isBetweenInclusive(
            Instant target,
            Instant start,
            Instant end
    ) {
        if (target == null || start == null || end == null) {
            return false;
        }

        return !target.isBefore(start)
                && !target.isAfter(end);
    }

    // =========================================================
    // Between - LocalDate
    // =========================================================

    public boolean isBetween(
            LocalDate target,
            LocalDate start,
            LocalDate end
    ) {
        if (target == null || start == null || end == null) {
            return false;
        }

        return !target.isBefore(start)
                && target.isBefore(end);
    }

    public boolean isBetweenInclusive(
            LocalDate target,
            LocalDate start,
            LocalDate end
    ) {
        if (target == null || start == null || end == null) {
            return false;
        }

        return !target.isBefore(start)
                && !target.isAfter(end);
    }

    // =========================================================
    // Add / Subtract
    // =========================================================

    public <T extends Temporal> T addTime(
            T temporal,
            long amount,
            ChronoUnit unit
    ) {
        if (temporal == null || unit == null) {
            return null;
        }

        return (T) temporal.plus(amount, unit);
    }

    public <T extends Temporal> T subtractTime(
            T temporal,
            long amount,
            ChronoUnit unit
    ) {
        if (temporal == null || unit == null) {
            return null;
        }

        return (T) temporal.minus(amount, unit);
    }

    public Instant addDays(Instant instant, long days) {
        return addTime(instant, days, ChronoUnit.DAYS);
    }

    public Instant subtractDays(Instant instant, long days) {
        return subtractTime(instant, days, ChronoUnit.DAYS);
    }

    public Instant addHours(Instant instant, long hours) {
        return addTime(instant, hours, ChronoUnit.HOURS);
    }

    public Instant subtractHours(Instant instant, long hours) {
        return subtractTime(instant, hours, ChronoUnit.HOURS);
    }

    public Instant addMinutes(Instant instant, long minutes) {
        return addTime(instant, minutes, ChronoUnit.MINUTES);
    }

    public Instant subtractMinutes(Instant instant, long minutes) {
        return subtractTime(instant, minutes, ChronoUnit.MINUTES);
    }

    public Instant addSeconds(Instant instant, long seconds) {
        return addTime(instant, seconds, ChronoUnit.SECONDS);
    }

    public Instant subtractSeconds(Instant instant, long seconds) {
        return subtractTime(instant, seconds, ChronoUnit.SECONDS);
    }

    // =========================================================
    // Start / End of Day - UTC
    // =========================================================

    public Instant getStartOfDay(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .toLocalDate()
                .atStartOfDay(zoneId)
                .toInstant();
    }

    public Instant getStartOfDay(Instant instant) {
        return getStartOfDay(instant, DEFAULT_ZONE_ID);
    }


    public Instant getEndOfDay(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .toLocalDate()
                .atTime(LocalTime.MAX)
                .atZone(zoneId)
                .toInstant();
    }

    public Instant getEndOfDay(Instant instant) {
        return getEndOfDay(instant, DEFAULT_ZONE_ID);
    }



    // =========================================================
    // Start / End of Month - UTC
    // =========================================================

    public Instant getStartOfMonth(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .with(TemporalAdjusters.firstDayOfMonth())
                .with(LocalTime.MIN)
                .toInstant();
    }

    public Instant getStartOfMonth(Instant instant) {
        return getStartOfMonth(instant, DEFAULT_ZONE_ID);
    }

    public Instant getEndOfMonth(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .with(TemporalAdjusters.lastDayOfMonth())
                .with(LocalTime.MAX)
                .toInstant();
    }

    public Instant getEndOfMonth(Instant instant) {
        return getEndOfMonth(instant, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Start / End of Year - UTC
    // =========================================================

    public Instant getStartOfYear(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .with(TemporalAdjusters.firstDayOfYear())
                .with(LocalTime.MIN)
                .toInstant();
    }

    public Instant getStartOfYear(Instant instant) {
        return getStartOfYear(instant, DEFAULT_ZONE_ID);
    }

    public Instant getEndOfYear(Instant instant, ZoneId zoneId) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .with(TemporalAdjusters.lastDayOfYear())
                .with(LocalTime.MAX)
                .toInstant();
    }

    public Instant getEndOfYear(Instant instant) {
        return getEndOfYear(instant, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Difference
    // =========================================================

    public long getDifference(
            Instant start,
            Instant end,
            ChronoUnit unit
    ) {
        if (start == null || end == null || unit == null) {
            return 0;
        }

        return unit.between(start, end);
    }

    // =========================================================
    // Convert Instant -> UTC LocalDate
    // =========================================================

    public LocalDate toLocalDate(
            Instant instant,
            ZoneId zoneId
    ) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .toLocalDate();
    }

    public LocalDate toLocalDate(Instant instant) {
        return toLocalDate(instant, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Convert Instant -> UTC LocalDateTime
    // =========================================================

    public LocalDateTime toLocalDateTime(
            Instant instant,
            ZoneId zoneId
    ) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return LocalDateTime.ofInstant(instant, zoneId);
    }

    public LocalDateTime toLocalDateTime(Instant instant) {
        return toLocalDateTime(instant, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Convert LocalDate -> Instant at UTC
    // =========================================================

    public Instant toInstant(
            LocalDate localDate,
            ZoneId zoneId
    ) {
        if (localDate == null || zoneId == null) {
            return null;
        }

        return localDate
                .atStartOfDay(zoneId)
                .toInstant();
    }

    public Instant toInstant(LocalDate localDate) {
        return toInstant(localDate, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Convert LocalDateTime -> Instant at UTC
    // =========================================================

    public Instant toInstant(
            LocalDateTime localDateTime,
            ZoneId zoneId
    ) {
        if (localDateTime == null || zoneId == null) {
            return null;
        }

        return localDateTime
                .atZone(zoneId)
                .toInstant();
    }

    public Instant toInstant(LocalDateTime localDateTime) {
        return toInstant(localDateTime, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Convert OffsetDateTime -> Instant
    // =========================================================

    public OffsetDateTime toOffsetDateTime(
            Instant instant,
            ZoneId zoneId
    ) {
        if (instant == null || zoneId == null) {
            return null;
        }

        return instant
                .atZone(zoneId)
                .toOffsetDateTime();
    }

    public OffsetDateTime toOffsetDateTime(Instant instant) {
        return toOffsetDateTime(instant, DEFAULT_ZONE_ID);
    }

    // =========================================================
    // Current time
    // =========================================================


    public Instant now() {
        return Instant.now();
    }

    public LocalDate currentDate(ZoneId zoneId) {
        return LocalDate.now(zoneId);
    }

    public LocalDate currentUtcDate() {
        return currentDate(DEFAULT_ZONE_ID);
    }

    public LocalDateTime currentDateTime(ZoneId zoneId) {
        return LocalDateTime.now(zoneId);
    }

    public LocalDateTime currentUtcDateTime() {
        return currentDateTime(DEFAULT_ZONE_ID);
    }

    public OffsetDateTime currentOffsetDateTime(ZoneId zoneId) {
        return OffsetDateTime.now(zoneId);
    }

    public OffsetDateTime currentUtcOffsetDateTime() {
        return currentOffsetDateTime(DEFAULT_ZONE_ID);
    }


    // =========================================================
    // Parse time
    // =========================================================


    private static DateTimeFormatter createFormatter(String pattern) {
        return new DateTimeFormatterBuilder()
                .appendPattern(pattern)
                .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                .parseDefaulting(ChronoField.NANO_OF_SECOND, 0)
                .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                .toFormatter();
    }

    private static DateTimeFormatter getFormatter(String pattern, ZoneId zoneId) {
        return FORMATTERS
                .computeIfAbsent(pattern, DateUtils::createFormatter)
                .withZone(zoneId);
    }

    public <T extends Temporal> T parse(String value, String pattern, Class<T> targetType, ZoneId zoneId) {
        if (value == null || value.isBlank() || pattern == null || pattern.isBlank() || targetType == null || zoneId == null) {
            return null;
        }

        try {
            DateTimeFormatter formatter = getFormatter(pattern, zoneId);

            var parsed = formatter.parse(value.trim());

            if (targetType == Instant.class) {
                return targetType.cast(Instant.from(parsed));
            }

            if (targetType == LocalDateTime.class) {
                return targetType.cast(LocalDateTime.from(parsed));
            }

            if (targetType == LocalDate.class) {
                return targetType.cast(LocalDate.from(parsed));
            }

            if (targetType == LocalTime.class) {
                return targetType.cast(LocalTime.from(parsed));
            }

            if (targetType == OffsetDateTime.class) {
                return targetType.cast(OffsetDateTime.from(parsed));
            }

            throw new IllegalArgumentException("Unsupported temporal type: " + targetType.getName());

        } catch (DateTimeException ex) {
            throw new IllegalArgumentException(
                    "Invalid date/time value: '" + value
                            + "' with pattern: '" + pattern
                            + "' for targetType: " + targetType.getSimpleName(),
                    ex
            );
        }
    }


    public <T extends Temporal> T parse(String value, Class<T> targetType, ZoneId zoneId) {
        if (value == null || value.isBlank() || targetType == null) {
            return null;
        }

        String defaultPattern;

        if (targetType == Instant.class || targetType == OffsetDateTime.class) {
            defaultPattern = INSTANT_PATTERN;
        } else if (targetType == LocalDateTime.class) {
            defaultPattern = LOCAL_DATE_TIME_PATTERN;
        } else if (targetType == LocalDate.class) {
            defaultPattern = LOCAL_DATE_PATTERN;
        } else if (targetType == LocalTime.class) {
            defaultPattern = LOCAL_TIME_PATTERN;
        } else {
            throw new IllegalArgumentException("Unsupported temporal type for default pattern: " + targetType.getName());
        }

        return parse(value, defaultPattern, targetType, zoneId);
    }

    public <T extends Temporal> T parse(String value, Class<T> targetType) {
        return parse(value, targetType, DEFAULT_ZONE_ID);
    }


    DateUtils() {}
}