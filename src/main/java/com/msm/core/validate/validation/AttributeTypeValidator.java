package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.ErrorCode;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.validate.domain.MessageError;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class AttributeTypeValidator implements AttributeValidator {

    private boolean isDataTypeValid(Attribute attr, Object data) {
        try {
            attr.cast(data);
            return true;
        } catch (Exception ignored) {
            return false;
        }
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
                        .code(ErrorCode.INVALID_DATA_TYPE.getCode())
                        .message(Utils.STR.format(Constants.INVALID_DATA_TYPE, attr.getFieldName()))
                        .build();
                errors.add(messageError);
            }
        }
        return errors;
    }
}
