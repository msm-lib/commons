package com.msm.core.filter.domain.pageable;

import lombok.Data;

@Data
public class Sort {
    private String attribute;
    private SortDirection sortDirection;
}
