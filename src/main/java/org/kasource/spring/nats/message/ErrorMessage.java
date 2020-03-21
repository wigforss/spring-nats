package org.kasource.spring.nats.message;

import java.io.Serializable;
import java.util.Date;

import org.apache.commons.lang3.exception.ExceptionUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class ErrorMessage implements Serializable {
    private static final long serialVersionUID = 7193952441991217423L;

    private Date timestamp;
    private String message;
    private String exceptionType;
    private String subject;
    private StackTraceElement[] trace;

    public ErrorMessage() {
        this.timestamp = new Date();
    }

    public ErrorMessage(final Throwable throwable, final String subject) {
       this();
       Throwable rootCause = ExceptionUtils.getRootCause(throwable);
       this.exceptionType = rootCause.getClass().getName();
       this.message = rootCause.getMessage();
       this.trace = throwable.getStackTrace();
       this.subject = subject;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    public Date getTimestamp() {
        return timestamp;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP")
    @SuppressWarnings("PMD.MethodReturnsInternalArray")
    public StackTraceElement[] getTrace() {
        return trace;
    }

    @SuppressFBWarnings("EI_EXPOSE_REP2")
    @SuppressWarnings({"PMD.ArrayIsStoredDirectly", "PMD.UseVarargs"})
    public void setTrace(StackTraceElement[] trace) {
        this.trace = trace;
    }
}
