package org.kasource.spring.nats.logging;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;

import org.kasource.spring.nats.event.NatsErrorEvent;
import org.kasource.spring.nats.event.NatsExceptionEvent;

import io.nats.client.Connection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

public class ErrorLogger {
    private static final Level DEFAULT_LOG_LEVEL = Level.WARN;
    @SuppressWarnings("PMD.LoggerIsNotStaticFinal") // For testing purposes not static final
    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    @SuppressWarnings("PMD.UseConcurrentHashMap")  // No concurrent writes
    private Map<Class<? extends Throwable>, Level> logLevelMapping = new HashMap<>();
    private Level defaultLogLevel = DEFAULT_LOG_LEVEL;

    public ErrorLogger() {
    }

    public ErrorLogger(final Level defaultLogLevel) {
        this.defaultLogLevel = defaultLogLevel;
    }

    public ErrorLogger(final Level defaultLogLevel,
                       final Map<Level, List<Class<? extends Throwable>>> logLevelPerException) {
        this(defaultLogLevel);
        logLevelPerException.entrySet()
                .stream()
                .forEach(e -> e.getValue().stream().forEach(ex -> logLevelMapping.put(ex, e.getKey())));
    }


    @Async
    @EventListener
    public void onError(NatsErrorEvent event) {
        logger.error("Error occurred in NATS: " + event.getError() + " for connection " + event.getSource().getConnectedUrl());
    }


    @Async
    @EventListener
    public void onException(NatsExceptionEvent event) {
        switch (resolveLogLevel(event.getException().getClass())) {
            case INFO:
                logger.info(event.getException().getMessage(), event.getException());
                break;
            case WARN:
                logger.warn(event.getException().getMessage(), event.getException());
                break;
            case ERROR:
                logger.error(event.getException().getMessage(), event.getException());
                break;
            case DEBUG:
                logger.debug(event.getException().getMessage(), event.getException());
                break;
            default:
                logger.trace(event.getException().getMessage(), event.getException());
                break;
        }
    }


    private Level resolveLogLevel(Class<? extends Throwable> exceptionClass) {
        return logLevelMapping.getOrDefault(exceptionClass, defaultLogLevel);
    }
}
