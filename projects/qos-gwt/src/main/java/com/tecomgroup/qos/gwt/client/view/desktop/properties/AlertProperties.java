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
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.ProbableCause;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.Status;

/**
 * @author ivlev.e
 * 
 */
public interface AlertProperties extends PropertyAccess<MAlert> {

	ValueProvider<MAlert, Boolean> acknowledged();

	ValueProvider<MAlert, Long> alertCount();

	ValueProvider<MAlert, Date> creationDateTime();

	@Path("alertType.displayName")
	ValueProvider<MAlert, String> displayName();

	@Path("id")
	ModelKeyProvider<MAlert> key();

	ValueProvider<MAlert, Date> lastUpdateDateTime();;

	@Path("originator.displayName")
	ValueProvider<MAlert, String> originator();

	ValueProvider<MAlert, PerceivedSeverity> perceivedSeverity();

	@Path("alertType.probableCause")
	ValueProvider<MAlert, ProbableCause> probableCause();

	@Path("settings")
	ValueProvider<MAlert, String> settings();

	@Path("source.displayName")
	ValueProvider<MAlert, String> source();

	ValueProvider<MAlert, SpecificReason> specificReason();

	ValueProvider<MAlert, Status> status();

	/**
	 * The requirement tells that {@link MAlert#getSource()} must be only
	 * {@link MAgentTask}, so the path of the system component is always the
	 * same.
	 * 
	 * @return
	 */
	@Path("source.parent.parent.displayName")
	ValueProvider<MAlert, String> systemComponent();
}
