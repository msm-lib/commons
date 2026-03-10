package com.msm.core.filter.domain.pageable;


import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PageRequest {
//    private int page;
//    private int size;
//    private long totalElements;
//    private int totalPages;
//    private boolean isLast;
//    private List<Sort> sorts;

    private int page = 0;
    private int size = 20;
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

//    public static PageRequest of(long totalElements, int currentPage, int pageSize) {
//       int totalPage = totalElements < pageSize ? 1 : (int) Math.ceil((double) totalElements / pageSize);
//        return PageRequest
//                .builder()
//                .page(currentPage)
//                .size(pageSize)
//                .totalElements(totalElements)
//                .totalPages(totalPage)
//                .isLast(currentPage + 1 >= totalPage)
//                .build();
//    }



    public PageRequest() {}

    public PageRequest(int page, int size) {
        this.page = Math.max(page, 0);
        this.size = Math.min(size, 100);
    }

    public int getOffset() {
        return page * size;
    }

    public int getLimit() {
        return size;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public List<Sort> getSorts() {
        return sorts;
    }

    public void addSort(Sort sort) {
        this.sorts.add(sort);
    }
}
