package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.ErrorCodeEnum;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.validate.domain.MessageError;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

@Slf4j
public class AttributeTypeValidator implements AttributeValidator {

    private boolean isDataTypeValid(Attribute attr, Object data) {
        try {
            if(attr.getEnumType() != null){
               return isEnumValueValid(attr.getEnumType(), data);
            }
            attr.cast(data);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }

    /**
     * A single value, or every element of a collection / array, must be exactly the {@code name()}
     * of a constant of the enum (case-sensitive). An empty collection is valid: whether a value
     * is required is checked by the required validator.
     */
    private boolean isEnumValueValid(Class<? extends Enum<?>> enumType, Object data) {
        if (data instanceof Collection<?> values) {
            return values.stream().allMatch(value -> isEnumConstant(enumType, value));
        }
        if (data.getClass().isArray()) {
            return IntStream.range(0, Array.getLength(data))
                    .mapToObj(i -> Array.get(data, i))
                    .allMatch(value -> isEnumConstant(enumType, value));
        }
        return isEnumConstant(enumType, data);
    }

    private boolean isEnumConstant(Class<? extends Enum<?>> enumType, Object value) {
        return value != null && Utils.E.isValid(enumType, String.valueOf(value));
    }

    @Override
    public List<MessageError> validate(ObjectMetadata objectAttribute, Map<String, Object> payload) {
        List<MessageError>  errors = new ArrayList<>();

        List<Attribute> attributes = objectAttribute.getAttributes();
        for(Attribute attr : attributes){
            Object data = payload.get(Utils.STR.toCamelCaseUnderscore(attr.getFieldName()));
            if(Objects.isNull(data)) continue;

            boolean isValid = isDataTypeValid(attr, data);
            if(!isValid){
                MessageError messageError = MessageError
                        .builder()
                        .attribute(attr.getFieldName())
                        .code(ErrorCodeEnum.INVALID_DATA_TYPE)
                        .message(Utils.STR.format(Constants.INVALID_DATA_TYPE, attr.getFieldName()))
                        .build();
                errors.add(messageError);
            }
        }
        return errors;
    }
}
