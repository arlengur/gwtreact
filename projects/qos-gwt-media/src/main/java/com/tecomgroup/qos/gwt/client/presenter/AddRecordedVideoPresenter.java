/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.gwt.client.event.video.RecordedVideoAddedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.view.desktop.AddRecordedVideoView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentDialogPresenter;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;
import com.tecomgroup.qos.service.VideoResultServiceAsync;

/**
 * @author novohatskiy.r
 * 
 */
public class AddRecordedVideoPresenter extends AgentDialogPresenter {

	public static interface MyView extends AgentDialogView {

		void setSelectedAgent(MAgent agent);

		void setStreams(
				List<StreamClientWrapper<MRecordedStreamWrapper>> streams);
	}

	private final MediaAgentServiceAsync mediaAgentService;

	private final VideoResultServiceAsync videoResultService;

	private final MainPagePresenter mainPagePresenter;

	private int pendingAsyncLoadings;
	private final List<StreamClientWrapper<MRecordedStreamWrapper>> loadedStreams;

	@Inject
	public AddRecordedVideoPresenter(final EventBus eventBus,
			final MyView view, final QoSMessages messages,
			final MediaAgentServiceAsync mediaAgentService,
			final VideoResultServiceAsync videoResultService,
			final MainPagePresenter mainPagePresenter) {
		super(eventBus, view, messages, mediaAgentService);
		this.mediaAgentService = mediaAgentService;
		this.videoResultService = videoResultService;
		this.mainPagePresenter = mainPagePresenter;
		getView().setUiHandlers(this);
		pendingAsyncLoadings = 0;
		loadedStreams = new ArrayList<StreamClientWrapper<MRecordedStreamWrapper>>();
	}

	public void addStreams(
			final List<StreamClientWrapper<MRecordedStreamWrapper>> streams,
			final TimeInterval timeInterval) {
		pendingAsyncLoadings = streams.size();
		loadedStreams.clear();
		final boolean streamsAreEmpty = streams.isEmpty();
		if (!streamsAreEmpty) {
			mainPagePresenter.showLoading(true, null);
		}
		for (final StreamClientWrapper<MRecordedStreamWrapper> clientWrapper : streams) {
			final MRecordedStreamWrapper wrapper = clientWrapper.getWrapper();
			// redundant code. Its destiny will be decided in
			// http://rnd.tecom.nnov.ru/issues/2625

			// videoResultService.getResults(wrapper.getStream()
			// .getComplexStreamSource(), timeInterval, null, null,
			// new DefaultAsyncCallback<List<MVideoResult>>() {
			// @Override
			// public void onFailure(final Throwable caught) {
			// super.onFailure(caught);
			// AppUtils.showErrorMessage(
			// messages.streamLoadingFail(), caught);
			// afterVideoLoadingCallbackInvoked();
			// }
			// @Override
			// public void onSuccess(final List<MVideoResult> result) {
			// wrapper.setVideos(result);
			// wrapper.setStartDateTime(timeInterval
			// .getStartDateTime());
			// wrapper.setEndDateTime(timeInterval
			// .getEndDateTime());
			// wrapper.setVideoTimeZone(timeInterval
			// .getTimeZone());
			// loadedStreams.add(clientWrapper);
			// afterVideoLoadingCallbackInvoked();
			// }
			// });

			wrapper.setIntervalType(timeInterval.getType());
			wrapper.setStartDateTime(timeInterval.getStartDateTime());
			wrapper.setEndDateTime(timeInterval.getEndDateTime());
			wrapper.setVideoTimeZone(timeInterval.getTimeZone());
			loadedStreams.add(clientWrapper);
		}
		if (!streamsAreEmpty) {
			mainPagePresenter.showLoading(false, null);
			AppUtils.getEventBus().fireEvent(
					new RecordedVideoAddedEvent(loadedStreams));
		}
	}

	private void afterVideoLoadingCallbackInvoked() {
		pendingAsyncLoadings--;
		if (pendingAsyncLoadings == 0) {
			AppUtils.getEventBus().fireEvent(
					new RecordedVideoAddedEvent(loadedStreams));
			mainPagePresenter.showLoading(false, null);
		}
	}

	@Override
	public void agentSelected(final MAgent agent) {
		getView().<AddRecordedVideoView> cast().setSelectedAgent(agent);
		final AsyncCallback<List<MRecordedStream>> callback = new AutoNotifyingAsyncCallback<List<MRecordedStream>>() {

			@Override
			protected void failure(final Throwable caught) {
				AppUtils.showErrorMessage(messages.streamLoadingFail(), caught);
				LOGGER.log(Level.SEVERE, "Cannot recorded stream for agent",
						caught);
			}

			@Override
			protected void success(final List<MRecordedStream> recordedStreams) {
				getView().<MyView> cast().setStreams(
						wrapStreams(recordedStreams, agent));
			}
		};
		loadRecordedStreamsByAgent(agent.getName(), callback);
	}

	private void loadRecordedStreamsByAgent(final String agentName,
			final AsyncCallback<List<MRecordedStream>> callback) {
		mediaAgentService.getAgentRecordedStreams(agentName, callback);
	}

	protected List<StreamClientWrapper<MRecordedStreamWrapper>> wrapStreams(
			final List<MRecordedStream> streams, final MAgent agent) {
		final List<StreamClientWrapper<MRecordedStreamWrapper>> wrappers = new LinkedList<StreamClientWrapper<MRecordedStreamWrapper>>();
		for (final MRecordedStream stream : streams) {
			final MRecordedStreamWrapper wrapper = new MRecordedStreamWrapper();
			wrapper.setAgent(agent);
			wrapper.setStream(stream);

			if (stream.getDisplayName() == null) {
				stream.setDisplayName("Key: " + wrapper.getStreamKey());
			}

			wrappers.add(new StreamClientWrapper<MRecordedStreamWrapper>(
					wrapper));

		}
		return wrappers;
	}
}
