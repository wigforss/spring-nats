package org.kasource.spring.nats.integration.kryo;

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


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = KryoConfiguration.class)
@TestPropertySource(properties = { "spring.nats.connection.url: nats://localhost:${NATS_PORT}"})
@DirtiesContext
public class NatsKryoJavaIntegration {

    @Autowired
    private NatsTemplate template;

    @Autowired
    private ProjectListener projectListener;

    @Test
    public void kryoSerDe() throws InterruptedException {
        String name = "name";
        int budget = 1000;
        String description = "description";
        Project project = new Project();
        project.setName(name);
        project.setBudget(budget);
        project.setDescription(description);

        CountDownLatch latch = new CountDownLatch(1);
        projectListener.setLatch(latch);



        template.publish(project, "project-subject");

        assertThat("Timed out waiting for message", latch.await(1, TimeUnit.SECONDS), is(true));
        assertThat(projectListener.getProject().isPresent(), is(true));
        assertThat(projectListener.getProject().get().getName(), equalTo(name));
        assertThat(projectListener.getProject().get().getBudget(), is(budget));
    }
}
