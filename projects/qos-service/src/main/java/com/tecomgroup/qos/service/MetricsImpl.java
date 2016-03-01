package com.tecomgroup.qos.service;

/**
 * Created by uvarov.m on 13.11.2015.
 */

import com.codahale.metrics.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetricsImpl implements Metrics {

    private static final Logger log = LoggerFactory.getLogger(MetricsImpl.class);

    private class Metrics {

        private final MetricRegistry registry;
        private final JmxReporter reporter;

        public Metrics(String domain) {
            this.registry = new MetricRegistry();
            this.reporter = JmxReporter.forRegistry(registry).inDomain(domain).build();
        }

        public void start() {
            reporter.start();
        }

        public void stop() {
            reporter.stop();
        }

        public Timer timer(/*Class<?> klass,*/ String name) {
            return registry.timer(name);
        }

        public Counter counter(/*Class<?> klass,*/ String name) {
            return registry.counter(name);
        }

        public <T> void gauge(/*Class<?> klass,*/ String name, Gauge<T> gauge) {
            registry.register(name, gauge);
        }
    }

    private final ConcurrentHashMap<String, Metrics> metrics = new ConcurrentHashMap<>();

    public MetricsImpl() {
    }

    public void start() {
        // do nothing
    }

    public void stop() {
        for(Map.Entry<String, Metrics> e : metrics.entrySet()) {
            e.getValue().stop();
        }
        log.info("stop -> done");
    }

    @Override
    public Timer timer(Class<?> klass, String name) {
        return findMetricsFor(klass).timer(name);
    }

    @Override
    public Counter counter(Class<?> klass, String name) {
        return findMetricsFor(klass).counter(name);
    }

    @Override
    public <T> void gauge(Class<?> klass, String name, Gauge<T> gauge) {
        findMetricsFor(klass).gauge(name, gauge);
    }

    private Metrics findMetricsFor(Class<?> klass) {
        String domain = getDomain(klass);
        Metrics m = new Metrics(domain);

        Metrics old = metrics.putIfAbsent(domain, m);
        if (old == null) {
            m.start();
            return m;
        }

        return old;
    }

    private String getDomain(Class<?> klass) {
        if(klass.isAnnotationPresent(MetricsDomain.class)) {
            return klass.getAnnotation(MetricsDomain.class).name();
        } else {
            return "metrics-" + klass.getSimpleName();
        }
    }
}
