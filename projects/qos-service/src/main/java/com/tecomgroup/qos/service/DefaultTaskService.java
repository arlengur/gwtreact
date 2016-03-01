/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.rest.data.Probe;
import com.tecomgroup.qos.rest.data.QoSTask;
import com.tecomgroup.qos.util.AuditLogger;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.tecomgroup.qos.OperationType;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.domain.MProperty.PropertyType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.event.AbstractEvent.EventType;
import com.tecomgroup.qos.event.TaskEvent;
import com.tecomgroup.qos.exception.DeletedSourceException;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;

/**
 * @author abondin
 * 
 */
public class DefaultTaskService extends AbstractService
		implements
			InternalTaskService,
			InitializingBean {

	private static Logger LOGGER = Logger.getLogger(DefaultTaskService.class);

	private AgentService agentService;

	private InternalPolicyConfigurationService policyConfigurationService;

	private StorageService storageService;

	private PropertyUpdater propertyUpdater;

	private TemplateDeleter templateDeleter;

	private WidgetDeleter widgetDeleter;

	private final Map<String, MAgentTask> taskCache = new HashMap<String, MAgentTask>();

	@Override
	public void afterPropertiesSet() throws Exception {
		initTaskConfigurations();
	}

	@Override
	public void createOrUpdateTask(final MAgentTask task) {
		EventType eventType = null;
		if (task.getId() != null) {
			eventType = EventType.UPDATE;
		} else {
			eventType = EventType.CREATE;
		}

		createOrUpdateTaskWithoutNotifyingListeners(task);

		notifyListenersWithoutTransaction(new TaskEvent(task, eventType));
	}

	private void createOrUpdateTaskWithoutNotifyingListeners(
			final MAgentTask task) {
		synchronized (taskCache) {
			taskCache.remove(task.getKey());
			executeInTransaction(false, new TransactionCallbackWithoutResult() {
				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					modelSpace.saveOrUpdate(task);
				}
			});
		}
	}

	@Override
	public void delete(final MAgentTask task) {
		if (!task.isDeleted()) {
			try {
				final List<MPolicy> taskPolicies = policyConfigurationService
						.getPolicies(Source.getTaskSource(task.getKey()));
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								if (widgetDeleter != null) {
									widgetDeleter
											.clearSourceRelatedWidgets(task);
								}
								task.setDeleted(true);
								createOrUpdateTaskWithoutNotifyingListeners(task);

								policyConfigurationService
										.deletePolicies(SimpleUtils
												.getKeys(taskPolicies));

								storageService.removeStorages(task);
								if (templateDeleter != null) {
									templateDeleter
											.clearSourceRelatedTemplates(task);
								}
								if (LOGGER.isInfoEnabled()) {
									LOGGER.info("Delete task: " + task);
								}
								notifyListenersInTransaction(new TaskEvent(
										task, EventType.DELETE));
							}
						});
			} catch (final Exception ex) {
				throw new ServiceException("Unable to delete task: " + task, ex);
			}
		}
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteTasks(final Set<String> taskKeys) {
		try {
			final List<MAgentTask> tasks = getTasksByKeys(taskKeys, false);
			for (final MAgentTask task : tasks) {
				delete(task);
			}
			AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.OK, "Tasks deleted : {}", Arrays.toString(taskKeys.toArray(new String[taskKeys.size()])));
		}catch (Exception e)
		{
			AuditLogger.major(AuditLogger.SyslogCategory.PROBE, AuditLogger.SyslogActionStatus.NOK,"Unable to delete tasks : {}, reason :", Arrays.toString(taskKeys.toArray(new String[taskKeys.size()])),e.getMessage());
			throw e;
		}
	}

	@Override
	public void disable(final MAgentTask task) {
		if (!task.isDisabled()) {
			try {
				final List<MPolicy> taskPolicies = policyConfigurationService
						.getPolicies(Source.getTaskSource(task.getKey()));
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								task.setDisabled(true);
								createOrUpdateTaskWithoutNotifyingListeners(task);

								policyConfigurationService
										.disablePolicies(SimpleUtils
												.getKeys(taskPolicies));

								storageService.removeStorages(task);

								if (LOGGER.isInfoEnabled()) {
									LOGGER.info("Disable task: " + task);
								}
								notifyListenersInTransaction(new TaskEvent(
										task, EventType.DELETE));
							}
						});
			} catch (final Exception ex) {
				throw new ServiceException("Unable to disable task: " + task,
						ex);
			}
		}
	}

	private void disableParameterPolicies(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier) {
		if (task != null) {
			final List<MPolicy> parameterPolicies = policyConfigurationService
					.getTaskParameterPolicies(task, parameterIdentifier);
			policyConfigurationService.disablePolicies(SimpleUtils
					.getKeys(parameterPolicies));
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentDeletedTasks(final String agentKey) {
		final List<String> agentModuleKeys = agentService
				.getAllModuleKeysByAgentKey(agentKey);

		final CriterionQuery query = modelSpace.createCriterionQuery();

		return modelSpace.find(
				MAgentTask.class,
				query.and(query.eq("deleted", true),
						query.in("parent.key", agentModuleKeys)));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentTasks(final Collection<String> agentNames,
			final Integer startPosition, final Integer size,
			final boolean activeOnly) {
		final List<MAgentTask> tasks = new LinkedList<MAgentTask>();

		for (final String agentName : agentNames) {
			tasks.addAll(getAgentTasks(agentName, null, null, startPosition,
					size, false, activeOnly));
		}

		return tasks;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentTasks(final String agentName,
			final Set<String> moduleNames, final Integer startPosition,
			final Integer size) {
		return getAgentTasks(agentName, moduleNames, null, startPosition, size);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentTasks(final String agentName,
			final Set<String> moduleNames, final TimeInterval timeInterval,
			final Integer startPosition, final Integer size) {
		return getAgentTasks(agentName, moduleNames, timeInterval,
				startPosition, size, false);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentTasks(final String agentName,
			final Set<String> moduleNames, final TimeInterval timeInterval,
			final Integer startPosition, final Integer size,
			final boolean onlyWithParameters) {
		return getAgentTasks(agentName, moduleNames, timeInterval,
				startPosition, size, onlyWithParameters, true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getAgentTasks(final String agentName,
			final Set<String> moduleNames, final TimeInterval timeInterval,
			final Integer startPosition, final Integer size,
			final boolean onlyWithParameters, final boolean onlyActive) {
		final List<MAgentTask> result = new ArrayList<MAgentTask>();
		try {

			final Criterion resultCriterion = getAgentTasksCriterion(agentName,
					moduleNames, timeInterval, startPosition, size,
					onlyWithParameters, onlyActive);

			if (resultCriterion != null) {
				result.addAll(modelSpace.find(MAgentTask.class,
						resultCriterion, null, startPosition, size));
			}
		} catch (final Exception ex) {
			throw new ServiceException(
					"Unable to get agent tasks by agentName = " + agentName
							+ ", modules = " + moduleNames
							+ ", timeInterval = " + timeInterval
							+ ", startPosition = " + startPosition
							+ ", size = " + size + ", onlyWithParameters = "
							+ onlyWithParameters + ", onlyActive = "
							+ onlyActive, ex);
		}

		Collections.sort(result,
						 new Comparator<MAgentTask>() {
							 public int compare(MAgentTask itemA,
												MAgentTask itemB) {
								 return itemA.getDisplayName().compareTo(itemB.getDisplayName());
							 }
							 
							 public boolean equals(Object other) {
								 return this.equals(other);
							 }
						 });
		
		return result;
	}

	private Criterion getAgentTasksCriterion(final String agentName,
			final Set<String> moduleNames, final TimeInterval timeInterval,
			final Integer startPosition, final Integer size,
			final boolean onlyWithParameters, final boolean onlyActive) {
		final Set<Long> moduleIds = new HashSet<Long>();
		final List<MAgentModule> agentModules = agentService
				.getAllModulesByAgentKey(agentName);
		for (final MAgentModule agentModule : agentModules) {
			if (((moduleNames == null || moduleNames.isEmpty()) || moduleNames
					.contains(agentModule.getKey()))
					&& (!onlyWithParameters || (onlyWithParameters && agentModule
							.hasTemplateParameters()))) {
				moduleIds.add(agentModule.getId());
			}
		}

		if (!moduleIds.isEmpty()) {
			final CriterionQuery query = modelSpace.createCriterionQuery();
			Criterion resultCriterion = query.in("module.id", moduleIds);
			resultCriterion = query.and(resultCriterion,
					Utils.createNotDeletedAndDisabledCriterion(onlyActive));
			if (timeInterval != null) {
				resultCriterion = query.and(
						resultCriterion,
						query.between("creationDateTime",
								timeInterval.getStartDateTime(),
								timeInterval.getEndDateTime()));
			}

			if (onlyWithParameters) {
				resultCriterion = query
						.and(resultCriterion,
								query.isNotEmpty("resultConfiguration.parameterConfigurations"));
			}

			return resultCriterion;
		}
		return null;
	}

	private List<MAgentTask> getAllActiveTasks() {
		final List<MAgentTask> activeAgentTasks = modelSpace.find(
				MAgentTask.class,
				Utils.createNotDeletedAndDisabledCriterion(true));
		return activeAgentTasks;
	}

	@Override
	public List<MAgentTask> getAllTasks() {

		return executeInTransaction(true,
				new TransactionCallback<List<MAgentTask>>() {
					@Override
					public List<MAgentTask> doInTransaction(
							final TransactionStatus status) {
						final CriterionQuery query = modelSpace.createCriterionQuery();
						return modelSpace.find(
								MAgentTask.class,
								query.and(query.eq("deleted", false),
										query.eq("disabled", false)));
					}
				});
	}


	@Override
	public MAgentTask getTaskByKey(final String taskKey) {
		return executeInTransaction(true,
				new TransactionCallback<MAgentTask>() {
					@Override
					public MAgentTask doInTransaction(
							final TransactionStatus status) {
						final CriterionQuery query = modelSpace
								.createCriterionQuery();

						return modelSpace.findUniqueEntity(
								MAgentTask.class,
								query.and(
										query.eq("key", taskKey),
										Utils.createNotDeletedAndDisabledCriterion(false)));
					}
				});
	}

	@Override
	public MAgentTask getTaskFromCache(final String taskKey) {
		MAgentTask task = null;
		synchronized (taskCache) {
			task = taskCache.get(taskKey);
			if (task == null) {
				task = getTaskByKey(taskKey);
				taskCache.put(taskKey, task);
			}
		}
		return task;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getTasksByKeys(final Collection<String> taskKeys) {
		return getTasksByKeys(taskKeys, true);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MAgentTask> getTasksByKeys(final Collection<String> taskKeys,
			final boolean activeOnly) {
		final List<MAgentTask> tasks = new ArrayList<>();
		if (SimpleUtils.isNotNullAndNotEmpty(taskKeys)) {
			final CriterionQuery query = modelSpace.createCriterionQuery();

			tasks.addAll(modelSpace.find(MAgentTask.class, query.and(
					Utils.createNotDeletedAndDisabledCriterion(activeOnly),
					query.in("key", taskKeys))));
		}
		return tasks;
	}

    @Override
    @Transactional(readOnly = true)
    public List<MAgentTask> getTasksByIds(final Collection<Long> ids) {
        final List<MAgentTask> tasks = new ArrayList<>();
        if (SimpleUtils.isNotNullAndNotEmpty(ids)) {
            final CriterionQuery query = modelSpace.createCriterionQuery();
            tasks.addAll(modelSpace.find(MAgentTask.class, query.in("id", ids)));
        }
        return tasks;
    }

	private void initTaskConfigurations() {
		final List<MAgentTask> activeTasks = executeInTransaction(true,
				new TransactionCallback<List<MAgentTask>>() {

					@Override
					public List<MAgentTask> doInTransaction(
							final TransactionStatus status) {
						return getAllActiveTasks();
					}
				});
		for (final MAgentTask activeTask : activeTasks) {
			updateTaskConfiguration(activeTask);
		}
	}

	private boolean isTaskDisabled(final String taskKey) {
		boolean result = false;
		final MAgentTask task = getTaskByKey(taskKey);
		if (task != null) {
			result = task.isDisabled();
		}
		return result;
	}

	private void registerTask(final String agentKey, final MAgentTask task) {
		final MAgentTask existingTask = executeInTransaction(true,
				new TransactionCallback<MAgentTask>() {

					@Override
					public MAgentTask doInTransaction(
							final TransactionStatus status) {
						return modelSpace.findUniqueEntity(
								MAgentTask.class,
								modelSpace.createCriterionQuery().eq("key",
										task.getKey()));
					}
				});
		if (existingTask != null) {
			if (existingTask.isDeleted()) {
				final String errorMessage = "Task " + task.getKey()
						+ " was already deleted";
				LOGGER.info("Reject task registration: " + errorMessage);
				throw new DeletedSourceException(errorMessage);
			}
			// task is not updated here
			return;
		}

		final String moduleName = task.getModule().getKey();

		// Validate module
		final List<MAgentModule> agentModules = agentService
				.getAllModulesByAgentKey(agentKey);
		MAgentModule module = null;
		for (final MAgentModule moduleCandidate : agentModules) {
			if (moduleCandidate.getKey().equalsIgnoreCase(moduleName.trim())) {
				module = moduleCandidate;
				break;
			}
		}
		if (module == null) {
			throw new ServiceException("Cannot find module " + moduleName
					+ " for agent " + agentKey);
		}
		task.setParent(module);
		if (module.getTemplateResultConfiguration() != null) {
			final MResultConfiguration resultConfigurationFromTemplate = ConfigurationUtil
					.createFromTemplate(
							module.getTemplateResultConfiguration(), task);
			if (task.getResultConfiguration() != null) {
				resultConfigurationFromTemplate.setSamplingRate(task
						.getResultConfiguration().getSamplingRate());
			}
			task.setResultConfiguration(resultConfigurationFromTemplate);
		}
		task.setDisabled(false);
		task.setDeleted(false);
		final Date creationDateTime = new Date();
		task.setCreationDateTime(creationDateTime);
		task.setModificationDateTime(creationDateTime);
		createOrUpdateTask(task);

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Register new task: " + task);
		}

		policyConfigurationService.applyAgentPolicyTemplates(agentKey, task);
	}

	@Override
	public void registerTasks(final String agentName,
			final List<MAgentTask> tasks) {
		updateTasks(agentName, tasks);
	}

	@Override
	public MAgentTask resetDisabledTask(final String taskKey) {
		MAgentTask task = null;
		try {
			task = getTaskByKey(taskKey);
			if (task.isDisabled()) {
				task.setDisabled(false);
				createOrUpdateTask(task);
				for (final MResultParameterConfiguration parameterConfiguration : task
						.getResultConfiguration().getParameterConfigurations()) {
					if (!parameterConfiguration.isDisabled()) {
						policyConfigurationService
								.resetDisabledPolicies(task,
										parameterConfiguration
												.getParameterIdentifier());
					}
				}
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("Reset disabled task: " + taskKey);
				}
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to reset disabled task: "
					+ taskKey, ex);
		}
		return task;
	}

	public void setAgentService(final AgentService agentService) {
		this.agentService = agentService;
	}

	public void setPolicyConfigurationService(
			final InternalPolicyConfigurationService policyConfigurationService) {
		this.policyConfigurationService = policyConfigurationService;
	}

	public void setPropertyUpdater(final PropertyUpdater propertyUpdater) {
		this.propertyUpdater = propertyUpdater;
	}

	public void setStorageService(final StorageService storageService) {
		this.storageService = storageService;
	}

	public void setTemplateDeleter(final TemplateDeleter templateDeleter) {
		this.templateDeleter = templateDeleter;
	}

	public void setWidgetDeleter(final WidgetDeleter widgetDeleter) {
		this.widgetDeleter = widgetDeleter;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public boolean updateModuleAndTaskParameterConfigurations(
			final String agentKey, final String moduleKey,
			final MResultConfigurationTemplate oldTemplateConfiguration,
			final MResultConfigurationTemplate newTemplateConfiguration) {

		final Map<String, MResultParameterConfiguration> oldTemplateParameterConfigurationMap = oldTemplateConfiguration
				.getParameterConfigurationsAsMap();
		final Map<String, MResultParameterConfiguration> newTemplateParameterConfigurationMap = newTemplateConfiguration
				.getParameterConfigurationsAsMap();

		final List<MAgentTask> moduleTasks = executeInTransaction(true,
				new TransactionCallback<List<MAgentTask>>() {

					@Override
					public List<MAgentTask> doInTransaction(
							final TransactionStatus status) {
						return getAgentTasks(agentKey, new HashSet<String>(
								Arrays.asList(moduleKey)), null, null, null,
								true, false);
					}
				});

		// update template property configurations
		// it must be done before updating parameters
		boolean configurationsAreUpdated = updateTemplatePropertyConfigurations(
				oldTemplateConfiguration, newTemplateConfiguration, moduleTasks);

		// removed parameters
		final Set<String> removedTemplateParameters = new HashSet<String>(
				oldTemplateParameterConfigurationMap.keySet());
		removedTemplateParameters
				.removeAll(newTemplateParameterConfigurationMap.keySet());
		for (final String parameterStorageKey : removedTemplateParameters) {
			final MResultParameterConfiguration templateParameterConfigurationToRemove = oldTemplateParameterConfigurationMap
					.get(parameterStorageKey);
			if (!templateParameterConfigurationToRemove.isDisabled()) {
				templateParameterConfigurationToRemove.setDisabled(true);
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								modelSpace
										.save(templateParameterConfigurationToRemove);
							}
						});
				LOGGER.info("Disable template parameter configuration: "
						+ templateParameterConfigurationToRemove
								.getParameterIdentifier() + " of the module: "
						+ moduleKey);
				// update tasks parameter configurations
				updateTasksParameterConfigurationsByTemplateParameterConfiguration(
						moduleTasks, templateParameterConfigurationToRemove,
						OperationType.DELETE);
				configurationsAreUpdated = true;
			}
		}

		// new parameters
		final Set<String> newTemplateParameters = new HashSet<String>(
				newTemplateParameterConfigurationMap.keySet());
		newTemplateParameters.removeAll(oldTemplateParameterConfigurationMap
				.keySet());
		for (final String parameterStorageKey : newTemplateParameters) {
			final MResultParameterConfiguration templateParameterConfigurationToAdd = newTemplateParameterConfigurationMap
					.get(parameterStorageKey);
			executeInTransaction(false, new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					modelSpace
							.saveOrUpdate(templateParameterConfigurationToAdd);
					oldTemplateConfiguration
							.addParameterConfiguration(templateParameterConfigurationToAdd);
				}
			});
			// don't add task parameter configuration. They are added on the
			// fly in runtime.
			LOGGER.info("Add template parameter configuration: "
					+ templateParameterConfigurationToAdd
							.getParameterIdentifier() + " to the module: "
					+ moduleKey);
			configurationsAreUpdated = true;
		}

		// updated parameters
		final Set<String> updatedTemplateParameters = new HashSet<String>(
				oldTemplateParameterConfigurationMap.keySet());
		updatedTemplateParameters
				.retainAll(newTemplateParameterConfigurationMap.keySet());
		for (final String parameterStorageKey : updatedTemplateParameters) {
			final MResultParameterConfiguration newTemplateParameterConfiguration = newTemplateParameterConfigurationMap
					.get(parameterStorageKey);
			final MResultParameterConfiguration oldTemplateParameterConfiguration = oldTemplateParameterConfigurationMap
					.get(parameterStorageKey);

			final boolean parameterWasDisabled = oldTemplateParameterConfiguration
					.isDisabled();
			if (parameterWasDisabled
					|| oldTemplateParameterConfiguration
							.updateSimpleFields(newTemplateParameterConfiguration)) {
				oldTemplateParameterConfiguration.setDisabled(false);
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								modelSpace
										.saveOrUpdate(oldTemplateParameterConfiguration);
							}
						});
				if (parameterWasDisabled) {
					LOGGER.info("Reset disabled template parameter configuration: "
							+ oldTemplateParameterConfiguration
									.getParameterIdentifier()
							+ " of the module: " + moduleKey);
				} else {
					LOGGER.info("Update template parameter configuration: "
							+ oldTemplateParameterConfiguration
									.getParameterIdentifier()
							+ " of the module: " + moduleKey);
				}
				if (SimpleUtils.isNotNullAndNotEmpty(moduleTasks)) {
					// update tasks parameter configurations
					updateTasksParameterConfigurationsByTemplateParameterConfiguration(
							moduleTasks, oldTemplateParameterConfiguration,
							OperationType.UPDATE);
				}
				configurationsAreUpdated = true;
			}
		}

		if (configurationsAreUpdated
				&& SimpleUtils.isNotNullAndNotEmpty(moduleTasks)) {
			// update task result configuration template
			for (final MAgentTask task : moduleTasks) {
				task.getResultConfiguration().setTemplateResultConfiguration(
						oldTemplateConfiguration);
				createOrUpdateTask(task);
				LOGGER.info("Update task: " + task);
			}
		}

		return configurationsAreUpdated;
	}

	private void updateParameterConfiguraiton(final MAgentTask task,
			final MResultParameterConfiguration parameterConfiguration,
			final boolean resetParameter, final boolean disableParameter) {
		executeInTransaction(false, new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				modelSpace.saveOrUpdate(parameterConfiguration);
			}
		});
		if (resetParameter) {
			LOGGER.info("Reset disabled parameter configuration: "
					+ parameterConfiguration.getParameterIdentifier()
					+ " of the task: " + task);
		} else if (disableParameter) {
			LOGGER.info("Disable parameter configuration: "
					+ parameterConfiguration.getParameterIdentifier()
					+ " of the task: " + task);
		} else {
			LOGGER.info("Update parameter configuration: "
					+ parameterConfiguration.getParameterIdentifier()
					+ " of the task: " + task);
		}
	}

	private void updateParameterProperties(
			final MProperty templatePropertyConfiguration,
			final List<MAgentTask> moduleTasks) {

		for (final MAgentTask task : moduleTasks) {
			for (final MResultParameterConfiguration parameterConfiguration : task
					.getResultConfiguration().getParameterConfigurations()) {
				final MProperty parameterProperty = parameterConfiguration
						.getProperty(templatePropertyConfiguration.getName());

				if (templatePropertyConfiguration.isRequired()
						^ parameterProperty.isRequired()) {
					disableParameterPolicies(task,
							parameterConfiguration.getParameterIdentifier());

					parameterProperty.setRequired(templatePropertyConfiguration
							.isRequired());
					executeInTransaction(false,
							(new TransactionCallbackWithoutResult() {

								@Override
								protected void doInTransactionWithoutResult(
										final TransactionStatus status) {
									modelSpace.saveOrUpdate(parameterProperty);
								}
							}));
					LOGGER.info("Update property: " + parameterProperty
							+ " of the parameter: " + parameterConfiguration);
					// reset existing disabled policies
					policyConfigurationService.resetDisabledPolicies(task,
							parameterConfiguration.getParameterIdentifier());
				}
			}
		}
	}

	private void updateTask(final String agentName,
			final MAgentTask existingTask, final MAgentTask updatedTask) {
		try {
			// update configuration
			if (updatedTask.getResultConfiguration() != null) {
				// updatedTask.getResultConfiguration() contains ONLY new
				// samplingRate and NOTHING MORE. Don't update anything else
				// using it.
				if (!existingTask
						.getResultConfiguration()
						.getSamplingRate()
						.equals(updatedTask.getResultConfiguration()
								.getSamplingRate())) {
					storageService.updateSamplingRate(existingTask, updatedTask
							.getResultConfiguration().getSamplingRate());
				}
			}
			final boolean taskIsUpdated = existingTask
					.updateSimpleFields(updatedTask)
					| propertyUpdater.updateProperties(existingTask,
							updatedTask, PropertyType.ALL);
			if (taskIsUpdated) {
				createOrUpdateTask(existingTask);
				LOGGER.info("Update task: " + existingTask);
			}
		} catch (final Exception ex) {
			throw new ServiceException("Unable to update task: " + existingTask
					+ " for agent: " + agentName, ex);
		}
	}

	@Override
	public void updateTaskConfiguration(final MAgentTask task) {
		if (task.getResultConfiguration() == null
				&& task.getModule().getTemplateResultConfiguration() != null) {
			final MResultConfiguration taskResultConfiguration = ConfigurationUtil
					.createFromTemplate(task.getModule()
							.getTemplateResultConfiguration(), task);
			task.setResultConfiguration(taskResultConfiguration);
			createOrUpdateTask(task);
			LOGGER.info("Create resultConfiguration for task: " + task.getKey());

		} else if (task.getModule().getTemplateResultConfiguration() == null) {
			LOGGER.error("Template result configuration is NULL for module: "
					+ task.getModule().getKey());
		}
	}

	private void updateTaskParameterConfigurationsByTemplateParameterConfiguration(
			final MAgentTask task,
			final MResultParameterConfiguration templateParameterConfiguration,
			final OperationType type) {
		final List<MResultParameterConfiguration> taskParameterConfigurations = task
				.getResultConfiguration().findParameterConfigurations(
						templateParameterConfiguration.getName());

		for (final MResultParameterConfiguration taskParameterConfiguration : taskParameterConfigurations) {
			final ParameterIdentifier parameterIdentifier = taskParameterConfiguration
					.getParameterIdentifier();
			switch (type) {
				case ADD : {
					// do nothing, parameters are added on the fly
					break;
				}
				case UPDATE : {
					boolean parameterIsUpdated = taskParameterConfiguration
							.updateSimpleFields(templateParameterConfiguration);
					final boolean parameterWasDisabled = taskParameterConfiguration
							.isDisabled();
					if (parameterWasDisabled) {
						taskParameterConfiguration.setDisabled(false);
						parameterIsUpdated = true;
					}
					final String templateDisplayFormat = task.getModule()
							.getTemplateResultConfiguration()
							.getParameterDisplayNameFormat();
					if (!MAbstractEntity.equals(
							taskParameterConfiguration.getDisplayFormat(),
							templateDisplayFormat)) {
						taskParameterConfiguration
								.setDisplayFormat(templateDisplayFormat);
						parameterIsUpdated = true;
					}

					if (parameterIsUpdated) {
						updateParameterConfiguraiton(task,
								taskParameterConfiguration,
								parameterWasDisabled, false);
					}

					if (parameterWasDisabled && !task.isDisabled()) {
						policyConfigurationService.resetDisabledPolicies(task,
								parameterIdentifier);
					}
					break;
				}
				case DELETE : {
					if (!taskParameterConfiguration.isDisabled()) {
						taskParameterConfiguration.setDisabled(true);
						updateParameterConfiguraiton(task,
								taskParameterConfiguration, false, true);
						storageService.removeStorage(task, parameterIdentifier);
						disableParameterPolicies(task, parameterIdentifier);
					}
					break;
				}
				default : {
					throw new UnsupportedOperationException(
							"Operation not supported: " + type);
				}
			}
		}
	}

	@Override
	public void updateTasks(final String agentName, final List<MAgentTask> tasks) {
		final List<MAgentTask> existingTasks = executeInTransaction(true,
				new TransactionCallback<List<MAgentTask>>() {

					@Override
					public List<MAgentTask> doInTransaction(
							final TransactionStatus status) {
						return getAgentTasks(agentName, null, null, null);
					}
				});

		final Set<String> existingTaskKeys = new HashSet<String>();
		final Map<String, MAgentTask> existingTaskMap = new HashMap<String, MAgentTask>();
		for (final MAgentTask existingTask : existingTasks) {
			existingTaskKeys.add(existingTask.getKey());
			existingTaskMap.put(existingTask.getKey(), existingTask);
		}

		final Set<String> updatedTaskKeys = new HashSet<String>();
		final Map<String, MAgentTask> updatedTaskMap = new HashMap<String, MAgentTask>();
		for (final MAgentTask updatedTask : tasks) {
			updatedTaskKeys.add(updatedTask.getKey());
			updatedTaskMap.put(updatedTask.getKey(), updatedTask);
		}

		// find tasks to remove
		final Set<String> tasksToRemove = new HashSet<String>(existingTaskKeys);
		tasksToRemove.removeAll(updatedTaskKeys);
		// disable found tasks
		for (final String taskToRemove : tasksToRemove) {
			disable(existingTaskMap.get(taskToRemove));
		}

		// find tasks to add
		final Set<String> tasksToAdd = new HashSet<String>(updatedTaskKeys);
		tasksToAdd.removeAll(existingTaskKeys);
		// register found tasks
		for (final String taskToAdd : tasksToAdd) {
			if (!isTaskDisabled(taskToAdd)) {
				registerTask(agentName, updatedTaskMap.get(taskToAdd));
			} else {
				// make task alive and add it to existingTaskMap to be updated
				final MAgentTask task = resetDisabledTask(taskToAdd);
				existingTaskMap.put(taskToAdd, task);
				existingTaskKeys.add(taskToAdd);
			}
		}

		// find tasks to update
		final Set<String> tasksToUpdate = new HashSet<String>(existingTaskKeys);
		tasksToUpdate.retainAll(updatedTaskKeys);
		// update found tasks
		for (final String taskToUpdate : tasksToUpdate) {
			updateTask(agentName, existingTaskMap.get(taskToUpdate),
					updatedTaskMap.get(taskToUpdate));
		}
	}

	private void updateTasksParameterConfigurationsByTemplateParameterConfiguration(
			final List<MAgentTask> tasks,
			final MResultParameterConfiguration templateParameterConfiguration,
			final OperationType type) {
		for (final MAgentTask task : tasks) {
			updateTaskParameterConfigurationsByTemplateParameterConfiguration(
					task, templateParameterConfiguration, type);
		}
	}

	private boolean updateTemplatePropertyConfigurations(
			final MResultConfigurationTemplate oldTemplateConfiguration,
			final MResultConfigurationTemplate newTemplateConfiguration,
			final List<MAgentTask> moduleTasks) {

		final Map<String, MProperty> oldTemplatePropertyConfigurations = ParameterIdentifier
				.getProperties(oldTemplateConfiguration
						.getPropertyConfigurations());

		// update required field of the properties of the existing parameters.
		// All fields of template property configurations will be updated below
		// via propertyUpdater.
		final List<MProperty> newTemplatePropertyConfigurations = newTemplateConfiguration
				.getPropertyConfigurations();
		if (SimpleUtils.isNotNullAndNotEmpty(newTemplatePropertyConfigurations)) {
			for (final MProperty newTemplatePropertyConfiguration : newTemplatePropertyConfigurations) {
				final MProperty oldTemplatePropertyConfiguration = oldTemplatePropertyConfigurations
						.get(newTemplatePropertyConfiguration.getName());

				if ((oldTemplatePropertyConfiguration != null)
						&& (newTemplatePropertyConfiguration.isRequired() ^ oldTemplatePropertyConfiguration
								.isRequired())) {

					if (SimpleUtils.isNotNullAndNotEmpty(moduleTasks)) {
						updateParameterProperties(
								newTemplatePropertyConfiguration, moduleTasks);
					}
				}
			}
		}

		return propertyUpdater.updateProperties(oldTemplateConfiguration,
				newTemplateConfiguration, PropertyType.ALL);
	}

	@Override
	public boolean validateTaskConfiguration(final MAgentTask task) {
		boolean result = true;

		MResultConfiguration configuration = task.getResultConfiguration();
		if (configuration == null) {
			updateTaskConfiguration(task);
			configuration = task.getResultConfiguration();
		}
		if (configuration == null) {
			result = false;
			throw new ServiceException(
					"Result configuration not found for task = "
							+ task.getKey());
		}
		return result;
	}
}
