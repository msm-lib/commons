package com.msm.core.security.enums;

import com.msm.core.security.IdentifiableCode;
import lombok.Getter;

@Getter
public enum PermissionAction implements IdentifiableCode {
    VIEW("V"),
    CREATE("C"),
    UPDATE("U"),
    DELETE("D"),
    APPROVE("A");

    private final String code;

    PermissionAction(String code) {
        this.code = code;
    }

    public static IdentifiableCode fromCode(String code) {
        for (IdentifiableCode action : values()) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }

        throw new IllegalArgumentException("Unknown action: " + code);
    }
}
