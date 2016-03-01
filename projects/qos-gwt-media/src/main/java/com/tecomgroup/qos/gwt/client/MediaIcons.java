/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author abondin
 * 
 */
public enum MediaIcons implements QoSIcons {

	/**
	 * Домашняя страница
	 */
	VIDEO("video60", "video30");

	public interface Resources extends ClientBundle {
		@Source("icons/30/video.png")
		ImageResource video30();
		@Source("icons/60/video.png")
		ImageResource video60();
	}

	public Resources IMAGES = GWT.create(Resources.class);

	private ImageResource icon60;
	private ImageResource icon30;

	/**
	 * 
	 */
	private MediaIcons(final String icon60, final String icon30) {
		this.icon60 = getByName(icon60);
		this.icon30 = getByName(icon30);
	}
	@Override
	public ImageResource getIcon60() {
		return icon60;
	}
	private ImageResource getByName(final String name) {
		if (name.equals("video60")) {
			return IMAGES.video60();
		} else if (name.equals("video30")) {
			return IMAGES.video30();
		}
		throw new RuntimeException("Unknown icon name " + name);
	}

	@Override
	public ImageResource getIcon30() {
		return icon30;
	}

}