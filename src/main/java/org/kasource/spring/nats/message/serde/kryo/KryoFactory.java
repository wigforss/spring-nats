package org.kasource.spring.nats.message.serde.kryo;

import com.esotericsoftware.kryo.Kryo;

public interface KryoFactory {
    Kryo createFor(Class<?> forClass);
}
