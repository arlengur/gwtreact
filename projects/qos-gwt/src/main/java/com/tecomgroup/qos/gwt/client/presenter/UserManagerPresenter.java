/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.annotations.UseGatekeeper;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.GridPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.GroupManagerWidgetPresenter;
import com.tecomgroup.qos.gwt.client.presenter.widget.users.UserManagerGridWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.UsersGatekeeper;

/**
 * @author meleshin.o
 * 
 */
public class UserManagerPresenter
		extends
			Presenter<UserManagerPresenter.MyView, UserManagerPresenter.MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@UseGatekeeper(UsersGatekeeper.class)
	@NameToken(QoSNameTokens.users)
	public static interface MyProxy extends ProxyPlace<UserManagerPresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<UserManagerPresenter> {
	}

	private static final Logger LOGGER = Logger
			.getLogger(UserManagerPresenter.class.getName());

	private final QoSMessages messages;

	private final Map<String, GridPresenter> tabs = new LinkedHashMap<String, GridPresenter>();

	@Inject
	public UserManagerPresenter(
			final EventBus eventBus,
			final MyView view,
			final MyProxy proxy,
			final UserManagerGridWidgetPresenter userManagerGridWidgetPresenter,
			final GroupManagerWidgetPresenter groupManagerWidgetPresenter,
			final QoSMessages messages) {
		super(eventBus, view, proxy);
		this.messages = messages;

		tabs.put(messages.users(), userManagerGridWidgetPresenter);
		tabs.put(messages.userGroups(), groupManagerWidgetPresenter);
		view.setUiHandlers(this);
	}

	public void actionSelectGridPreseter(final String tabKey) {
		final GridPresenter presenter = tabs.get(tabKey);
		if (presenter != null) {
			presenter.reload(true);
		}
	}

	@Override
	protected void onBind() {
		super.onBind();
		for (final Map.Entry<String, GridPresenter> entry : tabs.entrySet()) {
			setInSlot(entry.getKey(), (PresenterWidget<?>) entry.getValue());
		}
	}

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}
}
