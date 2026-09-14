package com.msm.core.dynamicquery.query;

import com.msm.core.commons.Utils;
import com.msm.core.dynamicquery.FieldResolver;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.pageable.Sort;
import com.msm.core.filter.domain.pageable.SortDirection;
import com.msm.core.metadata.ObjectMetadata;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.SelectConditionStep;
import org.jooq.SortField;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class SortingApplier {

    public static void apply(SelectConditionStep<Record> query,
                       ObjectFilterRequest request,
                       ObjectMetadata meta) {

        if (Objects.isNull(request.getPageRequest())) return;
        List<SortField<?>> sortFieldList = getSortField(meta, request.getPageRequest().getSorts());
        if(Utils.CL.isNotEmpty(sortFieldList)) {
            query.orderBy(sortFieldList);
        }
    }


    public static List<SortField<?>> getSortField(ObjectMetadata meta, List<Sort> sorts) {

        if (Objects.isNull(sorts)) return List.of();

        return sorts
                .stream()
                .map(s -> {
                    Field<?> field = FieldResolver.resolve(meta, s.getAttribute());
                    return SortDirection.ASC.name().equalsIgnoreCase(s.getDirection().name())
                            ? field.asc()
                            : field.desc();
                })
                .collect(Collectors.toList());
    }
}
