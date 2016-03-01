/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.video;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.video.CloseVideoPanelEvent.CloseVideoPanelEventHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.VideoPanel;

public class CloseVideoPanelEvent extends GwtEvent<CloseVideoPanelEventHandler> {

	public interface CloseVideoPanelEventHandler extends EventHandler {
		void onClose(CloseVideoPanelEvent event);
	}

	public final static Type<CloseVideoPanelEventHandler> TYPE = new Type<CloseVideoPanelEventHandler>();

	private final VideoPanel videoPanel;

	public CloseVideoPanelEvent(final VideoPanel videoPanel) {
		this.videoPanel = videoPanel;
	}

	@Override
	protected void dispatch(final CloseVideoPanelEventHandler handler) {
		handler.onClose(this);
	}

	@Override
	public Type<CloseVideoPanelEventHandler> getAssociatedType() {
		return TYPE;
	}

	public VideoPanel getVideoPanel() {
		return videoPanel;
	}

}