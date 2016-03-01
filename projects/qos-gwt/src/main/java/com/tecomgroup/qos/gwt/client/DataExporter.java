/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import java.util.Collection;

import com.tecomgroup.qos.CommonExportDataWrapper;

/**
 * Provides common methods to export data using {@link CommonExportDataWrapper}
 * 
 * @author kshnyakin.m
 * 
 */
public interface DataExporter<T extends CommonExportDataWrapper> {

	void export(T dataWrapper);

	Collection<String> getAgentDisplayNames();
}
