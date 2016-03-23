/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.tecomgroup.qos.domain.MLiveStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.gwt.client.QoSMediaNameTokens;
import com.tecomgroup.qos.gwt.client.QoSNameTokens;
import com.tecomgroup.qos.gwt.client.i18n.MediaMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.MediaUserProfilePresenter.MyProxy;
import com.tecomgroup.qos.gwt.client.presenter.widget.UserSettingsWidgetPresenter;
/**
 * @author ivlev.e
 * 
 */
public class MediaUserProfilePresenter
		extends
			UserProfilePresenter<UserProfilePresenter.MyView, MyProxy>
		implements
			UiHandlers {

	@ProxyCodeSplit
	@NameToken(QoSNameTokens.userProfile)
	public static interface MyProxy
			extends
				ProxyPlace<MediaUserProfilePresenter> {
	}

	private final MediaMessages mediaMessages;
	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 * @param messages
	 */
	@Inject
	public MediaUserProfilePresenter(
			final EventBus eventBus,
			final MyView view,
			final MyProxy proxy,
			final TemplatesGridWidgetPresenter gridPresenter,
			final UserSettingsWidgetPresenter userSettingsWidgetPresenter,
			final ProbeEventsGridWidgetPresenter probeEventsGridWidgetPresenter,
			final MediaMessages mediaMessages,
			final QoSMessages messages,
			final ChangeUserPasswordWidgetPresenter changeUserPasswordWidgetPresenter) {
		super(eventBus, view, proxy, gridPresenter,
				userSettingsWidgetPresenter, probeEventsGridWidgetPresenter,
				messages, changeUserPasswordWidgetPresenter);
		this.mediaMessages = mediaMessages;

		getView().setUiHandlers(this);
	}

	@Override
	protected Map<String, String> getTemplateHrefMap() {
		final Map<String, String> templateHrefMap = super.getTemplateHrefMap();

		templateHrefMap.put(MRecordedStreamTemplate.class.getName(),
				QoSMediaNameTokens.recordedVideo);
		templateHrefMap.put(MLiveStreamTemplate.class.getName(),
				QoSMediaNameTokens.mediaPlayer);
		return templateHrefMap;
	}

	@Override
	protected Map<TemplateType, String> getTemplateLabels() {
		final Map<TemplateType, String> labels = super.getTemplateLabels();
		labels.put(MediaTemplateType.LIVE_VIDEO,
				mediaMessages.liveVideoTemplates());
		labels.put(MediaTemplateType.RECORDED_VIDEO,
				mediaMessages.recordedVideoTemplates());

		return labels;
	}

	@Override
	protected List<TemplateType> getTemplateTypes() {
		final List<TemplateType> types = super.getTemplateTypes();
		types.add(MediaTemplateType.LIVE_VIDEO);
		types.add(MediaTemplateType.RECORDED_VIDEO);

		return types;
	}

	@Override
	protected Map<String, String> getEventHrefMap() {
		final Map<String, String> eventHrefMap = super.getEventHrefMap();
		return eventHrefMap;
	}

	@Override
	protected Map<MProbeEvent.EventType, String> getEventLabels() {
		final Map<MProbeEvent.EventType, String> labels = super.getEventLabels();
		return labels;
	}

	@Override
	protected List<MProbeEvent.EventType> getEventTypes() {
		final List<MProbeEvent.EventType> types = super.getEventTypes();
		return types;
	}

}
