/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.amqp;

import com.tecomgroup.qos.communication.result.Result;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.support.converter.MessageConverter;

import com.tecomgroup.qos.communication.handler.ResultHandler;
import com.tecomgroup.qos.communication.handler.ResultHandler.Interval;
import com.tecomgroup.qos.communication.message.ResultMessage;
import com.tecomgroup.qos.communication.message.ResultMessage.ResultType;
import com.tecomgroup.qos.communication.response.RequestResponse;
import com.tecomgroup.qos.exception.UnsupportedResultTypeException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author kunilov.p
 * 
 */
public class ResultMessageListener extends QoSMessageListener<ResultMessage> {
	private final static Logger LOGGER = Logger.getLogger(ResultMessageListener.class);
	private final ResultHandler handler;

	public ResultMessageListener(final AmqpTemplate amqpTemplate,
								 final MessageConverter messageConverter,
								 final ResultHandler handler) {
		super();
		setAmqpTemplate(amqpTemplate);
		setMessageConverter(messageConverter);
		setEnabled(true);
		this.handler = handler;
	}

	private void handleIntervalResult(final ResultMessage resultMessage) {
		if (resultMessage.getResults().size() < 2) {
			throw new IllegalArgumentException(
					"IntervalResult message should contain two or more results");
		}
		final Iterator<Result> results = resultMessage.getResults().iterator();
		Result currentResult = null;
		Result nextResult = results.next();
		List<Interval> intervals = new ArrayList<>();
		while (results.hasNext()) {
			currentResult = nextResult;
			nextResult = results.next();
			intervals.add(new Interval(currentResult, nextResult));
		}
		handler.handleIntervalResult(resultMessage.getTaskKey(), intervals);
	}

	@Override
	public RequestResponse handleQosMessage(final ResultMessage resultMessage) {
		RequestResponse response = null;
		try {
			final ResultType resultType = resultMessage.getResultType();
			switch (resultType) {
				case SINGLE_VALUE_RESULT :
					handler.handleSingleValueResult(resultMessage.getTaskKey(), resultMessage.getResults());
					break;
				case INTERVAL_RESULT :
					handleIntervalResult(resultMessage);
					break;
				default :
					throw new UnsupportedResultTypeException("ResultType: "	+ resultType);
			}
			response = new RequestResponse();
		} catch (final Exception ex) {
			LOGGER.error("Cannot handle message: " + resultMessage, ex);
			response = new RequestResponse(ex);
		}
		return response;
	}
}
