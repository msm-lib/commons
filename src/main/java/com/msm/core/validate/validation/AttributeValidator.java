package com.msm.core.validate.validation;


import com.msm.core.validate.domain.MessageError;
import com.msm.core.validate.domain.ObjectAttribute;

import java.util.List;
import java.util.Map;

public interface AttributeValidator {
    List<MessageError> validate(ObjectAttribute objectAttribute, Map<String, Object> payload);
}
