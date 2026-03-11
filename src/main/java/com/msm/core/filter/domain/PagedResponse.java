package com.msm.core.filter.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;

    @JsonIgnore
    private Map<String, Object> aggregate;

    public static <T> PagedResponse<T> of(List<T> records, long totalElements, int currentPage, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);
        return PagedResponse
                .<T>builder()
                .records(records)
                .page(currentPage)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages((int) Math.ceil((double) totalElements / pageSize))
                .last(currentPage >= totalPages - 1)
                .build();
    }

    public static <T> PagedResponse<T> of(List<T> records) {
        return PagedResponse
                .<T>builder()
                .records(records)
                .build();
    }
}
