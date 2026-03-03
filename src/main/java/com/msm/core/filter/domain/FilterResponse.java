package com.msm.core.filter.domain;

import com.msm.core.filter.domain.pageable.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilterResponse<T> {
    private List<T> records;
    private Map<String, Object> aggregate;
    private PageRequest pageable;
}
