package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.ErrorCodeEnum;
import com.msm.core.metadata.Attribute;
import com.msm.core.validate.attr.ValueValidationHandlerFactory;
import com.msm.core.validate.domain.MessageError;

import java.util.Map;
import java.util.Objects;


public class AttributeValidationSupport {

    public boolean requireCheck(Attribute attr, Map<String, Object> attributeData) {
        if(Boolean.TRUE.equals(attr.getIsRequired())) {
            if(Objects.nonNull(attr.getDefaultValue())) return true;
            return Objects.nonNull(attributeData.get(attr.getFieldName()));
        }
        return true;
    }


    public boolean isRequired(Attribute attr) {
        return Boolean.TRUE.equals(attr.getIsRequired())
                && Objects.isNull(attr.getDefaultValue());
    }

    public boolean hasValue(Attribute attr, Map<String, Object> payload) {
        return Objects.nonNull(payload.get(attr.getFieldName()));
    }

    public boolean isDataValid(Attribute attr, Map<String, Object> payload) {
        return ValueValidationHandlerFactory
                .getHandler(attr.getFieldType())
                .isValid(attr, payload.get(attr.getFieldName()));
    }

    public MessageError requiredError(Attribute attr) {
        return MessageError.builder()
                .attribute(attr.getFieldName())
                .code(ErrorCodeEnum.REQUIRE_INPUT_VALUE)
                .message(Utils.STR.format(Constants.REQUIRE_INPUT_VALUE, attr.getFieldName()))
                .build();
    }

    public MessageError invalidError(Attribute attr) {
        return MessageError.builder()
                .attribute(attr.getFieldName())
                .code(ErrorCodeEnum.INVALID_ARGUMENT)
                .message(Utils.STR.format(Constants.INVALID_INPUT_VALUE, attr.getFieldName()))
                .build();
    }
}