/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertUpdate;

/**
 * @author ivlev.e
 * 
 */
public interface AlertHistoryRetrieverAsync {

	void getAlertHistory(MAlert alert, Criterion criterion, Order order,
			Integer startPosition, Integer size,
			AsyncCallback<List<MAlertUpdate>> callback);

	void getAlertHistoryTotalCount(Criterion criterion,
			AsyncCallback<Long> callback);

	void getAllAlertHistory(Criterion criterion, Order order,
			Integer startPosition, Integer size,
			AsyncCallback<List<MAlertUpdate>> callback);
}
