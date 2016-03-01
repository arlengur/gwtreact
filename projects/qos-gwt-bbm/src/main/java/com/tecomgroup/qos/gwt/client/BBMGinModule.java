/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.tecomgroup.qos.gwt.client.presenter.BBMMainPresenter;
import com.tecomgroup.qos.gwt.client.view.BBMMainView;

/**
 * Не забудь связать ваш Presenter-Proxy-View в этом классе!
 * 
 * @author abondin
 * 
 */
public class BBMGinModule extends AbstractPresenterModule {
	@Override
	protected void configure() {
		bindPresenter(BBMMainPresenter.class,
				BBMMainPresenter.MyView.class, BBMMainView.class,
				BBMMainPresenter.MyProxy.class);
	}
}
