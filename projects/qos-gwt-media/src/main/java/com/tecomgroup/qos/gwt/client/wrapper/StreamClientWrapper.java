/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.wrapper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.gwt.user.client.ui.Image;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.gwt.client.style.HasIcon;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

/**
 * @author ivlev.e
 * 
 */
public class StreamClientWrapper<S extends MStreamWrapper> implements HasIcon {

	private final S wrapper;

	private final static String CHANNEL_IMAGE_PATH = GWT.getModuleName()
			+ "/channels/";

	private final static String DEFAULT_CHANNEL_IMAGE_URI = CHANNEL_IMAGE_PATH
			+ "default.png";

	private Image icon;

	public StreamClientWrapper(final S wrapper) {
		this.wrapper = wrapper;
		final MStream stream = wrapper.getStream();
		if (stream != null) {
			final MProperty programName = stream
					.getProperty(MStream.PROGRAM_KEY);
			if (programName != null) {
				icon = new Image();
				icon.setSize(ClientConstants.CHANNEL_ICON_SIZE + "px",
						ClientConstants.CHANNEL_ICON_SIZE + "px");

				final SafeUri uri = UriUtils.fromString(CHANNEL_IMAGE_PATH
						+ programName.getValue() + ".png");
				icon.setUrl(uri);
			}
		}
	}

	@Override
	public SafeUri getDefaultImageUri() {
		return UriUtils.fromString(DEFAULT_CHANNEL_IMAGE_URI);
	}

	@Override
	public Image getIcon() {
		return icon;
	}

	/**
	 * @return the wrapper
	 */
	public S getWrapper() {
		return wrapper;
	}

	public boolean isStreamCanBeDownloaded() {
		return getWrapper() instanceof MRecordedStreamWrapper;
	}
}