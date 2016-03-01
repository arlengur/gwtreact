/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.broker.federation;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * HTTP requester. Now supports only PUT method
 * 
 * @author novohatskiy.r
 * 
 */
public class Requester {

	private final static Log LOGGER = LogFactory.getLog(Requester.class);

	public static final String URI = "uri";
	public static final String BASIC_AUTH_USERNAME = "basic.auth.username";
	public static final String BASIC_AUTH_PASSWORD = "basic.auth.password";
	public static final String METHOD = "method";
	public static final String CONTENT_TYPE = "content-type";
	public static final String ENTITY = "entity";

	public static final String USER_AGENT = "Mozilla/5.0";

	private List<Map<String, String>> requests;

	public Requester(List<Map<String, String>> requests) {
		this.requests = requests;
	}

	/**
	 * Starts to perform HTTP requests defined in application context
	 */
	public void start() {
		LOGGER.info("Configure federation on oposite host.");
		for (Map<String, String> requestParams : requests) {
			LOGGER.info("New Put Request. Params:");
			for (final Map.Entry<String, String> param : requestParams
					.entrySet()) {
				LOGGER.info(param.getKey() + " = " + param.getValue());
			}
			if (requestParams.get(METHOD).equals("PUT")) {
				try {
					performPUT(requestParams);
				} catch (IOException e) {
					LOGGER.error("Failed to perform PUT request", e);
				}
			}
		}
	}

	private void performPUT(Map<String, String> requestParams)
			throws IOException {
		HttpClient client = new DefaultHttpClient();
		HttpPut put = new HttpPut(requestParams.get(URI));

		put.setHeader("User-Agent", USER_AGENT);
		put.setHeader("Content-type", requestParams.get(CONTENT_TYPE));
		UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				requestParams.get(BASIC_AUTH_USERNAME),
				requestParams.get(BASIC_AUTH_PASSWORD));
		put.addHeader(BasicScheme.authenticate(creds, "US-ASCII", false));
		put.setEntity(new StringEntity(requestParams.get(ENTITY)));

		client.execute(put);
	}

}
