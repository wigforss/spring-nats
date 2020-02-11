package org.kasource.spring.nats.integration.java;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.kasource.spring.nats.config.java.NatsJava;
import org.kasource.spring.nats.message.validation.BeanValidationValidator;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

@Import(NatsJava.class)
@Configuration
public class JavaSerDeConfiguration {

    @Bean
    public ProjectListener projectListener() {
        return new ProjectListener();
    }

    @Bean
    public MessageObjectValidator messageObjectValidator() {
        Validator validator = Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();
        return new BeanValidationValidator(validator);
    }
}
