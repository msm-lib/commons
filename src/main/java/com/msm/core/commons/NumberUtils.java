package com.msm.core.commons;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class NumberUtils {
    private static final String DEFAULT_FORMAT_PATTERN = "#,###";
    private static final DecimalFormatSymbols DECIMAL_FORMAT_SYMBOLS = new DecimalFormatSymbols(Locale.getDefault());

    public String format(BigDecimal input, String pattern) {
        DECIMAL_FORMAT_SYMBOLS.setDecimalSeparator(',');  // Not needed in this case since we don't want decimals
        DECIMAL_FORMAT_SYMBOLS.setGroupingSeparator('.');

        DecimalFormat formatter = new DecimalFormat(Utils.O.defaultIfNull(pattern, () -> DEFAULT_FORMAT_PATTERN));
        formatter.setDecimalFormatSymbols(DECIMAL_FORMAT_SYMBOLS);

        return formatter.format(input);
    }

    public String format(BigDecimal input) {
        return format(input, DEFAULT_FORMAT_PATTERN);
    }

    NumberUtils() {}
}