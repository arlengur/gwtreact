/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import java.util.Date;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MAlert;
import com.tecomgroup.qos.domain.MAlertReport;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;

/**
 * @author kunilov.p
 * 
 */
public interface AlertReportProperties extends PropertyAccess<MAlertReport> {

	@Path("alert.alertType.displayName")
	ValueProvider<MAlertReport, String> alertTypeDisplayName();

	ValueProvider<MAlertReport, Date> endDateTime();

	@Path("id")
	ModelKeyProvider<MAlertReport> key();

	@Path("alert.originator.displayName")
	ValueProvider<MAlertReport, String> originator();

	ValueProvider<MAlertReport, PerceivedSeverity> perceivedSeverity();

	@Path("alert.settings")
	ValueProvider<MAlertReport, String> settings();

	@Path("alert.source.displayName")
	ValueProvider<MAlertReport, String> source();

	ValueProvider<MAlertReport, Date> startDateTime();

	/**
	 * The requirement tells that {@link MAlert#getSource()} must be only
	 * {@link MAgentTask}, so the path of the system component is always the
	 * same.
	 * 
	 * @return
	 */
	@Path("alert.source.parent.parent.displayName")
	ValueProvider<MAlertReport, String> systemComponent();

	@Path("alert.detectionValue")
	ValueProvider<MAlertReport, Double> detectionValue();

	@Path("alert.extraData")
	ValueProvider<MAlertReport, String> thresholdValue();
}
