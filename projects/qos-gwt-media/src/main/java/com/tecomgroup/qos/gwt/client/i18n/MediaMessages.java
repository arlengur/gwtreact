/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.i18n;

import com.google.gwt.i18n.client.LocalizableResource.DefaultLocale;

/**
 * @author abondin
 */
@DefaultLocale("en")
public interface MediaMessages extends QoSMessages {
	@DefaultMessage("Live video templates")
	String liveVideoTemplates();

	@DefaultMessage("Recorded video")
	String navigationRecorded();

	@DefaultMessage("Live video")
	String navigationVideo();

	@DefaultMessage("Recorded video templates")
	String recordedVideoTemplates();
}