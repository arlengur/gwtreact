/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.service.ResultRetriever;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchRetrievingStrategy;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchType;

/**
 * An interface of strategy to retrieve data.
 * 
 * @author kunilov.p
 * 
 */
public class ResultsBatchRetrievingStrategy
		implements
			BatchRetrievingStrategy<List<Map<String, Object>>> {

	private final ResultRetriever resultRetriever;
	private final Map<String, Collection<?>> taskParameters;
	private final TimeInterval timeInterval;
	private final Long aggregationStep;

	public ResultsBatchRetrievingStrategy(
			final ResultRetriever resultRetriever,
			final Map<String, Collection<?>> taskParameters,
			final Long aggregationStep, final TimeInterval timeInterval) {
		super();
		this.resultRetriever = resultRetriever;
		this.taskParameters = taskParameters;
		this.timeInterval = timeInterval;
		this.aggregationStep = aggregationStep;
	}

	@Override
	public List<Map<String, Object>> getBatchData(final BatchType batchType,
			final long startIndex, final long size) {

		boolean addStartOfData = false;
		if (BatchType.ALL == batchType || BatchType.FIRST == batchType) {
			addStartOfData = true;
		}

		boolean addEndOfData = false;
		if (BatchType.ALL == batchType || BatchType.LAST == batchType) {
			addEndOfData = true;
		}

		final List<Map<String, Object>> results = resultRetriever.getResults(
				taskParameters, aggregationStep, timeInterval, startIndex,
				size, OrderType.ASC, addStartOfData, addEndOfData);

		return results;
	}
}
