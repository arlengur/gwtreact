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
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.MAlertUpdate;

/**
 * @author ivlev.e
 * 
 */
public interface AlertHistoryProperties extends PropertyAccess<MAlertUpdate> {

	@Path("updateType")
	ValueProvider<MAlertUpdate, UpdateType> action();

	@Path("alert.perceivedSeverity")
	ValueProvider<MAlertUpdate, PerceivedSeverity> alertSeverity();

	@Path("alert.alertType.displayName")
	ValueProvider<MAlertUpdate, String> alertTypeDisplayName();

	@Path("comment")
	ValueProvider<MAlertUpdate, String> comment();

	ValueProvider<MAlertUpdate, Date> dateTime();

	ValueProvider<MAlertUpdate, String> field();

	@Path("id")
	ModelKeyProvider<MAlertUpdate> key();

	ValueProvider<MAlertUpdate, String> newValue();

	@Path("alert.originator.displayName")
	ValueProvider<MAlertUpdate, String> originator();

	@Path("oldValue")
	ValueProvider<MAlertUpdate, String> previousValue();

	@Path("alert.source.displayName")
	ValueProvider<MAlertUpdate, String> source();

	/**
	 * The requirement tells that {@link MAlert#getSource()} must be only
	 * {@link MAgentTask}, so the path of the system component is always the
	 * same.
	 * 
	 * @return
	 */
	@Path("alert.source.parent.parent.displayName")
	ValueProvider<MAlertUpdate, String> systemComponent();
}
