/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchProcessingStrategy;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchType;

/**
 * The strategy of processing results.
 * 
 * @author kunilov.p
 * 
 */
public class ResultsToXlsxBatchProcessingStrategy
		implements
			BatchProcessingStrategy<List<Map<String, Object>>> {

	private final ResultXlsxWriter resultWriter;
	private final ExportResultsWrapper exportResultsWrapper;
	private final Map<String, Collection<?>> taskParameters;

	public ResultsToXlsxBatchProcessingStrategy(
			final ResultXlsxWriter resultWriter,
			final ExportResultsWrapper exportResultsWrapper,
			final Map<String, Collection<?>> taskParameters) {
		this.resultWriter = resultWriter;
		this.exportResultsWrapper = exportResultsWrapper;
		this.taskParameters = taskParameters;
	}

	private List<String> getStorageKeys(
			final Map<String, Collection<?>> taskParameters) {
		final List<String> storageKeys = new ArrayList<String>();
		for (final Map.Entry<String, Collection<?>> taskParameterEntry : taskParameters
				.entrySet()) {
			final String taskKey = taskParameterEntry.getKey();
			for (final Object parameterIdentifier : taskParameterEntry
					.getValue()) {
				final String storageKey = taskKey
						+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
						+ parameterIdentifier;
				storageKeys.add(storageKey);
			}
		}
		return storageKeys;
	}

	@Override
	public void processBatchData(final BatchType batchType,
			final List<Map<String, Object>> batchData) {
		resultWriter.writeResultData(batchData,
				getStorageKeys(taskParameters));
	}
}
