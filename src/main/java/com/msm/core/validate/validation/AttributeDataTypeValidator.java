package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.ValueConvertFactory;
import com.msm.core.commons.GenericTypeResolverFactory;
import com.msm.core.commons.Utils;
import com.msm.core.exceptions.ErrorCode;
import com.msm.core.filter.EntityClassFactory;
import com.msm.core.validate.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class AttributeDataTypeValidator implements AttributeValidator {

    private final EntityClassFactory entityClassFactory;

    private boolean isDataTypeValid(Attribute attr, Object data) {
        try {
            Class<?> aClass = GenericTypeResolverFactory.resolve(attr.getFieldType()).getRawClass();
            ValueConvertFactory.convert(aClass, data);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public List<MessageError> validate(ObjectAttribute objectAttribute, Map<String, Object> payload) {
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
