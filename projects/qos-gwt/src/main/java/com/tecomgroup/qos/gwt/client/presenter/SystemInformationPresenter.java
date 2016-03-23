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
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;
import com.tecomgroup.qos.gwt.client.event.CurrentUserChangedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.UserLogoutEventFilter;
import com.tecomgroup.qos.service.SystemInformationServiceAsync;
import com.tecomgroup.qos.service.UserManagerServiceAsync;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;


/**
 * @author abondin
 * 
 */
public class SystemInformationPresenter
		extends
			PresenterWidget<SystemInformationPresenter.MyView>
		implements
			UiHandlers,
			BeforeLogoutEventHandler,
			QoSEventListener,
			CurrentUserChangedEvent.CurrentUserChangedEventHandler{

	public static interface MyView
			extends
				View,
				HasUiHandlers<SystemInformationPresenter> {
		void setBuildInfo(BuildInfo buildInfo);
	}

	private final SystemInformationServiceAsync informationService;

	private final CurrentUser user;

	private final QoSMessages messages;

	private final QoSEventService eventService;

	private final UserManagerServiceAsync userManagerServiceAsync;

	/**
	 * @param eventBus
	 * @param view
	 */
	@Inject
	public SystemInformationPresenter(
			final SystemInformationServiceAsync informationService,
			final EventBus eventBus, final QoSMessages messages,
			final MyView view, final CurrentUser user,
			final QoSEventService eventService,
			final UserManagerServiceAsync userManagerServiceAsync) {
		super(eventBus, view);
		this.informationService = informationService;
		this.user = user;
		this.messages = messages;
		this.eventService = eventService;
		this.userManagerServiceAsync = userManagerServiceAsync;
		getView().setUiHandlers(this);
		init();

		getEventBus().addHandler(BeforeLogoutEvent.TYPE, this);
	}

	@Override
	protected void onBind() {
		super.onBind();
		getEventBus().addHandler(CurrentUserChangedEvent.TYPE, this);
	}

	@Override
	public void onServerEvent(AbstractEvent event) {
		eventService.unsubscribe(UserLogoutEvent.class, this);
		actionLogout();
	}

	@Override
	public void onBeforeLogout(BeforeLogoutEvent event) {
		eventService.unsubscribe(UserLogoutEvent.class, this);
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
				.getBuildInfo(new AutoNotifyingAsyncLogoutOnFailureCallback<BuildInfo>() {

					@Override
					protected void success(final BuildInfo buildInfo) {
						getView().setBuildInfo(buildInfo);
					}
				});
	}

	@Override
	public void onEvent(CurrentUserChangedEvent event) {
		final MUser user = event.getUser();
		if (user != null) {
			eventService
					.subscribe(
							UserLogoutEvent.class,
							this,
							new UserLogoutEventFilter(user.getLogin()));
		} else {
			eventService.unsubscribe(UserLogoutEvent.class, this);
		}
	}
}
