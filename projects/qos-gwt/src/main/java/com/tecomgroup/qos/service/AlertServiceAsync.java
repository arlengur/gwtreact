/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;

/**
 * Сервис для работы с алёртами
 * 
 * @author abondin
 * 
 */
public interface AlertServiceAsync {

	void acknowledgeAlert(MAlertIndication indication, String comment,
			String accountToken, AsyncCallback<MAlert> callback);

	void acknowledgeAlerts(List<MAlertIndication> indications, String comments,
			AsyncCallback<Void> callback);

	void activateAlert(MAlertIndication indication, String ssoToken,
			AsyncCallback<MAlert> callback);

	void clearAlert(MAlertIndication indication, String comment,
			String accountToken, AsyncCallback<MAlert> callback);

	void clearAlerts(List<MAlertIndication> indications, String comments,
			AsyncCallback<Void> callback);

	void commentAlert(MAlertIndication indication, String comment,
			String ssoToken, AsyncCallback<MAlert> callback);

	void commentAlerts(List<MAlertIndication> indications, String comment,
			AsyncCallback<Void> callback);

	void disable(MAlert alert, AsyncCallback<Void> callback);

	void disableAlertsByOriginator(Source originator,
			AsyncCallback<Void> callback);

	void getAlert(Long alertId, AsyncCallback<MAlert> async);

	void getAlert(String alertTypeName, Source source, Source originator,
			String settings, AsyncCallback<MAlert> callback);

	void getAlert(String alertTypeName, String sourceKey, String originatorKey,
			String settings, AsyncCallback<MAlert> callback);

	void getAlerts(Criterion criterion, Order order, Integer startPosition,
			Integer size, AsyncCallback<List<MAlert>> callback);

	void getAlertsByOriginator(Source originator, Order order,
			Integer startPosition, Integer size,
			AsyncCallback<List<MAlert>> callback);

	void getAlertsBySource(Source source, Order order, Integer startPosition,
			Integer size, Boolean propagated, final Criterion criterion,
			AsyncCallback<List<MAlert>> callback);

	void getAlertsCount(Criterion criterion, AsyncCallback<Long> callback);

	void getAlertsSummaryDurationByAgentKey(Set<PerceivedSeverity> severities,
			Type intervalType, AsyncCallback<Map<String, Long>> callback);

	void getAllAlertTypeNames(AsyncCallback<Set<String>> callback);

	void getAllTypes(AsyncCallback<Map<String, MAlertType>> callback);

	void getStatus(Collection<Source> source, Boolean propagated,
			AsyncCallback<Map<Source, PerceivedSeverity>> callback);

	void registerAlertType(MAlertType type, AsyncCallback<Void> callback);

	void resetDisabledAlerts(Source originator, AsyncCallback<Void> callback);

	void unAcknowledgeAlert(MAlertIndication indication, String comment,
			String accountToken, AsyncCallback<MAlert> callback);

	void unAcknowledgeAlerts(List<MAlertIndication> indications,
			String comment, AsyncCallback<Void> callback);
}
