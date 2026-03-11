package com.msm.core.filter.domain.pageable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CursorPageRequest {
    private String cursor;
    private int size;
    private List<Sort> sorts;

    public boolean hasCursor() {
        return cursor != null && !cursor.isEmpty();
    }
}
