/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.utils;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Navigator;

/**
 * The class is responsible for the determination of the type of user device
 * (operational system, platform, browser and so on)
 * 
 * @author ivlev.e
 */
public class UserAgentUtils {

	private static final String APPLE_MOBILE_DEVICES_LIST = "ipod|ipad|iphone";

	/**
	 * Determines whether device is based on Android OS
	 * 
	 * @return
	 */
	public static boolean isAndroidDevice() {
		return Navigator.getUserAgent().toLowerCase().matches(".*android.*");
	}

	public static boolean isDesktop() {
		return !isMobile();
	}

	/**
	 * Determines only Apple mobile devices (not Macintosh)
	 * 
	 * @return
	 */
	public static boolean isIOSDevice() {
		final String platform = Navigator.getPlatform();
		final boolean isIOSPlatform = ((platform != null) && platform
				.toLowerCase().matches(
						".*(" + APPLE_MOBILE_DEVICES_LIST + ").*"));

		return Navigator.getUserAgent().toLowerCase()
				.matches(".*(" + APPLE_MOBILE_DEVICES_LIST + ").*")
				|| isIOSPlatform;
	}

	public static boolean isMobile() {
		boolean isMobile = isIOSDevice() || isAndroidDevice()
				|| isUncknownMobileDevice() || isModeForced("mobile");
		isMobile = isModeForced("deskop") ? false : isMobile;
		return isMobile;
	}

	/**
	 * Determines whether QUERY_STRING contains mode parameter with modeName
	 * value
	 * 
	 * @param modeName
	 * @return
	 */
	private static boolean isModeForced(final String modeName) {
		final String mode = Window.Location.getParameter("mode");
		return (mode != null) && modeName.equalsIgnoreCase(mode);
	}

	/**
	 * Determines if this is mobile device (not Android and not Apple)
	 * 
	 * @return
	 */
	public static boolean isUncknownMobileDevice() {
		return Navigator.getUserAgent().toLowerCase().matches(".*mobile.*");
	}
}
