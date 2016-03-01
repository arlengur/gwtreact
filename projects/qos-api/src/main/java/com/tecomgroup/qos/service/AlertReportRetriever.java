/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * A retriever to get {@link MAlertReport} information.
 * 
 * @author kunilov.p
 * 
 */
@RemoteServiceRelativePath("springServices/alertReportService")
public interface AlertReportRetriever extends Service, RemoteService {
	/**
	 * Deserializes POJO bean to JSON with server side serializer
	 * 
	 * @param beanPayload
	 * @return
	 * @throws ServiceException
	 */
	AlertReportWrapper deserializeBean(String beanPayload);

	/**
	 * @param sourceKeys
	 *            keys of sources. The parameter can be null, in this case alert
	 *            reports for all sources will be retrieved.
	 * @param timeInterval
	 *            The parameter can be null, in this case alert reports for all
	 *            time will be retrieved.
	 * @param filteringCriterion
	 *            The criterion to filter reports. it can be null. It must not
	 *            contain timeInterval and sourceKyes. These parameters are
	 *            provided separately as first parameters.
	 * @return
	 */
	long getAlertReportCount(Set<String> sourceKeys, TimeInterval timeInterval,
			Criterion filteringCriterion);

	/**
	 * Gets alert reports for provided source keys.
	 * 
	 * @param sourceKeys
	 *            keys of sources. The parameter can be null, in this case alert
	 *            reports for all sources will be retrieved.
	 * @param timeInterval
	 *            The parameter can be null, in this case alert reports for all
	 *            time will be retrieved.
	 * @param filteringCriterion
	 *            A criterion to filter reports. it can be null. It must not
	 *            contain timeInterval and sourceKyes. These parameters are
	 *            provided separately as first parameters.
	 * @param order
	 *            it can be null
	 * @param startPosition
	 *            it can be null
	 * @param size
	 *            it can be null
	 * @return
	 */
	List<MAlertReport> getAlertReports(Set<String> sourceKeys,
			TimeInterval timeInterval, Criterion filteringCriterion,
			Order order, Integer startPosition, Integer size);

	/**
	 * Serializes POJO bean to JSON with server side serializer
	 * 
	 * @param bean
	 * @return
	 * @throws ServiceException
	 */
	String serializeBean(AlertReportWrapper bean);
}
