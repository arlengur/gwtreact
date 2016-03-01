/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.rrd4j.ConsolFun;
import org.rrd4j.core.FetchData;
import org.rrd4j.core.FetchRequest;
import org.rrd4j.core.RrdDb;

import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchRetrievingStrategy;
import com.tecomgroup.qos.util.DataBatchProcessor.BatchType;

/**
 * Retrieves rrd results of first found datasource in {@link #sourceRrdDb}
 * 
 * @author sviyazov.a
 * 
 */
public class RrdBatchRetrievingStrategy
		implements
			BatchRetrievingStrategy<List<Entry<Long, Double>>> {
	private final Long samplingRate;
	private final ConsolFun aggregationType;
	private final RrdDb sourceRrdDb;
	private final String datasourceName;

	public RrdBatchRetrievingStrategy(final RrdDb sourceRrdDb) {
		this.sourceRrdDb = sourceRrdDb;
		datasourceName = RrdUtil.getDatasourceName(sourceRrdDb, 0);
		samplingRate = RrdUtil.getSamplingRate(sourceRrdDb);
		aggregationType = RrdUtil.getAggregationType(sourceRrdDb, samplingRate);
	}

	@Override
	public List<Entry<Long, Double>> getBatchData(final BatchType batchType,
			final long startIndex, final long size) {
		List<Entry<Long, Double>> data = null;
		try {
			final FetchRequest fetchRequest = sourceRrdDb.createFetchRequest(
					aggregationType, startIndex, startIndex + size,
					samplingRate);
			final FetchData fetchData = fetchRequest.fetchData();
			if (fetchData.getRowCount() > 0) {

				int index = 0;
				final double[] values = fetchData.getValues(datasourceName);
				long[] timestamps = null;
				for (final double value : values) {
					if (!Double.isNaN(value)) {
						if (timestamps == null) {
							timestamps = fetchData.getTimestamps();
						}
						if (data == null) {
							data = new ArrayList<>();
						}
						final Entry<Long, Double> entry = new ImmutablePair<>(
								timestamps[index], value);
						data.add(entry);
					}
					index++;
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException(ex);
		}
		return data;
	}
}
