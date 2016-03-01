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
import com.tecomgroup.qos.communication.message.recording.ExportVideo;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.AutoNotifyingAsyncCallback;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.message.ApplicationMessage;
import com.tecomgroup.qos.service.RecordingScheduleMediaServiceAsync;
import com.tecomgroup.qos.util.RussianTransliterator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
    }

    private RegExp urlPrefixRegexp = RegExp.compile("^(http:.+)/VodSearcherServlet.+$");
    private final QoSMessages messages;
    private final AppearanceFactory appearanceFactory;
    private String taskDisplayName;
    private String taskDisplayNameTranslit;
    private String start;
    private String end;
    private long duration;
    private String taskKey;
    private String localLinkPrefix;

    private RecordingScheduleMediaServiceAsync recordingScheduleMediaService;

    private static final Map<String, DateTimeFormat> LOCALE_DATE_FORMATS = new HashMap<String, DateTimeFormat>() {
        {
            put("ru", DateTimeFormat.getFormat("dd.MM.yyyy"));
            put("en", DateTimeFormat.getFormat("MM/dd/yyyy"));
        }
    };

    private static final Map<String, DateTimeFormat> LOCALE_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
        {
            put("ru", DateTimeFormat.getFormat("HH:mm:ss"));
            put("en", DateTimeFormat.getFormat("h:mm:ss a"));
        }
    };

    private static final String dateTimeFormat = "yyyyMMdd'T'HHmmss";

    @Inject
    public ExportVideoPresenter(EventBus eventBus, MyView view, final QoSMessages messages, final AppearanceFactory appearanceFactory, RecordingScheduleMediaServiceAsync recordingScheduleMediaService) {
        super(eventBus, view);
        this.messages = messages;
        this.appearanceFactory = appearanceFactory;
        this.recordingScheduleMediaService = recordingScheduleMediaService;
        getView().setUiHandlers(this);
    }

    private String getBasePath(String url) {
        MatchResult matcher = urlPrefixRegexp.exec(url);
        if(matcher.getGroupCount() > 1) {
            return matcher.getGroup(1);
        }
        return "";
    }

    public void showDialog(String url, String taskKey, String taskDisplayName) {
        this.taskKey = taskKey;
        this.localLinkPrefix = getBasePath(url);

        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        for (String subString : url.split("&")) {
            if (subString.contains("startDateTime")) {
                start = subString.split("=")[1];
                String dateString = getFormatedString(LOCALE_DATE_FORMATS.get(localeName), start);
                getView().setStartDate(dateString);
                String timeString = getFormatedString(LOCALE_TIME_FORMATS.get(localeName), start);
                getView().setStartTime(timeString);
            } else if (subString.contains("endDateTime")) {
                end = subString.split("=")[1];
                String dateString = getFormatedString(LOCALE_DATE_FORMATS.get(localeName), end);
                getView().setFinishDate(dateString);
                String timeString = getFormatedString(LOCALE_TIME_FORMATS.get(localeName), end);
                getView().setFinishTime(timeString);
            }
        }
        duration = (Long.parseLong(end) - Long.parseLong(start)) / 1000;
        String durationString = getFormatedDuration(duration / 3600) + ":" + getFormatedDuration((duration % 3600) / 60) + ":" + getFormatedDuration(duration % 60);
        getView().setDuration(durationString);
        this.taskDisplayName = taskDisplayName.replaceAll(" ", "_");
		taskDisplayNameTranslit = RussianTransliterator.transliterate(this.taskDisplayName.replaceAll("[/|\\\\<>{};\"*?']", "_"));
        getView().setFileName(taskDisplayName);
        getView().showDialog();
    }

    private String getFormatedString(DateTimeFormat format, String date) {
        return format.format(new Date(Long.parseLong(date)), TimeZone.createTimeZone(-DateUtils.getCurrentTimeZoneOffset()));
    }

    private String getFormatedDuration(long number) {
        return NumberFormat.getFormat("00").format(number);
    }

    @Override
    protected void onBind() {
        getView().initialize();
    }

    public void downloadRequest(String quality, String destination) {
        String[] rootFolder = taskKey.split("\\.");
        String agentKey = rootFolder[0];
        String durationHMS = getFormatedDuration(duration / 3600) + getFormatedDuration((duration % 3600) / 60) + getFormatedDuration(duration % 60);
        String startUTCString = DateTimeFormat.getFormat(dateTimeFormat).format(new Date(Long.parseLong(start)), TimeZone.createTimeZone(0));
        String endUTCString = DateTimeFormat.getFormat(dateTimeFormat).format(new Date(), TimeZone.createTimeZone(0));
        String userName = AppUtils.getCurrentUser().getUser().getLogin();
        final String localPrefix;
        final String remotePrefix;
        if (destination.equals(messages.linkLocal())) {
            localPrefix = localLinkPrefix + "/" + getPrefix(rootFolder, userName, "/");
            remotePrefix = "file:///" + getPrefix(rootFolder, userName, "/");
        } else {
            remotePrefix = localPrefix = AppUtils.getClientProperties().get(ApplicationMessage.PROBE_VIDEO_EXPORT_PATH) + "/" + getPrefix(rootFolder, userName, "/");
        }
		
        final String fileName = getFileName(startUTCString, endUTCString, durationHMS, quality.toLowerCase(), "_", false);
        final String fileNameEncode = getFileName(startUTCString, endUTCString, durationHMS, quality.toLowerCase(), "_", true);
        try {
            ExportVideo exportVideo = new ExportVideo(userName, taskKey, taskDisplayNameTranslit, quality.toLowerCase(), startUTCString, duration, remotePrefix + fileName, true);

            recordingScheduleMediaService.sendExportVideoMessage(agentKey, exportVideo, new AutoNotifyingAsyncCallback<Void>() {
                @Override
                protected void failure(Throwable caught) {
                    AppUtils.showErrorMessage("Error");
                }

                @Override
                protected void success(Void result) {
                    AppUtils.showInfoMessage("Success");
                    new CopyToClipboardDialog(appearanceFactory, messages, messages.linkToFile(), localPrefix + fileNameEncode, fileName).show();
                }
            });

        } catch (Exception e) {
            AppUtils.showErrorMessage("Download failed.");
        }
    }

    private String getPrefix(String[] rootFolder, String userName, String delimiter) { // FIXME: maybe java.util.Paths.get(...) ??
        return new StringBuilder().append(rootFolder[0])
                                  .append(".")
                                  .append(rootFolder[1])
                                  .append(delimiter)
                                  .append(userName)
                                  .append(delimiter)
                                  .toString();
    }

    private String getFileName(String startUTCString, String endUTCString, String duration, String quality, String delimiter, boolean encode) {
        return new StringBuilder().append(encode ? UriUtils.encode(taskDisplayNameTranslit) : taskDisplayNameTranslit)
                                  .append(delimiter)
                                  .append(startUTCString)
                                  .append(delimiter)
                                  .append(duration)
                                  .append(delimiter)
                                  .append(endUTCString)
                                  .append(delimiter)
                                  .append(quality)
                                  .append(".flv")
                                  .toString();
    }

}
