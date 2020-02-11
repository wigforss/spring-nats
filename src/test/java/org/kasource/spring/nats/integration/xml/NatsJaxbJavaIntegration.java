package org.kasource.spring.nats.integration.xml;

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
import org.kasource.spring.nats.config.java.NatsJaxb;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.exception.SerializeException;
import org.kasource.spring.nats.integration.NatsTestListener;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NatsJaxb.class)
@TestPropertySource(properties = { "spring.nats.connection.url: nats://localhost:${NATS_PORT}"})
@DirtiesContext
public class NatsJaxbJavaIntegration {

    @Autowired
    private NatsConsumerManagerImpl natsConsumerManager;

    @Autowired
    private NatsTemplate template;


    @Test
    public void xmlSerDe() throws InterruptedException {
        String name = "name";
        final int age = 10;


        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<Person> personListener = new NatsTestListener<>();
        personListener.setLatch(latch);
        natsConsumerManager.register(personListener, Person.class, "person-subject");
        Person person = new ObjectFactory().createPerson();
        person.setName(name);
        person.setAge(age);

        template.publish(person, "person-subject");
        assertThat("Timed out waiting for message", latch.await(20, TimeUnit.SECONDS), is(true));
        assertThat(personListener.getObject().isPresent(), is(true));
        assertThat(personListener.getObject().get().getName(), equalTo(name));
        assertThat(personListener.getObject().get().getAge(), is(age));

    }

    @Test(expected = SerializeException.class)
    public void xmlSerDeNotCompliantToSchema() {
        String name = "name";
        final int age = -1;


        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<Person> personListener = new NatsTestListener<>();
        personListener.setLatch(latch);
        natsConsumerManager.register(personListener, Person.class, "person-subject");
        Person person = new ObjectFactory().createPerson();
        person.setName(name);
        person.setAge(age);

        template.publish(person, "person-subject");

    }



}
