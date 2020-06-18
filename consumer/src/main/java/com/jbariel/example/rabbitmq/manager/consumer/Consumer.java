package com.jbariel.example.rabbitmq.manager.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Consumer {

    private static final Logger LOG = LoggerFactory.getLogger(Consumer.class);

    public static void main(String[] args) {
        LOG.info("Hi from the Consumer...");
    }
}