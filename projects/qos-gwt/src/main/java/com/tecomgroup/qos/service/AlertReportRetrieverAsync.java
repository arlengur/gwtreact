/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.List;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlertReport;

/**
 * @author kunilov.p
 * 
 */
public interface AlertReportRetrieverAsync {

	void deserializeBean(String beanPayload,
			AsyncCallback<AlertReportWrapper> callback);

	void getAlertReportCount(Set<String> sourceKeys, TimeInterval timeInterval,
			Criterion filteringCriterion, AsyncCallback<Long> callback);

	void getAlertReports(Set<String> sourceKeys, TimeInterval timeInterval,
			Criterion filteringCriterion, Order order, Integer startPosition,
			Integer size, AsyncCallback<List<MAlertReport>> callback);

	void serializeBean(AlertReportWrapper bean, AsyncCallback<String> callback);
}
