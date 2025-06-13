package com.adaptris.webspheremq.test;

import com.adaptris.testing.DockerComposeFunctionalTest;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.containers.wait.strategy.WaitStrategy;

import java.io.File;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Hashtable;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultFunctionalTest extends DockerComposeFunctionalTest {


    private MQQueueManager queueManager;

    protected static String INTERLOK_SERVICE_NAME = "interlok-1";
    protected static int INTERLOK_PORT = 8080;
    protected static String WEBSPHEREMQ_SERVICE_NAME = "webspheremq-1";
    protected static int WEBSPHEREMQ_PORT = 1414;

    private static final String HOST = "webspheremq-1";

    private final String QUEUE_NAME = "DEV.QUEUE.1";
    private final String QUEUE_MANAGER_NAME = "QM1";
    private final String CHANNEL = "DEV.APP.SVRCONN";

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
        InetSocketAddress address = getHostAddressForService(WEBSPHEREMQ_SERVICE_NAME, WEBSPHEREMQ_PORT);
        if (!path.startsWith("/")) path = "/" + path;
        return "tcp://" + address.getHostString() + ":" + address.getPort() + path;
    }


    @Test
    public void test() throws Exception {
        Thread.sleep(10000);
        InetSocketAddress mgmt = getHostAddressForService(WEBSPHEREMQ_SERVICE_NAME, WEBSPHEREMQ_PORT);

        Hashtable<String, Object> props = new Hashtable<>();
        props.put(MQConstants.TRANSPORT_PROPERTY, MQConstants.TRANSPORT_MQSERIES_CLIENT);
        props.put(MQConstants.HOST_NAME_PROPERTY, mgmt.getHostName());
        props.put(MQConstants.PORT_PROPERTY, String.valueOf(mgmt.getPort()));
        props.put(MQConstants.CHANNEL_PROPERTY, CHANNEL);

        queueManager = new MQQueueManager(QUEUE_MANAGER_NAME, props);

        assertTrue(queueManager.isConnected(), "Should be connected to MQ");

//        Properties mqProperties = new Properties();
//        mqProperties.setProperty("mq.queueManager", QUEUE_NAME);
//        mqProperties.setProperty("mq.host", "localhost");
//        mqProperties.setProperty("mq.port", String.valueOf(WEBSPHEREMQ_PORT));
//        mqProperties.setProperty("mq.channel", "TEST.CHANNEL");
//        mqProperties.setProperty("mq.queue.input", "INPUT.QUEUE");
//        mqProperties.setProperty("mq.queue.output", "OUTPUT.QUEUE");

//        WebSphereMQService mqService = new WebSphereMQService(mqProperties);
    }
}
