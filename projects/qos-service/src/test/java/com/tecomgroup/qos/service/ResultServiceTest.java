/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.communication.result.Result.ResultIdentifier;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ResultServiceTest {

	@Autowired
	private ResultService resultService;

	@Autowired
	private StorageService storageService;

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private InternalTaskService taskService;

	@Value("${stored.days.count}")
	private int storedDaysCount;

	private MAgentTask agentTask = null;
	private MAgentModule module = null;
	private MAgent agent = null;
	private MResultConfiguration resultConfiguration = null;
	private final Date startTime = new Date(1355222784000L);
	private final Date endTime = new Date(1355227000000L);
	private final long timeShift = 1000L;

	private void addResult(final Date startTime, final long count) {
		final Map<String, Double> parameterValues = new HashMap<String, Double>();
		for (final ParameterIdentifier parameterIdentifier : agentTask
				.getModule().getTemplateResultConfiguration()
				.getParameterIdentifiers()) {
			parameterValues.put(
					parameterIdentifier.createParameterStorageKey(),
					Math.random());
		}
		final SortedMap<ResultIdentifier, Result> results = new TreeMap<ResultIdentifier, Result>();
		long resultMilliseconds = startTime.getTime();
		for (int i = 1; i <= count; i++) {
			resultMilliseconds += count * resultConfiguration.getSamplingRate()
					* TimeConstants.MILLISECONDS_PER_SECOND;
			final Date resultDate = new Date(resultMilliseconds);
			results.put(new ResultIdentifier(resultDate, null), new Result(
					resultDate, parameterValues));
		}

		resultService.addResults(agentTask, results);
	}

	private long getMinStoredTime(final long lastUpdateTime) {
		return lastUpdateTime - storedDaysCount
				* TimeConstants.MILLISECONDS_PER_DAY;
	}

	@Before
	public void setUp() {
		storageService.setStorageHome(SharedModelConfiguration.STORAGE_HOME);

		SharedModelConfiguration
				.deleteFolder(SharedModelConfiguration.STORAGE_HOME);
		SharedModelConfiguration
				.createFolder(SharedModelConfiguration.STORAGE_HOME);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				agent = SharedModelConfiguration
						.createLightWeightAgent("resultTestAgentName");
				modelSpace.save(agent);

				module = SharedModelConfiguration.createAgentModule(agent);
				modelSpace.save(module);

				agentTask = SharedModelConfiguration.createAgentTask(module);
				taskService.updateTaskConfiguration(agentTask);

				resultConfiguration = agentTask.getResultConfiguration();
			}
		});
	}

	@Test
	public void testGetResults() {
		// pre-condition
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		addResult(startTime, resultCount);

		for (final MResultParameterConfiguration parameterConfiguration : resultConfiguration
				.getParameterConfigurations()) {
			final Map<Date, Double> results = resultService
					.getResultsByAggregationStep(agentTask.getKey(),
							parameterConfiguration.getParameterIdentifier(),
							resultConfiguration.getSamplingRate(),
							TimeInterval.get(startTime, endTime),
							startPosition, size, OrderType.ASC);
			Assert.assertEquals(resultCount, results.size());
		}
	}

	@Test
	public void testGetResultsForAllParametersByTaskKeyUsingValidTimeInterval() {
		// pre-condition
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final Date castedEndTime = new Date(startTime.getTime() + resultCount
				* resultConfiguration.getSamplingRate()
				* TimeConstants.MILLISECONDS_PER_SECOND);
		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), resultConfiguration.getSamplingRate(),
				TimeInterval.get(startTime, castedEndTime), startPosition,
				size, OrderType.ASC);
		Assert.assertEquals(resultCount, results.size());

		final int parameterCount = resultConfiguration
				.getParameterConfigurations().size();
		for (final Map<String, Object> resultMap : results) {
			Assert.assertEquals(parameterCount + 1, resultMap.size());
		}
	}

	@Test
	public void testGetResultsForCustomParametersByTaskKeyUsingValidTimeInterval() {
		// pre-condition
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final int parameterCount = 2;
		final Date castedEndTime = new Date(startTime.getTime() + resultCount
				* resultConfiguration.getSamplingRate()
				* TimeConstants.MILLISECONDS_PER_SECOND);
		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		List<ParameterIdentifier> parameterIdentifiers = new ArrayList<ParameterIdentifier>(
				resultConfiguration.getParameterIdentifiers());
		parameterIdentifiers = parameterIdentifiers.subList(0, parameterCount);

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), parameterIdentifiers,
				resultConfiguration.getSamplingRate(),
				TimeInterval.get(startTime, castedEndTime), startPosition,
				size, OrderType.ASC);
		Assert.assertEquals(resultCount, results.size());

		for (final Map<String, Object> resultMap : results) {
			Assert.assertEquals(parameterCount + 1, resultMap.size());
		}
	}

	@Test
	public void testGetResultsForCustomTasksAndParameters() {
		// pre-condition
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final int parameterCount = 2;
		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		List<ParameterIdentifier> parameterIdentifiers = new ArrayList<ParameterIdentifier>(
				resultConfiguration.getParameterIdentifiers());
		parameterIdentifiers = parameterIdentifiers.subList(0, parameterCount);

		final Map<String, Collection<?>> parameters = new HashMap<String, Collection<?>>();
		parameters.put(agentTask.getKey(), parameterIdentifiers);
		final List<Map<String, Object>> results = resultService.getResults(
				parameters, resultConfiguration.getSamplingRate(),
				TimeInterval.get(startTime, endTime), startPosition, size,
				OrderType.ASC);
		for (final Map<String, Object> resultMap : results) {
			Assert.assertEquals(parameterCount + 1, resultMap.size());
		}
	}

	@Test
	public void testGetResultsFromLeftSideOfValidTimeInterval() {
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final Date castedEndTime = new Date(
				getMinStoredTime(startTime.getTime()) - timeShift);
		final Date castedStartTime = new Date(castedEndTime.getTime()
				- resultCount * resultConfiguration.getSamplingRate()
				* TimeConstants.MILLISECONDS_PER_SECOND);

		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), resultConfiguration.getSamplingRate(),
				TimeInterval.get(castedStartTime, castedEndTime),
				startPosition, size, OrderType.ASC);

		Assert.assertEquals(1, results.size());
		for (final Map<String, Object> resultMap : results) {
			for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
				if (!entry.getKey().equals(SimpleUtils.DATE_PARAMETER_NAME)) {
					Assert.assertTrue(entry.getValue().equals(
							Double.NEGATIVE_INFINITY));
				}
			}
		}
	}

	@Test
	public void testGetResultsFromRightSideOfValidTimeInterval() {
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final Date castedStartTime = new Date(endTime.getTime() + timeShift);
		final Date castedEndTime = new Date(castedStartTime.getTime()
				+ resultCount * resultConfiguration.getSamplingRate()
				* TimeConstants.MILLISECONDS_PER_SECOND);

		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), resultConfiguration.getSamplingRate(),
				TimeInterval.get(castedStartTime, castedEndTime),
				startPosition, size, OrderType.ASC);
		Assert.assertEquals(1, results.size());
		for (final Map<String, Object> resultMap : results) {
			for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
				if (!entry.getKey().equals(SimpleUtils.DATE_PARAMETER_NAME)) {
					Assert.assertTrue(entry.getValue().equals(
							Double.POSITIVE_INFINITY));
				}
			}
		}
	}

	@Test
	public void testGetResultsWithCalculationOfAggregationStep() {
		// pre-condition
		final int size = 10;
		final int startPosition = 0;
		final int resultCount = size - startPosition;
		addResult(startTime, resultCount);

		for (final MResultParameterConfiguration parameterConfiguration : agentTask
				.getModule().getTemplateResultConfiguration()
				.getParameterConfigurations()) {

			final TimeInterval timeInterval = TimeInterval.get(startTime,
					endTime);
			final Map<Date, Double> results = resultService
					.getResultsUsingAdaptiveAggregationStep(agentTask.getKey(),
							parameterConfiguration.getParameterIdentifier(),
							timeInterval, OrderType.ASC);

			Assert.assertFalse(results.isEmpty());
		}
	}

	@Test
	public void testGetResultsWithEndOfData() {
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;

		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), resultConfiguration.getSamplingRate(),
				TimeInterval.get(startTime, endTime), startPosition, size,
				OrderType.ASC);

		Assert.assertTrue(results.size() > 1);
		final Map<String, Object> resultMap = results.get(results.size() - 1);
		for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
			if (!entry.getKey().equals(SimpleUtils.DATE_PARAMETER_NAME)) {
				Assert.assertTrue(entry.getValue().equals(
						Double.POSITIVE_INFINITY));
			}
		}
	}

	@Test
	public void testGetResultsWithStartOfData() {
		final long size = 10;
		final long startPosition = 0;
		final long resultCount = size - startPosition;
		final Date castedStartTime = new Date(startTime.getTime()
				- getMinStoredTime(startTime.getTime()));

		addResult(startTime, resultCount);

		updateCurrentResultConfiguration();

		final List<Map<String, Object>> results = resultService.getResults(
				agentTask.getKey(), resultConfiguration.getSamplingRate(),
				TimeInterval.get(castedStartTime, startTime), startPosition,
				size, OrderType.ASC);

		Assert.assertTrue(results.size() > 1);
		final Map<String, Object> resultMap = results.get(0);
		for (final Map.Entry<String, Object> entry : resultMap.entrySet()) {
			if (!entry.getKey().equals(SimpleUtils.DATE_PARAMETER_NAME)) {
				Assert.assertTrue(entry.getValue().equals(
						Double.NEGATIVE_INFINITY));
			}
		}
	}

	private void updateCurrentResultConfiguration() {
		agentTask = taskService.getTaskByKey(agentTask.getKey());
		resultConfiguration = agentTask.getResultConfiguration();
	}
}
