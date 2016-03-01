/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyCondition;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;
import com.tecomgroup.qos.domain.pm.MPolicySharedData;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.exception.SourceNotFoundException;
import com.tecomgroup.qos.messages.PolicyValidationMessages;

/**
 * @author abondin
 * 
 */
public class PolicyUtils {

	private static boolean areConditionLevelsEqual(final ConditionLevel level,
			final ConditionLevel otherLevel) {
		if (level == otherLevel) {
			return true;
		}

		if (level == null || otherLevel == null) {
			return false;
		}

		boolean result = MAbstractEntity.equals(level.getCeaseDuration(),
				otherLevel.getCeaseDuration());
		result &= MAbstractEntity.equals(level.getCeaseLevel(),
				otherLevel.getCeaseLevel());
		result &= MAbstractEntity.equals(level.getRaiseDuration(),
				otherLevel.getRaiseDuration());
		result &= MAbstractEntity.equals(level.getRaiseLevel(),
				otherLevel.getRaiseLevel());

		return result;
	}

	private static boolean areConditionLevelsEqual(
			final Map<PerceivedSeverity, ConditionLevel> levels,
			final Map<PerceivedSeverity, ConditionLevel> otherLevels) {
		if (levels == otherLevels) {
			return true;
		}

		if ((levels == null || otherLevels == null)
				|| (levels.size() != otherLevels.size())) {
			return false;
		}

		boolean result = true;
		for (final Map.Entry<PerceivedSeverity, ConditionLevel> levelsEntry : levels
				.entrySet()) {
			final PerceivedSeverity severity = levelsEntry.getKey();

			result &= areConditionLevelsEqual(levelsEntry.getValue(),
					otherLevels.get(severity));
		}

		return result;
	}

	/**
	 * Compares two collections of {@link MPolicyAction}.
	 * 
	 * @param actions
	 * @param otherActions
	 * @return
	 */
	public static boolean arePolicyActionsEqual(
			final Collection<? extends MPolicyAction> actions,
			final Collection<? extends MPolicyAction> otherActions) {
		boolean result = false;

		final Collection<MPolicyAction> otherActionsCopy = new ArrayList<MPolicyAction>(
				otherActions);
		if ((actions == null) ^ (otherActions == null)) {
			result = false;
		} else if (!SimpleUtils.isNotNullAndNotEmpty(actions)
				&& !SimpleUtils.isNotNullAndNotEmpty(otherActions)) {
			result = true;
		} else {
			if (actions.size() != otherActions.size()) {
				result = false;
			} else {
				for (final MPolicyAction action : actions) {
					MPolicyAction foundOtherAction = null;
					result = false;
					for (final MPolicyAction otherAction : otherActionsCopy) {
						if (arePolicyActionsEqual(action, otherAction)) {
							foundOtherAction = otherAction;
							result = true;
							break;
						}
					}
					if (!result) {
						break;
					} else {
						otherActionsCopy.remove(foundOtherAction);
					}
				}
			}
		}

		return result;
	}

	private static boolean arePolicyActionsEqual(final MPolicyAction action,
			final MPolicyAction otherAction) {
		boolean result = false;

		if (action == otherAction) {
			result = true;
		} else if (action instanceof MPolicySendEmail
				&& (action.getClass() == otherAction.getClass())) {
			result = arePolicyActionsWithContactsEqual(
					(MPolicySendEmail) action, (MPolicySendEmail) otherAction);
		} else if (action instanceof MPolicySendSms
				&& (action.getClass() == otherAction.getClass())) {
			result = arePolicyActionsWithContactsEqual((MPolicySendSms) action,
					(MPolicySendSms) otherAction);
		} else if (action instanceof MPolicySendAlert
				&& (action.getClass() == otherAction.getClass())) {
			result = arePolicySendAlertActionsEqual((MPolicySendAlert) action,
					(MPolicySendAlert) otherAction);
		}

		return result;
	}

	private static <X extends MPolicyActionWithContacts> boolean arePolicyActionsWithContactsEqual(
			final X action, final X otherAction) {
		if (action == otherAction) {
			return true;
		}
		if (action == null || otherAction == null) {
			return false;
		}

		if (action.getContacts() == null) {
			if (otherAction.getContacts() != null) {
				return false;
			}
		} else {
			if (action.getContacts() == null) {
				if (otherAction.getContacts() != null) {
					return false;
				}
			} else {
				if (action.getContacts().size() != otherAction.getContacts()
						.size()) {
					return false;
				} else {
					final Set<String> contactKeys = SimpleUtils.getKeys(action
							.getContacts());
					final Set<String> otherContactKeys = SimpleUtils
							.getKeys(otherAction.getContacts());
					if (!(contactKeys.containsAll(otherContactKeys) && otherContactKeys
							.containsAll(contactKeys))) {
						return false;
					}
				}
			}
		}

		return true;
	}

	/**
	 * Checks two {@link MPolicyConditionLevels} on equality
	 * 
	 * @param level
	 * @param otherLevel
	 * @return true - if equal, otherwise - false;
	 */
	public static boolean arePolicyConditionLevelsEqual(
			final MPolicyConditionLevels level,
			final MPolicyConditionLevels otherLevel) {
		if (level == otherLevel) {
			return true;
		}

		if (level == null || otherLevel == null) {
			return false;
		}

		boolean result = areConditionLevelsEqual(level.getConditionLevelsMap(),
				otherLevel.getConditionLevelsMap());
		result &= MAbstractEntity.equals(level.getThresholdType(),
				otherLevel.getThresholdType());

		return result;
	}

	private static boolean arePolicySendAlertActionsEqual(
			final MPolicySendAlert action, final MPolicySendAlert otherAction) {
		if (action == otherAction) {
			return true;
		}
		if (action == null || otherAction == null) {
			return false;
		}

		if (action.getAlertType() == null) {
			if (otherAction.getAlertType() != null) {
				return false;
			}
		} else if (!action.getAlertType().equals(otherAction.getAlertType())) {
			return false;
		}

		return true;
	}

	/**
	 * Searches {@link MResultParameterConfiguration} in provided task's
	 * {@link MResultConfiguration} based on policy's
	 * {@link ParameterIdentifier
	 * }.
	 * 
	 * @throws SourceNotFoundException
	 *             if {@link MResultParameterConfiguration} is not found
	 */
	public static MResultParameterConfiguration findParameterConfiguration(
			final MPolicy policy, final MAgentTask task)
			throws SourceNotFoundException {
		MResultParameterConfiguration parameterConfiguration = null;

		if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
			final ParameterIdentifier parameterIdentifier = ((MContinuousThresholdFallCondition) policy
					.getCondition()).getParameterIdentifier();

			parameterConfiguration = task.getResultConfiguration()
					.findParameterConfiguration(parameterIdentifier);
		}

		if (parameterConfiguration == null) {
			throw new SourceNotFoundException(
					"There is no parameter configuration for task=" + task
							+ " and policy=" + policy);
		}
		return parameterConfiguration;
	}

	public static Double getAlertThresholdValue(final MAlert alert,
			final Evaluator evaluator, final PolicyValidationMessages messages) {
		Double thresholdValue = null;
		final MSource originator = alert.getOriginator();
		final MSource source = alert.getSource();
		if ((originator instanceof MPolicy) && (source instanceof MAgentTask)) {
			final MPolicy policy = (MPolicy) originator;
			final MAgentTask task = (MAgentTask) source;
			final MPolicyCondition condition = policy.getCondition();
			if (condition instanceof MContinuousThresholdFallCondition) {
				final MContinuousThresholdFallCondition continuousThresholFallCondition = (MContinuousThresholdFallCondition) condition;
				ConditionLevel conditionLevel = null;
				if (alert.getPerceivedSeverity().equals(
						PerceivedSeverity.CRITICAL)) {
					conditionLevel = continuousThresholFallCondition
							.getCriticalLevel();
				} else if (alert.getPerceivedSeverity().equals(
						PerceivedSeverity.WARNING)) {
					conditionLevel = continuousThresholFallCondition
							.getWarningLevel();
				}
				if (conditionLevel != null) {
					final MParameterThreshold parameterThreshold = getParameterThreshold(
							policy, task);
					if (initConditionLevel(conditionLevel, parameterThreshold,
							evaluator, messages)) {
						thresholdValue = conditionLevel.getRaiseLevelDouble();
					}
				}
			}
		}
		return thresholdValue;
	}

	public static String getDefaultActionName(final MPolicyAction policyAction) {
		String actionName = "";
		if (policyAction instanceof MPolicySendAlert) {
			actionName = "Send Alert";
		} else if (policyAction instanceof MPolicySendEmail) {
			actionName = "Send Email";
		} else if (policyAction instanceof MPolicySendSms) {
			actionName = "Send SMS";
		}

		return actionName;
	}

	private static MParameterThreshold getParameterThreshold(
			final MPolicySharedData policy, final MAgentTask task) {
		MParameterThreshold parameterThreshold = null;
		if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
			parameterThreshold = getParameterThreshold(policy,
					(MContinuousThresholdFallCondition) policy.getCondition(),
					task.getResultConfiguration());
		}
		return parameterThreshold;
	}

	private static MParameterThreshold getParameterThreshold(
			final MPolicySharedData policy,
			final MContinuousThresholdFallCondition policyCondition,
			final MResultConfiguration resultConfiguration) {
		MParameterThreshold threshold = null;
		if (Source.Type.TASK.equals(policy.getSource().getType())) {
			final MResultParameterConfiguration parameterConfiguration = resultConfiguration
					.findParameterConfiguration(policyCondition
							.getParameterIdentifier());
			if (parameterConfiguration != null) {
				threshold = parameterConfiguration.getThreshold();
			} else {
				throw new ServiceException("Parameter "
						+ policyCondition.getParameterIdentifier()
						+ " not found for source = " + policy.getSource()
						+ " and policy = " + policy);
			}
		} else {
			throw new UnsupportedOperationException("Usupported Source.Type: "
					+ policy.getSource().getType());
		}
		return threshold;
	}

	/**
	 * Gets a collection of {@link MResultParameterConfiguration} from provided
	 * tasks by {@link ParameterIdentifier} of {@link MPolicyCondition} from
	 * provided policies.
	 * 
	 * @param policies
	 * @param tasks
	 * @return list of {@link MResultParameterConfiguration}
	 * @throws SourceNotFoundException
	 *             if there is no task or no parameter found for any of policies
	 */
	public static Collection<MResultParameterConfiguration> getPolicyParameterConfigurations(
			final Collection<MPolicy> policies,
			final Collection<MAgentTask> tasks) {
		Collection<MResultParameterConfiguration> resultParameterConfigurations;
		if (policies.isEmpty() || tasks.isEmpty()) {
			resultParameterConfigurations = Collections
					.<MResultParameterConfiguration> emptyList();
		} else {
			resultParameterConfigurations = new ArrayList<MResultParameterConfiguration>();
			final Map<String, MAgentTask> tasksByKeys = SimpleUtils
					.getMap(tasks);
			for (final MPolicy policy : policies) {
				final MAgentTask task = tasksByKeys.get(policy.getSource()
						.getKey());
				if (task != null) {
					resultParameterConfigurations
							.add(findParameterConfiguration(policy, task));
				} else {
					throw new SourceNotFoundException(
							"Source not found for policy=" + policy);
				}
			}
		}

		return resultParameterConfigurations;
	}

	/**
	 * @param policy
	 *            an instance of {@link MPolicy}, whose conditions have already
	 *            been parsed with {@link Evaluator}
	 * @param severity
	 * @return if severity is not null, returns raise level for corresponding
	 *         severity, else returns cease level, depending on this policy
	 *         condition's severity.
	 */
	public static Double getPolicyThresholdValue(final MPolicy policy,
			final PerceivedSeverity severity) {
		final MPolicyCondition condition = policy.getCondition();
		Double result = null;
		if (condition instanceof MContinuousThresholdFallCondition) {
			final MContinuousThresholdFallCondition thresholdFallCondition = (MContinuousThresholdFallCondition) condition;
			if (severity == null) {
				final ConditionLevel level = thresholdFallCondition
						.getWarningLevel() != null ? thresholdFallCondition
						.getWarningLevel() : thresholdFallCondition
						.getCriticalLevel();
				result = level.getCeaseLevelDouble();
			} else {
				switch (severity) {
					case CRITICAL :
						result = thresholdFallCondition.getCriticalLevel()
								.getRaiseLevelDouble();
						break;
					case WARNING :
						result = thresholdFallCondition.getWarningLevel()
								.getRaiseLevelDouble();
						break;
					default :
						throw new IllegalArgumentException("Severity "
								+ severity + " is not supported.");
				}
			}
		}
		return result;
	}

	/**
	 * @param threshold
	 *            can be null
	 * @return should return not empty map in case if threshold is null.
	 */
	private static Map<String, String> getThresholdLevels(
			final MParameterThreshold threshold) {
		final Map<String, String> thresholdLevels = new HashMap<String, String>();

		if (threshold != null) {
			final Double criticalLevel = threshold.getCriticalLevel();
			if (criticalLevel != null) {
				thresholdLevels.put(ConditionLevel.THRESHOLD_CRITICAL_LEVEL,
						criticalLevel.toString());
			}

			final Double warningLevel = threshold.getWarningLevel();
			if (warningLevel != null) {
				thresholdLevels.put(ConditionLevel.THRESHOLD_WARNING_LEVEL,
						warningLevel.toString());
			}
		}
		return thresholdLevels;
	}

	private static Double getValue(final String expression,
			final Map<String, String> thresholdLevels, final Evaluator evaluator) {
		if (expression == null) {
			return null;
		}
		Double result;
		try {
			result = evaluator.evaluate(expression, thresholdLevels);
		} catch (final Exception e) {
			throw new ServiceException("Can't evaluate " + expression, e);
		}
		return result;
	}

	public static boolean initConditionLevel(
			final ConditionLevel conditionLevel,
			final MParameterThreshold parameterThreshold,
			final Evaluator evaluator, final PolicyValidationMessages messages) {
		boolean сonditionLevelIsInitialized = initConditionLevel(
				conditionLevel, messages);
		if (!сonditionLevelIsInitialized) {
			сonditionLevelIsInitialized = initConditionLevelByThreshold(
					conditionLevel, parameterThreshold, evaluator, messages);
		}
		return сonditionLevelIsInitialized;
	}

	private static boolean initConditionLevel(
			final ConditionLevel conditionLevel,
			final PolicyValidationMessages messages) {
		boolean initialized = false;
		if (conditionLevel != null) {
			if (conditionLevel.getCeaseLevel() == null) {
				throw new ServiceException(messages.levelIsNotDefined(messages
						.cease()));
			}
			if (conditionLevel.getRaiseLevel() == null) {
				throw new ServiceException(messages.levelIsNotDefined(messages
						.raise()));
			}
			if (isLevelDouble(conditionLevel)) {
				conditionLevel.setRaiseLevelDouble(Double
						.valueOf(conditionLevel.getRaiseLevel()));
				conditionLevel.setCeaseLevelDouble(Double
						.valueOf(conditionLevel.getCeaseLevel()));
				initialized = true;
			}
		}
		return initialized;
	}

	private static boolean initConditionLevelByThreshold(
			final ConditionLevel conditionLevel,
			final MParameterThreshold threshold, final Evaluator evaluator,
			final PolicyValidationMessages messages) {
		boolean initialized = false;
		if (conditionLevel != null) {
			final Map<String, String> thresholdLevels = getThresholdLevels(threshold);

			boolean raiseLevelIsInitialized = false;
			final String raiseLevel = conditionLevel.getRaiseLevel();
			if (raiseLevel != null
					&& (stringContainsOneOfStringsFromCollection(raiseLevel,
							thresholdLevels.keySet()) || SimpleUtils
							.isDouble(raiseLevel))) {
				conditionLevel.setRaiseLevelDouble(getValue(raiseLevel,
						thresholdLevels, evaluator));
				raiseLevelIsInitialized = true;
			} else {
				throw new ServiceException(
						messages.levelIsNotDefined(raiseLevel == null
								? messages.raise()
								: raiseLevel));
			}

			boolean ceaseLevelIsInitialized = false;
			final String ceaseLevel = conditionLevel.getCeaseLevel();
			if (ceaseLevel != null
					&& (stringContainsOneOfStringsFromCollection(ceaseLevel,
							thresholdLevels.keySet()) || SimpleUtils
							.isDouble(ceaseLevel))) {
				conditionLevel.setCeaseLevelDouble(getValue(ceaseLevel,
						thresholdLevels, evaluator));
				ceaseLevelIsInitialized = true;
			} else {
				throw new ServiceException(
						messages.levelIsNotDefined(ceaseLevel == null
								? messages.cease()
								: ceaseLevel));
			}

			initialized = raiseLevelIsInitialized && ceaseLevelIsInitialized;
		}
		return initialized;
	}

	private static boolean isLevelDouble(final ConditionLevel level) {
		return SimpleUtils.isDouble(level.getRaiseLevel())
				&& SimpleUtils.isDouble(level.getCeaseLevel());
	}

	public static void setDefaultPolicyActionNames(
			final Collection<? extends MPolicyAction> actions) {
		if (actions != null) {
			for (final MPolicyAction action : actions) {
				if (!SimpleUtils.isNotNullAndNotEmpty(action.getName())) {
					action.setName(getDefaultActionName(action));
				}
			}
		}
	}

	private static boolean stringContainsOneOfStringsFromCollection(
			final String pattern, final Collection<String> collection) {
		boolean result = false;

		for (final String string : collection) {
			if (pattern.contains(string)) {
				result = true;
				break;
			}
		}

		return result;
	}

	private static void validateAndInitConditionLevels(
			final MContinuousThresholdFallCondition policyCondition,
			final MParameterThreshold parameterThreshold,
			final Evaluator evaluator, final PolicyValidationMessages messages) {
		final Collection<ConditionLevel> levels = policyCondition
				.getConditionLevels();
		boolean levelsAreNull = true;
		for (final ConditionLevel conditionLevel : levels) {
			if (conditionLevel != null) {
				initConditionLevel(conditionLevel, parameterThreshold,
						evaluator, messages);
				levelsAreNull = false;
			}
		}
		if (levelsAreNull) {
			throw new ServiceException(messages.levelsAreNotDefined());
		}
	}

	/**
	 * Inits the conditions levels of the provided policy condition in both
	 * cases if they are simple double and if they are related to
	 * {@link MParameterThreshold} and must be parsed before the validation.
	 * 
	 * Validates the conditions levels and throws the exceptions if they are
	 * inconsistent.
	 * 
	 * @param policy
	 *            A policy with a condition to validate.
	 * @param resultConfiguration
	 *            A configuration of {@link MAgentTask} to get
	 *            {@link MParameterThreshold}.
	 * @param evaluator
	 *            A evaluator to parse the provided condition levels if they are
	 *            related to {@link MParameterThreshold}.
	 * @param messages
	 *            The messages used to throw the exceptions.
	 * 
	 * @throws ServiceException
	 */
	public static void validateAndInitPolicyCondition(
			final MPolicySharedData policy,
			final MResultConfiguration resultConfiguration,
			final Evaluator evaluator, final PolicyValidationMessages messages) {
		if (policy.getCondition() instanceof MContinuousThresholdFallCondition) {
			final MContinuousThresholdFallCondition policyCondition = (MContinuousThresholdFallCondition) policy
					.getCondition();
			final MParameterThreshold parameterThreshold = getParameterThreshold(
					policy, policyCondition, resultConfiguration);
			try {
				validateAndInitConditionLevels(policyCondition,
						parameterThreshold, evaluator, messages);
			} catch (final Exception ex) {
				throw new ServiceException(
						messages.unableToInitConditionLevels() + ": "
								+ ex.getMessage(), ex);
			}
			validateConditionLevels(policyCondition, messages);
		} else {
			throw new ClassCastException("Unsupported condition type: "
					+ policy.getCondition().getClass().getName());
		}
	}

	/**
	 * Inits the conditions levels of the provided policy condition if they are
	 * simple double, validates them and throws the exceptions if they are
	 * inconsistent.
	 * 
	 * Use {
	 * {@link #validateAndInitPolicyCondition(MPolicySharedData, MResultConfiguration, Evaluator, PolicyValidationMessages)}
	 * to init and validate the condition levels if they are not simple double,
	 * but if they are related to {@link MParameterThreshold} and must be parsed
	 * before the validation.
	 * 
	 * @param policyConditionLevels
	 *            A policy condition levels to validate.
	 * 
	 * @param messages
	 *            The messages used to throw the exceptions.
	 * 
	 * @throws ServiceException
	 */
	public static void validateConditionLevels(
			final MPolicyConditionLevels policyConditionLevels,
			final PolicyValidationMessages messages) {
		final ConditionLevel criticalLevel = policyConditionLevels
				.getCriticalLevel();
		final ConditionLevel warningLevel = policyConditionLevels
				.getWarningLevel();
		final ThresholdType thresholdType = policyConditionLevels
				.getThresholdType();

		// validate critical level
		boolean checkCritical = true;
		if (criticalLevel == null
				|| criticalLevel.getRaiseLevelDouble() == null
				|| criticalLevel.getCeaseLevelDouble() == null) {
			checkCritical = false;
		}
		if (!checkCritical && initConditionLevel(criticalLevel, messages)) {
			checkCritical = true;
		}

		// validate warning level
		boolean checkWarning = true;
		if (warningLevel == null || warningLevel.getRaiseLevelDouble() == null
				|| warningLevel.getCeaseLevelDouble() == null) {
			checkWarning = false;
		}
		if (!checkWarning && initConditionLevel(warningLevel, messages)) {
			checkWarning = true;
		}

		if (checkCritical || checkWarning) {
			switch (thresholdType) {
				case LESS :
				case LESS_OR_EQUALS :
					if (checkCritical
							&& criticalLevel.getRaiseLevelDouble() > criticalLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.criticalRaiseLevelHigherThanCriticalCeaseLevel());
					}
					if (checkWarning
							&& warningLevel.getRaiseLevelDouble() > warningLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.warningRaiseLevelHigherThanWarningCeaseLevel());
					}
					if (checkCritical
							&& checkWarning
							&& criticalLevel.getRaiseLevelDouble() > warningLevel
									.getRaiseLevelDouble()) {
						throw new ServiceException(
								messages.criticalRaiseLevelHigherThanWarningRaiseLevel());
					}
					if (checkCritical
							&& checkWarning
							&& criticalLevel.getCeaseLevelDouble() > warningLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.criticalCeaseLevelHigherThanWarningCeaseLevel());
					}
					break;
				case GREATER :
				case GREATER_OR_EQUALS :
					if (checkCritical
							&& criticalLevel.getRaiseLevelDouble() < criticalLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.criticalRaiseLevelLessThanCriticalCeaseLevel());
					}
					if (checkWarning
							&& warningLevel.getRaiseLevelDouble() < warningLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.warningRaiseLevelLessThanWarningCeaseLevel());
					}
					if (checkCritical
							&& checkWarning
							&& criticalLevel.getRaiseLevelDouble() < warningLevel
									.getRaiseLevelDouble()) {
						throw new ServiceException(
								messages.criticalRaiseLevelLessThanWarningRaiseLevel());
					}
					if (checkCritical
							&& checkWarning
							&& criticalLevel.getCeaseLevelDouble() < warningLevel
									.getCeaseLevelDouble()) {
						throw new ServiceException(
								messages.criticalCeaseLevelLessThanWarningCeaseLevel());
					}
					break;
				case EQUALS :
				case NOT_EQUALS :
					break;
				default :
					throw new UnsupportedOperationException(
							"Unsupported ThresholdType: " + thresholdType);
			}
		}
	}
}
