package com.tecomgroup.qos.gwt.client.presenter.widget;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.safehtml.shared.UriUtils;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.HasUiHandlers;
import com.gwtplatform.mvp.client.PopupView;
import com.gwtplatform.mvp.client.PresenterWidget;
import com.gwtplatform.mvp.client.UiHandlers;
import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MExportVideoEvent;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncLogoutOnFailureCallback;
import com.tecomgroup.qos.message.ApplicationMessage;
import com.tecomgroup.qos.service.ProbeEventServiceAsync;
import com.tecomgroup.qos.service.RecordingScheduleMediaServiceAsync;
import com.tecomgroup.qos.util.RussianTransliterator;

import java.util.*;

import com.google.gwt.regexp.shared.RegExp;

/**
 * Presenter for download video dialog window
 *
 * @author galin.a on 05.01.2016.
 */
public class ExportVideoPresenter extends PresenterWidget<ExportVideoPresenter.MyView> implements UiHandlers {

    public interface MyView extends PopupView, HasUiHandlers<ExportVideoPresenter> {
        void initialize();

        void showDialog();

        void setFileName(String fileName);

        void setDuration(String duration);

        void setStartDate(String date);

        void setStartTime(String time);

        void setFinishDate(String date);

        void setFinishTime(String time);

        void setComment(String comment);
    }

    private RegExp urlPrefixRegexp = RegExp.compile("^(http:.+)/VodSearcherServlet.+$");
    private final QoSMessages messages;
    private String taskDisplayName;
    private String taskDisplayNameTranslit;
    private String durationString;
    private String taskKey;
    private String localPrefix;

    private RecordingScheduleMediaServiceAsync recordingScheduleMediaService;
    private ProbeEventServiceAsync probeEventServiceAsync;

    public static final Map<String, DateTimeFormat> LOCALE_DATE_FORMATS = new HashMap<String, DateTimeFormat>() {
        {
            put("ru", DateTimeFormat.getFormat("dd.MM.yyyy"));
            put("en", DateTimeFormat.getFormat("MM/dd/yyyy"));
        }
    };

    public static final Map<String, DateTimeFormat> LOCALE_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
        {
            put("ru", DateTimeFormat.getFormat("HH:mm:ss"));
            put("en", DateTimeFormat.getFormat("h:mm:ss a"));
        }
    };

    @Inject
    public ExportVideoPresenter(EventBus eventBus,
                                MyView view,
                                final QoSMessages messages,
                                 final RecordingScheduleMediaServiceAsync recordingScheduleMediaService,
                                final ProbeEventServiceAsync probeEventServiceAsync) {
        super(eventBus, view);
        this.messages = messages;
        this.recordingScheduleMediaService = recordingScheduleMediaService;
        this.probeEventServiceAsync = probeEventServiceAsync;
        getView().setUiHandlers(this);
    }

    private String getBasePath(String url) {
        MatchResult matcher = urlPrefixRegexp.exec(url);
        if (matcher.getGroupCount() > 1) {
            return matcher.getGroup(1);
        }
        return "";
    }

    public void showDialog(String url, String taskKey, String taskDisplayName) {
        this.taskKey = taskKey;
        this.localPrefix = getBasePath(url);
        getView().setComment("");

        String start = "0", end = "0";
        for (String subString : url.split("&")) {
            if (subString.contains("startDateTime")) {
                start = subString.split("=")[1];
                getView().setStartDate(formatDate(start));
                getView().setStartTime(formatTime(start));
            } else if (subString.contains("endDateTime")) {
                end = subString.split("=")[1];
                getView().setFinishDate(formatDate(end));
                getView().setFinishTime(formatTime(end));
            }
        }


        long duration = (Long.parseLong(end) - Long.parseLong(start)) / 1000;
        durationString = getFormatedDuration(duration / 3600) + ":" + getFormatedDuration((duration % 3600) / 60) + ":" + getFormatedDuration(duration % 60);
        String durationString = getFormatedDuration(duration / 3600) + ":" + getFormatedDuration((duration % 3600) / 60) + ":" + getFormatedDuration(duration % 60);

        getView().setDuration(durationString);
        this.taskDisplayName = taskDisplayName.replaceAll(" ", "_");
        taskDisplayNameTranslit = RussianTransliterator.transliterate(taskDisplayName.replaceAll("[/|\\\\<>{};\"*?']", "_"));
        getView().setFileName(taskDisplayName);
        getView().showDialog();
    }

    private static String getFormatedDuration(long number) {
        return NumberFormat.getFormat("00").format(number);
    }

    @Override
    protected void onBind() {
        getView().initialize();
    }

    public void downloadRequest(final String startDateTimeFormattedString, String createdDateTime,
                                String durationHMS, long duration,
                                final String quality, final MExportVideoEvent.DESTINATION destination, final String comment) {
        String[] rootFolder = taskKey.split("\\.");
        final String agentKey = rootFolder[0];
        final String userName = AppUtils.getCurrentUser().getUser().getLogin();
        String extension = quality.equals(messages.label240pmp4()) ? ".mp4" : ".flv";

        final String remotePrefix;
        final String localPath;


        boolean immediate = MExportVideoEvent.DESTINATION.FTP_ON_SCHEDULE != destination;

        if (MExportVideoEvent.DESTINATION.LOCAL == destination) {
            remotePrefix = "file:///" + getPrefix(rootFolder, userName, "/");
            localPath = localPrefix + "/" + getPrefix(rootFolder, userName, "/");

        } else {
            remotePrefix = AppUtils.getClientProperties().get(ApplicationMessage.PROBE_VIDEO_EXPORT_PATH) + "/" + getPrefix(rootFolder, userName, "/");
            localPath = "";
        }

        final String fileName = getFileName(startDateTimeFormattedString, createdDateTime, durationHMS, getQualitySize(quality), extension, "_", false);
        final String fileNameEncode = getFileName(startDateTimeFormattedString, createdDateTime, durationHMS, getQualitySize(quality), extension, "_", true);

        try {
            recordingScheduleMediaService.sendExportVideoMessage(
                    agentKey, userName, taskKey,
                    taskDisplayNameTranslit, getQuality(quality),
                    startDateTimeFormattedString, duration, remotePrefix + fileNameEncode, immediate,
                    new AutoNotifyingAsyncLogoutOnFailureCallback<String>() {
                @Override
                protected void failure(Throwable caught) {
                    AppUtils.showErrorMessage(messages.error());
                }

                @Override
                protected void success(String uuid) {
                    probeEventServiceAsync.createProbeEvent(
                            fromExportVideoMessage(agentKey, userName,
                                    (MExportVideoEvent.DESTINATION.LOCAL != destination? (remotePrefix + fileName):((localPath + fileName))),
                                    getQuality(quality) ,
                                    taskDisplayName,
                                    startDateTimeFormattedString,
                                    durationString,
                                    comment, destination,
                                    uuid), new AutoNotifyingAsyncLogoutOnFailureCallback<Long>() {
                        @Override
                        protected void success(Long result) {
                            AppUtils.showInfoWithConfirmMessage(messages.videoExportSuccess());
                        }

                        @Override
                        protected void failure(Throwable caught) {
                            AppUtils.showErrorMessage(messages.error());
                        }
                    });
                }
            });


        } catch (Exception e) {
            AppUtils.showErrorMessage(messages.error());
        }
    }

    private String getPrefix(String[] rootFolder, String userName, String delimiter) { // FIXME: maybe java.util.Paths.get(...) ??
        return new StringBuilder().append(rootFolder[0]).append(".").append(rootFolder[1]).append(delimiter).append(userName).append(delimiter).toString();
    }

    private String getFileName(String startUTCString, String createdUTCString, String duration, String quality, String extension, String delimiter, boolean encode) {
        return new StringBuilder().append(encode ? UriUtils.encode(taskDisplayNameTranslit) : taskDisplayNameTranslit)
                                  .append(delimiter)
                                  .append(startUTCString)
                                  .append(delimiter)
                                  .append(duration)
                                  .append(delimiter)
                                  .append(createdUTCString)
                                  .append(delimiter)
                                  .append(quality)
                                  .append(extension)
                                  .toString();
    }


    public static MProbeEvent fromExportVideoMessage(
            String agentKey, String user, String exportLink, String quality,
            String taskDisplayName, String displayStartDateTime,
            String duration, String comment, MExportVideoEvent.DESTINATION destination, String uuid) {
        MProbeEvent event = new MProbeEvent();
        event.setKey(uuid);
        event.setAgentKey(agentKey);
        event.setStatus(MProbeEvent.STATUS.QUEUED);
        event.setUserLogin(user);
        event.setEventType(MExportVideoEvent.class.getName());
        Date createdDate = new Date();
        event.setTimestamp(createdDate);
        event.setCreatedTimestamp(createdDate);

        List<MEventProperty> p = new ArrayList<MEventProperty>();
        event.setPropertyList(p);
        p.add(new MEventProperty(MExportVideoEvent.FIELD.URL.name(), exportLink));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.START_DATE.name(), displayStartDateTime));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.DURATION.name(), duration));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.QUALITY.name(), quality));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.TASK_DISPLAY_NAME.name(), taskDisplayName));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.COMMENT.name(), comment));
        p.add(new MEventProperty(MExportVideoEvent.FIELD.DESTINATION.name(), destination.name()));
        return event;
    }

    public static String formatTime(String date) {
        return LOCALE_TIME_FORMATS.get(LocaleInfo.getCurrentLocale().getLocaleName())
                .format(new Date(Long.parseLong(date)), TimeZone.createTimeZone(0));
    }

    public static String formatDate(String date) {
        return LOCALE_DATE_FORMATS.get(LocaleInfo.getCurrentLocale().getLocaleName())
                .format(new Date(Long.parseLong(date)), TimeZone.createTimeZone(0));
    }

    private String getQuality(String quality){
        if (quality.equals(messages.label240pflv())) {
            return "240p_flv";
        }  else if (quality.equals(messages.label240pmp4())) {
            return "240p_mp4";
        } else {
            return "360p";
        }
    }

    private String getQualitySize(String quality){
        return quality.substring(0, 3);
    }
}
