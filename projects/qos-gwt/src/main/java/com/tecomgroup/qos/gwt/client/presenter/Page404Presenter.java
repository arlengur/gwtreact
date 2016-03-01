/*
 * Copyright (C) 2013 Tecomgroup.
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
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;

/**
 * @author ivlev.e
 * 
 */
public class Page404Presenter
		extends
			Presenter<Page404Presenter.MyView, Page404Presenter.MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.page404)
	public static interface MyProxy extends ProxyPlace<Page404Presenter> {
	}

	public interface MyView extends View, HasUiHandlers<Page404Presenter> {

	}

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 */
	@Inject
	public Page404Presenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy) {
		super(eventBus, view, proxy);
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}

}
