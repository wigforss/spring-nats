package org.kasource.spring.nats.message.serde.avro;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.message.serde.NatsMessageSerializer;
import org.kasource.spring.nats.message.validation.MessageObjectValidator;

import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecordBase;

public class AvroMessageSerializer implements NatsMessageSerializer {
    private ThreadLocal<BinaryEncoder> encoder = new ThreadLocal<>();

    private Optional<MessageObjectValidator> validator = Optional.empty();
    private Map<Class<?>, GenericDatumWriter> writers = new ConcurrentHashMap<>();

    @Override
    public void setValidator(Optional<MessageObjectValidator> validator) {
        this.validator = validator;
    }

    @Override
    public byte[] toMessageData(Object object) throws SerializeException {
        validator.filter(v -> v.shouldValidate(object.getClass())).ifPresent(v -> v.validate(object));
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            GenericDatumWriter datumWriter = writers.get(object.getClass());
            if (datumWriter == null) {
                datumWriter = createDatumWriter(object);
                writers.put(object.getClass(), datumWriter);
            }
            BinaryEncoder binaryEncoder = encoder.get();
            binaryEncoder = EncoderFactory.get().binaryEncoder(bos, binaryEncoder);
            datumWriter.write(object, binaryEncoder);
            binaryEncoder.flush();
            encoder.set(binaryEncoder);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new SerializeException("Could not serialize " + object, e);
        }

    }


    private GenericDatumWriter createDatumWriter(Object object) {

        if (object instanceof  SpecificRecordBase) {
            return new SpecificDatumWriter(((SpecificRecordBase) object).getSchema());
        } else {
            return new ReflectDatumWriter(object.getClass());
        }
    }




}
