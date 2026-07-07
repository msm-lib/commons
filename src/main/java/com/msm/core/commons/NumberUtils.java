package com.msm.core.commons;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;

public final class NumberUtils {

    private static final String DEFAULT_FORMAT_PATTERN = "#,###";
    private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(Locale.getDefault());

    static {
        SYMBOLS.setDecimalSeparator(',');
        SYMBOLS.setGroupingSeparator('.');
    }

    NumberUtils() {}

    public static String format(BigDecimal input, String pattern) {
        if (input == null) return null;
        String appliedPattern = Objects.requireNonNullElse(pattern, DEFAULT_FORMAT_PATTERN);
        DecimalFormat formatter = new DecimalFormat(appliedPattern);
        formatter.setDecimalFormatSymbols(SYMBOLS);
        return formatter.format(input);
    }

    public static String format(BigDecimal input) {
        return format(input, DEFAULT_FORMAT_PATTERN);
    }

    public static BigDecimal round(BigDecimal input, int scale, RoundingMode mode) {
        if (input == null) return BigDecimal.ZERO;
        return input.setScale(scale, mode);
    }

    public static BigDecimal round(BigDecimal num, int scale) {
        return round(num, scale, RoundingMode.HALF_UP);
    }

    public static boolean isPositive(BigDecimal input) {
        return input != null && input.compareTo(BigDecimal.ZERO) > 0;
    }

    public static int compare(BigDecimal num1, BigDecimal num2) {
        if (Objects.equals(num1, num2)) return 0;
        if (num1 == null) return -1;
        if (num2 == null) return 1;
        return num1.compareTo(num2);
    }

    public static boolean isEqual(BigDecimal num1, BigDecimal num2) {
        return compare(num1, num2) == 0;
    }

    public static boolean isGreaterThan(BigDecimal num1, BigDecimal num2) {
        return compare(num1, num2) > 0;
    }

    public static boolean isLessThan(BigDecimal num1, BigDecimal num2) {
        return compare(num1, num2) < -1;
    }

    public static boolean isNullOrZero(BigDecimal num) {
        return num == null || num.compareTo(BigDecimal.ZERO) == 0;
    }

    public static BigDecimal defaultIfNull(BigDecimal num, BigDecimal defaultValue) {
        return num == null ? defaultValue : num;
    }

    public static BigDecimal zeroIfNull(BigDecimal num) {
        return defaultIfNull(num, BigDecimal.ZERO);
    }

    public static BigDecimal add(BigDecimal num1, BigDecimal num2) {
        if (num1 == null && num2 == null) return BigDecimal.ZERO;
        return zeroIfNull(num1).add(zeroIfNull(num2));
    }

    public static BigDecimal subtract(BigDecimal num1, BigDecimal num2) {
        if (num1 == null && num2 == null) return BigDecimal.ZERO;
        return zeroIfNull(num1).subtract(zeroIfNull(num2));
    }

    public static BigDecimal multiplyAndRound(BigDecimal multiplicand, BigDecimal multiplier) {
        return multiplyAndRound(multiplicand, multiplier, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiplyAndRound(BigDecimal multiplicand, BigDecimal multiplier, int scale) {
        return multiplyAndRound(multiplicand, multiplier, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal multiplyAndRound(BigDecimal multiplicand, BigDecimal multiplier, int scale, RoundingMode roundingMode) {
        if (multiplicand == null || multiplier == null) return BigDecimal.ZERO;
        return multiplicand.multiply(multiplier).setScale(scale, roundingMode);
    }

    public static BigDecimal divide(BigDecimal num1, BigDecimal num2) {
        return divide(num1, num2, 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal num1, BigDecimal num2, int scale) {
        return divide(num1, num2, scale, RoundingMode.HALF_UP);
    }

    public static BigDecimal divide(BigDecimal num1, BigDecimal num2, int scale, RoundingMode roundingMode) {
        if (isNullOrZero(num1) || isNullOrZero(num2)) {
            return BigDecimal.ZERO;
        }
        return num1.divide(num2, scale, roundingMode);
    }
}
