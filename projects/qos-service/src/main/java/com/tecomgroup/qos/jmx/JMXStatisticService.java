/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.jmx;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedMetric;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.jmx.support.MetricType;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.SystemComponentStatisticProvider;
import com.tecomgroup.qos.util.Utils;

/**
 * @author abondin
 * 
 */
@ManagedResource(objectName = Utils.DEFAULT_JMX_DOMAIN + ":name=QoSStatistics")
public class JMXStatisticService extends AbstractService
		implements
			InitializingBean {

	private MBeanExporter beanExporter;

	private SystemComponentStatisticProvider systemComponentStatisticProvider;

	private final SystemComponentStatisticProvider.SystemComponentEventListener systemComponentListener = new SystemComponentStatisticProvider.SystemComponentEventListener() {

		@Override
		public void onAgentDeletion(final AgentStatistic agentStatistic) {
			removeAgent(agentStatistic);
		}

		@Override
		public void onAgentRegister(final AgentStatistic agentStatistic) {
			addAgent(agentStatistic);
		}

		@Override
		public void onListenerRegistration(
				final List<AgentStatistic> agentStatistics) {
			for (final AgentStatistic agentStatistic : agentStatistics) {
				addAgent(agentStatistic);
			}
		}
	};

	private final Map<String, AgentStatisticsBean> agentStatistics = new HashMap<String, AgentStatisticsBean>();

	private final static Logger LOGGER = Logger
			.getLogger(JMXStatisticService.class);

	private void addAgent(final AgentStatistic agentStatistic) {
		try {
			final AgentStatisticsBean statistics = new AgentStatisticsBean(
					agentStatistic);
			beanExporter.registerManagedResource(statistics);
		} catch (final Exception e) {
			LOGGER.error("Cannot register agent in JMX: "
					+ agentStatistic.getComponent().getDisplayName(), e);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		systemComponentStatisticProvider
				.registerListener(systemComponentListener);
	}

	public Map<String, AgentStatisticsBean> getAgentStatistics() {
		return agentStatistics;
	}

	@ManagedMetric(displayName = "Total Number of Handled Results", metricType = MetricType.COUNTER, unit = "results")
	public Long getHandledResults() {
		return systemComponentStatisticProvider.getHandledAgentResultsCount();
	}

	@ManagedAttribute(description = "Result statistic start time")
	public Date getResultsStatisticStartTime() {
		return systemComponentStatisticProvider.getAgentResultsStartTime();
	}

	public SystemComponentStatisticProvider getSystemComponentStatisticService() {
		return systemComponentStatisticProvider;
	}

	private void removeAgent(final AgentStatistic agentStatistic) {
		try {
			final AgentStatisticsBean statistics = new AgentStatisticsBean(
					agentStatistic);
			beanExporter.unregisterManagedResource(statistics.getObjectName());
		} catch (final Exception e) {
			LOGGER.error("Cannot unregister agent in JMX: "
					+ agentStatistic.getComponent().getDisplayName(), e);
		}
	}

	/**
	 * @param beanExporter
	 *            the beanExporter to set
	 */
	public void setBeanExporter(final MBeanExporter beanExporter) {
		this.beanExporter = beanExporter;
	}

	public void setSystemComponentStatisticProvider(
			final SystemComponentStatisticProvider systemComponentStatisticService) {
		this.systemComponentStatisticProvider = systemComponentStatisticService;
	}
}
