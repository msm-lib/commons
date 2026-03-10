package com.msm.core.filter.domain.pageable;

import lombok.Data;

@Data
public class Sort {
    private String attribute;
    private SortDirection direction;

    public Sort(String attribute, SortDirection direction) {
        this.attribute = attribute;
        this.direction = direction;
    }
}
