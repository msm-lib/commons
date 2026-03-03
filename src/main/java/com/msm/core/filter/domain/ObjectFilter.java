package com.msm.core.filter.domain;

import com.msm.core.filter.domain.pageable.Pagination;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectFilter {
    private ObjectInfo objectFilter;
    private List<String> returnAttributes;
    private FilterGroup filterGroup;
    private List<AggregateRequest> aggregate;
    private Pagination pagination;

    @Data
    public static class ObjectInfo {
        private String name;
        private String code;
    }
}
