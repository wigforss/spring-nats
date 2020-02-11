package org.kasource.spring.nats.consumer;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

import org.kasource.spring.nats.annotation.Consumer;

public class NatsPostBeanProcessor implements BeanPostProcessor {

    private NatsConsumerManager natsConsumerManager;

    public NatsPostBeanProcessor(final NatsConsumerManager natsConsumerManager) {
        this.natsConsumerManager = natsConsumerManager;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        Class<?> targetClass = AopUtils.getTargetClass(bean);
        List<Method> consumerMethods = new ArrayList<>();

        ReflectionUtils.doWithMethods(targetClass, m -> consumerMethods.add(m), m -> m.isAnnotationPresent(Consumer.class));

        if (!consumerMethods.isEmpty()) {
            createConsumers(bean, consumerMethods);
        }
        return bean;
    }



    private void createConsumers(Object bean, List<Method> consumerMethods) {
        consumerMethods.stream().forEach(m -> natsConsumerManager.register(bean, m));
    }



}
