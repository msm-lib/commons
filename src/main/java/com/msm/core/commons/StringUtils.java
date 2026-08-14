package com.msm.core.commons;

import java.nio.ByteBuffer;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
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

    public String replaceAllSpace(final String input) {
        return isBlank(input) ? null : input.replaceAll("\\s+","");
    }

    public String lowerCase(final String input) {
        if (input == null) {
            return null;
        }
        return input.toLowerCase();
    }

    public String toUpperCase(final String input) {
        if (input == null) {
            return null;
        }
        return input.toUpperCase();
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

    public boolean equalIgnoreCase(final String value1, final String value2) {
        if (Objects.equals(value1, value2)) {
            return true;
        }
        if (Objects.isNull(value1) || Objects.isNull(value2)) {
            return false;
        }
        if (length(value1) != length(value2)) {
            return false;
        }
        return Objects.equals(lowerCase(value1), lowerCase(value2));
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

    public String normalizeSystemText(final String input) {
        Pattern pattern = Pattern.compile("\\s+");
        return Utils.STR.isBlank(input) ? null : pattern.matcher(input.trim().toLowerCase()).replaceAll(" ");
    }

    public String normalizeText(final String input) {
        String strTrim = trim(input);
        return isEmpty(strTrim) ? null : strTrim;
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

    public String toSnakeCase(String input) {
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

    public boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }

        return str.contains(searchStr);
    }

    public boolean containsIgnoreCase(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }

        return str.toLowerCase(Locale.ROOT).contains(searchStr.toLowerCase(Locale.ROOT));
    }

    public boolean startsWith(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }

        return str.startsWith(prefix);
    }

    public boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.length() < prefix.length()) {
            return false;
        }

        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    public boolean endsWith(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }

        return str.endsWith(suffix);
    }

    public boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        int strStart = str.length() - suffix.length();
        return str.regionMatches(true, strStart, suffix, 0, suffix.length());
    }

    public String concat(final String... value) {
        StringBuilder builder = new StringBuilder();
        for (String s : value) {
            if (isNotBlank(s)) {
                builder.append(s);
            }
        }
        return builder.toString();
    }

    public boolean containWhitespace(String str) {
        int strLen;
        if ((strLen = length(str)) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            boolean isWhitespace = Character.isWhitespace(ch);
            if (isWhitespace) {
                return true;
            }
        }
        return false;
    }

    public boolean containSpecialCharacter(String str) {
        int strLen;
        if ((strLen = length(str)) == 0) {
            return false;
        }
        for (int i = 0; i < strLen; i++) {
            char ch = str.charAt(i);
            boolean isWhitespace = Character.isWhitespace(ch);
            if (!Character.isLetterOrDigit(ch) && !isWhitespace) {
                return true;
            }
        }
        return false;
    }

    StringUtils() {}
}
