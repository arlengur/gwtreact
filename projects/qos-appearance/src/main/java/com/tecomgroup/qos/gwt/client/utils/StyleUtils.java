/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;

/**
 * @author ivlev.e
 * 
 */
public class StyleUtils {

	private static final String CLOSING_BRACKET = "}";

	public static void configureNoHeaders(final ContentPanel panel) {
		panel.setHeaderVisible(false);
		panel.setBorders(false);
		panel.setBodyBorder(false);
	}

	public static Image createSeparator(final Margins margins) {
		final Image separator = AbstractImagePrototype.create(
				AppearanceFactoryProvider.instance().resources().devider())
				.createImage();
		separator.getElement().<XElement> cast().setMargins(margins);
		return separator;
	}

	public static String wrapMediaStylesheet(final String mediaQuery,
			final String content) {
		return mediaQuery + content + CLOSING_BRACKET;
	}

}
