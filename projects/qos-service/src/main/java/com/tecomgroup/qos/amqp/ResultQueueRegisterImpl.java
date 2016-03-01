/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import org.apache.log4j.Logger;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;
import java.util.HashMap;
import java.util.Map;

/**
 * Реализация интерфейса регистратора
 * слушателей очередей результатов
 *
 * @author uvarov.m
 */
@Component
public class ResultQueueRegisterImpl implements ResultQueueRegister {

    private final static Logger LOGGER = Logger
            .getLogger(ResultQueueRegisterImpl.class);

    private ResultQueueFactory resultQueueFactory;

    private final Map<String, SimpleMessageListenerContainer>
            containers = new HashMap<>();

    @Override
    public void register(String agentKey) {
        String queueName = resultQueueFactory.registerQueueForAgent(agentKey);
        updateContainer(queueName);
    }

    @Override
    public void unregister(String agentKey) {
        String queueName = resultQueueFactory.deleteQueueForAgent(agentKey);
        synchronized (containers) {
            SimpleMessageListenerContainer c = containers.remove(queueName);
            if (c != null) {
                c.shutdown();
            }
        }
    }

    private void updateContainer(String queueName) {
        if(queueName == null) {
            return;
        }

        synchronized (containers) {
            if (containers.containsKey(queueName)) {
                SimpleMessageListenerContainer c = containers.get(queueName);
                if (c.getActiveConsumerCount() > 0) {
                    return;
                }
                containers.remove(queueName);
                c.shutdown();
            }
            addListener(queueName);
        }
    }

    private void addListener(String queueName) {
        SimpleMessageListenerContainer c = resultQueueFactory.createQueueListener(queueName);
        containers.put(queueName, c);
        LOGGER.info("Created listeners on result queue: " + queueName + " -> ok" );
    }

    public void setResultQueueFactory(ResultQueueFactory resultQueueFactory) {
        this.resultQueueFactory = resultQueueFactory;
    }
}
