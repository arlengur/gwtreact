/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.inject.client.AsyncProvider;
import com.google.gwt.inject.client.GinModules;
import com.tecomgroup.qos.gwt.client.gin.QoSGinModule;
import com.tecomgroup.qos.gwt.client.gin.QoSGinjector;
import com.tecomgroup.qos.gwt.client.presenter.BBMMainPresenter;

/**
 * Ключевой класс. Необходимо описать все модули, которые будут загружены в @GinModules
 * Также необходимо создать get методы для всех презентеров
 * 
 * 
 * @author abondin
 * 
 */
@GinModules({QoSGinModule.class, BBMGinModule.class})
public interface QoSBBMGinjector extends QoSGinjector {
	/**
	 * 
	 * @return
	 */
	AsyncProvider<BBMMainPresenter> getMediaPlayerPresenter();
}
