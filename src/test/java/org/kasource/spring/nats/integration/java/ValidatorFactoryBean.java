package org.kasource.spring.nats.integration.java;

import javax.validation.Validation;
import javax.validation.Validator;

import org.springframework.beans.factory.FactoryBean;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;

public class ValidatorFactoryBean implements FactoryBean<Validator> {
    @Override
    public Validator getObject() throws Exception {
        return Validation.byDefaultProvider()
                .configure()
                .messageInterpolator(new ParameterMessageInterpolator())
                .buildValidatorFactory()
                .getValidator();
    }

    @Override
    public Class<?> getObjectType() {
        return Validator.class;
    }
}
