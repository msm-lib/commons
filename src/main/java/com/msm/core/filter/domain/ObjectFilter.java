package com.msm.core.filter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.msm.core.filter.domain.pageable.PageRequest;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ObjectFilter {
    @JsonIgnore
    private ObjectInfo objectFilter;
    private List<String> returnAttributes;
    private FilterGroup filterGroup;
    @JsonIgnore
    private List<AggregateRequest> aggregate;
    private PageRequest pageRequest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjectInfo {
        private String name;
        private String code;
    }
}
