package org.kasource.spring.nats.integration.thrift;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;

import org.kasource.spring.nats.annotation.Consumer;

public class CrossPlatformListener {

    private Optional<CrossPlatformResource> crossPlatform = Optional.empty();

    private CountDownLatch latch;

    @Consumer(subject = "cross-platform-subject")
    public void onCrossPlatformChanged(CrossPlatformResource crossPlatformResource) {
        crossPlatform = Optional.ofNullable(crossPlatformResource);
        if (latch != null) {
            latch.countDown();
        }
    }

    public Optional<CrossPlatformResource> getCrossPlatform() {
        return crossPlatform;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
