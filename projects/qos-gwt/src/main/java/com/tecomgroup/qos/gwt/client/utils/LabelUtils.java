/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.AlertReportWrapper;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MResultParameterConfiguration;
import com.tecomgroup.qos.domain.MUser.Role;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.util.SimpleUtils;
/**
 * @author abondin
 * 
 */
public class LabelUtils {
	public static String createSeriesLabel(final MAgent agent,
			final MAgentTask task,
			final MResultParameterConfiguration parameterConfiguration) {
		return createSeriesLabel(agent.getDisplayName(), task.getDisplayName(),
				parameterConfiguration.getParsedDisplayFormat(),
				parameterConfiguration.getUnits());
	}

	public static String createSeriesLabel(final MAgent agent,
			final String taskLabel,
			final MResultParameterConfiguration parameterConfiguration) {
		return createSeriesLabel(agent.getDisplayName(), taskLabel,
				parameterConfiguration.getParsedDisplayFormat(),
				parameterConfiguration.getUnits());
	}

	public static String createSeriesLabel(final String agentDisplayName,
			final String taskDisplayName, final String parameterDisplayFormat,
			final String parameterUnits) {

		String displayName = agentDisplayName + " | " + taskDisplayName + " | "
				+ parameterDisplayFormat;

		if (SimpleUtils.isNotNullAndNotEmpty(parameterUnits)) {
			displayName = displayName + " (" + parameterUnits + ")";
		}
		return displayName;
	}

	public static String getAlertTypeLabel(final MAlertType alertType) {
		if (alertType == null) {
			return null;
		} else {
			return alertType.getDisplayName() == null
					? alertType.getName()
					: alertType.getDisplayName();
		}
	}

	/**
	 * The order of the severity values must be the same as in the definition of
	 * {@link PerceivedSeverity}. It is used in
	 * {@link AlertReportWrapper.I18nReportLabels#severities}. Otherwise the
	 * order of the severities will be changed in
	 * {@link XlsWriter#createReportHeader}.
	 * 
	 * @param messages
	 * @return
	 */
	public static Map<PerceivedSeverity, String> getAllSeverityLabels(
			final QoSMessages messages) {
		/**
		 * Use only LinkedHashMap, because the order of the labels is important
		 * because it is used in {@link XlsWriter#createReportHeader}
		 */
		final Map<PerceivedSeverity, String> labels = new LinkedHashMap<PerceivedSeverity, String>();
		labels.put(PerceivedSeverity.CRITICAL,
				messages.perceivedSeverityCritical());
		labels.put(PerceivedSeverity.MAJOR, messages.perceivedSeverityMajor());
		labels.put(PerceivedSeverity.WARNING,
				messages.perceivedSeverityWarning());
		labels.put(PerceivedSeverity.MINOR, messages.perceivedSeverityMinor());
		labels.put(PerceivedSeverity.NOTICE, messages.perceivedSeverityNotice());
		labels.put(PerceivedSeverity.INDETERMINATE,
				messages.perceivedSeverityIndeterminate());
		return labels;
	}

	public static Map<Role, String> getRoleLabels(final QoSMessages messages) {
		final Map<Role, String> labels = new HashMap<Role, String>();
		labels.put(Role.ROLE_ADMIN, messages.roleAdmin());
		labels.put(Role.ROLE_USER, messages.roleUser());
		labels.put(Role.ROLE_CONFIGURATOR, messages.roleConfigurator());
		labels.put(Role.ROLE_SUPER_ADMIN, messages.roleSuperAdmin());
		return labels;
	}

	/**
	 * The order of the severity values must be the same as in the definition of
	 * {@link PerceivedSeverity}. It is used in
	 * {@link AlertReportWrapper.I18nReportLabels#severities}. Otherwise the
	 * order of the severities will be changed in
	 * {@link XlsWriter#createReportHeader}.
	 * 
	 * @param severities
	 *            the set of {@link PerceivedSeverity} to get its labels.
	 * @param messages
	 * @return
	 */
	public static Map<PerceivedSeverity, String> getSeverityLabels(
			final Set<PerceivedSeverity> severities, final QoSMessages messages) {
		/**
		 * Use only LinkedHashMap, because the order of the labels is important
		 * because it is used in {@link XlsWriter#createReportHeader}
		 */
		final Map<PerceivedSeverity, String> labels = new LinkedHashMap<PerceivedSeverity, String>();

		final List<PerceivedSeverity> sortedSeverities = new ArrayList<PerceivedSeverity>(
				severities);
		Collections.sort(sortedSeverities);

		final Map<PerceivedSeverity, String> allSeverityLabels = getAllSeverityLabels(messages);
		for (final PerceivedSeverity severity : sortedSeverities) {
			labels.put(severity, allSeverityLabels.get(severity));
		}

		return labels;
	}

	public static Map<UpdateType, String> getUpdateTypeLabels(
			final QoSMessages messages) {
		final Map<UpdateType, String> labels = new HashMap<UpdateType, String>();
		labels.put(UpdateType.ACK, messages.alertUpdateTypeAck());
		labels.put(UpdateType.AGENT_RESTART,
				messages.alertUpdateTypeAgentRestart());
		labels.put(UpdateType.AUTO_CLEARED,
				messages.alertUpdateTypeAutoCleared());
		labels.put(UpdateType.COMMENT, messages.alertUpdateTypeComment());
		labels.put(UpdateType.NEW, messages.alertUpdateTypeNew());
		labels.put(UpdateType.OPERATOR_CLEARED,
				messages.alertUpdateTypeOperatorCleared());
		labels.put(UpdateType.OPERATOR_DELETED,
				messages.alertUpdateTypeOperatorDeleted());
		labels.put(UpdateType.REPEAT, messages.alertUpdateTypeRepeat());
		labels.put(UpdateType.SEVERITY_UPGRADE,
				messages.alertUpdateTypeSeverityUpgrade());
		labels.put(UpdateType.SEVERITY_DEGRADATION,
				messages.alertUpdateTypeSeverityDegradation());
		labels.put(UpdateType.UNACK, messages.alertUpdateTypeUnack());
		labels.put(UpdateType.UPDATE, messages.alertUpdateTypeUpdate());
		return labels;
	}
}
