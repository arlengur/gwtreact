/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import java.text.ParseException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.Window;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.proxy.ParameterTokenFormatter;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.sencha.gxt.data.shared.loader.FilterConfig;
import com.sencha.gxt.data.shared.loader.FilterPagingLoadConfig;
import com.sencha.gxt.widget.core.client.grid.filters.GridFilters;
import com.tecomgroup.qos.criterion.BinaryCompositeCriterion;
import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.criterion.CriterionWithParameter;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.domain.UserSettings;
import com.tecomgroup.qos.domain.UserSettings.AudibleAlertFeatureMode;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.event.ShowMessageEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.secutiry.AdminGatekeeper;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.client.sound.SoundConstants;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.CriterionFilterConverter;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.filter.EnumMapper;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author ivlev.e
 * 
 */
public class AppUtils {
	public static enum ApplicationMode {
		VISION, POINT;
	}

	private static Logger LOGGER = Logger.getLogger(AppUtils.class.getName());

	private static final String UNDEFINED_PROPERTY_VALUE = "undefined";

	private static final String APPLICATION_MODE_PROPERTY_NAME = "client.application.mode";

	private static final String DEMO_MODE_PROPERTY_NAME = "client.application.demo";

	private static final String HIDE_CHANNEL_VIEW_PAGE = "client.hide.channelview.page";

	private static final String HIDE_LIVEVIDEO_PAGE = "client.hide.livevideo.page";

	private static final String HIDE_RECORDEDVIDEO_PAGE = "client.hide.recordedvideo.page";

	private static boolean hideChannelViewPage = false;

	private static boolean hideLiveVideoPage = false;

	private static boolean hideRecordedVideoPage = false;

	private static ApplicationMode applicationMode = ApplicationMode.VISION;

	private static boolean demoMode = false;

	private final static ParameterTokenFormatter PARAMETER_TOKEN_FORMATTER = new ParameterTokenFormatter();

	private static EventBus eventBus;

	private static Map<String, Object> clientProperties;

	private static CurrentUser currentUser;

	private static QoSMessages messages;

	private static AudibleAlertFeatureMode DEAFULT_AUDIBLE_ALERT_FEATURE_MODE;

	private static PlaceManager placeManager;

	public static <M> void applyCriterionToFilters(
			final FilterPagingLoadConfig pagingConfig,
			final GridFilters<M> filters, final Criterion criterion) {
		if (criterion instanceof BinaryCompositeCriterion) {
			applyCriterionToFilters(pagingConfig, filters,
					((BinaryCompositeCriterion) criterion).getLeft());
			applyCriterionToFilters(pagingConfig, filters,
					((BinaryCompositeCriterion) criterion).getRight());
		} else if (criterion instanceof CriterionWithParameter) {
			CriterionFilterConverter.applyBinaryCriterionToFilter(pagingConfig,
					filters, (CriterionWithParameter) criterion);
		}
	}

	public static String buildBaseUrl() {
		final UrlBuilder url = new UrlBuilder();
		url.setProtocol(Window.Location.getProtocol());
		url.setHost(Window.Location.getHost());
		url.setPath(Window.Location.getPath()
				+ Window.Location.getQueryString());
		return url.buildString();
	}

	public static String buildUrl(final String servletName,
			final Map<String, String> urlParameters) {
		final UrlBuilder url = new UrlBuilder();
		url.setProtocol(Window.Location.getProtocol());

		url.setHost(Window.Location.getHost());

		final String moduleBaseURL = GWT.getModuleBaseURL();
		final int substringStartIndex = Window.Location.getProtocol().length()
				+ Window.Location.getHost().length() + 2;
		String path = moduleBaseURL.substring(substringStartIndex,
				moduleBaseURL.length());
		if (!path.endsWith("/") && !servletName.startsWith("/")) {
			path += "/";
		}

		url.setPath(path + servletName);
		for (final Map.Entry<String, String> urlParameterEntry : urlParameters
				.entrySet()) {
			url.setParameter(urlParameterEntry.getKey(),
					urlParameterEntry.getValue());
		}
		return url.buildString();
	}

	public static <M> Criterion convertFiltersToCriterion(
			final FilterPagingLoadConfig pagingConfig, final EnumMapper mapper,
			final GridFilters<M> filters) {
		Criterion leftCriterion = null;
		for (final FilterConfig config : pagingConfig.getFilters()) {
			Criterion rightCriterion = CriterionFilterConverter
					.convertToCriterion(config, mapper, filters);
			if (leftCriterion != null) {
				rightCriterion = CriterionQueryFactory.getQuery().and(
						leftCriterion, rightCriterion);
			}
			leftCriterion = rightCriterion;
		}
		return leftCriterion;
	}

	/**
	 * Create safe internal href string for HTML hyperlink.
	 * 
	 * @param placeRequest
	 * @return
	 */
	public static String createHref(final PlaceRequest placeRequest) {
		return UriUtils.encode("#"
				+ PARAMETER_TOKEN_FORMATTER.toPlaceToken(placeRequest));
	}

	public static PlaceRequest createPlaceRequest(String placeToken) {
		if (placeToken.startsWith("#")) {
			placeToken = placeToken.substring(1);
		}
		return PARAMETER_TOKEN_FORMATTER.toPlaceRequest(placeToken);
	}

	public static native NodeList<Element> findElementsByClassName(
			String className) /*-{
		return $wnd.$("." + className);
	}-*/;

	/**
	 * @return the applicationMode
	 */
	public static ApplicationMode getApplicationMode() {
		return applicationMode;
	}

	public static AudibleAlertFeatureMode getAudibleAlertMode() {
		final AudibleAlertFeatureMode resultAudibleAlertMode;
		final MUser user = currentUser.getUser();
		final UserSettings userSettings = user == null ? null : user
				.getSettings();

		if (userSettings != null && userSettings.getAudibleAlertMode() != null) {
			resultAudibleAlertMode = userSettings.getAudibleAlertMode();
		} else {
			resultAudibleAlertMode = getDefaultAudbileAlertFeatureMode();
		}
		return resultAudibleAlertMode;

	}

	/**
	 * @return the clientProperties
	 */
	public static Map<String, Object> getClientProperties() {
		return clientProperties;
	}

	/**
	 * @return the currentUser
	 */
	public static CurrentUser getCurrentUser() {
		return currentUser;
	}

	private static AudibleAlertFeatureMode getDefaultAudbileAlertFeatureMode() {
		if (DEAFULT_AUDIBLE_ALERT_FEATURE_MODE == null) {
			DEAFULT_AUDIBLE_ALERT_FEATURE_MODE = AudibleAlertFeatureMode
					.valueOf(((String) clientProperties
							.get(SoundConstants.FEATURE_MODE)).toUpperCase());
		}
		return DEAFULT_AUDIBLE_ALERT_FEATURE_MODE;
	}

	public static String getDefaultNavigationLink() {
		if (AppUtils.getApplicationMode() == ApplicationMode.POINT) {
			return QoSNameTokens.chartResults;
		} else {
			return QoSNameTokens.dashboard;
		}
	}

	public static EventBus getEventBus() {
		return eventBus;
	}

	public static QoSMessages getMessages() {
		return messages;
	}

	public static UserSettings.NotificationLanguage getNotificationLanguage() {
		UserSettings.NotificationLanguage result = null;
		final MUser user = currentUser.getUser();
		final UserSettings userSettings = user == null ? null : user
				.getSettings();

		if (userSettings != null
				&& userSettings.getNotificationLanguage() != null) {
			result = userSettings.getNotificationLanguage();
		}

		return result;
	}

	public static PlaceManager getPlaceManager() {
		return placeManager;
	}

	private static void handlePropertyParseError(final Throwable cause,
			final String propertyName, final String propertyValue) {
		final String errorMessage = "Parsing of " + propertyName
				+ " property has failed. It has an invalid value \""
				+ propertyValue + "\"";
		LOGGER.log(Level.SEVERE, errorMessage, cause);
	}

	/**
	 * Initializes transparent flash button over buttonId element and source
	 * element from which data will be copied to clipboard
	 * 
	 * @param buttonId
	 * @param sourceElementId
	 */
	public static native void initClipboard(String buttonId,
			String sourceElementId, String message) /*-{
		var button = "#" + buttonId;
		if (!document.getElementById(sourceElementId)) {
			$wnd.$('<pre id=' + sourceElementId + '></pre>').hide().appendTo(
					button);
			var clip = new $wnd.ZeroClipboard($wnd.$(button));
			clip.on('complete', function(client, args) {
				$wnd.printMessage(message);
			});
		}
	}-*/;

	/**
	 * 
	 * @return true is admin is logged in
	 */
	public static boolean isAdminLoggedIn() {
		return AdminGatekeeper.isAdmin(currentUser.getUser());
	}

	public static boolean isPermitted(UISubject page) {
		return isPermitted(page, currentUser.getUser());
	}

	private static boolean isPermitted(UISubject page, MUser user) {
		for(MRole role: user.getRoles()) {
			if(role.isPermitted(page)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isDemoMode() {
		return demoMode;
	}

	public static native void logToConsole(String text) /*-{
		console.log(text);
	}-*/;

	/**
	 * @param currentUser
	 *            the currentUser to set
	 */
	public static void registerCurrentUser(final CurrentUser currentUser) {
		AppUtils.currentUser = currentUser;
	}

	/**
	 * Removes provided parameters from current history URL and replace it.
	 * 
	 * @param parametersToRemove
	 *            a collection of parameters to remove from history URL.
	 */
	public static void removeParametersFromHistoryUrl(
			final Collection<String> parametersToRemove) {
		final PlaceRequest currentPlaceRequest = placeManager
				.getCurrentPlaceRequest();
		if (SimpleUtils.isNotNullAndNotEmpty(parametersToRemove) && QoSNameTokens.chartResults.equals(currentPlaceRequest.getNameToken())) {

			final Map<String, String> newParameters = new HashMap<String, String>();
			for (final String currentParameter : currentPlaceRequest
					.getParameterNames()) {
				if (!parametersToRemove.contains(currentParameter)) {
					newParameters.put(currentParameter, currentPlaceRequest
							.getParameter(currentParameter, null));
				}
			}
			replaceHistoryUrl(QoSNameTokens.chartResults, newParameters);
		}
	}

	/**
	 * Replaces history url with provided parameters.
	 * 
	 * @param nameToken
	 * @param parameters
	 */
	public static void replaceHistoryUrl(final String nameToken,
			final Map<String, String> parameters) {
		PlaceRequest.Builder builder = new PlaceRequest.Builder();
		builder = builder.nameToken(nameToken);

		if (parameters != null) {
			for (final Map.Entry<String, String> parameter : parameters
					.entrySet()) {
				builder = builder
						.with(parameter.getKey(), parameter.getValue());
			}
		}
		placeManager.updateHistory(builder.build(), true);
	}

	/**
	 * Checks if screen width is large enough to display full version of web
	 * page
	 * 
	 * @return
	 */
	public static boolean screenWidthIsEnough() {
		return Window.getClientWidth() > ClientConstants.MOBILE_SCREEN_WIDTH;
	}

	private static void setApplicationMode(
			final Map<String, Object> clientProperties) {
		if (clientProperties.containsKey(APPLICATION_MODE_PROPERTY_NAME)) {
			String propertyValue = UNDEFINED_PROPERTY_VALUE;

			try {
				propertyValue = ((String) clientProperties
						.get(APPLICATION_MODE_PROPERTY_NAME)).trim();

				applicationMode = ApplicationMode.valueOf(propertyValue
						.toUpperCase());
			} catch (final Exception e) {
				handlePropertyParseError(e, APPLICATION_MODE_PROPERTY_NAME,
						propertyValue);
			}
		}
	}

	public static boolean isShowChannelViewPage() {
		return !hideChannelViewPage;
	}

	public static boolean isShowLiveVideoPage() {
		return !hideLiveVideoPage;
	}

	public static boolean isShowRecordedVideoPage() {
		return !hideRecordedVideoPage;
	}

	private static void setVideoMode(
			final Map<String, Object> clientProperties) {
		if(clientProperties.containsKey(HIDE_CHANNEL_VIEW_PAGE)) {
			String value = ((String) clientProperties
					.get(HIDE_CHANNEL_VIEW_PAGE)).trim();

			hideChannelViewPage = Boolean.valueOf(value);
		}

		if(clientProperties.containsKey(HIDE_LIVEVIDEO_PAGE)) {
			String value = ((String) clientProperties
					.get(HIDE_LIVEVIDEO_PAGE)).trim();
			hideLiveVideoPage = Boolean.valueOf(value);
		}

		if(clientProperties.containsKey(HIDE_RECORDEDVIDEO_PAGE)) {
			String value = ((String) clientProperties
					.get(HIDE_RECORDEDVIDEO_PAGE)).trim();
			hideRecordedVideoPage = Boolean.valueOf(value);
		}
	}

	public static void setClientProperties(
			final Map<String, Object> clientProperties) {
		AppUtils.clientProperties = Collections
				.unmodifiableMap(clientProperties);
		setApplicationMode(clientProperties);
		setDemoMode(clientProperties);
		setVideoMode(clientProperties);
	}

	private static void setDemoMode(final Map<String, Object> clientProperties) {
		if (clientProperties.containsKey(DEMO_MODE_PROPERTY_NAME)) {
			String propertyValue = UNDEFINED_PROPERTY_VALUE;

			try {
				propertyValue = ((String) clientProperties
						.get(DEMO_MODE_PROPERTY_NAME)).trim();

				if (!("true".equals(propertyValue.toLowerCase()))
						&& !("false".equals(propertyValue.toLowerCase()))) {
					throw new ParseException("Value of "
							+ DEMO_MODE_PROPERTY_NAME
							+ " must be true or false", 0);
				}

				demoMode = Boolean.parseBoolean(propertyValue);
			} catch (final Exception e) {
				handlePropertyParseError(e, DEMO_MODE_PROPERTY_NAME,
						propertyValue);
			}
		}
	}

	public static native void setElementValue(String elementId, String value) /*-{
		$wnd.$("#" + elementId).val(value);
	}-*/;

	public static void setEventBus(final EventBus eventBus) {
		AppUtils.eventBus = eventBus;
	}

	public static void setMessages(final QoSMessages messages) {
		AppUtils.messages = messages;
	}

	public static void setPlaceManager(final PlaceManager placeManager) {
		AppUtils.placeManager = placeManager;
	}

	public static void showErrorMessage(final String errorMessage) {
		getEventBus().fireEvent(ShowMessageEvent.error(errorMessage));
	}

	public static void showErrorMessage(final String errorMessage,
			final Throwable ex) {
		getEventBus().fireEvent(ShowMessageEvent.error(errorMessage, ex));
	}

	public static void showInfoMessage(final String message) {
		getEventBus().fireEvent(ShowMessageEvent.info(message));
	}

	public static void showInfoWithConfirmMessage(final String message) {
		getEventBus().fireEvent(ShowMessageEvent.infoWithConfirm(message));
	}
}
