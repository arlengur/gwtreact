/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.tecomgroup.qos.OrderType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.service.RRDResultService;
import com.tecomgroup.qos.service.ResultService;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author kunilov.p
 * 
 */
public class ResultServletHandler implements HttpRequestHandler {

	private ResultService resultService;

	/**
	 * The property is used in
	 * {@link RRDResultService#setMaxResultsCount(Integer)}. Both classes should
	 * use the same property.
	 */
	private int maxResultsCount;

	/**
	 * Implements workaround features for Highstock JS
	 * 
	 * @param timeInterval
	 * @param results
	 * @return
	 */
	protected Map<Date, Double> addNaNResults(final TimeInterval timeInterval,
			final Map<Date, Double> results) {
		Long startTimestamp = null;
		Long endTimestamp = null;

		if (!results.isEmpty()) {
			startTimestamp = timeInterval.getStartDateTime().getTime() - 1;
			endTimestamp = timeInterval.getEndDateTime().getTime() + 1;
			results.put(new Date(startTimestamp), Double.NaN);
			results.put(new Date(endTimestamp), Double.NaN);
		} else {
			// NB: if data array is empty, it should be filled with null
			// values for interval bounds in order to avoid zoom
			// problems in Highstock 1.3.0
			startTimestamp = timeInterval.getStartDateTime().getTime();

			endTimestamp = timeInterval.getEndDateTime().getTime();

			results.put(new Date(startTimestamp), Double.NaN);
			results.put(new Date(endTimestamp), Double.NaN);
		}
		return results;
	}

	private String createReturnedDataString(final Map<Long, Float> results,
			final Double minValue, final Double maxValue) {
		final StringBuilder result = new StringBuilder();
		result.append("{\"data\":[");
		boolean first = true;
		if (results != null) {
			for (final Map.Entry<Long, Float> resultEntry : results.entrySet()) {
				if (!first) {
					result.append(",");
				}
				result.append("[");
				result.append(resultEntry.getKey());
				result.append(",");
				result.append(resultEntry.getValue());
				result.append("]");
				first = false;
			}
		} else {
			result.append("null");
		}
		result.append("]");
		if (!minValue.equals(Double.MAX_VALUE)) {
			result.append(",");
			result.append("\"min\": " + minValue.floatValue() + ",");
			result.append("\"max\": " + maxValue.floatValue());
		}
		result.append("}");

		return result.toString();
	}

	@Override
	public void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		final String taskKey = request.getParameter("taskKey");
		final String parameterName = request.getParameter("parameterName");
		final String parameterProperties = request
				.getParameter("parameterProperties");
		final String startTimestamp = request.getParameter("startDate");
		final String endTimestamp = request.getParameter("endDate");
		String useInterpolation = request.getParameter("useInterpolation");
		String useAutoscaling = request.getParameter("useAutoscaling");
		if (useInterpolation == null) {
			useInterpolation = "1";
		}
		if (useAutoscaling == null) {
			useAutoscaling = "false";
		}

		if (!validateParameters(taskKey, parameterName, startTimestamp,
				endTimestamp, useInterpolation, useAutoscaling)) {
			response.sendError(404, "Incorrect provided parameters");
			return;
		}

		final boolean interpolation = (Integer.valueOf(useInterpolation)
				.intValue() == 1) ? true : false;
		final boolean autoscaling = Boolean.valueOf(useAutoscaling);

		final Date startDateTime = new Date(Long.parseLong(startTimestamp));
		final Date endDateTime = new Date(Long.parseLong(endTimestamp));

		final TimeInterval timeInterval = TimeInterval.get(startDateTime, endDateTime);
		// Get results from data source
		final Map<Date, Double> results = resultService.getResults(
				taskKey,
				new ParameterIdentifier(parameterName, ConfigurationUtil
						.stringToProperties(parameterProperties, true)),
				timeInterval, OrderType.ASC);

		// Workaround for Highstock JS
		addNaNResults(timeInterval, results);

		final Map<Long, Float> processedResults = new TreeMap<Long, Float>();
		Double minValue = Double.MAX_VALUE;
		Double maxValue = Double.MIN_VALUE;
		// Result's preprocessing
		for (final Map.Entry<Date, Double> resultEntry : results.entrySet()) {
			final Double value = resultEntry.getValue();
			final Long convertedTime = resultEntry.getKey().getTime();
			if (value == null || Double.isNaN(value)) {
				processedResults.put(convertedTime, null);
			} else {
				if (autoscaling) {
					minValue = (value < minValue) ? value : minValue;
					maxValue = (value > maxValue) ? value : maxValue;
				}
				processedResults.put(convertedTime, value.floatValue());
			}
		}

		Map<Long, Float> interpolatedResults = null;
		// Interpolation if need
		if (interpolation) {
			if (processedResults.size() < maxResultsCount) {
				interpolatedResults = performLinearInterpolation(
						processedResults, maxResultsCount, startDateTime,
						endDateTime, OrderType.ASC);
			}
		}
		// To string
		final String data = (interpolatedResults == null
				? createReturnedDataString(processedResults, minValue, maxValue)
				: createReturnedDataString(interpolatedResults, minValue,
						maxValue));
		response.setContentType("application/json");
		final PrintWriter out = response.getWriter();
		out.write(data);
		out.flush();
	}

	private Map<Long, Float> performLinearInterpolation(final long startTime,
			final long endTime, final float startValue, final float endValue,
			final int resultCount, final OrderType orderType) {
		final Map<Long, Float> interpolatedData;
		if (orderType.equals(OrderType.DESC)) {
			interpolatedData = new TreeMap<Long, Float>(
					Collections.reverseOrder());
		} else {
			interpolatedData = new TreeMap<Long, Float>();
		}

		// шаг интерполяции
		final long timeStep = (long) Math.ceil((endTime - startTime)
				/ resultCount);

		// тангенс угла касательной или коэффициент a в формуле прямой y=b+ax
		final float interpolatedFactor = (endValue - startValue)
				/ (endTime - startTime);
		// значения для начальной и конечной точки уже вычислены, поэтому index
		// изменяется в пределах [1..resultCount-1]
		for (int index = 1; index < resultCount - 1; index++) {
			// текущая точка для вычисления значения или x в формуле y=b+ax
			final long currentTime = startTime + index * timeStep;
			// значение линейной интерполяции в точке (currentTime - startTime),
			// y=b+a*x, b=startValue, a=interpolatedFactor
			final float currentValue = startValue + interpolatedFactor
					* (currentTime - startTime);
			interpolatedData.put(currentTime, currentValue);
		}

		return interpolatedData;
	}

	private Map<Long, Float> performLinearInterpolation(
			final Map<Long, Float> data, final int resultLength,
			final Date startDateTime, final Date endDateTime,
			final OrderType orderType) {
		final Map<Long, Float> result;
		if (orderType.equals(OrderType.DESC)) {
			result = new TreeMap<Long, Float>(Collections.reverseOrder());
		} else {
			result = new TreeMap<Long, Float>();
		}

		final List<Map.Entry<Long, Float>> dataList = new ArrayList<Map.Entry<Long, Float>>(
				data.entrySet());
		final Long fullTime = endDateTime.getTime() - startDateTime.getTime();
		for (int i = 0, dataSize = data.size(); i < dataSize - 1; i++) {
			final Map.Entry<Long, Float> currentDataEntry = dataList.get(i);
			final Map.Entry<Long, Float> nextDataEntry = dataList.get(i + 1);

			final Long currentStartTimestamp = currentDataEntry.getKey();
			final Float currentStartValue = currentDataEntry.getValue();

			final Long currentEndTimestamp = nextDataEntry.getKey();
			final Float currentEndValue = nextDataEntry.getValue();
			if (currentStartValue == null || currentEndValue == null) {
				result.put(currentStartTimestamp, currentStartValue);
				result.put(currentEndTimestamp, currentEndValue);
			} else {
				// add start point
				result.put(currentStartTimestamp, currentStartValue);

				// add interpolated points if necessary
				final double interpolatedFactor = (currentEndTimestamp - currentStartTimestamp)
						/ (double) fullTime;
				// it is necessary to increase interpolatedDataCount by 1,
				// because the ends of the intervals are repeated: [a, b], [b,
				// c], [c, d] (b and c are repeated twice), so it is reasonable
				// to add one more point in the interval to compensate the
				// repetition.
				final int interpolatedDataCount = (int) Math.round(resultLength
						* interpolatedFactor) + 1;
				// start and end points are provided, so interpolatedDataCount
				// should be greater than 2
				if (interpolatedDataCount > 2) {
					final Map<Long, Float> interpolatedData = performLinearInterpolation(
							currentStartTimestamp, currentEndTimestamp,
							currentStartValue, currentEndValue,
							interpolatedDataCount, orderType);
					result.putAll(interpolatedData);
				}

				// add end point
				result.put(currentEndTimestamp, currentEndValue);
			}
		}
		return result;
	}

	/**
	 * @param maxResultsCount
	 *            the maxResultsCount to set
	 */
	public void setMaxResultsCount(final int maxResultsCount) {
		this.maxResultsCount = maxResultsCount;
	}

	/**
	 * @param resultService
	 *            the resultService to set
	 */
	public void setResultService(final ResultService resultService) {
		this.resultService = resultService;
	}

	private boolean validateParameters(final String taskKey,
			final String parameterName, final String startTimestamp,
			final String endTimestamp, final String useInterpolation,
			final String useAutoscaling) {
		boolean result = true;

		if (!SimpleUtils.isNotNullAndNotEmpty(taskKey)) {
			result = false;
		} else if (!SimpleUtils.isNotNullAndNotEmpty(parameterName)) {
			result = false;
		} else if (!SimpleUtils.validateTimestamp(startTimestamp)) {
			result = false;
		} else if (!SimpleUtils.validateTimestamp(endTimestamp)) {
			result = false;
		} else if (!SimpleUtils.validateBoolean(useInterpolation)) {
			result = false;
		} else if (!SimpleUtils.validateBoolean(useAutoscaling)) {
			result = false;
		}

		return result;
	}
}
