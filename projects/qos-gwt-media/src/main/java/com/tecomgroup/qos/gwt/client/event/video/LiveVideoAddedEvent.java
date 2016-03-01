/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.video;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.event.video.LiveVideoAddedEvent.LiveVideoAddedEventHandler;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author ivlev.e
 * 
 */
public class LiveVideoAddedEvent extends VideoEvent<LiveVideoAddedEventHandler> {

	public interface LiveVideoAddedEventHandler extends EventHandler {
		void onLiveVideoAddedEvent(LiveVideoAddedEvent event);
	}

	private final List<StreamClientWrapper<MLiveStreamWrapper>> streams;

	public final static Type<LiveVideoAddedEventHandler> TYPE = new Type<LiveVideoAddedEventHandler>();

	public LiveVideoAddedEvent(
			final List<StreamClientWrapper<MLiveStreamWrapper>> streams) {
		this.streams = streams;
	}

	@Override
	protected void dispatch(final LiveVideoAddedEventHandler handler) {
		handler.onLiveVideoAddedEvent(this);
	}

	@Override
	public Type<LiveVideoAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public List<StreamClientWrapper<MLiveStreamWrapper>> getStreams() {
		return streams;
	}

}
