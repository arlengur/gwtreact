/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.servlet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.service.ResultRetriever;
import com.tecomgroup.qos.util.CallableTask;
import com.tecomgroup.qos.util.DataBatchProcessor;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Exports results to a temporary XLSX file with unique name.
 * 
 * @author sviyazov.a
 * 
 */
public class ExportResultsTask implements CallableTask<File> {

	private final ResultRetriever resultRetriever;

	private final ExportTaskClientInfo taskClientInfo;

	private final ExportResultsWrapper exportResultsWrapper;

	private final Map<String, Collection<?>> taskParameters;

	private final TimeInterval exportedTimeInterval;

	private final Long aggregationStep;

	private final int batchSize;

	private String taskIdentifier;

	private int totalResultsCount = 0;

	private DataBatchProcessor<List<Map<String, Object>>> dataBatchProcessor;

	public ExportResultsTask(final ExportTaskClientInfo taskClientInfo,
			final ResultRetriever resultRetriever,
			final ExportResultsWrapper exportResultsWrapper, final int batchSize) {
		this.taskClientInfo = taskClientInfo;
		this.resultRetriever = resultRetriever;
		this.exportResultsWrapper = exportResultsWrapper;
		this.batchSize = batchSize;

		taskParameters = RequestParams.getRelatedParameters(
				exportResultsWrapper.taskKeys,
				exportResultsWrapper.parameterIdentifiers);

		exportedTimeInterval = TimeInterval.get(TimeInterval.Type.CUSTOM,
				exportResultsWrapper.startDateTime,
				exportResultsWrapper.endDateTime,
				exportResultsWrapper.timeZoneType,
				exportResultsWrapper.timeZone,
				exportResultsWrapper.clientTimeZone);

		if (exportResultsWrapper.rawData) {
			// aggregation step is used as a multiplier of sampling rate to
			// calculate data interval
			aggregationStep = new Long(1);
		} else {
			aggregationStep = null;
		}
	}

	/**
	 * 
	 * @return created temporary XLSX file
	 * @throws Exception
	 */
	@Override
	public File call() throws Exception {

		final File tempFile = File.createTempFile(
				"results_" + UUID.randomUUID(), ".xlsx");
		tempFile.deleteOnExit();

		try (final ResultXlsxWriter resultXlsxWriter = new ResultXlsxWriter(
				new BufferedOutputStream(new FileOutputStream(tempFile)),
				exportResultsWrapper)) {

			final ResultsBatchRetrievingStrategy batchRetrievingStrategy = new ResultsBatchRetrievingStrategy(
					resultRetriever, taskParameters, aggregationStep,
					exportedTimeInterval);

			final ResultsToXlsxBatchProcessingStrategy batchProcessingStrategy = new ResultsToXlsxBatchProcessingStrategy(
					resultXlsxWriter, exportResultsWrapper, taskParameters);

			dataBatchProcessor = new DataBatchProcessor<>(getTaskIdentifier(),
					batchRetrievingStrategy, batchProcessingStrategy);

			totalResultsCount = resultRetriever.getTotalResultsCount(
					taskParameters, aggregationStep, exportedTimeInterval);

			if (exportResultsWrapper.rawData) {
				dataBatchProcessor.process(0, totalResultsCount, batchSize);
			} else {
				dataBatchProcessor.processAll();
			}
		}

		return tempFile;
	}

	@Override
	public int getPercentDone() {
		int percentDone = 0;
		if (dataBatchProcessor != null && totalResultsCount > 0) {
			percentDone = SimpleUtils.asPercentage(
					dataBatchProcessor.getProcessedDataCount(),
					totalResultsCount);
		}

		return percentDone;
	}

	public String getTaskIdentifier() {
		return taskIdentifier == null ? toString() : taskIdentifier;
	}

	public void setTaskIdentifier(final String taskIdentifier) {
		this.taskIdentifier = taskIdentifier;
	}

	@Override
	public String toString() {
		return "{clientInfo = " + taskClientInfo + "}";
	}
}
