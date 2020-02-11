package org.kasource.spring.nats.message.serde.avro;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

import org.kasource.spring.nats.exception.DeserializeException;
import org.kasource.spring.nats.message.serde.NatsMessageDeserializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import io.nats.client.Message;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;

public class AvroMessageDeserializer implements NatsMessageDeserializer {
    private ThreadLocal<BinaryDecoder> decoder = new ThreadLocal<>();
    private GenericDatumReader datumReader;
    private Optional<MessageObjectValidator> validator = Optional.empty();

    public AvroMessageDeserializer(final Class<?> forClass) {
        if (SpecificRecordBase.class.isAssignableFrom(forClass)) {
            datumReader = new SpecificDatumReader(forClass);
        } else {
            datumReader = new ReflectDatumReader(forClass);
        }
    }

    @Override
    public Object fromMessage(Message message) throws DeserializeException {
        ByteArrayInputStream bis = new ByteArrayInputStream(message.getData());
        try  {
            BinaryDecoder binaryDecoder = decoder.get();
            binaryDecoder = DecoderFactory.get().binaryDecoder(bis, binaryDecoder);
            Object object = datumReader.read(null, binaryDecoder);
            decoder.set(binaryDecoder);
            validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
            return object;
        } catch (IOException e) {
            throw new DeserializeException("Could not de-serialize " + message, e);
        }

    }

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }
}
