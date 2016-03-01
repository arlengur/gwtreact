/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.json;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.KeyDeserializer;

/**
 * @author kunilov.p
 * 
 */
public class SourceKeyDeserializer extends KeyDeserializer {

	private static Object fromString(final String s) throws IOException,
			ClassNotFoundException {
		final byte[] data = Base64.decodeBase64(s);
		final ObjectInputStream ois = new ObjectInputStream(
				new ByteArrayInputStream(data));
		final Object o = ois.readObject();
		ois.close();
		return o;
	}

	@Override
	public Object deserializeKey(final String paramString,
			final DeserializationContext paramDeserializationContext)
			throws IOException, JsonProcessingException {
		Object result = paramString;
		try {
			if (paramString.startsWith("objectKey=")) {
				final String[] paramParts = paramString.split("=");
				result = fromString(paramParts[1]);
			}
		} catch (final Exception ex) {
			result = paramString;
		}
		return result;
	}
}
