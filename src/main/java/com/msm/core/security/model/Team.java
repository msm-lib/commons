package com.msm.core.security.model;

import lombok.Data;

import java.util.UUID;

@Data
public class Team {
    private UUID id;
    private String code;
    private String name;
}
