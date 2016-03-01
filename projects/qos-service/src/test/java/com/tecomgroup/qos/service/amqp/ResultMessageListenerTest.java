/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.amqp;

import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.communication.result.Result.ResultIdentifier;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.service.ResultService;
import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.support.converter.JsonMessageConverter;

import java.util.Map;
import java.util.SortedMap;

/**
 * @author abondin
 * 
 */
public class ResultMessageListenerTest {

	@SuppressWarnings("unused")
	private MessageProperties getMessageProperties() {
		final MessageProperties messageProperties = new MessageProperties();
		messageProperties.setHeader("__TypeId__",
				"com.tecomgroup.qos.communication.ResultMessage");
		messageProperties.setContentType("application/json");
		return messageProperties;
	}

	@SuppressWarnings("unchecked")
	@Test
	public void sendResult() {
		new JsonMessageConverter();
		final ResultService resultServiceMock = EasyMock
				.createStrictMock(ResultService.class);

		resultServiceMock.addResults(EasyMock.anyObject(MAgentTask.class),
				(SortedMap<ResultIdentifier, Result>) EasyMock.anyObject());

		EasyMock.expectLastCall().andStubAnswer(new IAnswer<Object>() {
			@Override
			public Object answer() throws Throwable {
				final Object[] args = EasyMock.getCurrentArguments();
				Assert.assertNotNull(args[0]);
				Assert.assertNotNull(args[1]);
				Assert.assertNotNull(args[2]);
				Assert.assertEquals("384", args[0]);
				Assert.assertEquals(4, ((Map<String, Object>) args[2]).size());
				return null;
			}
		});

		EasyMock.replay(resultServiceMock);

		final StringBuilder body = new StringBuilder();
		body.append("{\n");
		body.append("  \"taskKey\" : \"384\",\n");
		body.append("  \"resultDateTime\" : \"20130102030405\",\n");
		body.append("  \"parameters\" : {\n");
		body.append("		\"signalLevel\" : \"10.0\",\n");
		body.append("       \"signalNoise\" : \"90.0\",\n");
		body.append("       \"videoAudio\" : \"5\",\n");
		body.append("       \"nicamLevel\" : \"-1\"\n");
		body.append("  }\n");
		body.append("}");
		// final Message message = new Message(body.toString().getBytes(),
		// getMessageProperties());

		EasyMock.verify(resultServiceMock);

	}
}
