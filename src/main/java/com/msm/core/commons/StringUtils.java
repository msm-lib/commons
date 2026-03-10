package com.msm.core.commons;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public final class StringUtils {
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
        return isEmpty(value) ? Utils.O.getSupplier(defaultSupplier) : value;
    }

    public String defaultIfBlank(final String value, final Supplier<String> defaultSupplier) {
        return isBlank(value) ? Utils.O.getSupplier(defaultSupplier) : value;
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
//            str = str.toLowerCase();
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

    public String camelCaseToUnderscore(String input) {
        if (isEmpty(input)) {
            return input;
        }
        StringBuilder result = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (Character.isUpperCase(c)) {
                result.append("_").append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    StringUtils() {}
}
