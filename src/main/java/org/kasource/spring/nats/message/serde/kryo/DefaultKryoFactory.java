package org.kasource.spring.nats.message.serde.kryo;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.esotericsoftware.kryo.Kryo;

public class DefaultKryoFactory implements KryoFactory {

    private ThreadLocal<Map<Class<?>, Kryo>> kryos = new ThreadLocal<>();

    @Override
    public Kryo createFor(Class<?> forClass) {
        Map<Class<?>, Kryo> kryoMap = kryos.get();
        if (kryoMap == null) {
            kryoMap = new ConcurrentHashMap<>();
            kryos.set(kryoMap);
        }
        Kryo kryo = kryoMap.get(forClass);
        if (kryo == null) {
            kryo = new Kryo();
            kryo.register(forClass);
            kryoMap.put(forClass, kryo);
        }
        return kryo;
    }


}
