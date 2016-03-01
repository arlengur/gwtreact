/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin.mobile;

import com.google.gwt.inject.client.GinModules;
import com.tecomgroup.qos.gwt.client.gin.QoSMediaGinjector;

/**
 * @author ivlev.e
 * 
 */
@GinModules({MobileQoSGinModule.class, MobileMediaGinModule.class})
public interface MobileQoSMediaGinjector
		extends
			QoSMediaGinjector,
			MobileQoSGinjector {

}
