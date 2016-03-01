/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.alert;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.event.AlertEvent;
import com.tecomgroup.qos.service.InternalEventBroadcaster;
import com.tecomgroup.qos.service.SystemComponentStatisticService;

/**
 * @author abondin
 * 
 */
@Service("alertService")
public class PropagationAlertService extends DefaultAlertService {

	private final Map<Source, Source> childToParent = new HashMap<Source, Source>();

	private boolean propagationEnabled = true;

	private boolean postProcessAlerts = true;

	private final static Logger LOGGER = Logger
			.getLogger(PropagationAlertService.class);

	@Autowired
	private InternalEventBroadcaster internalEventBroadcaster;

	@Autowired
	private AgentStatusMonitor statusMonitor;

	@Autowired
	SystemComponentStatisticService statisticService;


	private void addPropagatedSources(final List<String> sourceKeys,
			final Source source) {
		sourceKeys.add(source.getKey());
		for (final Map.Entry<Source, Source> entry : childToParent.entrySet()) {
			if (source.equals(entry.getValue())) {
				addPropagatedSources(sourceKeys, entry.getKey());
			}
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAlert> getAlertsBySource(final Source source,
			final Order order, final Integer startPosition, final Integer size,
			final Boolean propagated, final Criterion criterion) {

		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion sourceCriterion = getAlertsBySourceCriterion(source);
		if (criterion != null) {
			sourceCriterion = query.and(criterion, sourceCriterion);
		}

		final List<MAlert> alerts = modelSpace.find(MAlert.class,
				sourceCriterion, order, startPosition, size);
		postProcessAlerts(alerts);
		return alerts;
	}

	private Criterion getAlertsBySourceCriterion(final Source source) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion criterion = query.eq("disabled", false);

		if (source!=null) {
			final Criterion sourceCriterion = query
					.eq("source.parent.parent.key", source.getKey());
			criterion = query.and(criterion, sourceCriterion);
		}
		return criterion;
	}

	@Override
	public Map<Source, PerceivedSeverity> getStatus(
			final Collection<Source> sources, final Boolean propagated) {
		Map<String, AgentStatistic> registeredAgentsStatistic=statisticService.getAgentsStatistic();
		final Map<Source, PerceivedSeverity> map = new HashMap<Source, MAlertType.PerceivedSeverity>();
		for (final Source source : sources) {
			AgentStatistic agentStatistic=registeredAgentsStatistic.get(source.getKey());
			if(agentStatistic!=null && agentStatistic.getState()!=null && MAgent.AgentRegistrationState.NO_STATE != agentStatistic.getState()) {
				PerceivedSeverity severity = getWorstSeverity(source);
				map.put(source, severity);
			}
		}
		return map;
	}

	private PerceivedSeverity getWorstSeverity(final Source source) {
		return statusMonitor.getWorstSeverity(source.getKey());
	}

	@Override
	public void init() {
		super.init();
		if (postProcessAlerts) {
			// TODO put here initialization to post process alerts
		}
	}

	@Override
	protected void postProcessAlert(final MAlert alert) {
		if (alert == null || !postProcessAlerts) {
			return;
		}
		// TODO Add code here to post process alerts
	}

	@Override
	protected void sendAlertEvent(final AlertEvent event) {
		super.sendAlertEvent(event);
		if (propagationEnabled) {
			statusMonitor.sendStatusEvent(event);
		}
	}

	/**
	 * @param internalEventBroadcaster
	 *            the internalEventBroadcaster to set
	 */
	public void setInternalEventBroadcaster(
			final InternalEventBroadcaster internalEventBroadcaster) {
		this.internalEventBroadcaster = internalEventBroadcaster;
	}
	/**
	 * @param postProcessAlerts
	 *            the postProcessAlerts to set
	 */
	public void setPostProcessAlerts(final boolean postProcessAlerts) {
		this.postProcessAlerts = postProcessAlerts;
	}

	/**
	 * @param propagationEnabled
	 *            the propagationEnabled to set
	 */
	public void setPropagationEnabled(final boolean propagationEnabled) {
		this.propagationEnabled = propagationEnabled;
	}
}
