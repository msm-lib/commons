package com.msm.core.security.enums;

import com.msm.core.security.IdentifiableCode;
import lombok.Getter;

@Getter
public enum PermissionAction implements IdentifiableCode {
    CREATE("C"),
    VIEW("V"),
    UPDATE("U"),
    DELETE("D"),

    COPY("Y"),
    REVISE("R"),

    WON("W"),
    LOST("L"),

    CANCEL("N"),
    REOPEN("O"),

    PRINT("P"),
    IMPORT("I"),
    EXPORT("E"),

    APPROVE("A"),
    REJECT("J"),
    SUBMIT("S"),

    ALLOCATE("T"),
    RETURN("B"),

    REPLACE("H"),
    EXCHANGE("X"),

    LOOKUP("K"),

    QUALIFY("Q"),
    DISQUALIFY("DQ"),

    SHARE("SH"),
    CLOSED("CL"),
    INACTIVE("IA"),
    DIGITAL_SIGNATURE("DS"),
    ;

    private final String code;

    PermissionAction(String code) {
        this.code = code;
    }

    public static PermissionAction fromCode(String code) {
        return IdentifiableCode.fromCode(PermissionAction.class, code);
    }
}
