package com.tecomgroup.qos.domain.probestatus;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by uvarov.m on 01.03.2016.
 */
public class MExportVideoEvent extends MProbeEvent implements IsSerializable {
    public enum FIELD {DESTINATION, START_DATE, DURATION, URL, QUALITY,
        TASK_DISPLAY_NAME, ERROR_CODE, COMMENT, EXPIRATION_TIME, STAGE, MISSED_RECORDS}

    public enum DESTINATION {LOCAL, FTP, FTP_ON_SCHEDULE}

    public enum DESCRIPTION {TRANSCODE_WAITING, TRANSCODING, UPLOAD_WAITING, UPLOADING, MISSED_RECORDS,
        ERROR_NO_FREE_DISK_SPACE, ERROR_TRANSCODING_FAULT, ERROR_FTP_FAULT, TIMEOUT, ERROR_MISSING_FILES,
        ERROR_UPLOAD_EXPIRED, ERROR_INTERNAL;

        public static DESCRIPTION valueOff(String value) {
            if(TRANSCODE_WAITING.name().equals(value)) {
                return TRANSCODE_WAITING;
            } else if(TRANSCODING.name().equals(value)) {
                return TRANSCODING;
            } else if(UPLOAD_WAITING.name().equals(value)) {
                return UPLOAD_WAITING;
            } else if(UPLOADING.name().equals(value)) {
                return UPLOADING;
            } else if(MISSED_RECORDS.name().equals(value)) {
                return MISSED_RECORDS;
            } else if(ERROR_NO_FREE_DISK_SPACE.name().equals(value)) {
                return ERROR_NO_FREE_DISK_SPACE;
            } else if(ERROR_TRANSCODING_FAULT.name().equals(value)) {
                return ERROR_TRANSCODING_FAULT;
            } else if(ERROR_FTP_FAULT.name().equals(value)) {
                return ERROR_FTP_FAULT;
            } else if(TIMEOUT.name().equals(value)) {
                return TIMEOUT;
            } else if(ERROR_MISSING_FILES.name().equals(value)) {
                return ERROR_MISSING_FILES;
            } else if(ERROR_UPLOAD_EXPIRED.name().equals(value)) {
                return ERROR_UPLOAD_EXPIRED;
            } else if(ERROR_INTERNAL.name().equals(value)) {
                return ERROR_INTERNAL;
            } else if("YES".equals(value)) {
                return MISSED_RECORDS;
            }
            else {
                return null;
            }
        }
    }

    public MExportVideoEvent() {
        super();
    }

    public MExportVideoEvent(String name) {
        super(name);
    }

    public MExportVideoEvent(MProbeEvent event) {
        super(event, true);
    }

    public String getStartDate() {
        return getPropertyValue(FIELD.START_DATE.name());
    }

    public String getDuration() {
        return getPropertyValue(FIELD.DURATION.name());
    }

    public String getUrl() {
        return getPropertyValue(FIELD.URL.name());
    }

    public String getQuality() {
        return getPropertyValue(FIELD.QUALITY.name());
    }

    public String getDestination() {
        return getPropertyValue(FIELD.DESTINATION.name());
    }

    public String getTaskDisplayName() { return getPropertyValue(FIELD.TASK_DISPLAY_NAME.name()); }

    public String getError() { return  getPropertyValue(FIELD.ERROR_CODE.name()); }

    public String getComment() { return  getPropertyValue(FIELD.COMMENT.name()); }

    public DESCRIPTION getDescription() {
        if(STATUS.IN_PROGRESS == getStatus()) {
            return parsePropertyValue(FIELD.STAGE.name());
        }

        if(STATUS.OK == getStatus()) {
             return parsePropertyValue(FIELD.MISSED_RECORDS.name());
        }

        if(STATUS.TIMEOUT == getStatus()) {
            return DESCRIPTION.TIMEOUT;
        }

        if(STATUS.FAILED == getStatus()) {
            return parsePropertyValue(FIELD.ERROR_CODE.name());
        }

        return null;
    }

    private DESCRIPTION parsePropertyValue(String property) {
        String value = getPropertyValue(property);
        if(value != null) {
            return DESCRIPTION.valueOff(value);
        }
        return null;
    }

}
