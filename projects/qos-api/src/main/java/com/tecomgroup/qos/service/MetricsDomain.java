package com.tecomgroup.qos.service;

/**
 * Created by uvarov.m on 13.11.2015.
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface MetricsDomain {

    String name();
}