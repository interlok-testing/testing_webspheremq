package com.adaptris.webspheremq.test;

import com.adaptris.testing.DockerComposeFunctionalTest;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Properties;

public class DefaultFunctionalTest extends DockerComposeFunctionalTest {

    protected static String INTERLOK_SERVICE_NAME = "interlok-1";
    protected static int INTERLOK_PORT = 8080;
    protected static String WEBSPHEREMQ_SERVICE_NAME = "webspheremq-1";
    protected static int WEBSPHEREMQ_PORT = 1414;

    private static final String QUEUE_NAME = "TEST.SEND.QUEUE";
    private static final String TEST_MESSAGE = "Hello WebSphere MQ!";
    private static final String XML_MESSAGE = "<message><id>123</id><content>Test XML</content></message>";

    protected static WaitStrategy defaultWaitStrategy = Wait.forListeningPort().withStartupTimeout(Duration.ofSeconds(30));

    @Override
    protected ComposeContainer setupContainers() throws Exception {
        return new ComposeContainer(new File("docker-compose.yml"))
                .withExposedService(INTERLOK_SERVICE_NAME, INTERLOK_PORT, defaultWaitStrategy)
                .withExposedService(WEBSPHEREMQ_SERVICE_NAME, WEBSPHEREMQ_PORT, defaultWaitStrategy);
    }

    protected String getInterlokEndpoint(String path) {
        InetSocketAddress address = getHostAddressForService(INTERLOK_SERVICE_NAME, INTERLOK_PORT);
        if (!path.startsWith("/")) path = "/" + path;
        return "http://" + address.getHostString() + ":" + address.getPort() + path;
    }

    @Test
    public void test() throws Exception {
        Thread.sleep(10000);
        InetSocketAddress address = getHostAddressForService(INTERLOK_SERVICE_NAME, INTERLOK_PORT);
        String bootstrapServers = address.getHostString() + ":" + WEBSPHEREMQ_PORT;

        Properties mqProperties = new Properties();
        mqProperties.setProperty("mq.queueManager", QUEUE_NAME);
        mqProperties.setProperty("mq.host", "localhost");
        mqProperties.setProperty("mq.port", String.valueOf(WEBSPHEREMQ_PORT));
        mqProperties.setProperty("mq.channel", "TEST.CHANNEL");
        mqProperties.setProperty("mq.queue.input", "INPUT.QUEUE");
        mqProperties.setProperty("mq.queue.output", "OUTPUT.QUEUE");

//        WebSphereMQService mqService = new WebSphereMQService(mqProperties);
    }
}
