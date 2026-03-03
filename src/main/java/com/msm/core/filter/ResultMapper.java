package com.msm.core.filter;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ResultMapper {

    public static List<Map<String, Object>> map(List<Tuple> tuples, Map<String, Expression<?>> selectMap) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Tuple t : tuples) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (var entry : selectMap.entrySet()) {
                row.put(entry.getKey(), t.get(entry.getValue()));
            }
            result.add(row);
        }
        return result;
    }
}
