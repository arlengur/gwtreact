/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.event.shared.GwtEvent.Type;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.annotations.ContentSlot;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.NoGatekeeper;
import com.gwtplatform.mvp.client.annotations.ProxyEvent;
import com.gwtplatform.mvp.client.annotations.ProxyStandard;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallFailHandler;
import com.gwtplatform.mvp.client.proxy.AsyncCallStartEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallStartHandler;
import com.gwtplatform.mvp.client.proxy.AsyncCallSucceedEvent;
import com.gwtplatform.mvp.client.proxy.AsyncCallSucceedHandler;
import com.gwtplatform.mvp.client.proxy.NavigationEvent;
import com.gwtplatform.mvp.client.proxy.NavigationHandler;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentHandler;
import com.gwtplatform.mvp.client.proxy.RevealRootContentEvent;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.request.AgentActionStatus;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.PermissionScope;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.event.*;
import com.tecomgroup.qos.gwt.client.QoSIcons;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent;
import com.tecomgroup.qos.gwt.client.event.AddNavigationLinkEvent.AddNavigationLinkHandler;
import com.tecomgroup.qos.gwt.client.event.AudibleAlertModeChangeEvent;
import com.tecomgroup.qos.gwt.client.event.AudibleAlertModeChangeEvent.AudibleAlertModeChangeHandler;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.ChangeLocaleEvent;
import com.tecomgroup.qos.gwt.client.event.ChangeLocaleEvent.ChangeLocaleHandler;
import com.tecomgroup.qos.gwt.client.event.ClearContentOnMainPageEvent;
import com.tecomgroup.qos.gwt.client.event.ClearContentOnMainPageEvent.ClearContentInMainPagePresenterEventHandler;
import com.tecomgroup.qos.gwt.client.event.CurrentUserChangedEvent;
import com.tecomgroup.qos.gwt.client.event.CurrentUserChangedEvent.CurrentUserChangedEventHandler;
import com.tecomgroup.qos.gwt.client.event.RevealContentInMainPageEvent;
import com.tecomgroup.qos.gwt.client.event.RevealContentInMainPageEvent.RevealContentInMainPagePresenterEventHandler;
import com.tecomgroup.qos.gwt.client.event.RevealPlaceEvent;
import com.tecomgroup.qos.gwt.client.event.RevealPlaceEvent.RevealPlaceEventHandler;
import com.tecomgroup.qos.gwt.client.event.ShowMessageEvent;
import com.tecomgroup.qos.gwt.client.event.ShowMessageEvent.MessageType;
import com.tecomgroup.qos.gwt.client.event.ShowMessageEvent.ShowMessageEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.FlickeringEvent;
import com.tecomgroup.qos.gwt.client.event.alert.FlickeringEvent.FlickeringEventHandler;
import com.tecomgroup.qos.gwt.client.event.alert.StopAudibleAlertEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.sound.AudibleAlert;
import com.tecomgroup.qos.gwt.client.sound.AudibleAlertPlayer;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.shared.event.QoSEventService;
import com.tecomgroup.qos.gwt.shared.event.filter.ActivateAlertEventFilter;
import com.tecomgroup.qos.gwt.shared.event.filter.AgentActionStatusEventFilter;
import com.tecomgroup.qos.service.AgentServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * @author abondin
 * 
 */
public class MainPagePresenter
		extends
			Presenter<MainPagePresenter.MyView, MainPagePresenter.MyProxy>
		implements
			AddNavigationLinkHandler,
			AsyncCallStartHandler,
			AsyncCallFailHandler,
			AsyncCallSucceedHandler,
			ChangeLocaleHandler,
			UiHandlers,
			NavigationHandler,
			CurrentUserChangedEventHandler,
			ShowMessageEventHandler,
			FlickeringEventHandler,
			AudibleAlertModeChangeHandler,
			QoSEventListener,
			RevealPlaceEventHandler,
			RevealContentInMainPagePresenterEventHandler,
			ClearContentInMainPagePresenterEventHandler {

	@ProxyStandard
	@NameToken(QoSNameTokens.main)
	@NoGatekeeper
	public static interface MyProxy extends ProxyPlace<MainPagePresenter> {

	}

	public static interface MyView
			extends
				View,
				HasUiHandlers<MainPagePresenter> {

		/**
		 * 
		 * @param path
		 * @param displayName
		 * @param icon
		 * @param index
		 */
		void addNavigationLink(String path, String displayName, QoSIcons icon,
				Integer index, Integer minScreeenWidth);

		/**
		 * Обновить view если Блок Контроля сменился
		 */
		void agentChanged(String agentName);

		/**
		 * Добавить html теги audio к DOM-структуре
		 * 
		 * @param audibleAlerts
		 */
		void attachAudibleAlerts(Collection<AudibleAlert> audibleAlerts);

		/**
		 * 
		 * @return
		 */
		Set<String> getNavigationPaths();

		/**
		 * 
		 * @param navigationToken
		 */
		void load(String navigationToken);

		/**
		 * 
		 * @param content
		 */
		void setSystemInformation(final Widget content);

		/**
		 * @param title
		 */
		void setTitle(String title);

		/**
		 * @param message
		 * @param error
		 */
		void showErrorDialog(String message, Throwable error);

		/**
		 * @param message
		 */
		void showInfoDialog(String message);

		/**
		 * @param message
		 */
		void showInfoWithConfirmDialog(String message);

		/**
		 * @param loading
		 * @param text
		 */
		void showLoading(boolean loading, String text);

		/**
		 * Начать/остановить визуальное оповещение об аварии
		 * 
		 * @param severity
		 *            - в случае null визуальное оповещение останавливается
		 */
		void startStopFlickering(PerceivedSeverity severity);

		void userLoggedIn(MUser user);

		void userLoggedOut();

	}

	public static Logger LOGGER = Logger.getLogger(AudibleAlertPlayer.class
			.getName());

	private final PlaceManager placeManager;

	private final QoSEventService eventService;

	private final AudibleAlertPlayer audioPlayer;

	/**
	 * Основная панель
	 */
	@ContentSlot
	public static final Type<RevealContentHandler<?>> TYPE_SetMainContent = new Type<RevealContentHandler<?>>();

	@ContentSlot
	public static final Type<RevealContentHandler<?>> TYPE_SetBottomContent = new Type<RevealContentHandler<?>>();

	private final SystemInformationPresenter systemInformationPresenter;

	private final AgentServiceAsync agentService;

	private final UserServiceAsync userService;

	protected final QoSMessages messages;

	/**
	 * Время жизни cookie для выбранного агента
	 */
	public static final Long AGENT_COOKIE_LIFETIME = 1000l * 60l * 60l * 24l
			* 7l;

	private static final Long MILLISECONDS_PER_YEAR = TimeConstants.MILLISECONDS_PER_DAY * 365;

	@Inject
	public MainPagePresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final PlaceManager placeManager,
			final SystemInformationPresenter systemInformationPresenter,
			final AgentServiceAsync agentService,
			final QoSEventService eventService,
			final AudibleAlertPlayer audioPlayer,
			final UserServiceAsync userService) {
		super(eventBus, view, proxy);
		this.placeManager = placeManager;
		this.systemInformationPresenter = systemInformationPresenter;
		this.agentService = agentService;
		this.eventService = eventService;
		this.audioPlayer = audioPlayer;
		this.userService = userService;
		messages = AppUtils.getMessages();
		view.setUiHandlers(this);
	}

	public void actionLogout() {
		getEventBus().fireEvent(new BeforeLogoutEvent());
	}

	public void actionNavigate(final String path) {
		placeManager.revealPlace(new PlaceRequest(path), true);
	}

	public boolean audibleAlertIsActive() {
		return audioPlayer.getLastSeverity() != null;
	}

	@ProxyEvent
	@Override
	public void onAsyncCallFail(final AsyncCallFailEvent asyncCallFailEvent) {
		getView().showLoading(false, null);
	}

	@ProxyEvent
	@Override
	public void onAsyncCallStart(final AsyncCallStartEvent event) {
		getView().showLoading(true, null);
	}

	@ProxyEvent
	@Override
	public void onAsyncCallSucceed(
			final AsyncCallSucceedEvent asyncCallSucceedEvent) {
		getView().showLoading(false, null);
	}

	@Override
	public void onAudibleAlertModeChange(final AudibleAlertModeChangeEvent event) {
		final PerceivedSeverity previousSeverity = audioPlayer
				.getLastSeverity();
		// stop any possible ongoing alert notification
		getEventBus().fireEvent(new StopAudibleAlertEvent());
		getView().startStopFlickering(null);

		// if player was handling some alert, re-handle it with
		// new settings
		if (previousSeverity != null) {
			audioPlayer.handleAlertEvent(previousSeverity);
		}

		updateAudibleAlertSubscription(event.getMode());
	}

	@Override
	protected void onBind() {
		super.onBind();

		getEventBus().addHandler(AddNavigationLinkEvent.TYPE, this);
		getEventBus().addHandler(ChangeLocaleEvent.TYPE, this);
		getEventBus().addHandler(CurrentUserChangedEvent.TYPE, this);
		getEventBus().addHandler(NavigationEvent.getType(), this);
		getEventBus().addHandler(FlickeringEvent.TYPE, this);
		getEventBus().addHandler(ShowMessageEvent.TYPE, this);
		getEventBus().addHandler(AudibleAlertModeChangeEvent.TYPE, this);
		getEventBus().addHandler(RevealPlaceEvent.TYPE, this);
		getEventBus().addHandler(RevealContentInMainPageEvent.TYPE, this);
		getEventBus().addHandler(ClearContentOnMainPageEvent.TYPE, this);

		getView().setSystemInformation(
				systemInformationPresenter.getView().asWidget());
	}

	@Override
	public void onClearContentOnMainPage(final ClearContentOnMainPageEvent event) {
		clearSlot(event.getSlot());
	}

	@Override
	public void onEvent(final AddNavigationLinkEvent event) {
		String path = event.getPath();
		if (path != null) {
			path = path.trim();
			if (!getView().getNavigationPaths().contains(path)) {
				String displayName = event.getDisplayName();
				if (displayName == null) {
					displayName = path;
				}
				getView().addNavigationLink(path, displayName.trim(),
						event.getIcon(), event.getMenuIndex(),
						event.getMinScreenWidth());
			}
		}
	}

	@Override
	public void onEvent(final ChangeLocaleEvent event) {
		final String cookieName = LocaleInfo.getLocaleCookieName();
		final String queryParam = LocaleInfo.getLocaleQueryParam();

		if (cookieName != null) {
			// expires in one year
			final Date expires = new Date();
			expires.setTime(expires.getTime() + MILLISECONDS_PER_YEAR);
			Cookies.setCookie(cookieName, event.getLocale(), expires);
		}
		if (queryParam != null) {
			final UrlBuilder builder = Window.Location.createUrlBuilder()
					.setParameter(queryParam, event.getLocale());
			Window.Location.replace(builder.buildString());
		} else {
			// If we are using only cookies
			Window.Location.reload();
		}
	}

	@Override
	public void onEvent(final CurrentUserChangedEvent event) {
		final MUser user = event.getUser();
		if (user != null) {
			eventService
					.subscribe(
							ActivateAlertEvent.class,
							this,
							new ActivateAlertEventFilter(AppUtils
									.getAudibleAlertMode()));

			eventService
					.subscribe(
							AgentActionStatusEvent.class,
							this,
							new AgentActionStatusEventFilter(user.getLogin()));

			getView().userLoggedIn(event.getUser());
		} else {
			eventService.unsubscribe(ActivateAlertEvent.class, this);
			eventService.unsubscribe(AgentActionStatusEvent.class, this);
			getView().userLoggedOut();
		}
	}

	@Override
	public void onFlickerEventReceived(final FlickeringEvent event) {
		getView().startStopFlickering(event.getSeverity());
	}

	@Override
	public void onMessageEvent(final ShowMessageEvent event) {
		if (event.getMessageType() == MessageType.ERROR) {
			getView().showErrorDialog(event.getMessage(), event.getException());
		} else if (event.getMessageType() == MessageType.INFO_WITH_CONFIRM) {
			getView().showInfoWithConfirmDialog(event.getMessage());
		} else {
			getView().showInfoDialog(event.getMessage());
		}
	}

	@Override
	public void onNavigation(final NavigationEvent navigationEvent) {
		getView().load(navigationEvent.getRequest().getNameToken());
	}

	@Override
	protected void onReveal() {
		super.onReveal();
	}

	@Override
	public void onRevealInMainPage(final RevealContentInMainPageEvent event) {
		setInSlot(event.getSlot(), event.getContent());
	}

	@Override
	public void onRevealPlaceRequested(final RevealPlaceEvent event) {
		placeManager.revealPlace(AppUtils.createPlaceRequest(event
				.getPlaceToken()));
	}

	@Override
	public void onServerEvent(final AbstractEvent event) {
		if(event instanceof ActivateAlertEvent){
			userService.getCurrentUser(new AsyncCallback<MUser>() {
				@Override
				public void onFailure(Throwable caught) {
					LOGGER.log(
							Level.WARNING,
							"Unable to get current user to filter ActivateAlertEvent by agent",
							caught);
				}

				@Override
				public void onSuccess(MUser user) {
					if(user.isPermitted(PermissionScope.ALERTS)) {
						final String agentKey = ((ActivateAlertEvent) event).getAgentKey();
						agentService.doesAgentPermitted(
								agentKey,
								new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {
										LOGGER.log(
												Level.WARNING,
												"Unable to filter ActivateAlertEvent by agent",
												caught);
									}

									@Override
									public void onSuccess(Boolean result) {
										if (result) {
											audioPlayer
													.handleAlertEvent(((ActivateAlertEvent) event).getSeverity());
										}
									}
								});
					}
				}
			});

		} else if(event instanceof AgentActionStatusEvent) {
			AgentActionStatusEvent statusEvent = (AgentActionStatusEvent) event;
			MProbeEvent agentEvent = statusEvent.getEvent();
				agentService.doesAgentPermitted(
						agentEvent.getAgentKey(),
								new AsyncCallback<Boolean>() {

									@Override
									public void onFailure(Throwable caught) {
										LOGGER.log(
												Level.WARNING,
												"Unable to filter AgentActionStatusEvent by agent",
												caught);
									}

									@Override
									public void onSuccess(Boolean result) {
										AppUtils.showInfoMessage(messages.downloadVideoWarning());
									}

								});
				}
	}

	@Override
	protected void revealInParent() {
		RevealRootContentEvent.fire(MainPagePresenter.this,
				MainPagePresenter.this);
		getView().attachAudibleAlerts(audioPlayer.getAudibleAlerts().values());
		getView().load(placeManager.getCurrentPlaceRequest().getNameToken());
	}

	/**
	 * Показать/скрыть индикатор загрузки
	 * 
	 * @param loading
	 * @param text
	 */
	public void showLoading(final boolean loading, final String text) {
		getView().showLoading(loading, text);
	}

	private void updateAudibleAlertSubscription(
			final AudibleAlertFeatureMode mode) {
		eventService.unsubscribe(ActivateAlertEvent.class, this,
				new AsyncCallback<Void>() {

					@Override
					public void onFailure(final Throwable caught) {
						LOGGER.log(
								Level.SEVERE,
								"Unable to unsubscribe from listening for ActivateAlertEvent",
								caught);
					}

					@Override
					public void onSuccess(final Void result) {
						eventService.subscribe(ActivateAlertEvent.class,
								MainPagePresenter.this,
								new ActivateAlertEventFilter(mode));
					}
				});
	}
}
