/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/resultService")
public interface ResultRetriever extends Service, RemoteService {

	/**
	 * Deserializes POJO bean to JSON with server side serializer
	 * 
	 * @param beanPayload
	 * @return
	 * @throws ServiceException
	 */
	ExportResultsWrapper deserializeBean(String beanPayload);

	/**
	 * Get last results of provided tasks and its parameters.
	 * 
	 * @param taskParameters
	 *            Collection of {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier} or Strings
	 *            {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier#createParameterStorageKey()}. Can
	 *            be null then data for all task parameters will be returned.
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @return List<Map<taskStorageKey={@link
	 *         ParameterIdentifier.createTaskStorageKey(taskKey)}, value>>
	 */
	List<Map<String, Object>> getLastResults(
			Map<String, Collection<?>> taskParameters, Long startPosition,
			Long size, OrderType orderType);

	/**
	 * Gets results for provided task and collection of its parameters. If
	 * parameters is null or is empty, then data for all task parameters will be
	 * returned.
	 * 
	 * @param taskParameters
	 *            Collection of {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier} or Strings
	 *            {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier#createParameterStorageKey()}.
	 * @param aggregationStep
	 * @param timeInterval
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @return List<Map<taskStorageKey={@link
	 *         ParameterIdentifier.createTaskStorageKey(taskKey)}, value>>
	 * @throws IllegalArgumentException
	 *             if Map<String, Collection<?>> contains something different
	 *             from collection of {@link ParameterIdentifier} or Strings
	 *             {@link ParameterIdentifier#createParameterStorageKey()}.
	 */
	List<Map<String, Object>> getResults(
			Map<String, Collection<?>> taskParameters, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType);

	/**
	 * If parameters is null or is empty, then data for all task parameters will
	 * be returned.
	 * 
	 * @param aggregationStep
	 * @param timeInterval
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @param addStartOfData
	 *            whether or not add start of data entry.
	 * @param addEndOfData
	 *            whether or not add end of data entry.
	 * @return List<Map<taskStorageKey={@link
	 *         ParameterIdentifier.createTaskStorageKey(taskKey)}, value>>
	 */
	List<Map<String, Object>> getResults(
			Map<String, Collection<?>> taskParameters, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType, boolean addStartOfData, boolean addEndOfData);

	/**
	 * If parameters is null or is empty, then data for all task parameters will
	 * be returned.
	 * 
	 * @param taskKey
	 * @param parameters
	 *            Collection of {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier} or Strings
	 *            {@link com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier#createParameterStorageKey()}. Can
	 *            be null then data for all task parameters will be returned.
	 * @param aggregationStep
	 * @param timeInterval
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @return List<Map<taskStorageKey={@link
	 *         ParameterIdentifier.createTaskStorageKey(taskKey)}, value>>
	 */
	List<Map<String, Object>> getResults(String taskKey,
			Collection<?> parameters, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType);

	/**
	 * 
	 * @param taskKey
	 * @param aggregationStep
	 * @param timeInterval
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @return List<Map<taskStorageKey={@link
	 *         ParameterIdentifier.createTaskStorageKey(taskKey)}, value>>
	 */
	List<Map<String, Object>> getResults(String taskKey, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType);

	/**
	 * 
	 * @param taskKey
	 * @param parameterIdentifier
	 * @param timeInterval
	 * @param orderType
	 * @return
	 */
	Map<Date, Double> getResults(String taskKey,
			ParameterIdentifier parameterIdentifier, TimeInterval timeInterval,
			OrderType orderType);

	/**
	 * 
	 * @param taskKey
	 * @param parameterIdentifier
	 * @param aggregationStep
	 * @param timeInterval
	 * @param startPosition
	 * @param size
	 * @param orderType
	 * @return
	 */
	Map<Date, Double> getResultsByAggregationStep(String taskKey,
			ParameterIdentifier parameterIdentifier, Long aggregationStep,
			TimeInterval timeInterval, Long startPosition, Long size,
			OrderType orderType);

	/**
	 * 
	 * @param taskKey
	 * @param parameterIdentifier
	 * @param timeInterval
	 * @param orderType
	 * @return
	 */
	Map<Date, Double> getResultsUsingAdaptiveAggregationStep(String taskKey,
			ParameterIdentifier parameterIdentifier, TimeInterval timeInterval,
			OrderType orderType);

	/**
	 * 
	 * @param taskKey
	 * @param timeInterval
	 * @return
	 */
	Integer getTotalResultCount(String taskKey, Long aggregationStep,
			TimeInterval timeInterval);

	/**
	 * 
	 * @param taskParameters
	 *            Collection of {@link ParameterIdentifier} or Strings
	 *            {@link ParameterIdentifier#createParameterStorageKey()}. Can
	 *            be null then data for all task parameters will be returned.
	 * @param aggregationStep
	 * @param timeInterval
	 * @return
	 */
	Integer getTotalResultsCount(Map<String, Collection<?>> taskParameters,
			Long aggregationStep, TimeInterval timeInterval);

	/**
	 * Serializes POJO bean to JSON with server side serializer
	 * 
	 * @param bean
	 * @return
	 * @throws ServiceException
	 */
	String serializeBean(ExportResultsWrapper bean);
}
