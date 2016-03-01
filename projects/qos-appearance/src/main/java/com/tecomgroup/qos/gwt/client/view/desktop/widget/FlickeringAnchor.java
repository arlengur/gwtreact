/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.gwt.client.style.common.AppearanceUtils;
import com.tecomgroup.qos.gwt.client.style.common.button.AnchorAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorFlickeringAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.button.AnchorFlickeringAppearance.AnchorFlickeringStyle;

/**
 * Widget for representation html hyperlink which can flicker by the background
 * 
 * @author ivlev.e
 */
public class FlickeringAnchor extends Anchor implements HasClickHandlers {

	private class FlickeringEffect extends Timer {

		private final String styleName;

		private boolean isFired = false;

		public FlickeringEffect(final PerceivedSeverity severity) {
			super();
			this.styleName = AppearanceUtils.getSeverityStyle(getStyle(),
					severity);
		}

		@Override
		public void cancel() {
			super.cancel();
			FlickeringAnchor.this.removeStyleName(styleName);
		}

		@Override
		public void run() {
			if (isFired) {
				FlickeringAnchor.this.removeStyleName(styleName);
			} else {
				FlickeringAnchor.this.addStyleName(styleName);
			}
			isFired = !isFired;
		}
	}

	/**
	 * Flickering loop step in milliseconds
	 */
	private final static int FLICKERING_REPEAT_TIME = 500;

	private FlickeringEffect flickeringEffect;

	public FlickeringAnchor(final String href, final String text) {
		this(href, text, null, (AnchorAppearance) GWT
				.create(AnchorFlickeringAppearance.class));
	}

	public FlickeringAnchor(final String href, final String text,
			final AnchorAppearance appearance) {
		this(href, text, null, appearance);
	}

	public FlickeringAnchor(final String href, final String text,
			final String target) {
		this(href, text, target, (AnchorAppearance) GWT
				.create(AnchorFlickeringAppearance.class));
	}

	public FlickeringAnchor(final String href, final String text,
			final String target, final AnchorAppearance appearance) {
		super(href, text, target, appearance);
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	/**
	 * @return the flickeringEffect
	 */
	public FlickeringEffect getFlickeringEffect() {
		return flickeringEffect;
	}

	@Override
	public AnchorFlickeringStyle getStyle() {
		return (AnchorFlickeringStyle) super.getStyle();
	}

	public void handleAlertEvent(final PerceivedSeverity severity) {
		if (flickeringEffect != null) {
			flickeringEffect.cancel();
		}
		if (severity != null) {
			flickeringEffect = new FlickeringEffect(severity);
			flickeringEffect.scheduleRepeating(FLICKERING_REPEAT_TIME);
		} else {
			stopFlickering();
		}
	}

	public boolean stopFlickering() {
		boolean result = false;
		if (flickeringEffect != null) {
			flickeringEffect.cancel();
			flickeringEffect = null;
			result = true;
		}
		return result;
	}
}
