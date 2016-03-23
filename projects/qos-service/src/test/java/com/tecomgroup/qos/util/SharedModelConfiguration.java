/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.GISPosition;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAgentType;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MDivision;
import com.tecomgroup.qos.domain.MParameterThreshold;
import com.tecomgroup.qos.domain.MParameterThreshold.ThresholdType;
import com.tecomgroup.qos.domain.MProfile;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MProperty.PropertyValueType;
import com.tecomgroup.qos.domain.MResultConfiguration;
import com.tecomgroup.qos.domain.MResultConfigurationTemplate;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.AggregationType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterIdentifier;
import com.tecomgroup.qos.domain.MResultParameterConfiguration.ParameterType;
import com.tecomgroup.qos.domain.MResultParameterLocation;
import com.tecomgroup.qos.domain.MTestAgentModule;import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.domain.MUserResultTemplate;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.pm.ConditionLevel;
import com.tecomgroup.qos.domain.pm.MContinuousThresholdFallCondition;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyAction;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicyActionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicyConditionLevels;
import com.tecomgroup.qos.domain.pm.MPolicyConditionsTemplate;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;

/**
 * @author kunilov.p
 * 
 */
public class SharedModelConfiguration {

	protected static final int TASK_COUNT = 10;

	protected static final int PROFILE_COUNT = 10;

	protected static final int PARAMETER_COUNT = 10;

	protected static final int STREAM_COUNT = 3;

	public static final int VIDEO_RESULT_COUNT = 3;

	public static final String STORAGE_HOME = "./target/rrdHome";

	public static final String SIGNAL_LEVEL = "signalLevel";
	public static final String SIGNAL_NOISE = "signalNoise";
	public static final String NICAM_LEVEL = "nicamLevel";
	public static final String VIDEO_AUDIO = "videoAudio";
	public static final String FAKE_STRONG_SIGNAL_BOOLEAN = "fakeStrongSignalBOOL";
	public static final String IT09A_MODULE_KEY = "IT09AControlModule";
	public static final List<String> IT09A_MODULE_PARAMETER_LIST = Arrays
			.<String> asList(SIGNAL_LEVEL, SIGNAL_NOISE, NICAM_LEVEL,
					VIDEO_AUDIO, FAKE_STRONG_SIGNAL_BOOLEAN);

	public static final String IT09A_ALERT_TYPE_PREFIX = "qos.it09a.";
	public static final String IT09A_ALERT_TYPE_NAME = IT09A_ALERT_TYPE_PREFIX
			+ SIGNAL_LEVEL;

	public static Double ALERT_DETECTION_VALUE = 10.12345;

	public static final String MAIL_SUBJECT = "Subject";
	public static final String MAIL_BODY = "Body";

	protected static MAgentModule analogSignalQualityAgentModule;
	protected static MAgentModule tvStreamMonitorAgentModule;

	private final static String PROGRAM_NUMBER = "programNumber";

	private final static String PROGRAM_DISPLAY_NAME = "programName";

	public static boolean checkWhetherStorageExists(
			final MResultConfiguration resultConfiguration,
			final String storageHome) {

		return checkWhetherStorageExists(resultConfiguration, storageHome, null);
	}

	@SuppressWarnings("deprecation")
	public static boolean checkWhetherStorageExists(
			final MResultConfiguration resultConfiguration,
			final String storageHome,
			final ParameterIdentifier parameterIdentifier) {
		boolean result = true;

		for (final MResultParameterConfiguration parameterConfiguration : resultConfiguration
				.getParameterConfigurations()) {
			if (parameterIdentifier == null
					|| parameterConfiguration
							.getParameterIdentifier()
							.createParameterStorageKey()
							.equals(parameterIdentifier
									.createParameterStorageKey())) {
				final File file = new File(parameterConfiguration.getLocation()
						.getFullFilePath(storageHome));
				if (!file.exists()) {
					result = false;
				}
			}
		}

		return result;
	}

	@SuppressWarnings("deprecation")
	public static boolean checkWhetherStorageRemoved(
			final MResultConfiguration resultConfiguration,
			final String storageHome) {
		boolean result = true;

		for (final MResultParameterConfiguration parameterConfiguration : resultConfiguration
				.getParameterConfigurations()) {
			final File file = new File(parameterConfiguration.getLocation()
					.getFullFilePath(storageHome));
			if (file.exists()) {
				result = false;
			}
		}

		return result;
	}

	public static MTestAgentModule createAgentModule(final MAgent agent) {
		final MTestAgentModule mediaModule = new MTestAgentModule();
		mediaModule.setDisplayName("Analog Signal Quality - IT-09A");
		mediaModule.setKey(createModuleKey(agent.getKey()));
		mediaModule.setParent(agent);
		mediaModule
				.setTemplateResultConfiguration(createTemplateResultConfiguration(false));

		return mediaModule;
	}

	public static MProfile createAgentProfile(final MAgent agent,
			final String profileName) {
		final MProfile profile = new MProfile();
		profile.setCreatedBy("testCase" + profileName);
		profile.setCreationDateTime(new Date(System.currentTimeMillis()));
		profile.setDisplayName("agentProfileName" + profileName);
		profile.setDescription("agentProfileDescription" + profileName);
		profile.setModifiedBy("testCase" + profileName);
		profile.setModificationDateTime(new Date(System.currentTimeMillis()));
		profile.setVersion(0);
		// agentProfile.setTasks(createAgentTasks(agent));
		return profile;
	}

	public static MAgentTask createAgentTask(final MAgentModule module) {
		return createAgentTask(module, null);
	}

	public static MAgentTask createAgentTask(final MAgentModule module,
			final String taskKey) {
		final MAgentTask agentTask = new MAgentTask();
		agentTask.setParent(module);
		agentTask.setCreatedBy("testCase");
		agentTask.setCreationDateTime(new Date(System.currentTimeMillis()));
		agentTask.setModifiedBy("testCase");
		agentTask.setModificationDateTime(new Date(System.currentTimeMillis()));
		if (taskKey == null) {
			agentTask.setKey("key" + UUID.randomUUID());
		} else {
			agentTask.setKey(createTaskKey(null, module.getKey(), taskKey));
		}
		agentTask.setDisplayName("Task #" + agentTask.getKey());
		agentTask.setDisabled(false);

		agentTask.setProperties(createTaskProperties(agentTask));
		agentTask.setVersion(0);
		return agentTask;
	}

	public static List<MAgentTask> createAgentTasks(final MAgent agent,
			final MAgentModule module) {
		final List<MAgentTask> tasks = new ArrayList<MAgentTask>();
		for (Integer index = 0; index < TASK_COUNT; index++) {
			tasks.add(createAgentTask(module));
		}
		return tasks;
	}

	public static MAlertType createAlertType(final String typeName,
			final String displayName) {
		final MAlertType alertType = new MAlertType();
		alertType.setName(typeName);
		alertType.setDisplayName(displayName);
		alertType.setProbableCause(ProbableCause.THRESHOLD_CROSSED);
		alertType.setDisplayTemplate(displayName + "  ${CURRENT_VALUE}");
		alertType.setDescription(displayName);
		return alertType;
	}

	public static MAgentModule createAnalogSignalQualityAgentModule(
			final MAgent agent) {
		if (analogSignalQualityAgentModule == null) {
			analogSignalQualityAgentModule = new MAgentModule();
			analogSignalQualityAgentModule
					.setDisplayName("Analog Signal Quality - IT-09A");
			analogSignalQualityAgentModule.setKey("IT09AControlModule");
			analogSignalQualityAgentModule.setParent(agent);
			analogSignalQualityAgentModule
					.setTemplateResultConfiguration(createTemplateResultConfiguration(false));
		}
		return analogSignalQualityAgentModule;
	}

	/**
	 * Creates agent with all nested complex objects.
	 * 
	 * Use {@link SharedModelConfiguration#createLightWeightAgent(String)}
	 * instead.
	 * 
	 * @param agentName
	 * @return {@link MAgent}
	 */
	@Deprecated
	public static MAgent createComplexAgent(final String agentName) {
		final MAgent agent = new MAgent();
		agent.setDivision(createDivision("First Channel - Perm",
				"First Channel"));
		agent.setAgentType(MAgentType.INTERNAL);
		agent.setKey(agentName);
		agent.setDisplayName("Probe #" + agentName);
		agent.setDescription("Test Description");
		agent.setCreatedBy("testCase");
		agent.setCreationDateTime(new Date(System.currentTimeMillis()));
		agent.setModifiedBy("testCase");
		agent.setModificationDateTime(new Date(System.currentTimeMillis()));
		agent.setTimeZone("Europe/Moscow");
		agent.setNetAddress("localhost");
		agent.setPlatform("Windows 7 64x");

		final GISPosition gisPosition = new GISPosition();
		gisPosition.setTitle("Тестовое положение");
		gisPosition.setLatitude(54.94);
		gisPosition.setLongitude(43.32);

		agent.setGisPosition(gisPosition);

		final Set<MProfile> profiles = new HashSet<MProfile>();
		for (Integer index = 0; index < PROFILE_COUNT; index++) {
			profiles.add(createAgentProfile(agent, index.toString()));
		}
		agent.setProfiles(profiles);
		agent.setSelectedProfile(createAgentProfile(agent,
				"-selectedAgentProfile"));

		return agent;
	}

	public static MDivision createDivision(final String name,
			final String parentName) {
		return new MDivision(name, 0, new MDivision(parentName));
	}

	public static List<MProperty> createEmptyPropertyConfigurations() {
		final List<MProperty> propertyConfigurations = new ArrayList<MProperty>();

		propertyConfigurations.add(new MProperty(PROGRAM_NUMBER, true,
				"Program Number"));

		final MProperty programDisplayName = new MProperty(
				PROGRAM_DISPLAY_NAME, true, "Program Display Name");
		programDisplayName.setType(PropertyValueType.COMMON_STRING);
		propertyConfigurations.add(programDisplayName);

		return propertyConfigurations;
	}

	public static void createFolder(final String filePath) {
		final File folder = new File(filePath);
		folder.mkdirs();
	}

	public static MUserGroup createGroup(final String groupName,
			final Collection<MUser> users) {
		final MUserGroup group = new MUserGroup();
		group.setName(groupName);
		group.setUsers(new ArrayList<MUser>(users));

		return group;
	}

	public static List<MUserGroup> createGroupsWithUsers(
			final String groupNameTempalte, final int groupCount,
			final String userLoginTemplate, final int userCount) {
		final List<MUserGroup> groups = new ArrayList<>();

		for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
			final MUserGroup group = new MUserGroup();
			group.setName(groupNameTempalte + "-" + groupIndex);

			final List<MUser> groupUsers = new ArrayList<>();
			for (int userIndex = 0; userIndex < userCount; userIndex++) {
				final String userLogin = userLoginTemplate + "-" + groupIndex
						+ "-" + +userIndex;
				groupUsers.add(createUser(userLogin, userLogin, userLogin,
						userLogin, Arrays.asList(PredefinedRoles.ROLE_USER), ""));
			}
			group.setUsers(groupUsers);
			groups.add(group);
		}

		return groups;
	}

	public static MUserGroup createGroupWithDefaultUsers(final String groupName) {
		final List<MUser> users = new ArrayList<>();

		users.add(createUser("ivanov.i", "Ivan", "Ivanovich", "Ivanov",
				Arrays.asList(PredefinedRoles.ROLE_USER), "ivanov.i@tecomgroup.com"));
		users.add(createUser("petrov.p", "Petr", "Petrovich", "Petrov",
				Arrays.asList(PredefinedRoles.ROLE_USER), "petrov.p@tecomgroup.com"));
		users.add(createUser("sidorov.s", "Sidor", "Sidorovich", "Sidorov",
				Arrays.asList(PredefinedRoles.ROLE_USER), "sidorov.s@tecomgroup.com"));

		return createGroup(groupName, users);
	}

	public static MAlertIndication createIndication(final String alertTypeName,
			final Source source, final Source originator,
			final PerceivedSeverity perceivedSeverity, final String settings) {
		final MAlertIndication indication = new MAlertIndication();
		indication.setAlertType(new MAlertType(alertTypeName));
		indication.setContext("indicationContext");
		indication.setDateTime(new Date());
		indication.setExtraData("indicationExtraData");
		indication.setIndicationType(UpdateType.UPDATE);
		indication.setSettings(settings);
		indication.setPerceivedSeverity(perceivedSeverity);
		indication.setSpecificReason(SpecificReason.NONE);
		indication.setSource(source);
		indication.setOriginator(originator);

		return indication;
	}

	public static MAlertIndication createIndication(final String alertTypeName,
			final Source source, final Source originator, final String settings) {
		return createIndication(alertTypeName, source, originator,
				PerceivedSeverity.MINOR, settings);
	}

	/**
	 * Creates agent with only simple properties.
	 * 
	 * @param agentName
	 * @return {@link MAgent}
	 */
	public static MAgent createLightWeightAgent(final String agentName) {
		final MAgent agent = new MAgent();
		agent.setAgentType(MAgentType.INTERNAL);
		agent.setKey(agentName);
		agent.setDisplayName("Probe #" + agentName);
		agent.setDescription("Test Description");
		agent.setCreatedBy("testCase");
		agent.setCreationDateTime(new Date(System.currentTimeMillis()));
		agent.setModifiedBy("testCase");
		agent.setModificationDateTime(new Date(System.currentTimeMillis()));

		return agent;
	}

	/**
	 * Creates a full module key.
	 * 
	 * @param agentKey
	 *            an agent key. It can NOT be null.
	 * @return the key like agentKey.{@link #IT09A_MODULE_KEY}
	 */
	public static String createModuleKey(final String agentKey) {
		return createModuleKey(agentKey, null);
	}

	/**
	 * Creates a full module key.
	 * 
	 * @param agentKey
	 *            an agent key. It can NOT be null.
	 * @param moduleKey
	 *            a short module key. if it is null, then
	 *            {@link #IT09A_MODULE_KEY} will be used.
	 * @return the key like agentKey.moduleKey
	 */
	public static String createModuleKey(final String agentKey,
			final String moduleKey) {
		return agentKey
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ (SimpleUtils.isNotNullAndNotEmpty(moduleKey)
						? moduleKey
						: IT09A_MODULE_KEY);
	}

	public static MPolicy createPolicy(final MAgentTask task,
			final String actionName) {
		final ParameterIdentifier parameterIdentifier = new ParameterIdentifier(
				SIGNAL_LEVEL, null);
		return createPolicy(task, actionName, parameterIdentifier);
	}

	public static MPolicy createPolicy(final MAgentTask task,
			final String actionName,
			final ParameterIdentifier parameterIdentifier) {
		final String parameterStorageKey = parameterIdentifier
				.createParameterStorageKey();
		return createPolicy(
				createPolicyKey(
						createPolicyTemplateKey(SimpleUtils
								.findSystemComponent(task).getKey(),
								parameterStorageKey), task.getKey(),
						parameterStorageKey), task, actionName,
				parameterIdentifier);
	}

	public static MPolicy createPolicy(final String policyKey,
			final MAgentTask task, final String actionName,
			final ParameterIdentifier parameterIdentifier) {
		final MPolicy policy = new MPolicy();
		policy.setSource(Source.getTaskSource(task.getKey()));
		policy.setParent(task);
		policy.setKey(policyKey);
		policy.setDisabled(false);
		policy.setDisplayName("TestPolicyDisplayName");
		policy.setCondition(createPolicyCondition(parameterIdentifier));
		policy.addAction(createPolicySendAlertAction(actionName,
				IT09A_ALERT_TYPE_PREFIX + parameterIdentifier.getName()));

		return policy;
	}

	public static MPolicyActionsTemplate createPolicyActionsTemplate(
			final String name, final List<MPolicyActionWithContacts> actions) {
		final MPolicyActionsTemplate template = new MPolicyActionsTemplate();
		template.setName(name);
		template.setActions(actions);
		return template;
	}

	public static MContinuousThresholdFallCondition createPolicyCondition(
			final ParameterIdentifier parameterIdentifier) {
		final MContinuousThresholdFallCondition condition = new MContinuousThresholdFallCondition(
				createPolicyConditionLevels());
		condition.setParameterIdentifier(parameterIdentifier);

		return condition;
	}

	public static MPolicyConditionLevels createPolicyConditionLevels() {
		final MPolicyConditionLevels conditionLevels = new MPolicyConditionLevels();
		conditionLevels.setThresholdType(ThresholdType.LESS);
		conditionLevels.setCriticalLevel(new ConditionLevel("20.0", 10L,
				"30.0", 10L));
		conditionLevels.setWarningLevel(new ConditionLevel("40.0", 10L, "50.0",
				10L));

		return conditionLevels;
	}

	public static MPolicyConditionsTemplate createPolicyConditionsTemplate(
			final String name, final MPolicyConditionLevels conditionLevels) {
		final MPolicyConditionsTemplate template = new MPolicyConditionsTemplate();
		template.setName(name);
		template.setParameterType(ParameterType.LEVEL);
		template.setConditionLevels(conditionLevels);
		return template;
	}

	/**
	 * Creates a full policy key.
	 * 
	 * @param policyTemplateKey
	 *            a full policy template key.
	 * @param fullTaskKey
	 *            a full task key like "agentKey.moduleKey.taskKey"
	 * @param parameterKey
	 *            a parameterKey created by
	 *            {@link ParameterIdentifier#createParameterStorageKey()}.
	 * @return the key like policyTemplateKey.fullTaskKey.parameterKey
	 */
	public static String createPolicyKey(final String policyTemplateKey,
			final String fullTaskKey, final String parameterKey) {
		return policyTemplateKey
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ fullTaskKey
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ parameterKey;
	}

	public static MPolicyAction createPolicySendAlertAction(
			final String actionName, final String alertTypeName) {
		final MPolicySendAlert action = new MPolicySendAlert();
		action.setAlertType(alertTypeName);
		action.setName(actionName);
		return action;
	}

	public static MPolicyActionWithContacts createPolicySendEmailAction(
			final String actionName) {
		final MPolicySendEmail action = new MPolicySendEmail();
		action.setName(actionName);
		return action;
	}

	public static MPolicyActionWithContacts createPolicySendEmailAction(
			final String actionName, final String subject, final String body) {
		final MPolicySendEmail action = new MPolicySendEmail();
		action.setName(actionName);
		return action;
	}

	public static MPolicyActionWithContacts createPolicySendSmsAction(
			final String actionName) {
		final MPolicySendSms action = new MPolicySendSms();
		action.setName(actionName);
		return action;
	}

	public static MPolicy createPolicyTemplate(final String agentName,
			final String moduleName,
			final ParameterIdentifier parameterIdentifier,
			final ParameterType parameterType, final List<MPolicyAction> actions) {
		final MPolicy policy = new MPolicy();
		policy.setSource(Source.getModuleSource(agentName, moduleName));
		policy.setDisplayName("TestPolicyDisplayName");
		policy.setKey(createPolicyTemplateKey(agentName,
				parameterIdentifier.createParameterStorageKey()));
		final MContinuousThresholdFallCondition condition = new MContinuousThresholdFallCondition();
		condition.setParameterIdentifier(parameterIdentifier);
		if (parameterType == ParameterType.BOOL) {
			condition.setThresholdType(ThresholdType.EQUALS);
			condition
					.setCriticalLevel(new ConditionLevel("1.0", 10L, "1.0", 5L));
		} else {
			condition.setThresholdType(ThresholdType.LESS);
			condition.setCriticalLevel(new ConditionLevel("20.0", 10L, "30.0",
					10L));
			condition.setWarningLevel(new ConditionLevel("40.0", 10L, "50.0",
					10L));
		}
		policy.setCondition(condition);
		policy.setActions(actions);

		return policy;
	}

	public static MPolicy createPolicyTemplate(final String agentName,
			final String moduleName, final String parameterName,
			final ParameterType parameterType, final List<MPolicyAction> actions) {
		return createPolicyTemplate(agentName, moduleName,
				new ParameterIdentifier(parameterName, null), parameterType,
				actions);
	}

	/**
	 * Creates a full policy template key.
	 * 
	 * @param agentKey
	 *            an agent key. It can NOT be null.
	 * @param parameterKey
	 *            a parameterKey created by
	 *            {@link ParameterIdentifier#createParameterStorageKey()}. It
	 *            can NOT be null.
	 * 
	 * @return the key like agentKey. {@link #IT09A_MODULE_KEY}.parameterKey
	 */
	public static String createPolicyTemplateKey(final String agentKey,
			final String parameterKey) {
		return createModuleKey(agentKey, null)
				+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
				+ parameterKey;
	}

	public static Map<String, String> createPropertyConfigurationsAsMapWithValues(
			final String programNumber, final String programDisplayName) {
		final Map<String, String> result = new HashMap<String, String>();
		final List<MProperty> properties = createEmptyPropertyConfigurations();
		for (final MProperty property : properties) {
			final String propertyName = property.getName();
			String propertyValue = null;
			if (PROGRAM_NUMBER.equals(propertyName)) {
				propertyValue = programNumber;
			} else if (PROGRAM_DISPLAY_NAME.equals(propertyName)) {
				propertyValue = programDisplayName;
			}
			result.put(propertyName, propertyValue);
		}
		return result;
	}

	public static MResultConfiguration createResultConfiguration(
			final MAgentTask agentTask) {
		return createResultConfiguration(agentTask, true);
	}

	public static MResultConfiguration createResultConfiguration(
			final MAgentTask agentTask,
			final boolean enableMultipleConfigurations) {
		final MResultConfiguration configuration = ConfigurationUtil
				.createFromTemplate(agentTask.getModule()
						.getTemplateResultConfiguration(), agentTask);

		agentTask.setResultConfiguration(configuration);

		return configuration;
	}

	public static MResultParameterConfiguration createResultParameterConfiguration(
			final String parameterName, final AggregationType aggregationType,
			final ParameterType parameterType) {
		final MResultParameterConfiguration resultParameterConfiguration = new MResultParameterConfiguration();
		resultParameterConfiguration.setName(parameterName);
		resultParameterConfiguration.setDisplayName(parameterName);
		resultParameterConfiguration.setDescription("Description of the "
				+ parameterName);
		resultParameterConfiguration.setType(parameterType);
		resultParameterConfiguration.setUnits("Mbit/s");
		resultParameterConfiguration.setAggregationType(aggregationType);
		if (parameterType == ParameterType.BOOL) {
			resultParameterConfiguration.setThreshold(new MParameterThreshold(
					ThresholdType.EQUALS, null, 1d));
		} else {
			resultParameterConfiguration.setThreshold(new MParameterThreshold(
					ThresholdType.LESS, 50D, 40D));
		}
		resultParameterConfiguration.setLocation(new MResultParameterLocation(
				parameterName, "test"));
		return resultParameterConfiguration;
	}

	public static List<MResultParameterConfiguration> createResultParameterConfigurations(
			final Integer index) {
		final List<MResultParameterConfiguration> parameterConfigurations = new ArrayList<MResultParameterConfiguration>();
		final String indexString = index == null ? "" : index.toString();
		parameterConfigurations.add(createResultParameterConfiguration(
				SIGNAL_LEVEL + indexString, AggregationType.MIN,
				ParameterType.LEVEL));
		parameterConfigurations.add(createResultParameterConfiguration(
				SIGNAL_NOISE + indexString, AggregationType.MAX,
				ParameterType.LEVEL));
		parameterConfigurations.add(createResultParameterConfiguration(
				NICAM_LEVEL + indexString, AggregationType.MIN,
				ParameterType.LEVEL));
		parameterConfigurations.add(createResultParameterConfiguration(
				VIDEO_AUDIO + indexString, AggregationType.MIN,
				ParameterType.LEVEL));
		parameterConfigurations.add(createResultParameterConfiguration(
				FAKE_STRONG_SIGNAL_BOOLEAN + indexString, AggregationType.MIN,
				ParameterType.BOOL));
		return parameterConfigurations;
	}

	/**
	 * Creates a full task key.
	 * 
	 * @param agentKey
	 *            an agent key. It it is null, then moduleKey must be a full
	 *            key.
	 * @param moduleKey
	 *            a short module key or long module key. It can NOT be null. If
	 *            agentKey is null, then moduleKey must be a full key.
	 * @param taskKey
	 *            a short task key. It can NOT be null.
	 * @return the key like agentKey.moduleKey.shortTaskKey
	 */
	public static String createTaskKey(final String agentKey,
			final String moduleKey, final String taskKey) {
		if (agentKey != null) {
			return createModuleKey(agentKey, moduleKey)
					+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
					+ taskKey;
		} else {
			return moduleKey
					+ MResultParameterConfiguration.STORAGE_KEY_SEPARATOR
					+ taskKey;
		}
	}

	public static List<MProperty> createTaskProperties(final MAgentTask task) {
		final List<MProperty> taskProperties = new ArrayList<MProperty>();
		taskProperties.add(new MProperty("plp", "0", true));
		taskProperties.add(new MProperty(PROGRAM_NUMBER, "1160", true));
		taskProperties.add(new MProperty(PROGRAM_DISPLAY_NAME,
				"Program Display Name", true));
		taskProperties.add(new MProperty(
				MAgentTask.RELEATED_RECORDING_TASK_PROPERTY_NAME,
				task.getKey(), true));

		return taskProperties;
	}

	public static MResultConfigurationTemplate createTemplateResultConfiguration(
			final boolean enableMultipleConfigurations) {
		final MResultConfigurationTemplate configuration = new MResultConfigurationTemplate();
		configuration.setSamplingRate(1L);
		configuration
				.setPropertyConfigurations(createEmptyPropertyConfigurations());
		configuration
				.setParameterDisplayNameFormat("${displayName} - ${properties."
						+ PROGRAM_DISPLAY_NAME + "} (${properties."
						+ PROGRAM_NUMBER + "})");

		final List<MResultParameterConfiguration> parameterConfigurations = new ArrayList<MResultParameterConfiguration>();
		parameterConfigurations
				.addAll(createResultParameterConfigurations(null));
		if (enableMultipleConfigurations) {
			for (int index = 1; index <= PARAMETER_COUNT; index++) {
				parameterConfigurations
						.addAll(createResultParameterConfigurations(index));
			}
		}
		configuration.setParameterConfigurations(parameterConfigurations);

		return configuration;
	}

	public static MAgentModule createTvStreamMonitorAgentModule() {
		if (tvStreamMonitorAgentModule == null) {
			tvStreamMonitorAgentModule = new MAgentModule();
			tvStreamMonitorAgentModule.setDisplayName("TV Stream Monitor");
			tvStreamMonitorAgentModule.setKey("TVStreamControlModule");
			tvStreamMonitorAgentModule
					.setTemplateResultConfiguration(createTemplateResultConfiguration(false));
		}
		return tvStreamMonitorAgentModule;
	}

	public static MUser createUser(final String login, final String firstName,
			final String secondName, final String lastName,
			final List<MRole> roles, final String email) {
		final MUser user = new MUser();

		user.setFirstName(firstName);
		user.setSecondName(secondName);
		user.setLastName(lastName);
		user.setLogin(login);
		user.setRoles(roles);
		user.setEmail(email);

		return user;
	}

	public static MUserReportsTemplate createUserReportTemplate(
			final String templateName) {
		final MUserReportsTemplate template = new MUserReportsTemplate(
				templateName);
		template.setCriterion(CriterionQueryFactory.getQuery().le(
				"endDateTime", new Date()));
		template.setOrder(Order.asc("endDateTime"));
		template.setHiddenColumns(new String[]{"alert", "duration"});
		template.setTimeInterval(TimeInterval.get(
				new Date(System.currentTimeMillis()
						- TimeUnit.HOURS.toMillis(1)), new Date()));

		return template;
	}

	public static MUserResultTemplate createUserResultTemplate(
			final String templateName) {
		final MUserResultTemplate template = new MUserResultTemplate(
				templateName);
		template.setTimeInterval(TimeInterval.get(
				new Date(System.currentTimeMillis()
						- TimeUnit.HOURS.toMillis(1)), new Date()));
		return template;
	}

	public static void deleteFolder(final File folder) {
		if (folder.exists()) {
			final File[] files = folder.listFiles();
			if (files != null) { // some JVMs return null for empty dirs
				for (final File f : files) {
					if (f.isDirectory()) {
						deleteFolder(f);
					} else {
						f.delete();
					}
				}
			}
			folder.delete();
		}
	}

	public static void deleteFolder(final String filePath) {
		final File folder = new File(filePath);
		deleteFolder(folder);
	}

	@SuppressWarnings("deprecation")
	public static void deleteStorage(
			final MResultConfigurationTemplate resultConfiguration,
			final String storageHome) {
		for (final MResultParameterConfiguration parameterConfiguration : resultConfiguration
				.getParameterConfigurations()) {
			if (parameterConfiguration.getLocation().getFileLocation() != null) {
				deleteFolder(new File(storageHome
						+ "/"
						+ parameterConfiguration.getLocation()
								.getFileLocation()));
			}
		}
	}
}
