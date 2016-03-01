/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.style;

import javax.inject.Provider;

import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory.Theme;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkAppearanceFactory;
import com.tecomgroup.qos.gwt.client.style.theme.dark.DarkResources;

/**
 * @author ivlev.e
 * 
 */
public class AppearanceFactoryProvider implements Provider<AppearanceFactory> {

	private static AppearanceFactory instance;

	static {
		final Theme theme = getCurrentTheme();
		if (theme != null) {
			switch (theme) {
				case DARK :
					DarkResources.INSTANCE.css().ensureInjected();
					break;
				default :
					break;
			}
		} else {
			initDefaultAppearanceFactory();
		}
	}

	private static Theme getCurrentTheme() {
		Theme theme = null;
		String themeName = Window.Location.getParameter("theme");
		if (themeName == null) {
			themeName = Cookies.getCookie("theme");
		}
		if (themeName != null) {
			theme = Theme.valueOf(themeName);
		}
		return theme;
	}

	private static AppearanceFactory getInstance() {
		if (instance == null) {
			final Theme theme = getCurrentTheme();
			if (theme == null) {
				initDefaultAppearanceFactory();
			} else {
				switch (theme) {
					case DARK :
						instance = new DarkAppearanceFactory();
						break;
					default :
						break;
				}
			}
		}
		return instance;
	}

	private static void initDefaultAppearanceFactory() {
		instance = new DarkAppearanceFactory();
		DarkResources.INSTANCE.css().ensureInjected();
	}

	public static AppearanceFactory instance() {
		return getInstance();
	}

	@Override
	public AppearanceFactory get() {
		return getInstance();
	}
}
