/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.message;

/**
 * @author kunilov.p
 * 
 */
public interface ApplicationMessage {

	String APPLICATION_IS_STARTING = "application.is.starting";

	String APPLICATION_STARTED = "application.started";

	String CAPTURE_SERVER_SERVICE_STARTED = "capture.server.service.started";

	String CLIENT_SERVICE_STARTED = "client.service.started";

	String PLAYER_SERVICE_STARTED = "player.service.started";

	String PROBE_VIDEO_EXPORT_PATH = "probe.video.export.path";
}
