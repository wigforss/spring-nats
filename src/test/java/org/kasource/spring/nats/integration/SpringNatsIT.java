package org.kasource.spring.nats.integration;

import java.io.File;

import org.springframework.util.SocketUtils;

import org.kasource.spring.nats.integration.avro.NatsAvroJavaIntegration;
import org.kasource.spring.nats.integration.avro.NatsAvroXmlIntegration;
import org.kasource.spring.nats.integration.custom.NatsCustomJavaIntegration;
import org.kasource.spring.nats.integration.custom.NatsCustomXmlIntegration;
import org.kasource.spring.nats.integration.java.NatsJavaSerDeJavaIntegration;
import org.kasource.spring.nats.integration.java.NatsJavaSerDeXmlIntegration;
import org.kasource.spring.nats.integration.json.NatsGsonJavaIntegration;
import org.kasource.spring.nats.integration.json.NatsGsonXmlIntegration;
import org.kasource.spring.nats.integration.json.NatsJacksonJavaIntegration;
import org.kasource.spring.nats.integration.json.NatsJacksonXmlIntegration;
import org.kasource.spring.nats.integration.kryo.NatsKryoJavaIntegration;
import org.kasource.spring.nats.integration.kryo.NatsKryoXmlIntegration;
import org.kasource.spring.nats.integration.proto.NatsProtoJavaIntegration;
import org.kasource.spring.nats.integration.proto.NatsProtoXmlIntegration;
import org.kasource.spring.nats.integration.thrift.NatsThriftJavaIntegration;
import org.kasource.spring.nats.integration.thrift.NatsThriftXmlIntegration;
import org.kasource.spring.nats.integration.xml.NatsJaxbJavaIntegration;
import org.kasource.spring.nats.integration.xml.NatsJaxbXmlIntegration;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;


@RunWith(Suite.class)
@Suite.SuiteClasses({
                            NatsJacksonJavaIntegration.class,
                            NatsJacksonXmlIntegration.class,
                            NatsGsonXmlIntegration.class,
                            NatsGsonJavaIntegration.class,
                            NatsKryoJavaIntegration.class,
                            NatsKryoXmlIntegration.class,
                            NatsJavaSerDeJavaIntegration.class,
                            NatsJavaSerDeXmlIntegration.class,
                            NatsAvroXmlIntegration.class,
                            NatsAvroJavaIntegration.class,
                            NatsProtoXmlIntegration.class,
                            NatsProtoJavaIntegration.class,
                            NatsThriftJavaIntegration.class,
                            NatsThriftXmlIntegration.class,
                            NatsJaxbXmlIntegration.class,
                            NatsJaxbJavaIntegration.class,
                            NatsCustomXmlIntegration.class,
                            NatsCustomJavaIntegration.class
                    })
public class SpringNatsIT {
    public static final Integer NATS_PORT = SocketUtils.findAvailableTcpPort(10000);
    public static final Integer NATS_MONITOR_PORT = SocketUtils.findAvailableTcpPort(10000);


    @ClassRule
    public static DockerComposeContainer dockerComposeContainer =
            new DockerComposeContainer(new File("src/test/resources/docker/docker-compose.yml"))
                    .withEnv("NATS_PORT", NATS_PORT.toString())
                    .withEnv("NATS_MONITOR_PORT", NATS_MONITOR_PORT.toString())
                    .waitingFor("nats", Wait.forListeningPort())
                    .waitingFor("nats", Wait.forLogMessage(".*Server is ready.*\\n", 1));

    @BeforeClass
    public static void setupPorts() {
        System.setProperty("NATS_PORT", NATS_PORT.toString());
        System.setProperty("NATS_MONITOR_PORT", NATS_MONITOR_PORT.toString());
        System.out.println("\n\n##################################");
        System.out.println("Starting NATS with port " + NATS_PORT + " and monitoring port " + NATS_MONITOR_PORT);
        System.out.println("##################################\n\n");
    }

}
