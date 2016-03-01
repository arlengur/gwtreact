/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.rest.data.ParameterGroup;
import com.tecomgroup.qos.service.rbac.AuthorizeService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.AgentStatistic;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MSystemComponent;
import com.tecomgroup.qos.event.AbstractEvent.EventType;

/**
 * A service to aggregate {@link MSystemComponent} statistics (for example,
 * result statistics, component registration statistics etc)
 * 
 * @author sviyazov.a
 * 
 */
@Service("systemComponentStatisticService")
public class DefaultSystemComponentStatisticService extends AbstractService
		implements
			SystemComponentStatisticService,
			SystemComponentStatisticProvider,
			InitializingBean {
	private volatile Long handledResults = 0l;

	private Map<String, ParameterGroup> groupsConfig;

	private Date resultsStatisticStartTime;

	private final Map<String, AgentStatistic> agentStatistics = new ConcurrentHashMap<>();

	private final List<SystemComponentEventListener> listeners = new ArrayList<SystemComponentEventListener>();

	@Autowired
	private InternalTaskService taskService;

	@Autowired
	private InternalEventBroadcaster internalEventBroadcaster;

	@Autowired
	private AuthorizeService authorizeService;

	private static final long MAX_RESULTS = 100000000l;


	private AgentStatistic addAgentStatistics(final MAgent agent) {
		if(agent!=null) {
			final AgentStatistic agentStatistic = new AgentStatistic(agent);
			agentStatistics.put(agent.getKey(), agentStatistic);
			notifyListenersOnAgentRegister(agentStatistic);
			return agentStatistic;
		}
		return null;
	}

	private void fillAgentsStatistics() {
		List<MAgentTask> tasks=taskService.getAllTasks();
		for(MAgentTask task: tasks)
		{
			MAgent agent=task.getModule().getAgent();
			if(agent!=null && !agent.isDeleted())
			{
				AgentStatistic agentStatistic=getAgentsStatisticNotFiltered().get(agent.getKey());
				if(agentStatistic==null)
				{
					agentStatistic = addAgentStatistics(agent);
				}
				ParameterGroup group=getParameterGroup(task.getModule().getKey());
				agentStatistic.addTaskStatistic(task,group.name);
			}
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		initializeAgentStatistic();
		initializeResultStatistic();
		resetResultStatistics();
	}

	@Override
	public Date getAgentResultsStartTime() {
		return resultsStatisticStartTime;
	}

	private Map<String, AgentStatistic> getAgentsStatisticNotFiltered() {
		Map<String, AgentStatistic> statisticClone=new HashMap<>();
		statisticClone.putAll(agentStatistics);
		return statisticClone;
	}

	@Override
	public Map<String, AgentStatistic> getAgentsStatistic() {
		Map<String, AgentStatistic> statisticClone=new HashMap<>();
		List<String> agentKeys = authorizeService.getProbeKeysUserCanManage();
		for(String agent: agentKeys) {
			if(agentStatistics.containsKey(agent)) {
				statisticClone.put(agent, agentStatistics.get(agent));
			}
		}
		return statisticClone;
	}

	@Override
	public AgentStatistic getAgentsStatisticByComponentKey(String key) {
		if (key != null) {
			for (AgentStatistic statistic : agentStatistics.values()) {
				if (key.equals(statistic.getComponent().getKey())) {
					return statistic;
				}
			}
		}
		return null;
	}

	@Override
	public Long getHandledAgentResultsCount() {
		return handledResults;
	}

	@SuppressWarnings("serial")
	private void initializeAgentStatistic() {
		fillAgentsStatistics();
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				final AgentChangeStateEvent agentChangeStateEvent = (AgentChangeStateEvent) event;
				updateAgentStatistic(agentChangeStateEvent);
			}
		}, new QoSEventFilter() {
			@Override
			public boolean accept(final AbstractEvent event) {
				return event instanceof AgentChangeStateEvent;
			}
		});
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				final AgentEvent agentEvent = (AgentEvent) event;

				if (event.getEventType() == EventType.UPDATE || event.getEventType() == EventType.DELETE) {
					removeAgentStatistic(agentEvent.getAgent().getKey());
				}
			}
		}, new QoSEventFilter() {
			@Override
			public boolean accept(final AbstractEvent event) {
				return event instanceof AgentEvent;
			}
		});
	}

	@SuppressWarnings("serial")
	private void initializeResultStatistic() {
		internalEventBroadcaster.subscribe(new QoSEventListener() {
			@Override
			public void onServerEvent(final AbstractEvent event) {
				final ResultEvent resultEvent = (ResultEvent) event;
				updateResultStatistic(resultEvent.getAgentName(),
						resultEvent.getResultCount());
			}
		}, new QoSEventFilter() {
			@Override
			public boolean accept(final AbstractEvent event) {
				return event instanceof ResultEvent;
			}
		});
	}

	private void notifyListenersOnAgentDeletion(
			final AgentStatistic agentStatistic) {
		for (final SystemComponentEventListener listener : listeners) {
			listener.onAgentDeletion(agentStatistic);
		}
	}

	private void notifyListenersOnAgentRegister(
			final AgentStatistic agentStatistic) {
		for (final SystemComponentEventListener listener : listeners) {
			listener.onAgentRegister(agentStatistic);
		}
	}

	@Override
	public void registerListener(final SystemComponentEventListener listener) {
		listeners.add(listener);
			listener.onListenerRegistration(new ArrayList<AgentStatistic>(
					agentStatistics.values()));
	}

	private void removeAgentStatistic(final String agentKey) {
			final AgentStatistic agentStatistic = agentStatistics
					.remove(agentKey);
			if (agentStatistic != null) {
				agentStatistic.resetTasksStatistic();
				notifyListenersOnAgentDeletion(agentStatistic);
			}
	}

	public void resetResultStatistics() {
			resultsStatisticStartTime = new Date();
			handledResults = 0l;
			for (final AgentStatistic agentStatistic : agentStatistics.values()) {
				agentStatistic.setHanldedResults(0l);
				agentStatistic.setLastResultTime(null);
			}
	}

	public void resetAgentStatistics(final String agentKey) {
		final AgentStatistic agentStatistic = agentStatistics
				.get(agentKey);
		if (agentStatistic != null) {
			//agentStatistic.resetTasksStatistic();
			agentStatistic.setState(MAgent.AgentRegistrationState.NO_STATE);
			agentStatistic.setRegistrationTime(null);
			agentStatistic.setLastResultTime(null);
			agentStatistic.setHanldedResults(0l);
			agentStatistic.setAgentVersion(null);
		}
	}

	private void updateAgentStatistic(final AgentChangeStateEvent event) {
		AgentStatistic statistics = agentStatistics.get(event.getAgentKey());
		final MAgent agent = event.getAgent();
		if (agent != null) {
			if (statistics == null) {
				statistics = addAgentStatistics(agent);
			}
			// update agent as some of its properties could be changed
			if(statistics!=null) {
				//refresh component ID, in case when statistic creates from newly registed agent
				if(statistics.getId()==null)
				{
					statistics.setId(agent.getId());
				}
				statistics.setComponent(agent);
				statistics.setState(event.getState());
				if (MAgent.AgentRegistrationState.IN_PROGRESS == event.getState()) {
					statistics.setRegistrationTime(event.getDate());
					statistics.setAgentVersion(event.getAgentVersion());
				}
				//reset task statistic during registration process
				if(MAgent.AgentRegistrationState.ACCEPTED == event.getState()
				|| MAgent.AgentRegistrationState.IN_PROGRESS == event.getState())
				{
					statistics.resetTasksStatistic();
				}
			}
			List<MAgentTask> tasks=event.getTasks();
			if(tasks!=null && !tasks.isEmpty())
			{
				for (MAgentTask task : tasks) {
					ParameterGroup group=getParameterGroup(task.getModule().getKey());
					statistics.addTaskStatistic(task,group.name);
				}
			}
		}

	}

	private void updateResultStatistic(final String agentKey,
			final int resultCount) {
			handledResults += resultCount;
			final AgentStatistic agentStatistic = agentStatistics.get(agentKey);
			if (agentStatistic != null) {
				agentStatistic.recordHandledResults(resultCount, new Date());

			}
			if (handledResults > MAX_RESULTS) {
				resetResultStatistics();
			}
	}

	private ParameterGroup getParameterGroup(String moduleKey) {
		//@TODO remove this , is duplicate from channel view
		if(groupsConfig==null)
		{
			groupsConfig = getParametersGroupConfig();
		}
		for (String key : groupsConfig.keySet()) {
			if (moduleKey != null && moduleKey.contains(key)) {
				return groupsConfig.get(key);
			}
		}
		/**
		 * @TODO Log error , no param group defined for current module
		 */
		return ParameterGroup.DATA;
	}

	public Map<String, ParameterGroup> getParametersGroupConfig() {
		//@TODO cache this object
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationConfig.Feature.WRITE_DATES_AS_TIMESTAMPS,
				false);
		try {
			URL url = getClass().getResource("/parameters-group-mapping.json");
			Map<String, ParameterGroup> config = mapper.readValue(url,
					new TypeReference<Map<String, ParameterGroup>>() {
					});
			return config;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
