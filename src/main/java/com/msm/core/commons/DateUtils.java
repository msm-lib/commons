package com.msm.core.commons;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    DateUtils() {}
}