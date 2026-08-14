package com.msm.core.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public final class RandomUtils {
    public static final int DEFAULT_LENGTH_CODE = 7;
    private static final String ALPHA_NUMERIC_STRING = "A0B3CDE1F2G9H8I7J6K5LM4NOPQRSTUVWXYZ";
    private static final String ALPHA_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMERIC_STRING = "4625983071";//"0123456789"

    public String toCodeGenerator(String prefix, int length) {
        if (length <= 0) {
            length = DEFAULT_LENGTH_CODE;
        }
        StringBuilder stringBuilderCode = new StringBuilder(prefix);
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(ALPHA_NUMERIC_STRING.charAt(ThreadLocalRandom.current().nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public String randomAlphaNumeric(int length) {
        StringBuilder stringBuilderCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(ALPHA_NUMERIC_STRING.charAt(ThreadLocalRandom.current().nextInt(ALPHA_NUMERIC_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public String randomAlpha(int length) {
        StringBuilder stringBuilderCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(ALPHA_STRING.charAt(ThreadLocalRandom.current().nextInt(ALPHA_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public String randomNumeric(int length) {
        StringBuilder stringBuilderCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilderCode.append(NUMERIC_STRING.charAt(ThreadLocalRandom.current().nextInt(NUMERIC_STRING.length())));
        }
        return stringBuilderCode.toString();
    }

    public Set<String> randomAlphaNumeric(int charCodeCount, int maxLength, String prefix, String suffix) {
        char[] chars = ALPHA_NUMERIC_STRING.toCharArray();

        Set<String> result = generateAlphaNumeric(chars, charCodeCount, maxLength);
        return result
                .stream()
                .map(code -> Utils.STR.concat(prefix, code, suffix))
                .collect(Collectors.toSet());
    }

    public Set<String> generateAlphaNumeric(char[] chars, int k, int maxLength) {
        Set<String> result = new HashSet<>();
        if (k < 0 || k > chars.length) {
            return result;
        }
        backTrack(chars, k, 0, new ArrayList<>(), result, maxLength);
        return result;
    }

    private void backTrack(char[] nums, int k, int start, List<Character> currentSubarray, Set<String> result, int maxLength) {
        if (currentSubarray.size() == k) {
            addResult(result, currentSubarray, maxLength);
            return;
        }

        if (start >= nums.length || (result.size() >= maxLength)) {
            return;
        }

        currentSubarray.add(nums[start]);
        backTrack(nums, k, start + 1, currentSubarray, result, maxLength);
        currentSubarray.removeLast();
        if (nums.length - (start + 1) >= (k - currentSubarray.size())) {
            backTrack(nums, k, start + 1, currentSubarray, result, maxLength);
        }
    }

    private void addResult(Set<String> result, List<Character> currentSubarray, int maxLength) {
        int remaining = maxLength - result.size();
        if(remaining >= 1) {
            result.add(new ArrayList<>(currentSubarray)
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining()));
        }

        Character[] charPermutes = currentSubarray.toArray(new Character[0]);
        if((maxLength - result.size()) == 0) return;
        List<List<Character>> permuteList = generatePermutations(charPermutes);
        for(List<Character> characterList : permuteList) {
            if((maxLength - result.size()) > 0) {
                result.add(new ArrayList<>(characterList)
                        .stream()
                        .map(Object::toString)
                        .collect(Collectors.joining()));
            }
        }
    }

    static void permutations(List<List<Character>> result, Character[] arr, int idx) {

        if (idx == arr.length) {
            List<Character> temp = new ArrayList<>();
            Collections.addAll(temp, arr);
            result.add(temp);
            return;
        }

        for (int i = idx; i < arr.length; i++) {
            char temp = arr[idx];
            arr[idx] = arr[i];
            arr[i] = temp;

            permutations(result, arr, idx + 1);

            temp = arr[idx];
            arr[idx] = arr[i];
            arr[i] = temp;
        }
    }

    public List<List<Character>> generatePermutations(Character[] arr) {
        List<List<Character>> res = new ArrayList<>();
        permutations(res, arr, 0);
        return res;
    }

    RandomUtils() {}
}