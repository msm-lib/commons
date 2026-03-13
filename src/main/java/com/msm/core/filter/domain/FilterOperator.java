package com.msm.core.filter.domain;

//public enum FilterOperator {
//    EQ,
//    NE,
//    GT,
//    GTE,
//    LT,
//    LTE,
//    LIKE,
//    IN,
//    BETWEEN
//}


public enum FilterOperator {
    EQUALS,
    NOT_EQUALS,
    GREATER_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN,
    LESS_THAN_OR_EQUAL,
    IN,
    CONTAINS,
    BETWEEN,
    NOT_IN,
    NOT_CONTAINS,
    CONTAINS_ONE_OF,
    NOT_CONTAINS_ONE_OF,
    LIKE
}