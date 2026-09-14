package com.msm.core.commons;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

public class SafeBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {
    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_STRING)) {
            String value = p.getText();
            if (value == null || value.replaceAll("[\\s\\p{Z}]+", "").isEmpty()) {
                return null;
            }
            return new BigDecimal(value.trim());
        }

        if (p.hasToken(JsonToken.VALUE_NUMBER_FLOAT) || p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
            return p.getDecimalValue();
        }

        return null;
    }
}
