package com.msm.core.dynamicquery.query;

import com.msm.core.filter.domain.ObjectFilterRequest;
import org.jooq.Record;
import org.jooq.SelectConditionStep;

import java.util.Objects;

public class PagingApplier {
    public static void apply(SelectConditionStep<Record> query,
                             ObjectFilterRequest request) {

        if (Objects.isNull(request.getPageRequest())) return;
        int size = request.getPageRequest().getSize();
        int offset = request.getPageRequest().getOffset();

        query.limit(size).offset(offset);
    }
}
