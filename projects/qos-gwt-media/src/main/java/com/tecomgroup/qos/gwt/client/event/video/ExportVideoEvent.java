/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.video;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent.DownloadVideoEventHandler;

/**
 * @author ivlev.e
 * 
 */
public class ExportVideoEvent extends GwtEvent<DownloadVideoEventHandler> {

	public interface DownloadVideoEventHandler extends EventHandler {
		void onExport(ExportVideoEvent event);
	}

	public final static Type<DownloadVideoEventHandler> TYPE = new Type<DownloadVideoEventHandler>();

	private final String url;
	private final String playerId;
	private final String taskKey;
	private final String taskDisplayName;

	public ExportVideoEvent(final String url, final String playerId, final String taskKey, final String taskDisplayName) {
		this.url = url;
		this.playerId = playerId;
		this.taskKey = taskKey;
		this.taskDisplayName = taskDisplayName;
	}

	@Override
	protected void dispatch(final DownloadVideoEventHandler handler) {
		handler.onExport(this);
	}

	@Override
	public Type<DownloadVideoEventHandler> getAssociatedType() {
		return TYPE;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the playerId
	 */
	public String getPlayerId() {
		return playerId;
	}

	/**
	 * @return the taskKey
	 */
	public String getTaskKey() {
		return taskKey;
	}

	/**
	 * @return the taskDisplayName
	 */
	public String getTaskDisplayName() {
		return taskDisplayName;
	}
}
