package com.msm.core.validate.validation;


import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.validate.domain.MessageError;

import java.util.List;
import java.util.Map;

public interface AttributeValidator {
    List<MessageError> validate(ObjectMetadata objectAttribute, Map<String, Object> payload);
}
