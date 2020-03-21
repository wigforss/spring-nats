package org.kasource.spring.nats.integration.java;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import org.kasource.spring.nats.NatsTemplate;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:java/application-context.xml")
@DirtiesContext
public class NatsJavaSerDeXmlIntegration {

    @Autowired
    private NatsTemplate template;

    @Autowired
    private ProjectListener projectListener;

    @Test
    public void javaSerdeSucessfulValidation() throws InterruptedException {
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

    @Test(expected = ConstraintViolationException.class)
    public void javaSerdeFailedValidation() throws InterruptedException {
        String name = "name";
        int budget = -1;
        String description = "description";
        Project project = new Project();
        project.setName(name);
        project.setBudget(budget);
        project.setDescription(description);

        CountDownLatch latch = new CountDownLatch(1);
        projectListener.setLatch(latch);



        template.publish(project, "project-subject");

    }
}
