package com.msm.core.filter.domain;

import com.msm.core.filter.domain.pageable.PageRequest;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.domain.pageable.SortDirection;

import java.util.List;
import java.util.Objects;

public class PaginationUtils {

    public static PageRequest fromRequest(Integer page, Integer size, List<String> sortParams) {
        PageRequest pageRequest = PageRequest.of(page == null ? 0 : page, size == null ? 20 : size);
        if (Objects.nonNull(sortParams)) {
            for (String param : sortParams) {
                String[] parts = param.split(",");
                String property = parts[0];
                SortDirection direction = parts.length > 1 ? SortDirection.from(parts[1]) : SortDirection.ASC;
                pageRequest.addSort(new Sort(property, direction));
            }
        }

        return pageRequest;
    }
}