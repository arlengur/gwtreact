/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.json;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;

import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
public class SourceKeySerializer extends JsonSerializer<Source> {

	private static String toString(final Serializable o) throws IOException {
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.close();
		return new String(Base64.encodeBase64(baos.toByteArray()));
	}

	@Override
	public void serialize(final Source source,
			final JsonGenerator jsonGenerator,
			final SerializerProvider serializerProvider) throws IOException,
			JsonProcessingException {
		jsonGenerator.writeFieldName("objectKey=" + toString(source));
	}
}
