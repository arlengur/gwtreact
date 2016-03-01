/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.ResultConfigurationSettings;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.AggregationType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.util.ConfigurationUtil;
import com.tecomgroup.qos.util.RrdUtil;
import com.tecomgroup.qos.util.Utils;
import org.apache.log4j.Logger;
import org.rrd4j.ConsolFun;
import org.rrd4j.DsType;
import org.rrd4j.core.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

/**
 * @author kunilov.p
 *
 */
@Service("storageService")
public class DefaultStorageService extends AbstractService
		implements
			StorageService,
			InitializingBean,
			DisposableBean {

	private class UpdateSamplingRate implements Runnable {

		private final MAgentTask task;
		private final MResultParameterConfiguration parameterConfiguration;
		private final Long newSamplingRate;
		private final Long oldSamplingRate;

		public UpdateSamplingRate(final MAgentTask task,
				final MResultParameterConfiguration parameterConfiguration,
				final Long newSamplingRate, final Long oldSamplingRate) {
			this.task = task;
			this.parameterConfiguration = parameterConfiguration;
			this.newSamplingRate = newSamplingRate;
			this.oldSamplingRate = oldSamplingRate;
		}

		private String getDescription() {
			return "Task: " + task.getKey() + ", parameter: "
					+ parameterConfiguration.getName() + ", oldSamplingRate: "
					+ oldSamplingRate + ", newSamplingRate: " + newSamplingRate;
		}

		@Override
		public void run() {
			closeOpenStorage(task,
					parameterConfiguration.getParameterIdentifier(),
					doesLocationExist(parameterConfiguration.getLocation()));
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Sampling rate was changed: " + getDescription());
			}
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + ": " + getDescription();
		}
	}

	private static Logger LOGGER = Logger
			.getLogger(DefaultStorageService.class);

	private static final String DEFAULT_DATASOURCE = "datasource";

	private int storedDaysCount;

	private String storageHome;

	private int batchSize;

	private int rrdPoolCapacity;

	private String rrdBackendFactory;

	/**
	 * The initialization of {@link RrdBackendFactory} can be done only once in
	 * static context {@link RrdBackendFactory#setDefaultFactory(String)} or
	 * {@link RrdDb#setDefaultFactory(String)}. As we initialize
	 * {@link RrdBackendFactory} in non-static context, it is necessary to check
	 * the initialization state not to initialize it twice.
	 */
	private static boolean rrdBackendFactoryIsInitialized = false;

	/**
	 * The base of power-mode function to calculate aggregation step.
	 *
	 * @See {@link RRDResultService#getAdaptiveAggregationStep(Long, Date, Date)}
	 *      {@link RrdUtil#calcAggregationFactor(double, int)}
	 *      {@link RrdUtil#calcAggregationStep(double, int)}
	 */
	private int aggregationBase;

	private AsyncTaskExecutor executor;

	private InternalTaskService taskService;

	private InternalPolicyConfigurationService policyConfigurationService;

	private RrdDbPool rrdPool;

	private final Map<String, String> storages = new ConcurrentHashMap<String, String>();

	private final Map<String, Object> storageKeys = new HashMap<String, Object>();

	private MResultParameterConfiguration addNewParameterConfiguration(
			final MAgentTask task, final ParameterIdentifier parameterIdentifier) {
		final MResultConfiguration resultConfiguration = task
				.getResultConfiguration();

		final MResultConfigurationTemplate resultConfigurationTemplate = task
				.getModule().getTemplateResultConfiguration();
		final MResultParameterConfiguration parameterConfigurationTemplate = resultConfigurationTemplate
				.findTemplateParameterConfiguration(parameterIdentifier);

		final MResultParameterConfiguration newParameterConfiguration = new MResultParameterConfiguration(
				parameterConfigurationTemplate, resultConfigurationTemplate);
		// update property values
		final Map<String, MProperty> parameterProperties = ParameterIdentifier
				.getProperties(newParameterConfiguration.getProperties());
		final Map<String, MProperty> providedProperties = parameterIdentifier
				.getPropertyMap();
		for (final Map.Entry<String, MProperty> providedPropertyEntry : providedProperties
				.entrySet()) {
			final MProperty parameterProperty = parameterProperties
					.get(providedPropertyEntry.getKey());
			final MProperty providedProperty = providedPropertyEntry.getValue();
			parameterProperty.setValue(providedProperty.getValue());

		}
		newParameterConfiguration.setLocation(createResultParameterLocation(
				task.getKey(), parameterIdentifier));
		resultConfiguration
				.addParameterConfiguration(newParameterConfiguration);
		taskService.createOrUpdateTask(task);

		policyConfigurationService.applyParameterPolicyTemplates(task,
				newParameterConfiguration.getParameterIdentifier());

		return newParameterConfiguration;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// this condition is only necessary for tests, when the context is
		// recreated.
		if (!rrdBackendFactoryIsInitialized) {
			try {
				RrdDb.setDefaultFactory(rrdBackendFactory);
				rrdBackendFactoryIsInitialized = true;
			} catch (final Exception e) {
				LOGGER.error("Can not set RRD backend factory", e);
			}
		}
		rrdPool = RrdDbPool.getInstance();
		rrdPool.setCapacity(rrdPoolCapacity);
	}

	private void closeOpenStorage(final MAgentTask task,
								  final ParameterIdentifier parameterIdentifier,
								  final boolean forceToOpen) {
		final Object synchronizedStorageKey = getStorageKeyForSynchronization(task.getKey(), parameterIdentifier);
		synchronized (synchronizedStorageKey) {
			final String storageKey = parameterIdentifier.createTaskStorageKey(task.getKey());
			if (forceToOpen) {
				if (storages.containsKey(storageKey)){
					removeStorages(task);
				}
				executeInRRD(task, parameterIdentifier, true, null, null);
			}
		}
	}

	private void closeRrdDb(final String filePath) {
		if (filePath != null) {
			try {
				final RrdDb rrdDb = rrdPool.requestRrdDb(filePath);
				while (rrdPool.getOpenCount(filePath) > 0) {
					rrdPool.release(rrdDb);
				}
			} catch (final Exception ex) {
				LOGGER.error("Unable to close rrd database by file path: "
						+ filePath, ex);
			}

		}
	}

	@SuppressWarnings("deprecation")
	private MResultParameterLocation createResultParameterLocation(
			final String taskKey, final ParameterIdentifier parameterIdentifier) {
		MResultParameterLocation parameterLocation = new MResultParameterLocation(
				parameterIdentifier.createParameterStorageKey(), taskKey + "/");

		if (parameterIdentifier.hasUnsafeRequiredProperties()) {
			parameterLocation = executeInTransaction(false,
					new TransactionCallback<MResultParameterLocation>() {

						@Override
						public MResultParameterLocation doInTransaction(
								final TransactionStatus status) {
							final MResultParameterLocation resultParameterLocation = new MResultParameterLocation(
									null, taskKey + "/");

							final Long parameterLocationId = modelSpace
									.save(resultParameterLocation);

							resultParameterLocation.setFileName(parameterIdentifier
									.createParameterStorageKeyAsSafeString(parameterLocationId
											.toString()));

							modelSpace.saveOrUpdate(resultParameterLocation);

							return resultParameterLocation;
						}
					});
		}
		return parameterLocation;
	}

	private void createStorageDestinationFile(final MResultParameterLocation parameterLocation) throws Exception {
		final File file = new File(parameterLocation.getFullFilePath(storageHome));
		if (!file.exists()) {
			try {
				(new File(storageHome + "/"	+ parameterLocation.getFileLocation())).mkdirs();
				file.createNewFile();
			} catch (final Exception ex) {
				throw new Exception("Unable to create storage destination file: " + parameterLocation.getFullFilePath(storageHome), ex);
			}
		}
	}

	private void createStorageWithoutValidation(final MAgentTask task,
			final MResultConfiguration configuration,
			final MResultParameterConfiguration parameterConfiguration,
			final Date startTime) {
		final MResultParameterLocation parameterLocation = parameterConfiguration.getLocation();
		final String storageFullPath = parameterLocation.getFullFilePath(storageHome);
		final RrdDef rrdDef = new RrdDef(storageFullPath,
										 RrdUtil.castTimeForRRD(startTime, configuration.getSamplingRate()),
										 configuration.getSamplingRate(),
										 2);

		rrdDef.addDatasource(DEFAULT_DATASOURCE,
				DsType.valueOf(ResultConfigurationSettings.CONFIGURATION_TYPE),
				RrdUtil.getHeartbeat(2 * configuration.getSamplingRate()),
				ResultConfigurationSettings.MAX_VALUE,
				ResultConfigurationSettings.MIN_VALUE);

		final ConsolFun aggregationType = ConsolFun
				.valueOf(parameterConfiguration.getAggregationType().toString());

		RrdUtil.createAggregations(rrdDef, aggregationType,
				configuration.getSamplingRate(), aggregationBase,
				storedDaysCount);

		try {
			createStorageDestinationFile(parameterLocation);
			rrdPool.requestRrdDb(rrdDef);
		} catch (final Exception ex) {
			throw new ServiceException("Unable to create storage: "
					+ storageFullPath, ex);
		} finally {
			// must not release storage here
		}

		synchronized (storages) {
			storages.put(parameterConfiguration.getParameterIdentifier()
					.createTaskStorageKey(task.getKey()), storageFullPath);
		}
	}

	private void deleteFile(final String fileLocation) {
		final File file = new File(fileLocation);
		file.delete();
	}

	@Override
	public void destroy() throws Exception {
		synchronized (storages) {
			for (final Map.Entry<String, String> storageEntry : storages
					.entrySet()) {
				closeRrdDb(storageEntry.getValue());
			}
		}
	}

	private boolean doesLocationExist(
			final MResultParameterLocation parameterLocation) {
		return (new File(parameterLocation.getFullFilePath(storageHome)))
				.exists();
	}

	@Override
	public Object executeInRRD(final MAgentTask task,
							   final ParameterIdentifier parameterIdentifier,
							   final Boolean createStorage,
							   final Boolean releaseStorage,
							   final Date startTime,
							   final StorageService.RrdRequest request) {
		final RrdDb rrdDb = openStorage(task, parameterIdentifier, createStorage, startTime);
		if (request == null) {
			return null;
		} else {
			if (rrdDb == null) {
				return null;
			}
			Object result = null;
			try {
				result = request.execute(rrdDb);
			} catch (final Exception ex) {
				LOGGER.error("Unable to execute in RRD.", ex);
			} finally {
				if (releaseStorage) {
					releaseStorage(rrdDb);
				}
			}
			return result;
		}
	}

	@Override
	public Object executeInRRD(final MAgentTask task,
			final ParameterIdentifier parameterIdentifier,
			final Boolean createStorage, final Date startTime,
			final StorageService.RrdRequest request) {
		return executeInRRD(task, parameterIdentifier, createStorage, true,
				startTime, request);
	}

	private MResultConfiguration getResultConfigurationFromStorage(
			final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final MResultParameterLocation parameterLocation) {
		final String parameterName = parameterIdentifier.getName();
		final MResultConfiguration resultConfiguration = new MResultConfiguration();
		AggregationType currentAggregationType = null;
		RrdDb rrdDb = null;
		try {
			rrdDb = openRrdDb(parameterLocation.getFullFilePath(storageHome));

			try {
				resultConfiguration.setSamplingRate(rrdDb.getHeader().getStep());
			} catch (final Exception ex) {
				throw new ServiceException(
						"Unable to get datasource samplingRate value of storage by path "
								+ parameterLocation.getFullFilePath(storageHome)
								+ ": " + ex.getMessage(), ex);
			}

			AggregationType previousAggregationType = null;
			for (int agggregationIndex = 0; agggregationIndex < rrdDb
					.getArcCount(); agggregationIndex++) {
				final Archive archive = rrdDb.getArchive(agggregationIndex);

				previousAggregationType = currentAggregationType;
				try {
					currentAggregationType = AggregationType.valueOf(archive
							.getConsolFun().toString());
				} catch (final Exception ex) {
					throw new ServiceException(
							"Unable to get archive aggregation type value of storage by path "
									+ parameterLocation.getFullFilePath(storageHome)
									+ ": " + ex.getMessage(), ex);
				}

				if (previousAggregationType != null
						&& currentAggregationType != null
						&& !currentAggregationType
								.equals(previousAggregationType)) {
					throw new ServiceException(
							"There are different aggregation types for one parameter inside storage. It is not supported by current implementation. "
									+ "Rrd file location = "
									+ parameterLocation
											.getFullFilePath(storageHome)
									+ ", parameterName = " + parameterName);
				}
			}
		} finally {
			releaseStorage(rrdDb);
		}

		final MResultParameterConfiguration parameterConfiguration = new MResultParameterConfiguration(
				parameterName, parameterName, currentAggregationType, null,
				null);
		parameterConfiguration.setLocation(parameterLocation);

		resultConfiguration.addParameterConfiguration(parameterConfiguration);

		return resultConfiguration;
	}

	private MResultConfiguration getResultConfigurationFromStorageWithFileExistenceCheck(
			final String taskKey,
			final ParameterIdentifier parameterIdentifier,
			final MResultParameterLocation location) {
		MResultConfiguration storageConfiguration = null;
		if ((new File(location.getFullFilePath(storageHome))).exists()) {
			try {
				// get storage configuration from file
				// system
				storageConfiguration = getResultConfigurationFromStorage(taskKey, parameterIdentifier, location);
			} catch (final ServiceException se) {
				throw new ServiceException(
						"Unable to get task result configuration of storage in the file system for task: "
								+ taskKey
								+ " and filePath = "
								+ location.getFullFilePath(storageHome), se);
			}
		}

		return storageConfiguration;
	}

	/**
	 * @return the storageHome
	 */
	public String getStorageHome() {
		return storageHome;
	}

	private Object getStorageKeyForSynchronization(final String taskKey,
			final ParameterIdentifier parameterIdentifier) {
		final String storageKey = parameterIdentifier
				.createTaskStorageKey(taskKey);
		Object synchronizedStorageKey = storageKeys.get(storageKey);
		if (synchronizedStorageKey == null) {
			synchronized (storageKeys) {
				synchronizedStorageKey = storageKeys.get(storageKey);
				if (synchronizedStorageKey == null) {
					synchronizedStorageKey = new Object();
					storageKeys.put(storageKey, synchronizedStorageKey);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("Create synchronizedStorageKey for storageKey: "
								+ storageKey);
					}
				}
			}
		}
		return synchronizedStorageKey;
	}

	@Override
	public MResultConfiguration initStorage(final MAgentTask task,
											final ParameterIdentifier parameterIdentifier,
											final Boolean createStorage,
											final Date startTime) {
		taskService.validateTaskConfiguration(task);

		final MResultConfiguration resultConfiguration = task.getResultConfiguration();
		MResultParameterConfiguration foundParameterConfiguration = resultConfiguration.findParameterConfiguration(parameterIdentifier);

		final boolean parameterShouldBeAdded = foundParameterConfiguration == null && createStorage
																				   && task.hasTemplateParameter(parameterIdentifier);
		if (parameterShouldBeAdded) {
			foundParameterConfiguration = addNewParameterConfiguration(task, parameterIdentifier);
		}

		if (foundParameterConfiguration == null && parameterShouldBeAdded) {
			throw new ServiceException("Parameter configuration is not found for task = " + task.getKey()
					               + ", parameterIdentifier = "	+ parameterIdentifier);
		}

		if (foundParameterConfiguration != null) {
			// flag indicates that there is a parameter configuration which
			// exists in db and in the file system and its configurations
			// are equal
			boolean existingStorageFound = false;
			// parameter configuration from the file system
			final MResultConfiguration storageConfiguration = getResultConfigurationFromStorageWithFileExistenceCheck(
					task.getKey(),
					foundParameterConfiguration.getParameterIdentifier(),
					foundParameterConfiguration.getLocation());
			// check whether configurations (from db and file system) are
			// compatible
			if (storageConfiguration != null && ConfigurationUtil.areResultConfigurationsCompatible(resultConfiguration, storageConfiguration)) {
				existingStorageFound = true;
			}

			// if storage doesn't exist in the file system or configurations (from db and file system) are not compatible
			if (createStorage && !existingStorageFound) {
				final MResultParameterLocation parameterLocation = foundParameterConfiguration.getLocation();
				if (!(new File(parameterLocation.getFullFilePath(storageHome))).exists()) {
					// storage doesn't exist in the file system, so create it.
					createStorageWithoutValidation(task, resultConfiguration, foundParameterConfiguration, startTime);
				} else {
					// storage exists in the file system, so re-create it with updated configuration.
					recreateStorageWithUpdatedConfiguration(task, resultConfiguration, storageConfiguration, foundParameterConfiguration);
				}
			}
		}
		return resultConfiguration;
	}

	public RrdDb openRrdDb(final String fullFilePath) {
		RrdDb rrdDb = null;
		try {
			rrdDb = rrdPool.requestRrdDb(fullFilePath);
		} catch (final IOException ex) {
			final String errorMessage = "Unable to open rrd database by file path: " + fullFilePath;
			LOGGER.error(errorMessage, ex);
		}
		return rrdDb;
	}

	private RrdDb openStorage(final MAgentTask task,
							  final ParameterIdentifier parameterIdentifier,
							  final Boolean createStorage,
							  final Date startTime) {
		RrdDb storage = null;
		final Object synchronizedStorageKey = getStorageKeyForSynchronization(task.getKey(), parameterIdentifier);
		synchronized (synchronizedStorageKey) {
			final String storageKey = parameterIdentifier.createTaskStorageKey(task.getKey());
			String storagePath = storages.get(storageKey);
			if (storagePath == null) {
				final MResultConfiguration storageConfiguration = initStorage(task, parameterIdentifier, createStorage, startTime);
				final MResultParameterConfiguration currentParameterConfiguration = storageConfiguration.findParameterConfiguration(parameterIdentifier);
				if (storageConfiguration != null && currentParameterConfiguration != null && currentParameterConfiguration.getLocation() != null) {
					storagePath = currentParameterConfiguration.getLocation().getFullFilePath(storageHome);
					if ((new File(storagePath)).exists()) {
						synchronized (storages) {
							storages.put(storageKey, storagePath);
						}
					} else {
						LOGGER.info("Storage doesn't exist: " + storagePath);
					}
				} else if (createStorage && task.hasTemplateParameter(parameterIdentifier)) {
					final String errorMessage = "Unable to initialize configuration for task = " + task.getKey()
							                + ", parameterIdentifier = " + parameterIdentifier;
					LOGGER.error(errorMessage);
				}
			}
			if (storagePath != null) {
				storage = openRrdDb(storagePath);
			}
		}
		return storage;
	}

	private void recreateStorageWithUpdatedConfiguration(final MAgentTask task,
			final MResultConfiguration newConfiguration,
			final MResultConfiguration oldConfiguration,
			final MResultParameterConfiguration parameterConfiguration) {
		final Object synchronizedStorageKey = getStorageKeyForSynchronization(
				task.getKey(), parameterConfiguration.getParameterIdentifier());
		synchronized (synchronizedStorageKey) {
			final String storageFilePath = parameterConfiguration.getLocation()
					.getFullFilePath(storageHome);
			final String oldStorageFilePath = parameterConfiguration
					.getLocation().getFullFilePath(storageHome, "old_");

			closeRrdDb(storageFilePath);
			Utils.moveFile(storageFilePath, oldStorageFilePath);

			RrdDb oldRrdDb = null;
			RrdDb newRrdDb = null;
			try {
				oldRrdDb = openRrdDb(oldStorageFilePath);

				final Long oldStorageStartTimeInSeconds = RrdUtil
						.findDatasourceStartTime(oldRrdDb,
								parameterConfiguration.getName(), batchSize);

				createStorageWithoutValidation(task, newConfiguration,
						parameterConfiguration,
						new Date(oldStorageStartTimeInSeconds
								* TimeConstants.MILLISECONDS_PER_SECOND));
				newRrdDb = openRrdDb(storageFilePath);

				RrdUtil.copyRrdData(parameterConfiguration.getName(), oldRrdDb,
						newRrdDb, oldStorageStartTimeInSeconds, batchSize);
			} catch (final Exception ex) {
				LOGGER.error(ex.getMessage(), ex);
			} finally {
				releaseStorage(oldRrdDb);
				releaseStorage(newRrdDb);
				deleteFile(oldStorageFilePath);
			}
		}
	}

	private void releaseStorage(final RrdDb rrdDb) {
		if (rrdDb != null) {
			try {
				rrdPool.release(rrdDb);
			} catch (final Exception ex) {
				LOGGER.error("Unable to release rrd database", ex);
			}
		}
	}

	@Override
	public void removeStorage(final MAgentTask task,
							  final ParameterIdentifier parameterIdentifier) {
		if (task.hasParameters()) {
			MResultParameterConfiguration parameterConfiguration = null;
			if (parameterIdentifier != null) {
				parameterConfiguration = task.getResultConfiguration().findParameterConfiguration(parameterIdentifier);
			}
			if (parameterConfiguration != null) {
				removeStorages(task, Arrays.asList(parameterConfiguration));
			}
		}
	}

	@Override
	public void removeStorages(final MAgentTask task) {
		if (task.hasParameters()) {
			removeStorages(task, task.getResultConfiguration().getParameterConfigurations());
		}
	}

	/**
	 * Clears all associated storage caches with provided parameter.
	 *
	 * @param task
	 * @param parameterConfigurations
	 */
	private void removeStorages(final MAgentTask task,
								final Collection<MResultParameterConfiguration> parameterConfigurations) {
		for (final MResultParameterConfiguration parameterConfiguration : parameterConfigurations) {
			if (parameterConfiguration != null) {
				final String storageKey = parameterConfiguration.getParameterIdentifier().createTaskStorageKey(task.getKey());
				synchronized (storages) {
					closeRrdDb(storages.get(storageKey));
					storages.remove(storageKey);
				}
				synchronized (storageKeys) {
					storageKeys.remove(storageKey);
				}
				final MResultParameterLocation parameterLocation = parameterConfiguration.getLocation();
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info("RRD file "	+ parameterLocation.getFullFilePath(storageHome) + " is closed");
				}
			}
		}
	}

	public void setAggregationBase(final Integer aggregationBase) {
		this.aggregationBase = aggregationBase;
	}

	public void setBatchSize(final int batchSize) {
		this.batchSize = batchSize;
	}

	public void setExecutor(final AsyncTaskExecutor executor) {
		this.executor = executor;
	}

	public void setPolicyConfigurationService(
			final InternalPolicyConfigurationService policyConfigurationService) {
		this.policyConfigurationService = policyConfigurationService;
	}

	public void setRrdBackendFactory(final String rrdBackendFactory) {
		this.rrdBackendFactory = rrdBackendFactory;
	}

	public void setRrdPoolCapacity(final int rrdPoolCapacity) {
		this.rrdPoolCapacity = rrdPoolCapacity;
	}

	@Override
	public void setStorageHome(final String storageHome) {
		this.storageHome = storageHome;
	}

	public void setStoredDaysCount(final int storedDaysCount) {
		this.storedDaysCount = storedDaysCount;
	}

	public void setTaskService(final InternalTaskService taskService) {
		this.taskService = taskService;
	}

	@Override
	public List<Future<?>> updateSamplingRate(final MAgentTask task, final Long newSamplingRate) {
		final MResultConfiguration resultConfiguration = task.getResultConfiguration();
		final Long oldSamplingRate = resultConfiguration.getSamplingRate();
		List<Future<?>> futures = new ArrayList<>();
		if (!oldSamplingRate.equals(newSamplingRate)) {
			resultConfiguration.setSamplingRate(newSamplingRate);
			taskService.createOrUpdateTask(task);
			for (final MResultParameterConfiguration parameterConfiguration : resultConfiguration.getParameterConfigurations()) {
				Future<?> future = executor.submit(new UpdateSamplingRate(task, parameterConfiguration, newSamplingRate, oldSamplingRate));
				futures.add(future);
			}
		}

		return futures;
	}
}
