/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.*;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.gwt.client.QoSMediaNameTokens;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent.GridGroupRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetAddedEvent.DashboardWidgetAddedEventHandler;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.dashboard.DashboardWidgetRemovedEvent.DashboardWidgetRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.video.LiveVideoAddedEvent;
import com.tecomgroup.qos.gwt.client.event.video.LiveVideoAddedEvent.LiveVideoAddedEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.LiveVideoPresenter.MyProxy;
import com.tecomgroup.qos.gwt.client.presenter.LiveVideoPresenter.MyView;
import com.tecomgroup.qos.gwt.client.presenter.widget.AddWidgetToDashboardWidgetPresenter;
import com.tecomgroup.qos.gwt.client.secutiry.CurrentUser;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.MediaPlayerView;
import com.tecomgroup.qos.gwt.client.view.desktop.AbstractMediaPlayerView.AddVideoToDashboardListener;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

import java.util.List;

/**
 * @author novohatskiy.r
 *
 */
public class LiveVideoPresenter extends MediaPlayerPresenter<MyView, MyProxy>
		implements
			GridGroupRemovedEventHandler<StreamClientWrapper<MLiveStreamWrapper>>,
			LiveVideoAddedEventHandler,
			DashboardWidgetAddedEventHandler,
			DashboardWidgetRemovedEventHandler {

	@ProxyCodeSplit
	@NameToken(QoSMediaNameTokens.mediaPlayer)
	public static interface MyProxy extends ProxyPlace<LiveVideoPresenter> {

	}

	public static interface MyView
			extends
				MediaPlayerView<MLiveStreamWrapper>,
				HasUiHandlers<LiveVideoPresenter> {
		MLiveStream findStreamByUrl(String url);

		void markWidgetAsAddedToDashboard(String widgetKey);

		void unmarkWidgetAsAddedToDashboard(String widgetKey);
	}

	private final AddLiveVideoPresenter dialogPresenter;

	private final CurrentUser user;

	/**
	 * @param eventBus
	 * @param view
	 * @param proxy
	 * @param mediaAgentService
	 * @param dialogPresenter
	 */
	@Inject
	public LiveVideoPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final QoSMessages messages,
			final UserServiceAsync userService,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final MediaAgentServiceAsync mediaAgentService,
			final AddLiveVideoPresenter dialogPresenter,
			final AddWidgetToDashboardWidgetPresenter addWidgetToDashboardDialog) {
		super(eventBus, view, proxy, MediaTemplateType.LIVE_VIDEO,
				mediaAgentService, loadTemplatePresenter,
				saveTemplatePresenter, userService, messages);
		this.dialogPresenter = dialogPresenter;
		this.user = AppUtils.getCurrentUser();

		view.setUiHandlers(this);
		view.setAddVideoToDashboardListener(new AddVideoToDashboardListener() {
			@Override
			public void onAddVideoToDashboard(final DashboardWidget widget) {
				addWidgetToDashboardDialog.setDashboardWidget(widget);
				addToPopupSlot(addWidgetToDashboardDialog, false);
			}

		});
	}

	public void getDashboard(final AsyncCallback<MDashboard> callback) {
		userService.getDashboard(
				user.getUser().getLogin(),
				new AutoNotifyingAsyncCallback<MDashboard>(messages
						.loadDashbordFail(), true) {
					@Override
					protected void success(final MDashboard dashboard) {
						callback.onSuccess(dashboard);
					}
				});
	}

	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		super.loadTemplate(event);
		getView().applyButtonHandler(new SelectEvent());
	}

	@Override
	protected void onBind() {
		super.onBind();
		getEventBus().addHandler(LiveVideoAddedEvent.TYPE, this);
		getEventBus().addHandler(DashboardWidgetAddedEvent.TYPE, this);
		getEventBus().addHandler(DashboardWidgetRemovedEvent.TYPE, this);
	}

	@Override
	public void onDashboardWidgetAdded(final DashboardWidgetAddedEvent event) {
		getView().markWidgetAsAddedToDashboard(event.getWidget().getKey());
	}

	@Override
	public void onDashboardWidgetRemoved(final DashboardWidgetRemovedEvent event) {
		getView().unmarkWidgetAsAddedToDashboard(event.getWidgetKey());
	}

	@Override
	public void onGridGroupRemovedEvent(
			final GridGroupRemovedEvent<StreamClientWrapper<MLiveStreamWrapper>> event) {
		for (final StreamClientWrapper<MLiveStreamWrapper> item : event
				.getItems()) {
			getView().removeStream(item);
			setCurrentTemplate(null);
		}
	}

	@Override
	public void onLiveVideoAddedEvent(final LiveVideoAddedEvent event) {
		getView().addStreams(event.getStreams());
		setCurrentTemplate(null);
	}

	@Override
	public void openAddVideoDialog() {
		addToPopupSlot(dialogPresenter, false);
	}

    @Override
    protected void loadTaskStreams(List<Long> taskIds, TimeInterval interval) {
        mediaAgentService.getTasksLiveStreams(taskIds, new AutoNotifyingAsyncCallback<List<MLiveStreamWrapper>>() {
            @Override
            protected void success(List<MLiveStreamWrapper> result) {
                getView().clearStreams();

                for (MLiveStreamWrapper wrapper : result){
                    getView().addStream(new StreamClientWrapper<MLiveStreamWrapper>(wrapper));
                }
                getView().setUpPlayers();
            }
        });
    }

    @Override
	protected void revealInParent() {
		super.revealInParent();
		gridGroupRemovedHandler = getEventBus().addHandler(
				GridGroupRemovedEvent.TYPE, this);
	}
}
