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
public class ObjectFilterRequest {
    @JsonIgnore
    private ObjectInfo objectInfo;
    private List<String> returnFields;
    private SearchRequest search;
    private FilterGroup filters;
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
