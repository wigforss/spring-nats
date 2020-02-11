package org.kasource.spring.nats.annotation;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a method as a NATS message listener, which makes the registration of the method automatic.
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Consumer {
    /**
     * NATS subject to listen to.
     **/
    String subject();

    /**
     * Name of consumer group to share messages with.
     **/
    String queueName() default "";
}
