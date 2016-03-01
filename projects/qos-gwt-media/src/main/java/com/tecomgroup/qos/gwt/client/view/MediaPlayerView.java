/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view;

import java.util.List;

import javax.validation.ValidationException;

import com.gwtplatform.mvp.client.View;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.view.desktop.AbstractMediaPlayerView.AddVideoToDashboardListener;
import com.tecomgroup.qos.gwt.client.view.desktop.AbstractMediaPlayerView.RemoveVideoToDashboardListener;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author novohatskiy.r
 * 
 */
public interface MediaPlayerView<W extends MStreamWrapper> extends View {
	void addStream(StreamClientWrapper<W> clientStreamWrapper);

	void addStreams(List<StreamClientWrapper<W>> clientStreamWrappers);

	void applyButtonHandler(SelectEvent e);

	void clearStreams();

	void closePlayers();

	List<W> getStreamWrappers();

	TimeInterval getSyncTimeInterval();

	void refreshGridView();

	void removeBigTvButton();

	void removeStream(StreamClientWrapper<W> stream);

	void savePlayerStates();

	void setAddVideoToDashboardListener(
			final AddVideoToDashboardListener addVideoToDashboardListener);

	void setRemoveVideoToDashboardListener(
			final RemoveVideoToDashboardListener removeVideoToDashboardListener);

	void setBigTvButton();

	void setTemplateLabel(String templateName);

	void setUpPlayer(QoSPlayer player, W streamWrapper)
			throws ValidationException;

	void setUpPlayers();

	void updateSyncTimeInterval(TimeInterval interval);

	void activateAgentTimeZone();
}