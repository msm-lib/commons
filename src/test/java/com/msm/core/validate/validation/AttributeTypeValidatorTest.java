package com.msm.core.validate.validation;

import com.msm.core.exceptions.ErrorCodeEnum;
import com.msm.core.metadata.Attribute;
import com.msm.core.metadata.ObjectMetadata;
import com.msm.core.validate.domain.MessageError;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AttributeTypeValidatorTest {

    private enum PurposeType { BILL_TO, SHIP_TO }

    private final AttributeTypeValidator validator = new AttributeTypeValidator();

    @Test
    void singleValue_validConstant_passes() {
        assertThat(validate("status", "BILL_TO")).isEmpty();
    }

    @Test
    void singleValue_unknownConstant_fails() {
        assertInvalid(validate("status", "ABC"), "status");
    }

    @Test
    void list_allConstantsValid_passes() {
        assertThat(validate("purposeType", List.of("BILL_TO", "SHIP_TO"))).isEmpty();
    }

    @Test
    void array_allConstantsValid_passes() {
        assertThat(validate("purposeType", new String[]{"BILL_TO", "SHIP_TO"})).isEmpty();
    }

    @Test
    void singleValue_differentCase_fails() {
        assertInvalid(validate("status", "Bill_To"), "status");
    }

    @Test
    void list_differentCase_fails() {
        assertInvalid(validate("purposeType", List.of("BILL_TO", "ship_to")), "purposeType");
    }

    @Test
    void list_surroundingSpaces_fails() {
        assertInvalid(validate("purposeType", List.of(" BILL_TO")), "purposeType");
    }

    @Test
    void list_oneUnknownConstant_fails() {
        assertInvalid(validate("purposeType", List.of("BILL_TO", "ABC")), "purposeType");
    }

    @Test
    void array_oneUnknownConstant_fails() {
        assertInvalid(validate("purposeType", new String[]{"BILL_TO", "ABC"}), "purposeType");
    }

    @Test
    void list_nullElement_fails() {
        assertInvalid(validate("purposeType", Arrays.asList("BILL_TO", null)), "purposeType");
    }

    @Test
    void list_empty_passes() {
        assertThat(validate("purposeType", List.of())).isEmpty();
    }

    @Test
    void value_absent_isSkipped() {
        assertThat(validate("purposeType", null)).isEmpty();
    }

    private List<MessageError> validate(String fieldName, Object value) {
        Attribute attribute = Attribute.builder().fieldName(fieldName).fieldType("String").enumType(PurposeType.class).build();
        ObjectMetadata metadata = ObjectMetadata.builder().name("test").attributes(new ArrayList<>(List.of(attribute))).build();
        Map<String, Object> payload = new HashMap<>();
        payload.put(fieldName, value);
        return validator.validate(metadata, payload);
    }

    private void assertInvalid(List<MessageError> errors, String fieldName) {
        assertThat(errors).singleElement().satisfies(error -> {
            assertThat(error.getAttribute()).isEqualTo(fieldName);
            assertThat(error.getCode()).isEqualTo(ErrorCodeEnum.INVALID_DATA_TYPE);
        });
    }
}
