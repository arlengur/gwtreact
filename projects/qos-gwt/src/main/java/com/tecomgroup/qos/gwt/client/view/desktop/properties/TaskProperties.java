/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.properties;

import java.util.Date;
import java.util.List;

import com.google.gwt.editor.client.Editor.Path;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MProperty;

/**
 * @author ivlev.e
 * 
 */
public interface TaskProperties extends PropertyAccess<MAgentTask> {

	/**
	 * Access to displayName of {@link MAgent}
	 */
	@Path("parent.parent.displayName")
	ValueProvider<MAgentTask, String> agentDisplayName();

	@Path("creationDateTime")
	ValueProvider<MAgentTask, Date> dateCreation();

	@Path("disabled")
	ValueProvider<MAgentTask, Boolean> disabled();

	@Path("displayName")
	ValueProvider<MAgentTask, String> displayName();

	@Path("key")
	ValueProvider<MAgentTask, String> key();

	@Path("displayName")
	LabelProvider<MAgentTask> label();

	@Path("key")
	ModelKeyProvider<MAgentTask> modelKey();

	@Path("module.displayName")
	ValueProvider<MAgentTask, String> moduleName();

	@Path("properties")
	ValueProvider<MAgentTask, List<MProperty>> properties();

	@Path("resultConfiguration.samplingRate")
	ValueProvider<MAgentTask, Long> samplingRate();

	@Path("version")
	ValueProvider<MAgentTask, Long> version();

}
