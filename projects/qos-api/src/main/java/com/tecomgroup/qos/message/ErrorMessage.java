/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.message;

/**
 * @author kunilov.p
 * 
 */
public interface ErrorMessage {

	String UNABLE_TO_CREATE_CAPTURED_VIDEO = "create.captured.video.error.message";

	String UNABLE_TO_CREATE_CONVEYER = "create.conveyer.error.message";

	String UNABLE_TO_CREATE_MODULE = "create.module.error.message";

	String UNABLE_TO_CREATE_PLAYER_REQUEST = "create.player.request.error.message";

	String UNABLE_TO_CREATE_CONVERTER_REQUEST = "create.converter.request.error.message";

	String UNABLE_TO_CREATE_CAPTURE_SERVER = "create.capture.server.error.message";

	String UNABLE_TO_CREATE_ASRUNLOG_STORAGE = "create.as.run.log.storage.error.message";

	String UNABLE_TO_REMOVE_CONVEYER = "remove.conveyer.error.message";

	String UNABLE_TO_REMOVE_MODULE = "remove.module.error.message";

	String UNABLE_TO_REMOVE_SCHEDULE = "remove.schedule.error.message";

	String UNABLE_TO_UPDATE_SCHEDULE = "update.schedule.error.message";

	String UNABLE_TO_UPDATE_COMMAND_RESULT = "update.command.result.error.message";

	String UNABLE_TO_UPDATE_WRITING_VIDEO_LOCATION = "update.writing.video.location.error.message";

	String UNABLE_TO_UPDATE_AS_RUN_LOG_LOCATION = "update.as.run.log.location.error.message";

	String UNABLE_TO_ADD_COMMAND = "add.command.error.message";

	String UNABLE_TO_SAVE_COMMAND = "save.command.error.message";

	String UNABLE_TO_REGISTER_CAPTURE_SERVER = "register.capture.server.error.message";

	String UNABLE_TO_FIND_COMMAND_POOL_FOR_CAPTURE_SERVER = "find.capture.server.command.pool.error.message";

	String UNABLE_TO_UPDATE_CONVEYER_STATE = "update.conveyer.state.error.message";

	String UNABLE_TO_UPDATE_MODULES_STATES = "update.modules.states.error.message";

	String UNABLE_TO_UPDATE_CURRENTLY_PLAYED_MEDIA_FILES = "update.currently.played.media.files.error.message";

	String UNABLE_TO_GET_MEDIA_FILES = "getting.media.files.error.message";

	String NAME_IS_EMTPY = "name.is.empty.error.message";

	String DISPLAY_NAME_IS_EMTPY = "display.name.is.empty.error.message";

	String HOSTNAME_IS_EMTPY = "hostname.is.empty.error.message";

	String PORT_IS_EMTPY = "port.is.empty.error.message";

	String MODEL_IS_NULL = "model.is.null.error.message";

	String SUPPORTED_SIGNAL_TYPES_IS_NULL = "supported.signal.types.is.null.error.message";

	String IP_IS_EMTPY = "ip.is.empty.error.message";

	String PATH_IS_EMTPY = "path.is.empty.error.message";

	String SOURCE_IS_NULL = "source.is.null.error.message";

	String QUEUE_IS_NULL = "queue.is.null.error.message";

	String ATTACHMENT_POINT_IS_NULL = "attachment.point.is.null.error.message";

	String CHANNEL_IS_NULL = "channel.is.null.error.message";

	String SCHEDULE_START_TIME_GREATER_THAN_END_TIME = "schedule.start.time.greater.than.end.time.error.message";

	String DURATION_IS_NULL = "duration.is.null.error.message";

	String START_DATE_TIME_IS_NULL = "start.time.is.null.error.message";

	String END_DATE_TIME_IS_NULL = "end.time.is.null.error.message";
}
