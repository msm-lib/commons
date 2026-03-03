package com.msm.core.filter.domain;

import com.msm.core.filter.domain.pageable.Pagination;
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
public class PagedResponse<T> {
    private List<T> records;

    private Map<String, Object> aggregate;
    private Pagination pagination;

    public static <T> PagedResponse<T> of(List<T> records, long totalElements, int currentPage, int pageSize) {
        return PagedResponse
                .<T>builder()
                .records(records)
                .pagination(Pagination.of(totalElements, currentPage, pageSize))
                .build();
    }
}
