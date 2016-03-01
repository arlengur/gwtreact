/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.View;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.domain.MUserResultTemplate;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.UserSettingsWidgetPresenter;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author ivlev.e
 * 
 */
public abstract class UserProfilePresenter<V extends UserProfilePresenter.MyView, P extends ProxyPlace<? extends UserProfilePresenter<?, ?>>>
		extends
			Presenter<V, P> implements UiHandlers {

	public interface MyView
			extends
				View,
				HasUiHandlers<UserProfilePresenter<?, ?>> {
	}

	private final QoSMessages messages;

	private final TemplatesGridWidgetPresenter templatesGridWidgetPresenter;

	private final Map<String, PresenterWidget<?>> tabs = new LinkedHashMap<String, PresenterWidget<?>>();

	protected final Long currentUserId;

	private final ChangeUserPasswordWidgetPresenter changeUserPasswordWidgetPresenter;
	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 */
	@Inject
	public UserProfilePresenter(
			final EventBus eventBus,
			final V view,
			final P proxy,
			final TemplatesGridWidgetPresenter templatesGridPresenter,
			final UserSettingsWidgetPresenter userSettingsWidgetPresenter,
			final QoSMessages messages,
			final ChangeUserPasswordWidgetPresenter changeUserPasswordWidgetPresenter) {
		super(eventBus, view, proxy);
		this.messages = messages;
		this.templatesGridWidgetPresenter = templatesGridPresenter;
		this.currentUserId = AppUtils.getCurrentUser().getUser().getId();
		this.changeUserPasswordWidgetPresenter = changeUserPasswordWidgetPresenter;

		tabs.put(messages.userTemplates(), templatesGridPresenter);
		tabs.put(messages.userSettings(), userSettingsWidgetPresenter);

		getView().setUiHandlers(this);
	}

	/**
	 * @return the gridPresenter
	 */
	public TemplatesGridWidgetPresenter getGridPresenter() {
		return templatesGridWidgetPresenter;
	}

	protected Map<String, String> getTemplateHrefMap() {
		final Map<String, String> templateHrefMap = new HashMap<String, String>();

		templateHrefMap.put(MUserAlertsTemplate.class.getName(),
				QoSNameTokens.alerts);
		templateHrefMap.put(MUserResultTemplate.class.getName(),
				QoSNameTokens.chartResults);
		templateHrefMap.put(MUserReportsTemplate.class.getName(),
				QoSNameTokens.reports);
		return templateHrefMap;
	}

	protected Map<TemplateType, String> getTemplateLabels() {
		final Map<TemplateType, String> labels = new HashMap<TemplateType, String>();
		labels.put(BaseTemplateType.ALERT, messages.alertsTemplates());
		labels.put(BaseTemplateType.RESULT, messages.resultTemplates());
		labels.put(BaseTemplateType.REPORT, messages.reportTemplates());

		return labels;
	}

	protected List<TemplateType> getTemplateTypes() {
		final List<TemplateType> types = new ArrayList<TemplateType>();
		types.add(BaseTemplateType.ALERT);
		types.add(BaseTemplateType.RESULT);
		types.add(BaseTemplateType.REPORT);
		return types;
	}

	@Override
	protected void onBind() {
		super.onBind();
		templatesGridWidgetPresenter.setTemplateLabels(getTemplateLabels());
		templatesGridWidgetPresenter.setTemplateTypes(getTemplateTypes());
		templatesGridWidgetPresenter.setTemplateHrefs(getTemplateHrefMap());

		for (final Entry<String, PresenterWidget<?>> entry : tabs.entrySet()) {
			setInSlot(entry.getKey(), entry.getValue());
		}
	}

	public void openChangeUserPasswordDialog() {
		addToPopupSlot(changeUserPasswordWidgetPresenter, false);
	}

	@Override
	public void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
	}
}
