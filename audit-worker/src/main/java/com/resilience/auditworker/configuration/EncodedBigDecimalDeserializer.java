package com.resilience.auditworker.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.resilience.domain.exception.NoStacktraceException;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.format.DateTimeParseException;
import java.util.Base64;

public class EncodedBigDecimalDeserializer extends JsonDeserializer<BigDecimal> {

    private static final int SCALE = 2;

    @Override
    public BigDecimal deserialize(final JsonParser jsonParser, final DeserializationContext context) throws IOException {
        try {
            final String encodedValue = jsonParser.getValueAsString();
            final byte[] bytes = Base64.getDecoder().decode(encodedValue);
            return new BigDecimal(new BigInteger(bytes), SCALE);
        } catch (final DateTimeParseException ex) {
            throw new NoStacktraceException("Unable to parse decimal value", ex);
        }
    }

}
