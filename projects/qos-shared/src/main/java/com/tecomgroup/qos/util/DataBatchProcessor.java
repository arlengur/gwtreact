/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.Collection;

import org.apache.log4j.Logger;

/**
 * A processor to process provided data by batches.
 * 
 * @author sviyazov.a
 * 
 */
public class DataBatchProcessor<T extends Collection<?>> {

	/**
	 * An interface of strategy of processing batch data.
	 * 
	 * @author kunilov.p
	 * 
	 * @param <T>
	 *            the type of processing data.
	 * @param batchData
	 *            the batch data to process.
	 */
	public interface BatchProcessingStrategy<T> {
		void processBatchData(BatchType batchType, T batchData);
	}

	/**
	 * An interface of strategy of retrieving batch data.
	 * 
	 * @author kunilov.p
	 * 
	 * @param <T>
	 *            the type of retrieving data.
	 * @param startIndex
	 *            the start index to retrieve data.
	 * @param size
	 *            the size of total retrieving data
	 * @return the retrieved batch data.
	 */
	public interface BatchRetrievingStrategy<T> {
		T getBatchData(BatchType batchType, long startIndex, long size);
	}

	public enum BatchType {
		FIRST, MIDDLE, LAST, ALL
	}

	private final static Logger LOGGER = Logger
			.getLogger(DataBatchProcessor.class);

	private final String taskIdentifier;

	private final BatchRetrievingStrategy<T> batchRetrievingStrategy;

	private final BatchProcessingStrategy<T> batchProcessingStrategy;

	private int processedDataCount = 0;

	public DataBatchProcessor(final String taskIdentifier,
			final BatchRetrievingStrategy<T> batchRetrievingStrategy,
			final BatchProcessingStrategy<T> batchProcessingStrategy) {
		this.taskIdentifier = taskIdentifier;
		this.batchRetrievingStrategy = batchRetrievingStrategy;
		this.batchProcessingStrategy = batchProcessingStrategy;
	}

	/**
	 * @return the processedDataCount
	 */
	public int getProcessedDataCount() {
		return processedDataCount;
	}

	/**
	 * Process data without splitting to batches.
	 * 
	 * @param startIndex
	 *            the global start index
	 * @param size
	 *            the size of total retrieving data
	 */
	public void process(final int startIndex, final int size) {
		final BatchType batchType = BatchType.ALL;

		final T data = batchRetrievingStrategy.getBatchData(batchType,
				startIndex, size);
		batchProcessingStrategy.processBatchData(batchType, data);

		processedDataCount = data.size();
		LOGGER.info("Processed " + processedDataCount + " rows ("
				+ SimpleUtils.asPercentage(processedDataCount, size)
				+ "%) of " + size + " rows of task " + taskIdentifier);
	}

	/**
	 * Splits range of [startIndex, endIndex] to <code>n</code> sub ranges of
	 * size <code>batchSize</code>. If data size is not multiple of
	 * <code>batchSize</code>) it will be exported without splitting. Calls
	 * {@link BatchProcessingStrategy#processBatch(long, long)} for each sub
	 * range.
	 * 
	 * @param startIndex
	 *            the global start index
	 * @param size
	 *            the size of total retrieving data
	 * @param batchSize
	 *            the batch size to split the provided range.
	 */
	public void process(final long startIndex, final long size,
			final long batchSize) {
		BatchType batchType = BatchType.FIRST;

		long batchStartIndex = startIndex;
		while (batchStartIndex < size) {
			if (batchStartIndex + batchSize >= size) {
				batchType = BatchType.LAST;
			}
			final T batchData = batchRetrievingStrategy.getBatchData(batchType,
					batchStartIndex,
					Math.min(size - batchStartIndex, batchSize));

			batchProcessingStrategy.processBatchData(batchType, batchData);

			processedDataCount += batchData.size();

			LOGGER.info("Processed " + processedDataCount + " rows ("
					+ SimpleUtils.asPercentage(processedDataCount, size)
					+ "%) of " + size + " rows of task " + taskIdentifier);

			batchStartIndex += batchSize;
			batchType = BatchType.MIDDLE;
		}
	}

	/**
	 * Process all retrieved data.
	 * 
	 */
	public void processAll() {
		final BatchType batchType = BatchType.ALL;

		final T data = batchRetrievingStrategy.getBatchData(batchType, 0,
				Integer.MAX_VALUE);
		batchProcessingStrategy.processBatchData(batchType, data);

		processedDataCount = data.size();
		LOGGER.info("Processed " + processedDataCount
				+ " rows (100%) of data of task " + taskIdentifier);
	}
}
