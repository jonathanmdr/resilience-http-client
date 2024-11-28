package com.resilience.domain.validation;

import com.resilience.domain.validation.handler.ThrowableHandler;
import org.junit.jupiter.api.Test;

import java.util.logging.Logger;

import static org.assertj.core.api.Assertions.assertThat;

class ValidatorTest {

    @Test
    void testValidate() {
        final ValidationHandler validationHandler = ThrowableHandler.create();
        final Validator validator = new StubValidator(validationHandler);
        assertThat(validator.validationHandler())
            .isNotNull()
            .isExactlyInstanceOf(ThrowableHandler.class);
    }

    static class StubValidator extends Validator {

        final Logger logger = Logger.getLogger(StubValidator.class.getName());

        public StubValidator(final ValidationHandler validationHandler) {
            super(validationHandler);
        }

        @Override
        public void validate() {
            logger.info("Validating...");
        }

    }
}
