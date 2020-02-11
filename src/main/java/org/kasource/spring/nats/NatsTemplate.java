package org.kasource.spring.nats;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.kasource.spring.nats.exception.RequestFailedException;
import org.kasource.spring.nats.exception.RequestTimeoutException;

import io.nats.client.Message;

/**
 * NATS Template
 *
 * The template is used to publish messages or to request a response message.
 **/
public interface NatsTemplate {

    /**
     * Publish an object as the payload (data) to a subject.
     *
     * @param object  The object to be sent, as the payload (data) of the message to the subject.
     * @param subject The subject to send the message to
     */
    void publish(Object object, String subject);

    /**
     *  Publish an object as the payload (data) to a subject.
     *
     * @param object  The object to be sent, as the payload (data) of the message to the subject.
     * @param subject The subject to send the message to
     * @param replyTo The subject were a response is expected to be published.
     */
    void publish(Object object, String subject, String replyTo);


    /**
     *  Request (Synchronous) a response from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     * @param responseType   The type of the response
     * @param timeout        Timeout to wait for response before throwing RequestTimeoutException.
     * @param timeUnit       Time unit for timeout
     * @param <T>            The response type
     *
     * @return A NatsResponse object with both the response object as well as the actual response message.
     *
     * @throws RequestFailedException    If parsing the response failed or the wait was interrupted.
     * @throws RequestTimeoutException   If the request timed out
     */
    <T> NatsResponse<T> request(Object requestObject,
                                String subject,
                                Class<T> responseType,
                                long timeout,
                                TimeUnit timeUnit) throws RequestFailedException, RequestTimeoutException;

    /**
     *  Request (Synchronous) a response object from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     * @param responseType   The type of the response
     * @param timeout        Timeout to wait for response before throwing RequestTimeoutException.
     * @param timeUnit       Time unit for timeout
     * @param <T>            The response type
     *
     * @return The response object.
     *
     * @throws RequestFailedException    If parsing the response failed or the wait was interrupted.
     * @throws RequestTimeoutException   If the request timed out
     */
    <T> T requestForObject(Object requestObject,
                           String subject,
                           Class<T> responseType,
                           long timeout,
                           TimeUnit timeUnit) throws RequestFailedException, RequestTimeoutException;

    /**
     *  Request (Synchronous) a response message from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     * @param timeout        Timeout to wait for response before throwing RequestTimeoutException.
     * @param timeUnit       Time unit for timeout
     *
     * @return The response message.
     *
     * @throws RequestFailedException    If parsing the response failed or the wait was interrupted.
     * @throws RequestTimeoutException   If the request timed out
     */
    Message requestForMessage(Object requestObject,
                              String subject,
                              long timeout,
                              TimeUnit timeUnit) throws RequestFailedException, RequestTimeoutException;

    // Asynchronous request

    /**
     *  Request (Asynchronous) a response from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     * @param responseType   The type of the response
     * @param <T>            The response type
     *
     * @return A CompletableFuture of NatsResponse object with both the response object as well as the actual response message.
     */
    <T> CompletableFuture<NatsResponse<T>> requestAsync(Object requestObject,
                                                        String subject,
                                                        Class<T> responseType);

    /**
     *  Request (Asynchronous) a response object from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     * @param responseType   The type of the response
     * @param <T>            The response type
     *
     * @return A CompletableFuture of the response object.
     */
    <T> CompletableFuture<T> requestForObjectAsync(Object requestObject,
                                                   String subject,
                                                   Class<T> responseType);

    /**
     *  Request (Asynchronous) a response message from a consumer of a subject.
     *
     * @param requestObject  The request object, sent as the payload (data) of the request message to the subject.
     * @param subject        The subject to send the request to
     *
     * @return A CompletableFuture of the response message.
     */
    CompletableFuture<Message> requestForMessageAsync(Object requestObject,
                                                      String subject);






}
