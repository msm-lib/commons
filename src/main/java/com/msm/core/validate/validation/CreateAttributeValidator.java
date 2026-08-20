package com.msm.core.validate.validation;

import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.validate.domain.MessageError;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class CreateAttributeValidator implements AttributeValidator {
    private final AttributeValidator attributeDataTypeValidator;
    private final AttributeValidationSupport attributeValidationSupport;

    @Override
    public List<MessageError> validate(ObjectMetadata objectAttribute, Map<String, Object> payload) {
        List<MessageError> errors = attributeDataTypeValidator.validate(objectAttribute, payload);
        Set<String> attributeNameErrors = errors.stream().map(MessageError::getAttribute).collect(Collectors.toSet());
        List<Attribute> attributes = objectAttribute.getAttributes();
        for(Attribute attr : attributes){

            if (attributeNameErrors.contains(attr.getFieldName())) {
                continue;
            }

            //required field check
            if (!attributeValidationSupport.requireCheck(attr, payload)) {
                errors.add(attributeValidationSupport.requiredError(attr));
                continue;
            }

            if(!attributeValidationSupport.isDataValid(attr, payload)) {
                errors.add(attributeValidationSupport.invalidError(attr));
            }
        }
        return errors;
    }
}
