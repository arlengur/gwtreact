/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MSource;

/**
 * An interface of the service clearing/removing widgets.
 * 
 * @author kshnyakin.m
 * 
 */
public interface WidgetDeleter {

	void clearSourceRelatedWidgets(MSource source);
}
