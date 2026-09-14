package com.msm.core.metadata.ref;

import com.msm.core.commons.Utils;
import com.msm.core.metadata.annotation.AttributeDefinitionRef;
import com.msm.core.metadata.annotation.RefProfileConfig;

import java.util.Locale;

public final class RefDataResolver {

    private RefDataResolver() {}

    public static RefDataDefinition resolve(Class<? extends Enum<?>> refDataType, String refDataProfile) {

        Enum<?> value = Enum.valueOf(
                (Class) refDataType,
                refDataProfile
        );

        if (!(value instanceof RefDataDefinition definition)) {
            throw new IllegalArgumentException(refDataType.getName() + " must implement RefDataDefinition");
        }

        return definition;
    }

    public static RefDataDefinition resolve(RefProfileConfig config, AttributeDefinitionRef attributeDefinitionRef) {

        String refName = attributeDefinitionRef.refProfile();
        Class<? extends Enum<?>> refDataType;

        if(config != null) {
            refDataType = config.refDataType();
            if(Utils.STR.isBlank(refName)) {
                String fieldNameRef = Utils.STR.toSnakeCase(attributeDefinitionRef.fieldName());
                refName = Utils.STR.toUpperCase(fieldNameRef);
            }
        } else {
            refDataType = DefaultReference.class;
            refName = "DEFAULT_REFERENCE";
        }


        Enum<?> value = getOrDefault(refDataType, refName);

        if (!(value instanceof RefDataDefinition definition)) {
            throw new IllegalArgumentException(refDataType.getName() + " must implement RefDataDefinition");
        }

        return definition;
    }

    private static Enum<?> getOrDefault(Class<? extends Enum<?>> refDataType, String refName) {
        try {
            return Enum.valueOf((Class) refDataType, refName);
        } catch (Exception e) {
            return Enum.valueOf((Class) DefaultReference.class, "DEFAULT_REFERENCE");
        }
    }

    private static String toEnumName(String value) {
        return value
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase(Locale.ROOT);
    }
}
