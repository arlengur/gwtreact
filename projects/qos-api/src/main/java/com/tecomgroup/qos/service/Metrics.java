package com.tecomgroup.qos.service;

/**
 * Created by uvarov.m on 13.11.2015.
 */
import com.codahale.metrics.Counter;
import com.codahale.metrics.Gauge;
import com.codahale.metrics.Timer;

public interface Metrics {

    Timer timer(Class<?> klass, String name);

    Counter counter(Class<?> klass, String name);

    <T> void gauge(Class<?> klass, String name, Gauge<T> gauge);
}
