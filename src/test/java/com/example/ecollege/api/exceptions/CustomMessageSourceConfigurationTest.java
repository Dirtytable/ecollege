package com.example.ecollege.api.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.MessageSource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@ExtendWith(MockitoExtension.class)
class CustomMessageSourceConfigurationTest {

    @Test
    void messageSource() {
        MessageSource result = new CustomMessageSourceConfiguration().messageSource();
        assertThat(result).isNotNull();
    }

    @Test
    void getValidator() {
        LocalValidatorFactoryBean result = new CustomMessageSourceConfiguration().getValidator();
        assertThat(result).isNotNull();
    }
}