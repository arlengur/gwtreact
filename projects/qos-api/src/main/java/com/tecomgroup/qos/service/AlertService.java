/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jws.WebService;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
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
@WebService
@RemoteServiceRelativePath("springServices/alertService")
public interface AlertService extends Service, RemoteService, Disabler<MAlert> {

	/**
	 * Store information about all alert types
	 * 
	 * @author abondin
	 * 
	 */
	public static class AlertTypesConfiguration {
		private List<MAlertType> types;

		/**
		 * @return the types
		 */
		public List<MAlertType> getTypes() {
			return types;
		}
		/**
		 * @param types
		 *            the types to set
		 */
		public void setTypes(final List<MAlertType> types) {
			this.types = types;
		}
	}

	/**
	 * 
	 * @param indication
	 * @param comment
	 * @param accountToken
	 * @return
	 */
	MAlert acknowledgeAlert(MAlertIndication indication, String comment,
			String accountToken);

	/**
	 * 
	 * @param indications
	 * @param comments
	 */
	void acknowledgeAlerts(List<MAlertIndication> indications, String comments);

	/**
	 * 
	 * @param indication
	 * @param accountToken
	 * @return
	 */
	MAlert activateAlert(MAlertIndication indication, String accountToken);

	/**
	 * 
	 * @param indication
	 * @param comment
	 * @param accountToken
	 * @return
	 */
	MAlert clearAlert(MAlertIndication indication, String comment,
			String accountToken);

	/**
	 * 
	 * @param indications
	 * @param comments
	 */
	void clearAlerts(List<MAlertIndication> indications, String comments);

	/**
	 * 
	 * @param indication
	 * @param comment
	 * @param accountToken
	 * @return
	 */
	MAlert commentAlert(MAlertIndication indication, String comment,
			String accountToken);

	/**
	 * 
	 * @param indications
	 * @param comment
	 * @return
	 */
	void commentAlerts(List<MAlertIndication> indications, String comment);

	/**
	 * Disables alerts by originator.
	 * 
	 * @param originator
	 *            An originator of alerts.
	 */
	void disableAlertsByOriginator(Source originator);

	/**
	 * @param alertId
	 * @return
	 */
	MAlert getAlert(Long alertId);

	/**
	 * Deprecated. Please use {@link #getAlert(String, String, String, String)}
	 * 
	 * @param alertTypeName
	 * @param source
	 * @param originator
	 * @param settings
	 * @return
	 */
	@Deprecated
	MAlert getAlert(String alertTypeName, Source source, Source originator,
			String settings);

	/**
	 * @param alertTypeName
	 * @param sourceKey
	 * @param originatorKey
	 * @param settings
	 * @return
	 */
	MAlert getAlert(String alertTypeName, String sourceKey,
			String originatorKey, String settings);

	/**
	 * Список всех активных алёртов для данной задачи
	 * 
	 * @param criterion
	 * @return
	 */
	List<MAlert> getAlerts(Criterion criterion, Order order,
			Integer startPosition, Integer size);

	/**
	 * 
	 * @param originator
	 * @param order
	 * @param startPosition
	 * @param size
	 * @return
	 */
	List<MAlert> getAlertsByOriginator(Source originator, Order order,
			Integer startPosition, Integer size);

	/**
	 * 
	 * @param source
	 * @param order
	 * @param startPosition
	 * @param size
	 * @param propagated
	 * @param criterion
	 * @return
	 */
	List<MAlert> getAlertsBySource(Source source, Order order,
			Integer startPosition, Integer size, Boolean propagated,
			final Criterion criterion);

	/**
	 * 
	 * @param criterion
	 * @return
	 */
	Long getAlertsCount(Criterion criterion);

	/**
	 * Computes the summary duration of alerts with provided
	 * {@link PerceivedSeverity} of each agent during provided interval.
	 * 
	 * @param severities
	 * @param intervalType
	 * 
	 * @return - Map of results (summary duration by agent's displayName) or
	 *         null if there are no alerts in the system
	 */
	Map<String, Long> getAlertsSummaryDurationByAgentKey(
			Set<PerceivedSeverity> severities, Type intervalType);

	/**
	 * Имена типов алёртов
	 * 
	 * @return
	 */
	Set<String> getAllAlertTypeNames();

	/**
	 * 
	 * @return all alert types
	 */
	Map<String, MAlertType> getAllTypes();

	/**
	 * 
	 * @param sources
	 * @param propagated
	 * @return
	 */
	Map<Source, PerceivedSeverity> getStatus(Collection<Source> sources,
			Boolean propagated);

	/**
	 * Регистрация нового типа алёрта
	 * 
	 * @param type
	 */
	void registerAlertType(MAlertType type);

	/**
	 * 
	 * @param originator
	 */
	void resetDisabledAlerts(Source originator);

	/**
	 * 
	 * @param indication
	 * @param comment
	 * @param accountToken
	 * @return
	 */
	MAlert unAcknowledgeAlert(MAlertIndication indication, String comment,
			String accountToken);

	/**
	 * 
	 * @param indications
	 * @param comment
	 */
	void unAcknowledgeAlerts(List<MAlertIndication> indications, String comment);
}
