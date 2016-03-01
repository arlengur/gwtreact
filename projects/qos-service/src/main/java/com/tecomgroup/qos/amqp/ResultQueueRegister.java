/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

/**
 * Интерфейс регистратора для
 * слушателей результатов
 * @author uvarov.m
 */
public interface ResultQueueRegister {

    void register(String agentKey);
    void unregister(String queueName);
}
