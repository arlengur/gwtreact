/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.gwt.client.QoSBBMNameTokens;
import com.tecomgroup.qos.gwt.client.presenter.BBMMainPresenter.MyProxy;
import com.tecomgroup.qos.gwt.client.presenter.BBMMainPresenter.MyView;

/**
 * Основной презентер для QoS ШПД модуля
 * 
 * @author abondin
 * 
 */
public class BBMMainPresenter extends Presenter<MyView, MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@NameToken(QoSBBMNameTokens.probesAndTasks)
	public static interface MyProxy extends ProxyPlace<BBMMainPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<BBMMainPresenter> {
		void sayHello();
	}

	private final PlaceManager placeManager;

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 * @param placeManager
	 */
	@Inject
	public BBMMainPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		view.setUiHandlers(this);
	}

	public void actionTest() {
		placeManager.revealPlace(new PlaceRequest(QoSBBMNameTokens.probesAndTasks), true);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}
}
