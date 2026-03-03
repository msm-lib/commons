package com.msm.core.filter.domain;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = FilterGroup.class, name = "GROUP"),
        @JsonSubTypes.Type(value = FilterCondition.class, name = "CONDITION")
})
public sealed interface FilterObject permits FilterGroup, FilterCondition{
}
