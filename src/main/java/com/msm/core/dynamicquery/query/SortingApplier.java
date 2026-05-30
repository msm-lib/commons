package com.msm.core.dynamicquery.query;

import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.domain.pageable.SortDirection;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortingApplier {
    public static void apply(SelectConditionStep<org.jooq.Record> query,
                       ObjectFilterRequest request,
                       ObjectMetadata meta) {

        if (Objects.isNull(request.getPageRequest())) return;
        List<Sort> sorts = request.getPageRequest().getSorts();
        if (Objects.isNull(sorts)) return;

        List<SortField<?>> sortFields = sorts.stream()
                .map(s -> {
                    Field<?> field = FieldResolver.resolve(meta, s.getAttribute());
                    return SortDirection.ASC.name().equalsIgnoreCase(s.getDirection().name())
                            ? field.asc()
                            : field.desc();
                })
                .collect(Collectors.toList());

        query.orderBy(sortFields);
    }
}
