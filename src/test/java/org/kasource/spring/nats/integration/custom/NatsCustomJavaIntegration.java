package org.kasource.spring.nats.integration.custom;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.NatsTemplate;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.integration.NatsTestListener;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NatsCustomJavaConfiguration.class)
@TestPropertySource(properties = { "spring.nats.connection.url: nats://localhost:${NATS_PORT}"})
@DirtiesContext
public class NatsCustomJavaIntegration {

    @Autowired
    private NatsConsumerManagerImpl natsConsumerManager;

    @Autowired
    private NatsTemplate template;


    @Test
    public void customSerDe() throws InterruptedException {
        String name = "name";


        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<String> stringListener = new NatsTestListener<>();
        stringListener.setLatch(latch);
        natsConsumerManager.register(stringListener, String.class, "string-subject");
        template.publish(name, "string-subject");
        assertThat("Timed out waiting for message", latch.await(20, TimeUnit.SECONDS), is(true));
        assertThat(stringListener.getObject().isPresent(), is(true));
        assertThat(stringListener.getObject().get(), equalTo(name));

    }


}
