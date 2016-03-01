/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.service;

import java.util.*;

import com.tecomgroup.qos.TimeConstants;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tecomgroup.qos.communication.message.PolicyManagerConfiguration;
import com.tecomgroup.qos.communication.pm.PMConfiguration;
import com.tecomgroup.qos.communication.pm.PMTaskConfiguration;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.pm.action.ActionHandler;
import com.tecomgroup.qos.pm.configuration.ConfigurationStorage;
import com.tecomgroup.qos.pm.handler.ContinuousThresholdFallHandler;
import com.tecomgroup.qos.pm.handler.ParameterPolicyHandler;
import com.tecomgroup.qos.service.PolicyManagerService;
import com.tecomgroup.qos.util.PolicyUtils;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author abondin
 * 
 */
@Service("policyManagerService")
public class DefaultPolicyManagerService implements PolicyManagerService {

	private final static Logger LOGGER = Logger
			.getLogger(DefaultPolicyManagerService.class);

	@Autowired
	private ActionHandler actionHandler;

	@Autowired
	private ConfigurationStorage configurationStorage;

	private PolicyManagerConfiguration configuration;

	private final Map<Source, Map<String, ParameterPolicyHandler>> handlers = new HashMap<Source, Map<String, ParameterPolicyHandler>>();

	@Override
	public void applyConfiguration(
			final PolicyManagerConfiguration configuration) {
		this.configuration = configuration;
		resetHandlers();
		updateConfigurationStorage();
	}

	protected void doAction(final MPolicy policy,
			final Map<String, Object> outputParams) {
		actionHandler.doAction(policy, outputParams);
	}

	@Override
	public PolicyManagerConfiguration getConfiguration() {
		return configuration;
	}

	private Map<String, Collection<ParameterPolicyHandler>> getHandlers(
			final Source source) {
		Map<String, Collection<ParameterPolicyHandler>> handlerMap = null;

		if (handlers.get(source) != null) {
			synchronized (handlers) {
				if (handlers.get(source) != null) {
					handlerMap = new HashMap<String, Collection<ParameterPolicyHandler>>();
					for (final ParameterPolicyHandler handler : handlers.get(
							source).values()) {
						final String parameterStorageKey = handler
								.getParameterIdentifier()
								.createParameterStorageKey();
						Collection<ParameterPolicyHandler> parameterHandlers = handlerMap
								.get(parameterStorageKey);
						if (parameterHandlers == null) {
							parameterHandlers = new LinkedList<ParameterPolicyHandler>();
							handlerMap.put(parameterStorageKey,
									parameterHandlers);
						}
						parameterHandlers.add(handler);

					}
				}
			}
		}
		return handlerMap;
	}

	private String getParamDisplayName(
			final ParameterIdentifier parameterIdentifier,
			final PMTaskConfiguration taskConfig) {
		final String result;
		if (taskConfig == null
				|| taskConfig.getConfiguration() == null
				|| !taskConfig.getConfiguration().hasParameter(
						parameterIdentifier)) {
			result = parameterIdentifier.getName();
		} else {
			result = taskConfig.getConfiguration()
					.findParameterConfiguration(parameterIdentifier)
					.getParsedDisplayFormat();
		}
		return result;
	}

	@Override
	public void handleIntervalResult(final String taskKey, final List<Interval> intervals) {
		if (configuration != null) {
			PMTaskConfiguration taskConfiguration = null;
			final Source taskSource = Source.getTaskSource(taskKey);
			synchronized (configuration) {
				taskConfiguration = configuration.getConfiguration(taskSource);
			}
			if (taskConfiguration != null) {
				final MResultConfiguration resultConfiguration = taskConfiguration.getConfiguration();
				if (resultConfiguration != null) {
					for (final Interval interval : intervals) {
						final Long samplingRateMilis = resultConfiguration.getSamplingRate() * TimeConstants.MILLISECONDS_PER_SECOND;
						final Date leftDate = interval.getLeft().getConvertedResultDateTime();
						final Date rightDate = interval.getRight().getConvertedResultDateTime();

						final Map<String, Collection<ParameterPolicyHandler>> handlers = getHandlers(taskSource);
						if (handlers != null) {
							for (Long timestamp = leftDate.getTime(); timestamp < rightDate.getTime(); timestamp += samplingRateMilis) {
								handleParameters(resultConfiguration,
										         handlers,
										         // We don't use taskSource, because it doesn't have displayName
										         taskConfiguration.getSource(),
										         new Result(new Date(timestamp),	interval.getLeft().getParameters(),	interval.getLeft().getProperties()));
							}
						}
					}
				}
			}
		}
	}

	private void handleParameter(final ParameterPolicyHandler handler,
			final Result result, final Source source) {
		final ParameterIdentifier parameterIdentifier = handler
				.getParameterIdentifier();
		final Double value = result.getParameters().get(
				parameterIdentifier.getName());
		final MPolicy policy = handler.getPolicy();
		if (value == null) {
			LOGGER.warn("Policy parameter " + parameterIdentifier.getName()
					+ " not found in result message");
		} else {
			final Date resultDateTime = result.getConvertedResultDateTime();
			final Map<String, Object> outputParams = handler.handleResult(
					resultDateTime, value);
			if (outputParams != null) {
				outputParams.put(OUTPUT_PARAMETER_CURRENT_TASK_KEY,
						source.getKey());
				outputParams.put(OUTPUT_PARAMETER_CURRENT_VALUE, value);
				outputParams.put(OUTPUT_PARAMETER_TASK_DISPLAY_NAME,
						source.getDisplayName());
				final String paramDisplayName;
				String systemComponentDisplayName = null;
				synchronized (configuration) {
					final PMTaskConfiguration taskConfig = configuration
							.getConfiguration(source);
					paramDisplayName = getParamDisplayName(parameterIdentifier,
							taskConfig);
					if (taskConfig != null
							&& taskConfig.getSystemComponenet() != null) {
						systemComponentDisplayName = taskConfig
								.getSystemComponenet().getDisplayName();
					}
				}
				outputParams.put(OUTPUT_PARAMETER_DISPLAY_NAME,
						paramDisplayName);
				final PerceivedSeverity severity = (PerceivedSeverity) outputParams
						.get(ActionHandler.OUTPUT_PARAMETER_ALERT_SEVERITY);
				final Double policyThresholdValue = PolicyUtils
						.getPolicyThresholdValue(policy, severity);
				outputParams.put(OUTPUT_PARAMETER_THRESHOLD,
						policyThresholdValue);
				outputParams.put(OUTPUT_PARAMETER_AGENT_DISPLAY_NAME,
						systemComponentDisplayName);
				outputParams.put(OUTPUT_PARAMETER_POLICY_DISPLAY_NAME,
						policy.getDisplayName());

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Do action on " + result + " : "
							+ outputParams);
				}
				doAction(policy, outputParams);

			}
		}
	}

	private void handleParameters(
			final MResultConfiguration resultConfiguration,
			final Map<String, Collection<ParameterPolicyHandler>> handlers,
			final Source source, final Result result) {
		if (handlers != null) {
			final List<MProperty> resultProperties = result
					.getModelProperties(resultConfiguration
							.getTemplateResultConfiguration());
			for (final String parameterName : result.getParameters().keySet()) {
				final ParameterIdentifier parameterIdentifier = new ParameterIdentifier(
						parameterName, resultProperties);
				final Collection<ParameterPolicyHandler> parameterHandlers = handlers
						.get(parameterIdentifier.createParameterStorageKey());
				if (parameterHandlers != null) {
					for (final ParameterPolicyHandler handler : parameterHandlers) {
						handleParameter(handler, result, source);
					}
				} else {
					LOGGER.debug("There is no handler to process results for parameter: "
							+ parameterIdentifier
							+ ". The possible reason is that there are first results for this paramerter"
							+ " and its configuration has not come yet from server.");
				}
			}
		}
	}

	@Override
	public void handleSingleValueResult(final String taskKey,
			final List<Result> results) {
		if (configuration != null) {
			PMTaskConfiguration taskConfiguration = null;
			final Source taskSource = Source.getTaskSource(taskKey);
			synchronized (configuration) {
				taskConfiguration = configuration.getConfiguration(taskSource);
			}
			if (taskConfiguration != null) {
				final MResultConfiguration resultConfiguration = taskConfiguration
						.getConfiguration();
				if (resultConfiguration != null) {
					final Map<String, Collection<ParameterPolicyHandler>> handlers = getHandlers(taskSource);
					for (final Result result : results) {
						try {
							// We don't use taskSource, because it doesn't have
							// displayName
							handleParameters(resultConfiguration, handlers,
									taskConfiguration.getSource(), result);
						} catch (final Exception e) {
							LOGGER.error("Cannot handle result for taskKey = ["
									+ taskKey + "], result = [" + result + "]",
									e);
							// TODO Send alert
						}
					}
				}
			} else {
				LOGGER.debug("There is no result configuration to handle results for task: "
						+ taskKey
						+ ". The possible reason is that there are first results for this task"
						+ " and its configuration has not come yet from server.");
			}
		}
	}

	@Override
	public void loadLocalConfiguration() {
		if (configurationStorage != null) {
			this.configuration = configurationStorage.loadLocal();
		}
		resetHandlers();
	}

	@Override
	public void removePMConfiguration(final PMConfiguration updatedConfiguration) {
		if (updatedConfiguration instanceof PMTaskConfiguration) {
			final PMTaskConfiguration updatedPMConfiguration = (PMTaskConfiguration) updatedConfiguration;

			synchronized (configuration) {
				List<MPolicy> foundPolicies = null;
				final Map<Source, PMConfiguration> existingPMConfigurationMap = configuration
						.getConfigurations();
				final PMTaskConfiguration existingPMConfiguration = (PMTaskConfiguration) existingPMConfigurationMap
						.get(updatedPMConfiguration.getSource());
				if (existingPMConfiguration != null) {
					// TASK remove
					if (updatedPMConfiguration.getConfiguration() != null) {
						existingPMConfigurationMap
								.remove(updatedPMConfiguration.getSource());
					} else if (updatedPMConfiguration.getPolicies() != null
							&& !updatedPMConfiguration.getPolicies().isEmpty()) {
						// POLICY remove
						if (existingPMConfiguration.getPolicies() != null
								&& !existingPMConfiguration.getPolicies()
										.isEmpty()) {
							foundPolicies = new ArrayList<MPolicy>();
							for (final MPolicy existingPolicy : existingPMConfiguration
									.getPolicies()) {
								for (final MPolicy updatedPolicy : updatedPMConfiguration
										.getPolicies()) {
									if (existingPolicy.getKey().equals(
											updatedPolicy.getKey())) {
										foundPolicies.add(existingPolicy);
									}
								}
							}
						}
						if (foundPolicies != null && !foundPolicies.isEmpty()) {
							existingPMConfiguration.getPolicies().removeAll(
									foundPolicies);
						}
					}
					updateConfiguration(existingPMConfigurationMap);
					updateHandlers(new PMTaskConfiguration(
							existingPMConfiguration.getSystemComponenet(),
							existingPMConfiguration.getSource(),
							existingPMConfiguration.getConfiguration(),
							foundPolicies), true);
				}
			}
		}
	}

	private void resetHandlers() {
		synchronized (handlers) {
			this.handlers.clear();
			if (this.configuration != null) {
				for (final PMConfiguration pmConfiguration : configuration
						.getConfigurations().values()) {
					for (final MPolicy policy : pmConfiguration.getPolicies()) {
						if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
							Map<String, ParameterPolicyHandler> handlers = this.handlers
									.get(policy.getSource());
							if (handlers == null) {
								handlers = new HashMap<String, ParameterPolicyHandler>();
								this.handlers.put(policy.getSource(), handlers);
							}
							handlers.put(
									policy.getKey(),
									new ContinuousThresholdFallHandler(
											policy,
											((PMTaskConfiguration) pmConfiguration)
													.getConfiguration()));
						} else {
							throw new ServiceException(
									"Unsupported policy condition "
											+ policy.getCondition().getClass());
						}
					}
				}
			}
		}

	}

	/**
	 * @param actionHandler
	 *            the actionHandler to set
	 */
	public void setActionHandler(final ActionHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	private void updateConfiguration(
			final Map<Source, PMConfiguration> configurationMap) {
		configuration.setConfigurations(configurationMap);
		updateConfigurationStorage();
	}

	private void updateConfigurationStorage() {
		if (configurationStorage != null) {
			configuration.setLastUpdateTime(new Date());
			configurationStorage.updateConfiguration(configuration);
		}
	}

	private ParameterPolicyHandler updateHandler(
			final ParameterPolicyHandler existingHandler, final MPolicy policy,
			final PMTaskConfiguration taskPMConfiguration) {
		ParameterPolicyHandler updateHandler = existingHandler;
		if (existingHandler != null) {
			if (existingHandler instanceof ContinuousThresholdFallHandler) {
				final ContinuousThresholdFallHandler fallHandler = (ContinuousThresholdFallHandler) existingHandler;
				final MPolicyCondition oldCondition = fallHandler.getPolicy()
						.getCondition();
				final MPolicyCondition newCondition = policy.getCondition();
				if (oldCondition.updateSimpleFields(newCondition)) {
					// need to reset handler
					updateHandler = new ContinuousThresholdFallHandler(policy,
							taskPMConfiguration.getConfiguration());
				} else {
					fallHandler.getPolicy().setActions(policy.getActions());
				}
			} else {
				throw new ServiceException("Unsupported policy handler "
						+ existingHandler.getClass());
			}
		} else {
			updateHandler = new ContinuousThresholdFallHandler(policy,
					taskPMConfiguration.getConfiguration());
		}
		return updateHandler;
	}

	private void updateHandlers(final PMConfiguration pmConfiguration,
			final boolean remove) {
		synchronized (handlers) {
			if (pmConfiguration instanceof PMTaskConfiguration) {
				final PMTaskConfiguration taskPMConfiguration = (PMTaskConfiguration) pmConfiguration;

				if (taskPMConfiguration.getConfiguration() != null
						&& taskPMConfiguration.getPolicies() != null
						&& !taskPMConfiguration.getPolicies().isEmpty()) {
					for (final MPolicy policy : taskPMConfiguration
							.getPolicies()) {
						if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
							Map<String, ParameterPolicyHandler> handlers = this.handlers
									.get(policy.getSource());
							if (handlers == null) {
								handlers = new HashMap<String, ParameterPolicyHandler>();
								this.handlers.put(policy.getSource(), handlers);
							}
							if (!remove) {
								final String policyKey = policy.getKey();
								final ParameterPolicyHandler handler = updateHandler(
										handlers.get(policyKey), policy,
										taskPMConfiguration);
								handlers.put(policyKey, handler);
							} else {
								handlers.remove(policy.getKey());
							}
						} else {
							throw new ServiceException(
									"Unsupported policy condition "
											+ policy.getCondition().getClass());
						}
					}
				} else if (remove
						&& taskPMConfiguration.getConfiguration() != null
						&& (taskPMConfiguration.getPolicies() == null || taskPMConfiguration
								.getPolicies().isEmpty())) {
					this.handlers.remove(taskPMConfiguration.getSource());
				}
			}
		}
	}

	@Override
	public void updatePMConfiguration(final PMConfiguration updatedConfiguration) {
		if (updatedConfiguration instanceof PMTaskConfiguration) {
			final PMTaskConfiguration updatedPMConfiguration = (PMTaskConfiguration) updatedConfiguration;

			synchronized (configuration) {
				final Map<Source, PMConfiguration> existingPMConfigurationMap = configuration
						.getConfigurations();
				PMTaskConfiguration existingPMConfiguration = null;
				if (updatedPMConfiguration.getSource() == null
						&& updatedPMConfiguration.getSystemComponenet() != null) {
					for (final Map.Entry<Source, PMConfiguration> entry : existingPMConfigurationMap
							.entrySet()) {
						final PMTaskConfiguration taskPMConfiguration = (PMTaskConfiguration) entry
								.getValue();
						if (taskPMConfiguration.getSystemComponenet() != null
								&& taskPMConfiguration
										.getSystemComponenet()
										.getKey()
										.equals(updatedPMConfiguration
												.getSystemComponenet().getKey())) {
							taskPMConfiguration
									.setSystemComponenet(updatedPMConfiguration
											.getSystemComponenet());
						}
					}
				} else {
					existingPMConfiguration = (PMTaskConfiguration) existingPMConfigurationMap
							.get(updatedPMConfiguration.getSource());
					if (existingPMConfiguration != null) {
						// ResultConfiguration update
						if (updatedPMConfiguration.getConfiguration() != null) {
							existingPMConfiguration
									.setConfiguration(updatedPMConfiguration
											.getConfiguration());
						}
						if (updatedPMConfiguration.getPolicies() != null
								&& !updatedPMConfiguration.getPolicies()
										.isEmpty()) {
							// POLICY update
							List<MPolicy> foundPolicies = null;
							if (SimpleUtils
									.isNotNullAndNotEmpty(existingPMConfiguration
											.getPolicies())) {
								foundPolicies = new ArrayList<MPolicy>();
								for (final MPolicy existingPolicy : existingPMConfiguration
										.getPolicies()) {
									for (final MPolicy updatedPolicy : updatedPMConfiguration
											.getPolicies()) {
										if (existingPolicy.getKey().equals(
												updatedPolicy.getKey())) {
											foundPolicies.add(existingPolicy);
										}
									}
								}
							}
							if (SimpleUtils.isNotNullAndNotEmpty(foundPolicies)) {
								existingPMConfiguration.getPolicies()
										.removeAll(foundPolicies);
								existingPMConfiguration.getPolicies().addAll(
										updatedPMConfiguration.getPolicies());
							} else {
								existingPMConfiguration.getPolicies().addAll(
										updatedPMConfiguration.getPolicies());
							}
						} else {
							// Task Source update
							existingPMConfiguration
									.setSource(updatedConfiguration.getSource());
						}
					} else {
						existingPMConfiguration = new PMTaskConfiguration(
								updatedPMConfiguration);
						existingPMConfigurationMap.put(
								updatedPMConfiguration.getSource(),
								existingPMConfiguration);
					}
					updateHandlers(new PMTaskConfiguration(
							existingPMConfiguration.getSystemComponenet(),
							existingPMConfiguration.getSource(),
							existingPMConfiguration.getConfiguration(),
							updatedConfiguration.getPolicies()), false);
				}
				updateConfiguration(existingPMConfigurationMap);
			}
		}
	}
}
