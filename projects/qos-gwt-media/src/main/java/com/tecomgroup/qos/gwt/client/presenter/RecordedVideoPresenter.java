/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.annotations.NameToken;
import com.gwtplatform.mvp.client.annotations.ProxyCodeSplit;
import com.gwtplatform.mvp.client.proxy.ProxyPlace;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.gwt.client.QoSMediaNameTokens;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent;
import com.tecomgroup.qos.gwt.client.event.GridGroupRemovedEvent.GridGroupRemovedEventHandler;
import com.tecomgroup.qos.gwt.client.event.LoadTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.SaveTemplateEvent;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent.DownloadVideoEventHandler;
import com.tecomgroup.qos.gwt.client.event.video.RecordedVideoAddedEvent;
import com.tecomgroup.qos.gwt.client.event.video.RecordedVideoAddedEvent.RecordedVideoAddedEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.RecordedVideoPresenter.MyProxy;
import com.tecomgroup.qos.gwt.client.presenter.RecordedVideoPresenter.MyView;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.MediaPlayerView;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.UserServiceAsync;

import java.util.List;

/**
 * @author novohatskiy.r
 * 
 */
public class RecordedVideoPresenter
		extends
			MediaPlayerPresenter<MyView, MyProxy>
		implements
			GridGroupRemovedEventHandler<StreamClientWrapper<MRecordedStreamWrapper>>,
			RecordedVideoAddedEventHandler,
			DownloadVideoEventHandler {

	@ProxyCodeSplit
	@NameToken(QoSMediaNameTokens.recordedVideo)
	public static interface MyProxy extends ProxyPlace<RecordedVideoPresenter> {

	}

	public static interface MyView
			extends
				MediaPlayerView<MRecordedStreamWrapper>,
				HasUiHandlers<RecordedVideoPresenter> {
	}

	private final AddRecordedVideoPresenter addVideoDialogPresenter;

	private final ExportVideoPresenter exportVideoPresenter;

	@Inject
	public RecordedVideoPresenter(final EventBus eventBus, final MyView view,
			final MyProxy proxy, final QoSMessages messages,
			final UserServiceAsync userService,
			final MediaAgentServiceAsync mediaAgentService,
			final AddRecordedVideoPresenter addVideoDialogPresenter,
			final LoadTemplatePresenterWidget loadTemplatePresenter,
			final SaveTemplatePresenterWidget saveTemplatePresenter,
			final ExportVideoPresenter exportVideoPresenter) {
		super(eventBus, view, proxy, MediaTemplateType.RECORDED_VIDEO,
				mediaAgentService, loadTemplatePresenter,
				saveTemplatePresenter, userService, messages);
		this.addVideoDialogPresenter = addVideoDialogPresenter;
		this.exportVideoPresenter = exportVideoPresenter;

		view.setUiHandlers(this);
		addHandler(RecordedVideoAddedEvent.TYPE, this);
		addHandler(ExportVideoEvent.TYPE, this);
	}

	@Override
	public void loadTemplate(final LoadTemplateEvent event) {
		super.loadTemplate(event);
		getView().activateAgentTimeZone();
		final MRecordedStreamTemplate template = (MRecordedStreamTemplate) event.getTemplate();
		getView().updateSyncTimeInterval(template.getSyncTimeInterval());
		getView().applyButtonHandler(new SelectEvent());
	}

	@Override
    public void onExport(final ExportVideoEvent event) {
        if (isVisible()) {
			exportVideoPresenter.showDialog(event.getUrl(), event.getTaskKey(), event.getTaskDisplayName());
			addToPopupSlot(exportVideoPresenter, false);
        }
    }

	@Override
	public void onGridGroupRemovedEvent(final GridGroupRemovedEvent<StreamClientWrapper<MRecordedStreamWrapper>> event) {
		for (final StreamClientWrapper<MRecordedStreamWrapper> item : event.getItems()) {
			getView().removeStream(item);
			setCurrentTemplate(null);
		}
		getView().activateAgentTimeZone();
	}

	@Override
	public void onRecordedVideoAddedEvent(final RecordedVideoAddedEvent event) {
		getView().addStreams(event.getStreams());
		getView().activateAgentTimeZone();
		setCurrentTemplate(null);
	}

	@Override
	public void openAddVideoDialog() {
		addToPopupSlot(addVideoDialogPresenter, false);
	}

    @Override
    protected void loadTaskStreams(final List<Long> taskIds, final TimeInterval interval) {
        if (interval != null) {
            mediaAgentService.getTasksRecordedStreams(taskIds, new AutoNotifyingAsyncCallback<List<MRecordedStreamWrapper>>() {
                @Override
                protected void success(List<MRecordedStreamWrapper> result) {
                    getView().clearStreams();

                    for (MRecordedStreamWrapper wrapper : result) {
                        wrapper.setIntervalType(interval.getType());
                        wrapper.setStartDateTime(interval.getStartDateTime());
                        wrapper.setEndDateTime(interval.getEndDateTime());
                        getView().addStream(new StreamClientWrapper<MRecordedStreamWrapper>(wrapper));
                    }
                    getView().setUpPlayers();
                }
            });
        }
    }

    @Override
	protected void revealInParent() {
		super.revealInParent();
		gridGroupRemovedHandler = getEventBus().addHandler(
				GridGroupRemovedEvent.TYPE, this);
	}

	@Override
	public void saveTemplate(final SaveTemplateEvent event) {
		((MRecordedStreamTemplate) event.getTemplate())
				.setSyncTimeInterval(getView().getSyncTimeInterval());
		super.saveTemplate(event);
	}
}