package com.msm.core.validate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleFactsContext {
    private boolean isCamelCase;
    private Map<String, Object> data;
}
