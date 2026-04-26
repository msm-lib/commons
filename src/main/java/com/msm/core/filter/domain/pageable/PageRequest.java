package com.msm.core.filter.domain.pageable;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageRequest {
    private int page;
    private int size;
    private List<Sort> sorts;

    public static PageRequest of(int page, int size, List<Sort> sorts) {
        return new PageRequest(page, size, sorts);
    }

    public static PageRequest of(int page, int size) {
        return of(page, size, null);
    }

    @JsonIgnore
    public int getOffset() {
        return (page - 1) * size;
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
