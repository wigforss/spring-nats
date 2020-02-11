package org.kasource.spring.nats.message.validation;

import javax.validation.Validator;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class BeanValidationValidatorFactoryBean implements FactoryBean<BeanValidationValidator>, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @SuppressFBWarnings(value = "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR",
                        justification = "Spring provides applicationContext")
    @Override
    public BeanValidationValidator getObject() throws Exception {
        Validator validator = applicationContext.getBean(Validator.class);
        return new BeanValidationValidator(validator);
    }

    @Override
    public Class<?> getObjectType() {
        return BeanValidationValidator.class;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
