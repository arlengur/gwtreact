/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.event.video.LiveVideoAddedEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.gwt.client.utils.UserAgentUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentDialogPresenter;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.service.MediaAgentServiceAsync;

/**
 * @author ivlev.e
 * 
 */
public class AddLiveVideoPresenter extends AgentDialogPresenter {

	public static interface MyView extends AgentDialogView {

		void setStreams(List<StreamClientWrapper<MLiveStreamWrapper>> streams);
	}

	private final MediaAgentServiceAsync mediaAgentService;

	private final MainPagePresenter mainPagePresenter;

	@Inject
	public AddLiveVideoPresenter(final EventBus eventBus, final MyView view,
			final QoSMessages messages,
			final MediaAgentServiceAsync agentService,
			final MainPagePresenter mainPagePresenter) {
		super(eventBus, view, messages, agentService);
		this.mediaAgentService = agentService;
		this.mainPagePresenter = mainPagePresenter;
		getView().setUiHandlers(this);
	}

	public void addStreams(
			final List<StreamClientWrapper<MLiveStreamWrapper>> streams) {
		AppUtils.getEventBus().fireEvent(new LiveVideoAddedEvent(streams));
	}

	@Override
	public void agentSelected(final MAgent agent) {
		final AsyncCallback<List<MLiveStream>> callback = new AutoNotifyingAsyncLogoutOnFailureCallback<List<MLiveStream>>(
				messages.streamLoadingFail(), true) {

			@Override
			protected void success(final List<MLiveStream> liveStreams) {
				getView().<MyView> cast().setStreams(
						wrapStreams(
								filterStreams(liveStreams,
										UserAgentUtils.isDesktop()), agent));
			}
		};
		loadLiveStreamByAgent(agent.getName(), callback);
	}

	private List<MLiveStream> filterStreams(final List<MLiveStream> streams,
			final boolean isDesktop) {
		final List<MLiveStream> result = new ArrayList<MLiveStream>();
		for (final MLiveStream stream : streams) {
			if ((isDesktop && stream.forDesktop())
					|| (!isDesktop && !stream.forDesktop())) {
				result.add(stream);
			}
		}
		return result;
	}

	private void loadLiveStreamByAgent(final String agentName,
			final AsyncCallback<List<MLiveStream>> callback) {
		mediaAgentService.getAgentLiveStreams(agentName, callback);
	}

	protected List<StreamClientWrapper<MLiveStreamWrapper>> wrapStreams(
			final List<MLiveStream> streams, final MAgent agent) {
		final List<StreamClientWrapper<MLiveStreamWrapper>> wrappers = new LinkedList<StreamClientWrapper<MLiveStreamWrapper>>();
		for (final MLiveStream stream : streams) {
			final MLiveStreamWrapper wrapper = new MLiveStreamWrapper();
			wrapper.setAgent(agent);
			wrapper.setStream(stream);
			wrappers.add(new StreamClientWrapper<MLiveStreamWrapper>(wrapper));
		}
		return wrappers;
	}
}
