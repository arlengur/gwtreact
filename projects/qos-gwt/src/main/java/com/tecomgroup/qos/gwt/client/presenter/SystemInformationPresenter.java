/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.*;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.tecomgroup.qos.BuildInfo;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.service.SystemInformationServiceAsync;

/**
 * @author abondin
 * 
 */
public class SystemInformationPresenter
		extends
			PresenterWidget<SystemInformationPresenter.MyView>
		implements
			UiHandlers,
			BeforeLogoutEventHandler {

	public static interface MyView
			extends
				View,
				HasUiHandlers<SystemInformationPresenter> {
		void setBuildInfo(BuildInfo buildInfo);
	}

	private final SystemInformationServiceAsync informationService;

	private final CurrentUser user;

	private final QoSMessages messages;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public SystemInformationPresenter(
			final SystemInformationServiceAsync informationService,
			final EventBus eventBus, final QoSMessages messages,
			final MyView view, final CurrentUser user) {
		super(eventBus, view);
		this.informationService = informationService;
		this.user = user;
		this.messages = messages;
		getView().setUiHandlers(this);
		init();

		getEventBus().addHandler(BeforeLogoutEvent.TYPE, this);
	}

	@Override
	public void onBeforeLogout(BeforeLogoutEvent event) {
		actionLogout();
	}

	public void actionLogout() {
		final RequestBuilder requestBuilder = new RequestBuilder(
				RequestBuilder.POST, GWT.getHostPageBaseURL()
				+ "j_spring_security_logout");
		try {
			requestBuilder.sendRequest("", new RequestCallback() {
				@Override
				public void onError(final Request request,
									final Throwable exception) {
					AppUtils.showErrorMessage(messages.logoutError(), exception);
				}

				@Override
				public void onResponseReceived(final Request request,
											   final Response response) {
					user.setUser(null);
					Window.Location.reload();
				}
			});
		} catch (final RequestException e) {
			AppUtils.showErrorMessage(messages.logoutError(), e);
		}
	}

	protected void init() {
		informationService
				.getBuildInfo(new AutoNotifyingAsyncCallback<BuildInfo>() {

					@Override
					protected void success(final BuildInfo buildInfo) {
						getView().setBuildInfo(buildInfo);
					}
				});
	}
}
