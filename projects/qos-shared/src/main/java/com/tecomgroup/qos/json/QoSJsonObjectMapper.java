/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.json;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerProvider;


/**
 * @author kunilov.p
 * 
 */
public class QoSJsonObjectMapper extends ObjectMapper {
	
	private void initialize() {
		configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		registerModule(new SourceModule());
	}

	public QoSJsonObjectMapper() {
		super();
		initialize();
	}

	/**
	 * @param jf
	 */
	public QoSJsonObjectMapper(final JsonFactory jf) {
		super(jf);
		initialize();
	}

	/**
	 * @param jf
	 * @param sp
	 * @param dp
	 */
	public QoSJsonObjectMapper(final JsonFactory jf,
			final SerializerProvider sp, final DeserializerProvider dp) {
		super(jf, sp, dp);
		initialize();
	}

	/**
	 * @param jf
	 * @param sp
	 * @param dp
	 * @param sconfig
	 * @param dconfig
	 */
	public QoSJsonObjectMapper(final JsonFactory jf,
			final SerializerProvider sp, final DeserializerProvider dp,
			final SerializationConfig sconfig,
			final DeserializationConfig dconfig) {
		super(jf, sp, dp, sconfig, dconfig);
		initialize();
	}
}
