package com.msm.core.filter.domain.pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sort {
    private String attribute;
    private SortDirection direction;

    public static Sort of(String attribute, SortDirection direction) {
        return new Sort(attribute, direction);
    }
}
