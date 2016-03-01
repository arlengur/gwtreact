/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.video;

import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.gwt.client.event.video.RecordedVideoAddedEvent.RecordedVideoAddedEventHandler;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author novohatskiy.r
 * 
 */
public class RecordedVideoAddedEvent
		extends
			VideoEvent<RecordedVideoAddedEventHandler> {

	public interface RecordedVideoAddedEventHandler extends EventHandler {
		void onRecordedVideoAddedEvent(RecordedVideoAddedEvent event);
	}

	private final List<StreamClientWrapper<MRecordedStreamWrapper>> streams;

	public final static Type<RecordedVideoAddedEventHandler> TYPE = new Type<RecordedVideoAddedEventHandler>();

	public RecordedVideoAddedEvent(
			final List<StreamClientWrapper<MRecordedStreamWrapper>> streams) {
		this.streams = streams;
	}

	@Override
	protected void dispatch(final RecordedVideoAddedEventHandler handler) {
		handler.onRecordedVideoAddedEvent(this);
	}

	@Override
	public Type<RecordedVideoAddedEventHandler> getAssociatedType() {
		return TYPE;
	}

	public List<StreamClientWrapper<MRecordedStreamWrapper>> getStreams() {
		return streams;
	}

}
