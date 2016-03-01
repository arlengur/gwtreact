/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.event.video;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.tecomgroup.qos.gwt.client.event.video.DownloadVideoEvent.DownloadVideoEventHandler;

/**
 * @author ivlev.e
 *
 */
public class DownloadVideoEvent extends GwtEvent<DownloadVideoEventHandler> {

    public interface DownloadVideoEventHandler extends EventHandler {
        void onDownload(DownloadVideoEvent event);
    }

    public final static Type<DownloadVideoEventHandler> TYPE = new Type<DownloadVideoEventHandler>();

    private final String url;

    public DownloadVideoEvent(final String url) {
        this.url = url;
    }

    @Override
    protected void dispatch(final DownloadVideoEventHandler handler) {
        handler.onDownload(this);
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

}
