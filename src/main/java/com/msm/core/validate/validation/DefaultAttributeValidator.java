package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.Utils;
import com.msm.core.validate.attr.ValueValidationHandlerFactory;
import com.msm.core.validate.domain.*;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class DefaultAttributeValidator implements AttributeValidator {
    private final AttributeValidator attributeDataTypeValidator;

    @Override
    public List<MessageError> validate(ObjectAttribute objectAttribute, Map<String, Object> payload) {
        List<MessageError> errors = attributeDataTypeValidator.validate(objectAttribute, payload);
        Set<String> attributeNameErrors = errors.stream().map(MessageError::getAttribute).collect(Collectors.toSet());
        List<Attribute> attributes = objectAttribute.getAttributes();
        for(Attribute attr : attributes){
            if(!attributeNameErrors.contains(attr.getFieldName())) {
                boolean requireValidate = requireValidate(attr, payload);
                if(!requireValidate){
                    MessageError messageError = MessageError
                            .builder()
                            .attribute(attr.getFieldName())
                            .message(Utils.STR.format(Constants.REQUIRE_INPUT_VALUE, attr.getFieldName()))
                            .build();
                    errors.add(messageError);
                    continue;
                }
                boolean isValid = isDataValid(attr, payload);
                if(!isValid) {
                    MessageError messageError = MessageError
                            .builder()
                            .attribute(attr.getFieldName())
                            .message(Utils.STR.format(Constants.INVALID_INPUT_VALUE, attr.getFieldName()))
                            .build();
                    errors.add(messageError);
                }
            }
        }
        return errors;
    }

    private boolean requireValidate(Attribute attr, Map<String, Object> attributeData) {
        if(attr.getIsRequired()) {
            return Objects.nonNull(attributeData.get(attr.getFieldName()));
        }
        return true;
    }

    private boolean isDataValid(Attribute attr, Map<String, Object> attributeData) {
        String attributeType = attr.getFieldType();
        String attributeName = attr.getFieldName();
        return ValueValidationHandlerFactory.getHandler(attributeType).isValid(attr, attributeData.get(attributeName));
    }
}
