package org.kasource.spring.nats.integration.json;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import org.kasource.json.schema.validation.InvalidJsonException;
import org.kasource.spring.nats.NatsTemplate;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.integration.NatsTestListener;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NatsJacksonJavaConfiguration.class)
@TestPropertySource(properties = {
        "spring.nats.connection.url: nats://localhost:${NATS_PORT}",
        "spring.nats.jackson.json-schema.scan-packages: org.kasource.spring.nats.integration.json"
})
@DirtiesContext
public class NatsJacksonJavaIntegration {

    @Autowired
    private NatsConsumerManagerImpl natsConsumerManager;

    @Autowired
    private NatsTemplate template;


    @Test
    public void jacksonSerDeValid() throws InterruptedException {
        String name = "name";
        final int age = 1;


        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<Person> personListener = new NatsTestListener<>();
        personListener.setLatch(latch);
        natsConsumerManager.register(personListener, Person.class, "person-subject");
        Person person = new Person();
        person.setName(name);
        person.setAge(age);

        template.publish(person, "person-subject");
        assertThat("Timed out waiting for message", latch.await(20, TimeUnit.SECONDS), is(true));
        assertThat(personListener.getObject().isPresent(), is(true));
        assertThat(personListener.getObject().get().getName(), equalTo(name));
        assertThat(personListener.getObject().get().getAge(), is(age));

    }

    @Test(expected = InvalidJsonException.class)
    public void jacksonSerDeInvalid() throws InterruptedException {
        String name = "name";
        final int age = -1;


        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<Person> personListener = new NatsTestListener<>();
        personListener.setLatch(latch);
        natsConsumerManager.register(personListener, Person.class, "person-subject");
        Person person = new Person();
        person.setName(name);
        person.setAge(age);

        template.publish(person, "person-subject");
    }

}
