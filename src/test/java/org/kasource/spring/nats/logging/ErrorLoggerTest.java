package org.kasource.spring.nats.logging;

import java.util.List;
import java.util.Map;

import org.kasource.spring.nats.event.NatsErrorEvent;
import org.kasource.spring.nats.event.NatsExceptionEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import io.nats.client.Connection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.unitils.inject.util.InjectionUtils;

@RunWith(MockitoJUnitRunner.class)
public class ErrorLoggerTest {
    @Mock
    private Logger logger;

    @Mock
    private NatsExceptionEvent event;

    @Mock
    private NatsErrorEvent natsErrorEvent;

    @Mock
    private Connection natsConnection;

    @Mock
    private RuntimeException runtimeException;

    @Mock
    private SecurityException securityException;

    @Mock
    private IllegalStateException illegalStateException;

    @Test
    public void onError() {
        ErrorLogger errorLogger = new ErrorLogger();
        InjectionUtils.injectInto(logger, errorLogger, "logger");

        String errorMessage = "errorMessage";
        String url = "url";

        when(natsErrorEvent.getError()).thenReturn(errorMessage);
        when(natsErrorEvent.getSource()).thenReturn(natsConnection);
        when(natsConnection.getConnectedUrl()).thenReturn(url);

        errorLogger.onError(natsErrorEvent);

        verify(logger).error("Error occurred in NATS: " + errorMessage + " for connection " + url);
    }

    @Test
    public void log() {
        ErrorLogger errorLogger = new ErrorLogger();
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(runtimeException);
        when(runtimeException.getMessage()).thenReturn(exceptionMessage);

        errorLogger.onException(event);

        verify(logger).warn(exceptionMessage, runtimeException);
    }

    @Test
    public void logChangeDefaultLevel() {
        ErrorLogger errorLogger = new ErrorLogger(Level.INFO);
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(runtimeException);
        when(runtimeException.getMessage()).thenReturn(exceptionMessage);
        errorLogger.onException(event);

        verify(logger).info(exceptionMessage, runtimeException);
    }

    @Test
    public void logConfigureLevelsDefault() {
        Map<Level,  List<Class<? extends Throwable>>> levelsPerException = Map.of(
                Level.ERROR, List.of(securityException.getClass()),
                Level.INFO, List.of(illegalStateException.getClass())
        );
        ErrorLogger errorLogger = new ErrorLogger(Level.DEBUG, levelsPerException);
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(runtimeException);
        when(runtimeException.getMessage()).thenReturn(exceptionMessage);

        errorLogger.onException(event);
        verify(logger).debug(exceptionMessage, runtimeException);


    }

    @Test
    public void logConfigureLevelsToError() {
        Map<Level,  List<Class<? extends Throwable>>> levelsPerException = Map.of(
                Level.ERROR, List.of(securityException.getClass()),
                Level.INFO, List.of(illegalStateException.getClass())
        );
        ErrorLogger errorLogger = new ErrorLogger(Level.DEBUG, levelsPerException);
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(securityException);

        when(securityException.getMessage()).thenReturn(exceptionMessage);

        errorLogger.onException(event);
        verify(logger).error(exceptionMessage, securityException);
    }

    @Test
    public void logConfigureLevelsToInfo() {
        Map<Level,  List<Class<? extends Throwable>>> levelsPerException = Map.of(
                Level.ERROR, List.of(securityException.getClass()),
                Level.INFO, List.of(illegalStateException.getClass())
        );
        ErrorLogger errorLogger = new ErrorLogger(Level.DEBUG, levelsPerException);
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(illegalStateException);


        when(illegalStateException.getMessage()).thenReturn(exceptionMessage);

        errorLogger.onException(event);

        verify(logger).info(exceptionMessage, illegalStateException);


    }

    @Test
    public void logConfigureLevelsToTrace() {
        Map<Level,  List<Class<? extends Throwable>>> levelsPerException = Map.of(
                Level.ERROR, List.of(securityException.getClass()),
                Level.TRACE, List.of(illegalStateException.getClass())
        );
        ErrorLogger errorLogger = new ErrorLogger(Level.DEBUG, levelsPerException);
        InjectionUtils.injectInto(logger, errorLogger, "logger");
        String exceptionMessage = "exceptionMessage";

        when(event.getException()).thenReturn(illegalStateException);


        when(illegalStateException.getMessage()).thenReturn(exceptionMessage);

        errorLogger.onException(event);

        verify(logger).trace(exceptionMessage, illegalStateException);
    }


}
