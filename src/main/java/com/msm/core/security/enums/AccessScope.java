package com.msm.core.security.enums;

import com.msm.core.security.IdentifiableCode;
import lombok.Getter;

@Getter
public enum AccessScope implements IdentifiableCode {

    NONE("N"),
    OWNER("O"),
    TEAM("T"),
    BUSINESS_UNIT("B"),
    PARENT_CHILD("P"),
    PARENT_CHILD_PARENT("L"),
    ORGANIZATION("OG");


    private final String code;

    AccessScope(String code) {
        this.code = code;
    }

    public static AccessScope fromCode(String code) {
        for (AccessScope scope : values()) {
            if (scope.getCode().equals(code)) {
                return scope;
            }
        }
        return NONE;
    }
}
