package org.kasource.spring.nats.message.serde.kryo;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import com.esotericsoftware.kryo.Kryo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DefaultKryoFactoryTest {
    @Mock
    private ThreadLocal<Map<Class<?>, Kryo>> kryos;

    @Mock
    private Map<Class<?>, Kryo> kryosMap;

    @Mock
    private Kryo kryo;

    @InjectMocks
    private DefaultKryoFactory factory;

    @Test
    public void createForCached() {
        when(kryos.get()).thenReturn(kryosMap);
        when(kryosMap.get(String.class)).thenReturn(kryo);

        assertThat(factory.createFor(String.class), is(equalTo(kryo)));
    }

    @Test
    public void createFor() {

        Kryo created = factory.createFor(String.class);

        assertThat(created.getRegistration(String.class), is(notNullValue()));
    }
}
