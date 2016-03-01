/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.json;

import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.module.SimpleModule;

import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
public class SourceModule extends SimpleModule {

	public SourceModule() {
		this("DefaultSourceModule", Version.unknownVersion());
	}

	/**
	 * @param name
	 * @param version
	 */
	public SourceModule(final String name, final Version version) {
		super(name, version);
		addKeySerializer(Source.class, new SourceKeySerializer());
		addKeyDeserializer(Source.class, new SourceKeyDeserializer());
	}
}
