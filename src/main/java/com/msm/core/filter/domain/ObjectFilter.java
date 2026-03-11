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
    private ObjectInfo objectInfo;
    private List<String> returnFields;
    private Search search;
    private FilterGroup filters;
    @JsonIgnore
    private List<AggregateRequest> aggregate;
    private PageRequest pageRequest;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Search {
        private String value;
        private List<String> fields;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ObjectInfo {
        private String name;
        private String code;
    }
}
