package org.kasource.spring.nats.integration.avro;

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

import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.NatsTemplate;
import org.kasource.spring.nats.consumer.NatsConsumerManagerImpl;
import org.kasource.spring.nats.integration.NatsTestListener;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = NatsAvroJavaConfiguration.class)
@TestPropertySource(properties = { "spring.nats.connection.url: nats://localhost:${NATS_PORT}"})
@DirtiesContext
public class NatsAvroJavaIntegration {

    @Autowired
    private NatsConsumerManagerImpl natsConsumerManager;

    @Autowired
    private SimpleMeterRegistry simpleMeterRegistry;

    @Autowired
    private NatsTemplate template;


    @Test
    public void specificAvroSerde() throws InterruptedException {
        String name = "name";
        final int favoriteNumber = 1;
        String favoriteColor = "green";

        CountDownLatch latch = new CountDownLatch(1);

        NatsTestListener<User> userListener = new NatsTestListener<>();
        userListener.setLatch(latch);
        natsConsumerManager.register(userListener, User.class, "user-subject");
        User user = User.newBuilder().setName(name).setFavoriteNumber(favoriteNumber).setFavoriteColor(favoriteColor).build();

        template.publish(user, "user-subject");
        assertThat("Timed out waiting for message", latch.await(20, TimeUnit.SECONDS), is(true));
        assertThat(userListener.getObject().isPresent(), is(true));
        assertThat(userListener.getObject().get().getName().toString(), equalTo(name));
        assertThat(userListener.getObject().get().getFavoriteNumber(), equalTo(favoriteNumber));
        assertThat(userListener.getObject().get().getFavoriteColor().toString(), equalTo(favoriteColor));
    }

    @Test
    public void reflectionAvroSerde() throws InterruptedException {
        String name = "name";
        int budget = 1000;
        String description = "description";
        Project project = new Project();
        project.setName(name);
        project.setBudget(budget);
        project.setDescription(description);
        CountDownLatch latch = new CountDownLatch(1);
        NatsTestListener<Project> projectListener = new NatsTestListener<>();
        projectListener.setLatch(latch);
        natsConsumerManager.register(projectListener, Project.class, "project-subject");
        template.publish(project, "project-subject");
        assertThat("Timed out waiting for message", latch.await(20, TimeUnit.SECONDS), is(true));
        assertThat(projectListener.getObject().isPresent(), is(true));
    }

}
