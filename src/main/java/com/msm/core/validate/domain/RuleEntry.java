package com.msm.core.validate.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.util.List;

@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RuleEntry {
    private String condition;
    private List<RuleAction> actions;
    private String facts;
    private List<String> importPackages;
    private List<ReferenceObject> referenceObjects;

    @Builder
    @Setter
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RuleAction {
        private String type;
        private String action;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReferenceObject {
        private String name;
        private String srcAttribute;
    }
}
