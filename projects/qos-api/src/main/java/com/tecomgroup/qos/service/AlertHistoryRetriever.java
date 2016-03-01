/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertUpdate;

/**
 * @author ivlev.e
 * 
 */
@RemoteServiceRelativePath("springServices/alertHistoryService")
public interface AlertHistoryRetriever extends Service, RemoteService {

	/**
	 * 
	 * @param alert
	 * @param timeInterval
	 * @param order
	 * @param startPosition
	 * @param size
	 * @return
	 */
	List<MAlertUpdate> getAlertHistory(MAlert alert, Criterion criterion,
			Order order, Integer startPosition, Integer size);

	/**
	 * 
	 * @param criterion
	 * @return
	 */
	Long getAlertHistoryTotalCount(Criterion criterion);

	/**
	 * 
	 * @param criterion
	 * @param order
	 * @param startPosition
	 * @param size
	 * @return
	 */
	List<MAlertUpdate> getAllAlertHistory(Criterion criterion, Order order,
			Integer startPosition, Integer size);
}
