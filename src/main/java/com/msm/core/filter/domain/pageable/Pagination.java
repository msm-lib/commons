package com.msm.core.filter.domain.pageable;


import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Pagination {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean isLast;
    private List<Sort> sorts;

//   public static Pagination of(Page<?> pageInfo) {
//
//       return Pagination
//               .builder()
//               .page(pageInfo.getNumber())
//               .size(pageInfo.getSize())
//               .totalElements(pageInfo.getTotalElements())
//               .totalPages(pageInfo.getTotalPages())
//               .isLast(pageInfo.isLast())
//               .build();
//   }

    public static Pagination of(long totalElements, int currentPage, int pageSize) {
       int totalPage = totalElements < pageSize ? 1 : (int) Math.ceil((double) totalElements / pageSize);
        return Pagination
                .builder()
                .page(currentPage)
                .size(pageSize)
                .totalElements(totalElements)
                .totalPages(totalPage)
                .isLast(currentPage + 1 >= totalPage)
                .build();
    }
}
