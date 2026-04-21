package com.msm.core.commons;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public class DateUtils {

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
        if (d1 == d2) return true;
        return d1 != null && d1.equals(d2);
    }

    public boolean lessThan(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && d1.isBefore(d2);
    }

    public boolean lessThanOrEqual(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && !d1.isAfter(d2);
    }

    public boolean moreThan(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && d1.isAfter(d2);
    }

    public boolean moreThanOrEqual(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && !d1.isBefore(d2);
    }

    public boolean equal(OffsetDateTime d1, OffsetDateTime d2) {
        return d1 != null && d2 != null && d1.isEqual(d2);
    }

    DateUtils() {}
}