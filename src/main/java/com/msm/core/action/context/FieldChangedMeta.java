package com.msm.core.action.context;

import com.msm.core.metadata.typesafe.TypedAttribute;

import java.util.UUID;

import static com.msm.core.metadata.typesafe.MetaFieldBuilder.attr;

public class FieldChangedMeta {
    private FieldChangedMeta() {
    }

    public static final String OBJECT_NAME = "FieldChanged";

    public static final TypedAttribute<String> NAME =
            attr("name", String.class);

    public static final TypedAttribute<Object> BEFORE =
            attr("before", Object.class);

    public static final TypedAttribute<Object> AFTER =
            attr("after", Object.class);
}
