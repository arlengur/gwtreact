/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.type.TypeFactory;

/**
 * @author sviyazov.a
 * 
 */
public class ToolsUtil {

	public static <T> T parseListFromJsonFile(final InputStream inputStream,
			final Class clazz) throws JsonProcessingException, IOException {

		final ObjectMapper mapper = new ObjectMapper();
		try (JsonParser jp = new JsonFactory().createJsonParser(inputStream)) {
			final TypeFactory typeFactory = TypeFactory.defaultInstance();
			return mapper.reader()
					.readValue(
							jp,
							typeFactory.constructCollectionType(
									ArrayList.class, clazz));
		}

	}

}
