package org.kasource.spring.nats.integration.thrift;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.kasource.spring.nats.NatsTemplate;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:thrift/application-context.xml")
@DirtiesContext
public class NatsThriftXmlIntegration {

    @Autowired
    private NatsTemplate template;

    @Autowired
    private CrossPlatformListener crossPlatformListener;

    @Test
    public void thriftSerde() throws InterruptedException {
        String name = "name";
        int id = 3;

        CountDownLatch latch = new CountDownLatch(1);
        crossPlatformListener.setLatch(latch);

        CrossPlatformResource crossPlatformResource = new CrossPlatformResource(id, name);

        template.publish(crossPlatformResource, "cross-platform-subject");

        assertThat("Timed out waiting for message", latch.await(1, TimeUnit.SECONDS), is(true));
        assertThat(crossPlatformListener.getCrossPlatform().isPresent(), is(true));
        assertThat(crossPlatformListener.getCrossPlatform().get().getName(), equalTo(name));
        assertThat(crossPlatformListener.getCrossPlatform().get().getId(), equalTo(id));
    }
}
