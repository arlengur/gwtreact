/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchProcessingStrategy;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchType;

/**
 * Writes rrd results to .csv file
 * 
 * @author sviyazov.a
 * 
 */
public class RrdToCsvBatchProcessingStrategy
		implements
			BatchProcessingStrategy<List<Entry<Long, Double>>> {
	private final PrintWriter writer;
	private final static String DEFAULT_SEPARATOR = "\t";
	private final DateFormat dateFormat;

	public RrdToCsvBatchProcessingStrategy(final PrintWriter writer,
			final DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		this.writer = writer;
		writer.write("sep=" + DEFAULT_SEPARATOR + "\n");
	}

	@Override
	public void processBatchData(final BatchType batchType,
			final List<Entry<Long, Double>> batchData) {
		for (final Entry<Long, Double> entry : batchData) {
			final Long timestamp = entry.getKey();
			final String output = timestamp
					+ DEFAULT_SEPARATOR
					+ dateFormat.format(new Date(timestamp
							* TimeConstants.MILLISECONDS_PER_SECOND))
					+ DEFAULT_SEPARATOR + entry.getValue() + "\n";
			writer.write(output);
		}
	}
}
