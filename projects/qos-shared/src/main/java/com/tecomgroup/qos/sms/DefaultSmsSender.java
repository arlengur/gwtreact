/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.sms;

import com.tecomgroup.qos.AbstractSender;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements notifications delivery by SMS
 * 
 * @author ivlev.e
 */
public class DefaultSmsSender implements AbstractSender {

	private final static Logger LOGGER = Logger
			.getLogger(DefaultSmsSender.class);

	@Value("${sms.provider.app.id}")
	private String smsProviderAppId;

	@Value("${sms.provider.username}")
	private String smsProviderUsername;

	/**
	 * Max amount of concatenated sms messages. 0 < concat <= 35
	 */
	@Value("${sms.provider.concat}")
	private String smsProviderConcat;

	@Value("${sms.provider.http.method}")
	private String httpMethod;

	@Value("${sms.provider.password}")
	private String smsProviderPassword;

	@Value("${sms.provider.http.api.sendmsg.url}")
	private String smsProviderHttpApiSendmsgUrl;

	private static final String VELOCITY_LOG_TAG = "TemplateSmsMessage";

	@Autowired
	private VelocityEngine velocityEngine;

	private Status checkResultStatus(final String result) {
		Status status = Status.SUCCESS;
		if (result.contains("ERR")) {
			status = Status.INCORRECT_INPUT;
		}
		return status;
	}

	/**
	 * Returns string, containing hex unicode code points of every character of
	 * the provided string
	 */
	private String convertStringToCodePoints(final String text) {
		final StringBuilder builder = new StringBuilder();
		for (final char ch : text.toCharArray()) {
			builder.append(String.format("%04x", (int) ch));
		}
		return builder.toString();
	}

	private String formatNumber(final String phoneNumber) {
		// removes all allowed symbols ("+", "-", "(", ")", " ") except digits,
		return phoneNumber.trim().replaceAll("(\\+|-|\\(|\\)| )*", "");
	}

	private String processTemplate(final Context context, final String template) {
		final StringWriter output = new StringWriter();
		velocityEngine.evaluate(context, output, VELOCITY_LOG_TAG, template);
		return output.toString();
	}

	private Status sendSms(final Collection<String> contacts,
			final String processedBody, final boolean unicode) {
		Status status = Status.SUCCESS;
		String textToSend = processedBody;

		if (unicode) {
			textToSend = convertStringToCodePoints(processedBody);
		}
		final Map<String, Object> urlVariables = new HashMap<>();
		try {
			urlVariables.put("user", smsProviderUsername);
			urlVariables.put("password",smsProviderPassword);
			urlVariables.put("api_id", smsProviderAppId);
			urlVariables.put("text", URLEncoder.encode(textToSend, "UTF-8"));
			urlVariables.put("concat", smsProviderConcat);
			urlVariables.put("unicode", unicode ? "1" : "0");
		} catch (final Exception e) {
			LOGGER.error("Unable to parse SMS parameters",e);
			return  Status.INCORRECT_INPUT;
		}
		for (final String contact : contacts) {
			if (contact != null) {
				final String phoneNumber = formatNumber(contact);

				try {
					urlVariables.put("to", phoneNumber);
					String requestString=StrSubstitutor.replace(smsProviderHttpApiSendmsgUrl,urlVariables,"{","}");
					sendHttpRequest(requestString);
					LOGGER.info("SMS has been successfully sent to "+ phoneNumber);
				} catch (final Exception e) {
					status = Status.INCORRECT_INPUT;
					LOGGER.error("Unable to send SMS",e);
				}
			} else {
				LOGGER.warn("Trying to send message to null phone number");
			}
		}
		return status;
	}

	private void sendHttpRequest(String requestString) throws IOException {
		CloseableHttpClient httpClient = null;
		CloseableHttpResponse response=null;
		try {
			httpClient = HttpClients.createDefault();
			HttpRequestBase method = null;
			if (HttpGet.METHOD_NAME.equals(httpMethod)) {
				method = new HttpGet(requestString);
			} else if (HttpPut.METHOD_NAME.equals(httpMethod)) {
				method = new HttpPut(requestString);
			} else {
				method = new HttpPost(requestString);
			}
			response = httpClient.execute(method);
			LOGGER.debug("Send request to sms gate :"+method.toString());
			StatusLine status = response.getStatusLine();
			if (status.getStatusCode() >= HttpStatus.SC_MULTIPLE_CHOICES) {
				throw new HttpResponseException(status.getStatusCode(), status.getReasonPhrase());
			} else {
				LOGGER.info("SMS request success , response code: " + status.getStatusCode());
				if (LOGGER.isDebugEnabled()) {
					BufferedReader br = new BufferedReader(
							new InputStreamReader((response.getEntity().getContent())));
					String output;
					LOGGER.debug("SMS send responce : ");
					while ((output = br.readLine()) != null) {
						LOGGER.debug(output);
					}
				}
			}
		}finally {
			IOUtils.closeQuietly(response);
			IOUtils.closeQuietly(httpClient);
		}
	}


	@Override
	public Status sendTemplatedMessage(final Collection<String> contacts,
			final String subject, final String body,
			final Map<String, Object> templateParameters) {
		final VelocityContext context = new VelocityContext(templateParameters);
		String processedBody = null;
		try {
			processedBody = processTemplate(context, body);
		} catch (final Exception e) {
			LOGGER.warn("Failed to process SMS template. ", e);
			return Status.INCORRECT_INPUT;
		}
		final Boolean unicode = (Boolean) templateParameters
				.get(UNICODE_TEXT_PARAMETER_NAME);
		return sendSms(contacts, processedBody, unicode == null
				? false
				: unicode);
	}

}
