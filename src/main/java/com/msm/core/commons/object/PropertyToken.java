package com.msm.core.commons.object;

import lombok.Data;

@Data
public class PropertyToken {
    private String name;
    private Integer index;
    private String key;

    PropertyToken(String name) {
        this.name = name;
    }
}