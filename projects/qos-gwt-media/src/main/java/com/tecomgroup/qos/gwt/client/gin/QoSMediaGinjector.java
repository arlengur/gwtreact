/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.gin;

import com.google.gwt.inject.client.AsyncProvider;
import com.tecomgroup.qos.gwt.client.presenter.LiveVideoPresenter;
import com.tecomgroup.qos.gwt.client.presenter.RecordedVideoPresenter;

/**
 * Ключевой класс. Необходимо описать все модули, которые будут загружены в @GinModules
 * Также необходимо создать get методы для всех презентеров
 * 
 * 
 * @author abondin
 * 
 */
public interface QoSMediaGinjector {

	/**
	 * 
	 * @return
	 */
	AsyncProvider<LiveVideoPresenter> getLiveVideoPresenter();

	/**
	 * 
	 * @return
	 */
	AsyncProvider<RecordedVideoPresenter> getRecordedVideoPresenter();
}
