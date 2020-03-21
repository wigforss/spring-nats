package org.kasource.spring.nats.integration.proto;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.kasource.spring.nats.NatsTemplate;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:proto/application-context.xml")
@DirtiesContext
public class NatsProtoXmlIntegration {

    @Autowired
    private NatsConsumerManagerImpl natsConsumerManager;

    @Autowired
    private NatsTemplate template;



    @Test
    public void protoSerde() throws InterruptedException {
        String name = "name";
        final int id = 1;

        CountDownLatch latch = new CountDownLatch(1);
        PersonListener personListener = new PersonListener(latch);
        natsConsumerManager.register(personListener, AddressBookProtos.Person.class, "person-subject");
        AddressBookProtos.Person person = AddressBookProtos.Person.newBuilder().setId(id).setName(name).build();

        template.publish(person, "person-subject");
        assertThat("Timed out waiting for message", latch.await(1, TimeUnit.SECONDS), is(true));
        assertThat(personListener.getPerson().isPresent(), is(true));
        assertThat(personListener.getPerson().get().getName(), equalTo(name));
        assertThat(personListener.getPerson().get().getId(), equalTo(id));
    }

    private static class PersonListener implements Consumer<AddressBookProtos.Person> {



        private Optional<AddressBookProtos.Person> person = Optional.empty();
        private CountDownLatch latch;


        private PersonListener(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void accept(AddressBookProtos.Person person) {
            this.person = Optional.ofNullable(person);
            latch.countDown();
        }

        public Optional<AddressBookProtos.Person> getPerson() {
            return person;
        }
    }
}
