/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.regexp.shared.RegExp;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;

/**
 * Параметры URL, используемые для хранения состояния
 * 
 * @author ivlev.e
 */
public class RequestParams {

	public final static String agentName = "agentName";

	public final static String moduleName = "moduleName";

	public final static String taskKey = "taskKey";

    public final static String tasks = "tasks";

	public final static String paramName = "paramName";

	public final static String dateTimeFormat = "dateTimeFormat";

	public final static String clientTimeZone = "clientTimeZone";

	public final static String timeZone = "timeZone";

	public final static String timeIntervalType = "timeIntervalType";

	public final static String timeZoneType = "timeZoneType";

	public final static String policyKey = "policyKey";

	public final static String parameterIdentifier = "paramStorageKey";

	public final static String startDate = "startDate";

	public final static String endDate = "endDate";

	public final static String chartName = "chartName";

	public final static String locale = "locale";

	public final static String userName = "userName";

	public final static String alertTypeName = "alertTypeName";

	public final static String invalidHistoryToken = "invalidHistoryToken";

	public final static String unauthorizedHistoryToken = "unauthorizedHistoryToken";

	public final static String originatorKey = "originatorKey";

	public final static String settings = "settings";

	public final static String sourceKey = "sourceKey";

	public final static String template = "template";

	public final static String page = "page";

    public final static String exportResultsTaskParameterName = "export_task_id";

    public static List<String> getParametersSortByIndex(
			final Map<String, String> parameters, final String parameterTemplate) {
		final RegExp regexp = RegExp.compile("^" + parameterTemplate + "\\d+$");
		final TreeMap<Integer, String> sortedParameters = new TreeMap<Integer, String>();
		for (final String param : parameters.keySet()) {
			if (regexp.test(param)) {
				final Integer index = Integer.valueOf(param
						.substring(parameterTemplate.length()));
				final String value = parameters.get(param);
				sortedParameters.put(index, value);
			}
		}
		return new ArrayList<String>(sortedParameters.values());
	}

	public static List<String> getParametersSortByIndex(
			final PlaceRequest request, final String parameterTemplate) {
		final Map<String, String> parameters = new HashMap<String, String>();
		for (final String parameterName : request.getParameterNames()) {
			parameters.put(parameterName,
					request.getParameter(parameterName, null));
		}
		return getParametersSortByIndex(parameters, parameterTemplate);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Collection<?>> getRelatedParameters(
			final Collection<String> parentParameters,
			final Collection<String> childParameters) {
		final Map<String, Collection<?>> result = new LinkedHashMap<String, Collection<?>>();
		final Iterator<String> parentParametersIterator = parentParameters
				.iterator();
		final Iterator<String> childParametersIterator = childParameters
				.iterator();
		while (parentParametersIterator.hasNext()
				&& childParametersIterator.hasNext()) {
			final String parentParameter = parentParametersIterator.next();
			final String childParameter = childParametersIterator.next();
			Collection<String> parentChildParameters = (Collection<String>) result
					.get(parentParameter);
			if (parentChildParameters == null) {
				parentChildParameters = new ArrayList<String>();
				result.put(parentParameter, parentChildParameters);
			}
			parentChildParameters.add(childParameter);
		}
		return result;
	}

}
