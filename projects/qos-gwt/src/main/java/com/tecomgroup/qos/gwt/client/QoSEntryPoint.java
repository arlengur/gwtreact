/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.shared.UmbrellaException;
import com.google.gwt.user.client.Window.Navigator;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.DelayedBindRegistry;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent;
import com.tecomgroup.qos.gwt.client.event.ClientPropertiesLoadedEvent;
import com.tecomgroup.qos.gwt.client.gin.QoSGinjector;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.MainPagePresenter;
import com.tecomgroup.qos.gwt.client.utils.*;
import com.tecomgroup.qos.gwt.client.utils.AppUtils.ApplicationMode;
import com.tecomgroup.qos.service.SystemInformationServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * NOTE: using of {@link AutoNotifyingAsyncCallback} is forbidden in this class
 * due to it triggers creating instance of {@link MainPagePresenter} before GWTP
 * normal lifecycle. Use {@link AsyncCallback} instead of this.
 * 
 * @author abondin
 */
public abstract class QoSEntryPoint
		implements
			EntryPoint {

	protected QoSMessages messages = GWT.create(QoSMessages.class);

	protected final static Logger LOGGER = Logger.getLogger(QoSEntryPoint.class
			.getName());

	@Inject
	private SystemInformationServiceAsync systemInformationService;

	@Inject
	private UserServiceAsync userService;

	@Inject
	@Named("clientProperties")
	private Map<String, Object> clientProperties;

	/**
	 * Дополнительные действия по загрузки приложения
	 */
	protected void afterModuleLoad() {
	}

	public abstract QoSGinjector getInjector();

	protected void load() {
		final QoSGinjector ginjector = getInjector();
		AppUtils.setEventBus(ginjector.getEventBus());
		AppUtils.setMessages(messages);
		AppUtils.setPlaceManager(ginjector.getPlaceManager());
		DelayedBindRegistry.bind(ginjector);
		ginjector.injectEntryPoint(this);

		systemInformationService
				.getClientProperties(new AsyncCallback<Map<String, Object>>() {

					@Override
					public void onFailure(final Throwable caught) {
						throw new RuntimeException(
								"Unable to load client properties");
					}

					@Override
					public void onSuccess(final Map<String, Object> result) {
						clientProperties.putAll(result);
						AppUtils.setClientProperties(clientProperties);
						AppUtils.getEventBus().fireEvent(
								new ClientPropertiesLoadedEvent());
						systemInformationService
								.getTimeZoneList(new AsyncCallback<List<TimeZoneWrapper>>() {

									@Override
									public void onFailure(final Throwable caught) {
										throw new RuntimeException(
												"Unable to load server time zones");
									}

									@Override
									public void onSuccess(
											final List<TimeZoneWrapper> timeZoneWrappers) {
										DateUtils
												.initializeClientTimeZones(timeZoneWrappers);
										DateUtils
												.initializeServerTimeZones(timeZoneWrappers);

										userService.getCurrentUser(new AutoNotifyingAsyncLogoutOnFailureCallback<MUser>(
												"Unable to load current user", true) {
											@Override
											protected void success(final MUser mUser) {
												AppUtils.getCurrentUser().setUser(mUser);
												loadNavigationLinks(getInjector().getEventBus());

												ginjector.getPlaceManager()
														.revealCurrentPlace();

												afterModuleLoad();
											}
										});


									}
								});
					}
				});

		ginjector.getAlertViewEventHandler();
	}

	/**
	 * Загрузить навигационные линки
	 */
	protected void loadNavigationLinks(final EventBus eventBus) {
		if (AppUtils.getApplicationMode() != ApplicationMode.POINT) {
			final AddNavigationLinkEvent event = new AddNavigationLinkEvent();

			if(AppUtils.isPermitted(PermissionScope.USER_MANAGER_ROLES)) {
				event.setPath(QoSNameTokens.userManager);
				event.setDisplayName(messages.navigationRoles());
				eventBus.fireEvent(event);
			}

			if(AppUtils.isPermitted(PermissionScope.USER_MANAGER_ROLES)) {
				event.setPath(QoSNameTokens.users);
				event.setDisplayName(messages.navigationUsers());
				eventBus.fireEvent(event);
			}

			if(AppUtils.isPermitted(PermissionScope.PROBE_CONFIG)) {
				event.setPath(QoSNameTokens.probesAndTasks);
				event.setDisplayName(messages.navigationProbesAndTasks());
				eventBus.fireEvent(event);
			}

			if(AppUtils.isPermitted(PermissionScope.PROBE_CONFIG)) {
				event.setPath(QoSNameTokens.remoteProbeConfig);
				event.setDisplayName(messages.navigationRemoteProbeConfig());
				eventBus.fireEvent(event);
			}
		}
	}

	private void logClientInfo() {
		LOGGER.log(Level.INFO, "User-Agent: " + Navigator.getUserAgent());
		LOGGER.log(Level.INFO, "Platform: " + Navigator.getPlatform());
		LOGGER.log(Level.INFO, "App name: " + Navigator.getAppName());
	}

	@Override
	public void onModuleLoad() {
		logClientInfo();
		GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {

			@Override
			public void onUncaughtException(final Throwable e) {
				final Throwable unwrapped = unwrap(e);
				LOGGER.log(Level.SEVERE, "Unexpected error", unwrapped);
			}

			public Throwable unwrap(final Throwable e) {
				if (e instanceof UmbrellaException) {
					final UmbrellaException ue = (UmbrellaException) e;
					if (ue.getCauses().size() == 1) {
						return unwrap(ue.getCauses().iterator().next());
					}
				}
				return e;
			}
		});
		Document.get().getBody()
				.removeClassName(ClientConstants.PRELOADING_CLASS_NAME);
	}
}
