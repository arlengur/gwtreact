/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin.desktop;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.tecomgroup.qos.gwt.client.gin.QoSMediaGinjector;
import com.tecomgroup.qos.gwt.client.presenter.AlertWithVideoDetailsPresenter;
import com.tecomgroup.qos.gwt.client.presenter.MediaUserProfilePresenter;

/**
 * @author ivlev.e
 * 
 */
@GinModules({DesktopQoSGinModule.class, DesktopMediaGinModule.class})
public interface DesktopQoSMediaGinjector
		extends
			QoSMediaGinjector,
			DesktopQoSGinjector {

	AsyncProvider<AlertWithVideoDetailsPresenter> getAlertWithVideoDetailsPresenter();

	AsyncProvider<MediaUserProfilePresenter> getMediaUserProfilePresenter();
}
