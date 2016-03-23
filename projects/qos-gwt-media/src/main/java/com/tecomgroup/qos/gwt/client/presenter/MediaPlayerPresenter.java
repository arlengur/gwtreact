/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;
import com.gwtplatform.mvp.client.Presenter;
import com.gwtplatform.mvp.client.UiHandlers;
import com.gwtplatform.mvp.client.proxy.PlaceRequest;
import com.gwtplatform.mvp.client.proxy.Proxy;
import com.gwtplatform.mvp.client.proxy.RevealContentEvent;
import com.tecomgroup.qos.Statefull;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MStreamTemplate;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent;
import com.tecomgroup.qos.gwt.client.event.BeforeLogoutEvent.BeforeLogoutEventHandler;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent.LoadTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent.SaveTemplateEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.view.MediaPlayerView;
import com.tecomgroup.qos.gwt.client.view.desktop.AbstractMediaPlayerView;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

/**
 * Презентер для проигрывания видео
 * 
 * @author abondin
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class MediaPlayerPresenter<V extends MediaPlayerView, Proxy_ extends Proxy<?>>
		extends
			Presenter<V, Proxy_>
		implements
			UiHandlers,
			LoadTemplateEventHandler,
			SaveTemplateEventHandler,
			BeforeLogoutEventHandler,
			Statefull {

	protected final MediaAgentServiceAsync mediaAgentService;

	protected final LoadTemplatePresenterWidget loadTemplatePresenter;

	protected final SaveTemplatePresenterWidget saveTemplatePresenter;

	protected final QoSMessages messages;

	protected String templateNameFromUrl;

	protected final UserServiceAsync userService;

	private final TemplateType templateType;

	protected HandlerRegistration gridGroupRemovedHandler;

	protected HandlerRegistration loadTemplateHandlerRegistration;

	protected HandlerRegistration saveTemplateHandlerRegistration;

	public static Logger LOGGER = Logger.getLogger(MediaPlayerPresenter.class
			.getName());

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 */
	public MediaPlayerPresenter(final EventBus eventBus, final V view,
			final Proxy_ proxy, final TemplateType templateType,
			final MediaAgentServiceAsync mediaAgentService,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final UserServiceAsync userService, final QoSMessages message) {
		super(eventBus, view, proxy);
		this.userService = userService;
		this.mediaAgentService = mediaAgentService;
		this.messages = message;
		this.loadTemplatePresenter = loadTemplatePresenter;
		this.saveTemplatePresenter = saveTemplatePresenter;
		this.templateType = templateType;
		getEventBus().addHandler(BeforeLogoutEvent.TYPE, this);
		view.setRemoveVideoToDashboardListener(new AbstractMediaPlayerView.RemoveVideoToDashboardListener() {
			@Override
			public void onRemoveVideoToDashboard() {
				setCurrentTemplate(null);
			}
		});
	}

	@Override
	public void clearState() {
		getView().clearStreams();
	}

	protected void loadAgentByName(final String agentName,
			final AsyncCallback<MAgent> callback) {
		mediaAgentService.getAgentByKey(agentName, callback);
	}

	private void loadSelectedTemplate(final String templateName) {
		userService.getTemplate(templateType, AppUtils.getCurrentUser()
				.getUser().getId(), templateName,
				new AutoNotifyingAsyncLogoutOnFailureCallback<MUserAbstractTemplate>() {

					@Override
					protected void success(final MUserAbstractTemplate template) {
						loadTemplate(new LoadTemplateEvent(template));
					}

				});
	}

	@Override
	public void loadState() {
		getView().setUpPlayers();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		if (event.getTemplate() instanceof MStreamTemplate) {
			final MStreamTemplate template = (MStreamTemplate) event
					.getTemplate();

			getView().clearStreams();

			for (final MStreamWrapper wrapper : template.getWrappers()) {
				if (wrapper.isValid()) {
					final String agentKey = wrapper.getAgent().getKey();
					mediaAgentService.doesAgentPermitted(
							agentKey,
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									LOGGER.log(
											Level.WARNING,
											"Unable to filter recording template by agent",
											caught);
									AppUtils.showInfoMessage(messages.templateLoadingFail());
								}

								@Override
								public void onSuccess(Boolean result) {
									if (result) {
										getView().addStream(
												new StreamClientWrapper<MStreamWrapper>(wrapper));
									} else {
										AppUtils.showInfoMessage(messages.templateLoadingFail());
									}
								}
							});
				}
			}
			if (template.getWrappers().size() == getView().getStreamWrappers().size()) {
				AppUtils.showInfoMessage(messages.templateLoadingSuccess());
			} else {
				AppUtils.showInfoMessage(messages.templateDataLoadingFail());
			}
			setCurrentTemplate(template.getName());
		}
	}

	@Override
	public void onBeforeLogout(final BeforeLogoutEvent event) {
		clearState();
	}

	@Override
	protected void onHide() {
		super.onHide();
		saveState();
		getView().closePlayers();
		gridGroupRemovedHandler.removeHandler();

		loadTemplateHandlerRegistration.removeHandler();
		saveTemplateHandlerRegistration.removeHandler();
	}

	@Override
	protected void onReset() {
		super.onReset();

		if (templateNameFromUrl == null) {
			loadState();
		}
	}

	public abstract void openAddVideoDialog();

	public void openLoadTemplateDialog() {
		loadTemplatePresenter.setTemplateType(templateType);

		addToPopupSlot(loadTemplatePresenter, false);
	}

	public void openSaveTemplateDialog() {
		saveTemplatePresenter.setTemplateType(templateType);

		addToPopupSlot(saveTemplatePresenter, false);
	}

	public void cleanupTemplate() {
		setCurrentTemplate(null);
	}

	@Override
	public void prepareFromRequest(final PlaceRequest request) {
		super.prepareFromRequest(request);

		templateNameFromUrl = request
				.getParameter(RequestParams.template, null);

        final String tasksListString = request.getParameter(
                RequestParams.tasks, null);
        final String startTimestampString = request.getParameter(
                RequestParams.startDate, null);
        final String endTimestampString = request.getParameter(
                RequestParams.endDate, null);

        // start and end timestamps can be null for live streams
        if (tasksListString != null) {
            final String[] taskIdStrings = tasksListString.split(",");
            final List<Long> ids = new ArrayList<Long>();

            for (String idString : taskIdStrings) {
				if(idString!=null & !idString.trim().isEmpty()) {
					ids.add(Long.parseLong(idString));
				}
            }

            TimeInterval interval = null;
            if (startTimestampString != null && endTimestampString != null) {
                final Date start = new Date(Long.parseLong(startTimestampString));
                final Date end = new Date(Long.parseLong(endTimestampString));
                interval = TimeInterval.get(start, end);
            }
            loadTaskStreams(ids, interval);
        }
	}

    protected abstract void loadTaskStreams(final List<Long> taskIds, final TimeInterval interval);

	@Override
	protected void revealInParent() {
		RevealContentEvent.fire(this, MainPagePresenter.TYPE_SetMainContent,
				this);
		getView().setBigTvButton();

		/* now presenter listens events from save and load dialog presenters */
		loadTemplateHandlerRegistration = getEventBus().addHandler(
				LoadTemplateEvent.TYPE, this);
		saveTemplateHandlerRegistration = getEventBus().addHandler(
				SaveTemplateEvent.TYPE, this);

		if (templateNameFromUrl != null) {
			loadSelectedTemplate(templateNameFromUrl);
		}
	}

	@Override
	public void saveState() {
		getView().savePlayerStates();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void saveTemplate(final SaveTemplateEvent event) {
		if (event.getTemplate() instanceof MStreamTemplate) {
			final MStreamTemplate template = (MStreamTemplate) event
					.getTemplate();
			final String templateName = template.getName();
			template.setWrappers(getView().getStreamWrappers());
			if (template.isValid()) {
				setCurrentTemplate(templateName);
			} else {
				saveTemplatePresenter.setTemplate(templateName);
			}
		}
	}

	protected void setCurrentTemplate(final String templateName) {
		setTemplateLabel(templateName);
		saveTemplatePresenter.setTemplate(templateName);
		loadTemplatePresenter.setTemplate(templateName);
	}

	private void setTemplateLabel(final String templateName) {
		getView().setTemplateLabel(templateName);
	}
}
