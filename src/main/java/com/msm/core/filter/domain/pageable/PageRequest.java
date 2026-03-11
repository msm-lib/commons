package com.msm.core.filter.domain.pageable;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class PageRequest {
    private final int page;
    private final int size;
    private List<Sort> sorts;

    private PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }
    private PageRequest(int page, int size, List<Sort> sorts) {
        this.page = page;
        this.size = size;
        this.sorts = sorts;
    }

    public static PageRequest of(int page, int size, List<Sort> sorts) {
        return new PageRequest(page, size, sorts);
    }

    public static PageRequest of(int page, int size) {
        return new PageRequest(page, size);
    }

    @JsonIgnore
    public int getOffset() {
        return page * size;
    }

    @JsonIgnore
    public int getLimit() {
        return size;
    }

    public void addSort(Sort sort) {
        if (Objects.isNull(sort)) {
            this.sorts = new ArrayList<>();
        }
        this.sorts.add(sort);
    }
}
