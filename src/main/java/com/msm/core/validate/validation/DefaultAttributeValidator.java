package com.msm.core.validate.validation;

import com.msm.core.commons.Constants;
import com.msm.core.commons.DataConvertFactory;
import com.msm.core.commons.Utils;
import com.msm.core.validate.domain.*;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
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
        String attributeType = attr.getAttributeType();
        String attributeName = attr.getFieldName();
        switch (attributeType){
            case "SHORT_TEXT", "TEXT", "TEXT_AREA", "String" -> {
                Object value = attributeData.get(attributeName);
                if(Objects.isNull(value)) {
                    return true;
                }
                boolean isValid = true;
                String text = DataConvertFactory.convert(String.class, value);
                if(Objects.nonNull(attr.getRegex())) {
                    isValid = text.matches(attr.getRegex());
                }
                return isValid && (Objects.isNull(attr.getMaxLength()) || text.length() <= attr.getMaxLength());
            }
            case "Long" -> {
                Object value = attributeData.get(attributeName);
                if(Objects.isNull(value)) {
                    return true;
                }

                Long val = DataConvertFactory.convert(Long.class, value);
                boolean isLessThan = Objects.isNull(attr.getMaxValue()) || val <= attr.getMaxValue();
                boolean isMoreThan = Objects.isNull(attr.getMinValue()) || attr.getMaxValue() >= val;
                return isLessThan && isMoreThan;
            }
            case "Integer" -> {
                Object value = attributeData.get(attributeName);
                if(Objects.isNull(value)) {
                    return true;
                }

                Integer val = DataConvertFactory.convert(Integer.class, value);
                boolean isLessThan = Objects.isNull(attr.getMaxValue()) || val <= attr.getMaxValue();
                boolean isMoreThan = Objects.isNull(attr.getMinValue()) || attr.getMinValue() >= val;
                return isLessThan && isMoreThan;
            }
            case "BigDecimal" -> {
                Object value = attributeData.get(attributeName);
                if(Objects.isNull(value)) {
                    return true;
                }

                BigDecimal bigDecimal = DataConvertFactory.convert(Integer.class, value);
                boolean isLessThan = Objects.isNull(attr.getMaxValue()) || bigDecimal.compareTo(BigDecimal.valueOf(attr.getMaxValue())) <= 0;
                boolean isMoreThan = Objects.isNull(attr.getMinValue()) || bigDecimal.compareTo(BigDecimal.valueOf(attr.getMinValue())) >= 0;
                return isLessThan && isMoreThan;
            }
            default -> {
                return true;
            }
        }
    }
}
