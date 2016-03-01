package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MSource;

/**
 * Created by uvarov.m on 28.05.2015.
 */
public interface InternalSourceService extends SourceService{
    /**
     * Get source customized with class
     *
     * @param clazz
     * @param sourceKey
     * @return {@link MSource}
     */
    <M extends MSource> M getDomainSource(Class<M> clazz,  String sourceKey);
}
