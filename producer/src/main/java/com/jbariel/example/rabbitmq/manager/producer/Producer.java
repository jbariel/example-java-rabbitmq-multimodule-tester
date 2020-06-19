package com.jbariel.example.rabbitmq.manager.producer;

import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Producer {

    private static final Logger LOG = LoggerFactory.getLogger(Producer.class);

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

    private static final String ENC = StandardCharsets.UTF_8.name();

    public static final ScheduledThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(10);

    public static void main(String[] args) {
        LOG.info("Starting the Producer...");

        try {
            LOG.info("Waiting " + INIT_DELAY + "s...");
            Thread.sleep(INIT_DELAY * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }

        ScheduledFuture<?> future = null;
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

            future = THREAD_POOL.scheduleAtFixedRate(new Runnable() {

                @Override
                public void run() {
                    try {
                        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null,
                                ZonedDateTime.now().toString().getBytes(ENC));
                    } catch (IOException e) {
                        LOG.error(e.getLocalizedMessage(), e);
                    }
                }
            }, 1L, 1L, TimeUnit.SECONDS);

        } catch (Exception e) {
            LOG.error(e.getLocalizedMessage(), e);
        }

        LOG.info("Press 'ENTER' to continue...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null != future) {
            future.cancel(false);
            try {
                Thread.sleep(1 * 1000L);
            } catch (InterruptedException e) {
                // ignore
            }
        }
    }
}