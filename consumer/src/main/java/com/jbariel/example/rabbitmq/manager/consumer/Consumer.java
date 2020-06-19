package com.jbariel.example.rabbitmq.manager.consumer;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownSignalException;
import com.rabbitmq.client.AMQP.BasicProperties;

public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    private static final String EXCHANGE_TYPE = "direct";
    private static final boolean EXCHANGE_DURABLE = false;
    private static final boolean EXCHANGE_AUTO_DELETE = true;
    private static final Map<String, Object> EXCHANGE_ARGS = null;

    private static final boolean QUEUE_DURABLE = false;
    private static final boolean QUEUE_EXCLUSIVE = false;
    private static final boolean QUEUE_AUTO_DELETE = true;
    private static final Map<String, Object> QUEUE_ARGS = null;

    private static final String URI = System.getenv("RMQ_URI");
    private static final String EXCHANGE_NAME = System.getenv("RMQ_EXCHANGE_NAME");
    private static final String QUEUE_NAME = System.getenv("RMQ_QUEUE_NAME");
    private static final int INIT_DELAY = NumberUtils.toInt(System.getenv("RMQ_INIT_DELAY"), 1);

    private static final String ROUTING_KEY = "routingKey" + QUEUE_NAME + EXCHANGE_NAME;

    private static final boolean AUTO_ACK = false;

    private static final String ENC = StandardCharsets.UTF_8.name();

    public static void main(String[] args) {
        LOG.info("Starting the Consumer...");

        try {
            LOG.info("Waiting " + INIT_DELAY + "s...");
            Thread.sleep(INIT_DELAY * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }

        try {
            final ConnectionFactory factory = new ConnectionFactory();
            LOG.info("Connecting on " + URI);
            factory.setUri(URI);
            final Connection conn = factory.newConnection();
            final Channel channel = conn.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, EXCHANGE_TYPE, EXCHANGE_DURABLE, EXCHANGE_AUTO_DELETE,
                    EXCHANGE_ARGS);
            channel.queueDeclare(QUEUE_NAME, QUEUE_DURABLE, QUEUE_EXCLUSIVE, QUEUE_AUTO_DELETE, QUEUE_ARGS);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            channel.basicConsume(QUEUE_NAME, AUTO_ACK, new com.rabbitmq.client.Consumer() {

                @Override
                public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void handleRecoverOk(String consumerTag) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, BasicProperties properties,
                        byte[] body) throws IOException {
                    LOG.info("Got message: " + new String(body, ENC));
                    channel.basicAck(envelope.getDeliveryTag(), false);
                }

                @Override
                public void handleConsumeOk(String consumerTag) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void handleCancelOk(String consumerTag) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void handleCancel(String consumerTag) throws IOException {
                    // TODO Auto-generated method stub

                }
            });

            LOG.info("Press 'ENTER' to continue...");
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }
}