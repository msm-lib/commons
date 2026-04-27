package com.msm.core.dynamicquery;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.filter.domain.FilterCondition;
import com.msm.core.filter.domain.FilterGroup;
import com.msm.core.filter.domain.FilterObject;
import com.msm.core.filter.domain.FilterOperator;
import com.msm.core.filter.domain.LogicalOperator;
import com.msm.core.filter.domain.ObjectFilterRequest;
import com.msm.core.filter.domain.SearchRequest;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;

import java.util.List;
import java.util.Objects;

public class SearchOrDefaultFilter {
    private  SearchOrDefaultFilter() {}

    public static void resolveSearchFilter(ObjectFilterRequest filter) {
        FilterGroup searchFilterGroup = toSearchFilterGroup(filter.getSearch());
        if(Objects.nonNull(searchFilterGroup)) {
            FilterGroup currentFilterGroup = filter.getFilters();
            if(Objects.isNull(currentFilterGroup)) {
                filter.setFilters(searchFilterGroup);
            } else {
                List<FilterObject> filterConditionList = currentFilterGroup.getConditions();
                if(Utils.CL.isEmpty(filterConditionList)) {
                    filter.setFilters(searchFilterGroup);
                } else {
                    FilterGroup newSearchFilterGroup = com.msm.core.filter.domain.FilterGroup.builder().operator(LogicalOperator.AND).conditions(Utils.CL.newArrayList(searchFilterGroup)).build();
                    currentFilterGroup.getConditions().add(newSearchFilterGroup);
                }
            }
        }
    }

    private static FilterGroup toSearchFilterGroup(SearchRequest searchRequest) {
        if(Objects.nonNull(searchRequest)) {
            List<String> fields = searchRequest.getFields();
            if(Utils.CL.isNotEmpty(fields) && Utils.STR.isNotBlank(searchRequest.getKeyword())) {
                FilterGroup.FilterGroupBuilder filterGroup = FilterGroup.builder();
                filterGroup.operator(LogicalOperator.OR);
                List<FilterObject> filterConditionList = fields.stream().map(field -> (FilterObject) FilterCondition
                        .builder()
                        .field(field)
                        .operator(FilterOperator.LIKE)
                        .value(searchRequest.getKeyword())
                        .build()).toList();

                filterGroup.conditions(filterConditionList);
                return filterGroup.build();
            }
        }
        return  null;
    }

    public static void addIsDeletedFilter(ObjectMetadata objectMetadata, ObjectFilterRequest filter) {
        Attribute attribute = objectMetadata.getAttributeByName(Constants.IS_DELETED_FIELD);
        if (Objects.isNull(attribute)) {
            return;
        }

        FilterGroup currentFilter = filter.getFilters();
        if (Objects.isNull(currentFilter)) {
            filter.setFilters(com.msm.core.filter.domain.FilterGroup.builder()
                    .operator(LogicalOperator.AND)
                    .conditions(Utils.CL.newArrayList(defaultFilterGroup()))
                    .build()
            );
            return;
        }

        if (!isDeletedFilter(currentFilter)) {
            if(Utils.CL.isEmpty(currentFilter.getConditions())) {
                currentFilter.setConditions(Utils.CL.newArrayList(defaultFilterGroup()));
            } else {
                currentFilter.getConditions().add(defaultFilterGroup()); // no extra AND wrapper
            }
        }
    }

    private static FilterGroup defaultFilterGroup() {
        FilterGroup filterGroup = new FilterGroup();
        filterGroup.setOperator(LogicalOperator.OR);
        FilterCondition filterCondition = FilterCondition.create(Constants.IS_DELETED_FIELD, FilterOperator.EQUALS, Boolean.FALSE);
        FilterCondition filterCondition0 = FilterCondition.create(Constants.IS_DELETED_FIELD, FilterOperator.EQUALS, null);
        filterGroup.setConditions(Utils.CL.newArrayList(filterCondition, filterCondition0));

        return filterGroup;
    }

    private static boolean isDeletedFilter(FilterGroup filterGroup) {
        if (Objects.isNull(filterGroup)) {
            return false;
        }
        for (FilterObject filter : Utils.CL.emptyIfNull(filterGroup.getConditions())) {
            if (filter instanceof FilterCondition condition) {
                if (Utils.STR.equalIgnoreCase(condition.getField(), Constants.IS_DELETED_FIELD)) {
                    return true;
                }
            } else if (filter instanceof FilterGroup group && isDeletedFilter(group)) {
                return true;
            }
        }

        return false;
    }
}
