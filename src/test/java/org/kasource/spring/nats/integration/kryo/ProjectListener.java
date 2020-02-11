package org.kasource.spring.nats.integration.kryo;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.kasource.spring.nats.annotation.Consumer;
import org.kasource.spring.nats.integration.java.Project;

public class ProjectListener {

    private Optional<Project> project = Optional.empty();

    private CountDownLatch latch;

    @Consumer(subject = "project-subject")
    public void onProjectChanged(Project project) {
        this.project = Optional.ofNullable(project);
        if (latch != null) {
            latch.countDown();
        }
    }

    public Optional<Project> getProject() {
        return project;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
