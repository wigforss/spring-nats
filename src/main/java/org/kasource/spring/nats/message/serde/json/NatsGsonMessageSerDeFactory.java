package org.kasource.spring.nats.message.serde.json;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.serde.NatsMessageSerDeFactory;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import com.google.gson.Gson;

public class NatsGsonMessageSerDeFactory implements NatsMessageSerDeFactory, ApplicationContextAware, InitializingBean {

    private Gson gson;
    private NatsGsonMessageSerializer serializer;
    private Map<Class<?>, NatsMessageDeserializer> deserializers = new ConcurrentHashMap<>();
    private ApplicationContext applicationContext;
    private MessageObjectValidator validator;

    @Override
    public NatsMessageDeserializer createDeserializer(Class<?> forClass) {
        Optional<NatsMessageDeserializer> deserializer = Optional.ofNullable(deserializers.get(forClass));
        return deserializer.orElseGet(() -> createAndPut(forClass));
    }

    private NatsMessageDeserializer createAndPut(Class<?> forClass) {
        NatsGsonMessageDeserializer gsonDeserializer = new NatsGsonMessageDeserializer(gson, forClass);
        gsonDeserializer.setValidator(Optional.ofNullable(validator));
        deserializers.put(forClass, gsonDeserializer);
        return gsonDeserializer;
    }

    @Override
    public NatsMessageSerializer createSerializer() {
        return serializer;
    }

    @Override
    public void setValidator(MessageObjectValidator validator) {
        this.validator = validator;
    }

    @Override
    public void afterPropertiesSet() {
        if (gson == null) {
            gson = applicationContext.getBean(Gson.class);
        }
        serializer = new NatsGsonMessageSerializer(gson);
        serializer.setValidator(Optional.ofNullable(validator));
    }

    public void setGson(Gson gson) {
        this.gson = gson;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
