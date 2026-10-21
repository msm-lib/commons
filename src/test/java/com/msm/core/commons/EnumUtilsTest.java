package com.msm.core.commons;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EnumUtilsTest {

    private enum Status { NEW, ACTIVE }

    @Test
    void fromString_exactName_returnsConstant() {
        assertThat(Utils.E.<Status>fromString(Status.class, "ACTIVE")).contains(Status.ACTIVE);
    }

    @Test
    void fromString_differentCase_returnsEmpty() {
        assertThat(Utils.E.<Status>fromString(Status.class, "active")).isEmpty();
    }

    @Test
    void fromString_surroundingSpaces_returnsEmpty() {
        assertThat(Utils.E.<Status>fromString(Status.class, " ACTIVE ")).isEmpty();
    }

    @Test
    void fromString_nullOrBlank_returnsEmpty() {
        assertThat(Utils.E.<Status>fromString(Status.class, null)).isEmpty();
        assertThat(Utils.E.<Status>fromString(Status.class, "")).isEmpty();
    }

    @Test
    void fromStringOrDefault_differentCase_returnsDefault() {
        assertThat(Utils.E.fromStringOrDefault(Status.class, "New", Status.ACTIVE)).isEqualTo(Status.ACTIVE);
    }

    @Test
    void isValid_followsExactName() {
        assertThat(Utils.E.isValid(Status.class, "NEW")).isTrue();
        assertThat(Utils.E.isValid(Status.class, "new")).isFalse();
    }
}
