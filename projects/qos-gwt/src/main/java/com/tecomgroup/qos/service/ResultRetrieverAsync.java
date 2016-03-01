/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;

/**
 * @author ivlev.e
 * 
 */
public interface ResultRetrieverAsync {

	void deserializeBean(String beanPayload,
			AsyncCallback<ExportResultsWrapper> callback);

	void getLastResults(Map<String, Collection<?>> taskParameters,
			Long startPosition, Long size, OrderType orderType,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getResults(Map<String, Collection<?>> taskParameters,
			Long aggregationStep, TimeInterval timeInterval,
			Long startPosition, Long size, OrderType orderType,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getResults(Map<String, Collection<?>> taskParameters,
			Long aggregationStep, TimeInterval timeInterval,
			Long startPosition, Long size, OrderType orderType,
			boolean addStartOfData, boolean addEndOfData,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getResults(String taskKey, Collection<?> parameters,
			Long aggregationStep, TimeInterval timeInterval,
			Long startPosition, Long size, OrderType orderType,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getResults(String taskKey, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType,
			AsyncCallback<List<Map<String, Object>>> callback);

	void getResults(String taskKey, ParameterIdentifier parameterIdentifier,
			TimeInterval timeInterval, OrderType orderType,
			AsyncCallback<Map<Date, Double>> callback);

	void getResultsByAggregationStep(String taskKey,
			ParameterIdentifier parameterIdentifier, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType, AsyncCallback<Map<Date, Double>> callback);

	void getResultsUsingAdaptiveAggregationStep(String taskKey,
			ParameterIdentifier parameterIdentifier, TimeInterval timeInterval,
			OrderType orderType, AsyncCallback<Map<Date, Double>> callback);

	void getTotalResultCount(String taskKey, Long aggregationStep,
			TimeInterval timeInterval, AsyncCallback<Integer> callback);

	void getTotalResultsCount(Map<String, Collection<?>> taskParameters,
			Long aggregationStep, TimeInterval timeInterval,
			AsyncCallback<Integer> callback);

	void serializeBean(ExportResultsWrapper bean, AsyncCallback<String> callback);
}
