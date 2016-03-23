package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Padding;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.TextField;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.domain.probestatus.MExportVideoEvent;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tractionsoftware.gwt.user.client.ui.UTCDateBox;
import com.tractionsoftware.gwt.user.client.ui.UTCTimeBox;
import org.goda.time.*;
import org.goda.time.format.DateTimeFormatter;

import java.util.Arrays;
import java.util.Date;


/**
 * @author galin.a on 05.01.2016.
 */
public class ExportVideoView extends SenchaPopupView<ExportVideoPresenter> implements ExportVideoPresenter.MyView {

    private final AppearanceFactory appearanceFactory;
    private final QoSMessages messages;
    private QoSDialog dialog;
    private final static int DIALOG_WIDTH = 590;
    private final static int DIALOG_HEIGHT = 305;

    private final VerticalLayoutContainer mainContainer;
    private TextField fileName;
    private TextField comment;
    private UTCDateBox startDate;
    private UTCTimeBox startTime;
    private UTCDateBox finishDate;
    private UTCTimeBox finishTime;
    private UTCTimeBox durationTime;
    private ComboBox<String> qualityControl;
    private ComboBox<String> destinationControl;

    public static DateTimeFormatter DATETIME_PATTERN = org.goda.time.format.DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");

    @Inject
    public ExportVideoView(final EventBus eventBus, final AppearanceFactory appearanceFactory, final QoSMessages messages) {
        super(eventBus);
        this.messages = messages;
        this.appearanceFactory = appearanceFactory;
        mainContainer = new VerticalLayoutContainer();
        dialog = createDialog();
    }

    @Override
    public Widget asWidget() {
        return dialog;
    }

    @Override
    public void initialize() {
        initializeTextField();
        initializeQualityComboBox();
        initializeStartTextFields();
        initializeFinishTextFields();
        initializeDurationTextFields();
        initializeDestinationComboBox();
        initializeCommentField();

    }

    private QoSDialog createDialog() {
        final QoSDialog dialog = new QoSDialog(appearanceFactory, messages) {
            @Override
            protected String getTitleText(final QoSMessages messages) {
                return messages.downloadVideo();
            }

            @Override
            protected void initializeComponents() {
                add(mainContainer);
            }

            @Override
            protected void onButtonPressed(final TextButton button) {
                if (button == getCancelButton()) {
                    hide();
                } else if (button == getButtonById(PredefinedButton.OK.name()) && validateDate(startDate) && validateDate(finishDate) && validateDuration()) {
                    DateTimeZone dateTimeZone = DateTimeZone.forOffsetHours(DateUtils.getCurrentTimeZoneOffset() / 60);
                    DateTime startDateTime = new LocalDateTime(startDate.getValue() + startTime.getValue()).toDateTime(dateTimeZone);
                    String startTime = DATETIME_PATTERN.withZone(DateTimeZone.UTC).print(startDateTime);
                    String createdTime = DATETIME_PATTERN.withZone(DateTimeZone.UTC).print(new DateTime().getMillis());

                    long duration = getDuration().getMillis() / 1000;
                    getUiHandlers().downloadRequest(startTime,
                            createdTime, getFormatedDuration(""),
                            duration, qualityControl.getValue(),
                            parseDestinationValue(destinationControl.getValue()), comment.getValue());
                    hide();
                }
            }
        };
        dialog.setPredefinedButtons(Dialog.PredefinedButton.OK, Dialog.PredefinedButton.CANCEL);
        dialog.setWidth(DIALOG_WIDTH);
        dialog.setHeight(DIALOG_HEIGHT);

        return dialog;
    }

    public MExportVideoEvent.DESTINATION parseDestinationValue(String value) {
        if(messages.linkLocal().equals(value)) {
            return MExportVideoEvent.DESTINATION.LOCAL;
        } else if(messages.linkFTP().equals(value)) {
            return MExportVideoEvent.DESTINATION.FTP;
        } else {
            return MExportVideoEvent.DESTINATION.FTP_ON_SCHEDULE;
        }
    }

    private boolean validateDate(UTCDateBox datebox) {
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        DateTimeFormat format = ExportVideoPresenter.LOCALE_DATE_FORMATS.get(localeName);
        try {
            format.parseStrict(datebox.getText());
            return true;
        } catch (IllegalArgumentException e) {
            AppUtils.showInfoMessage(messages.invalidDateFormat(datebox.getText(), format.getPattern()));
            return false;
        }
    }

    private boolean validateDuration() {
        if (getDuration().getMillis() > TimeConstants.MILLISECONDS_PER_DAY) {
            AppUtils.showInfoMessage(messages.invalidDateTimeInterval());
            return false;
        }
        return true;
    }

    private void initializeTextField() {
        final CssFloatLayoutContainer fileNameContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.fileName(), fileNameContainer);

        fileName = createTextField();
        fileNameContainer.add(fileName, new CssFloatData(.82));

        addToolbarToParentContainer(fileNameContainer, new Margins(6, 0, 0, 0));
    }

    private void initializeCommentField() {
        final CssFloatLayoutContainer commentContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.comment(), commentContainer);

        comment = createTextField();
        comment.setEmptyText(messages.comment());
        comment.setEnabled(true);
        comment.setReadOnly(false);

        commentContainer.add(comment, new CssFloatData(0.82));

        addToolbarToParentContainer(commentContainer);
    }

    private void initializeStartTextFields() {
        final CssFloatLayoutContainer startContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.start(), startContainer);

        startDate = createDatebox();
        startTime = createTimeBox(new Margins(6, 0, 2, 6));
        startContainer.add(startDate, new CssFloatData());
        startContainer.add(startTime, new CssFloatData());

        addToolbarToParentContainer(startContainer);
    }

    private void initializeFinishTextFields() {
        final CssFloatLayoutContainer finishContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.finish(), finishContainer);

        finishDate = createDatebox();
        finishTime = createTimeBox(new Margins(6, 0, 2, 6));
        finishContainer.add(finishDate, new CssFloatData());
        finishContainer.add(finishTime, new CssFloatData());

        addToolbarToParentContainer(finishContainer);
    }

    private void initializeDurationTextFields() {
        final CssFloatLayoutContainer durationContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.duration(), durationContainer);

        durationTime = createDurationTimeBox(new Margins(6, 0, 2, 0));
        durationContainer.add(durationTime, new CssFloatData());

        addToolbarToParentContainer(durationContainer);
    }

    private void initializeQualityComboBox() {
        final CssFloatLayoutContainer qualityContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.quality(), qualityContainer);

        qualityControl = createComboBox(createStore(messages.label240pflv(), messages.label240pmp4(), messages.label360p()));
        qualityContainer.add(qualityControl, new CssFloatData());

        addToolbarToParentContainer(qualityContainer);
    }

    private void initializeDestinationComboBox() {
        final CssFloatLayoutContainer destinationContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.destination(), destinationContainer);

        destinationControl = createComboBox(createStore(messages.linkLocal(), messages.linkFTP(), messages.linkFTPonSchedule()));
        destinationControl.setWidth(190);
        destinationContainer.add(destinationControl, new CssFloatData());

        addToolbarToParentContainer(destinationContainer);
    }

    private TextField createTextField() {
        return createTextField(new Margins(6, 0, 2, 0));
    }

    private TextField createTextField(Margins margins) {
        TextField textField = new TextField();
        textField.getElement().setMargins(margins);
        textField.setEnabled(false);
        textField.setReadOnly(true);
        textField.setWidth(80);
        return textField;
    }

    private UTCTimeBox createTimeBox(Margins margins) {
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        UTCTimeBox timebox = new UTCTimeBox(ExportVideoPresenter.LOCALE_TIME_FORMATS.get(localeName));
        timebox.addValueChangeHandler(new ValueChangeHandler<Long>() {
            @Override
            public void onValueChange(ValueChangeEvent<Long> event) {
                calcDuration();
            }
        });
        timebox.getElement().<XElement>cast().setMargins(margins);
        timebox.getElement().getStyle().setProperty("textAlign", "center");
        timebox.setWidth("80px");
        return timebox;
    }

    private UTCTimeBox createDurationTimeBox(Margins margins) {
        UTCTimeBox timebox = new UTCTimeBox(DateTimeFormat.getFormat("HH:mm:ss"));
        timebox.getElement().<XElement>cast().setMargins(margins);
        timebox.getElement().getStyle().setProperty("textAlign", "center");
        timebox.setWidth("76px");
        timebox.setEnabled(false);
        return timebox;
    }

    private UTCDateBox createDatebox() {
        return createDatebox(new Margins(6, 0, 2, 0));
    }

    private UTCDateBox createDatebox(Margins margins) {
        String localeName = LocaleInfo.getCurrentLocale().getLocaleName();
        UTCDateBox datebox = new UTCDateBox(ExportVideoPresenter.LOCALE_DATE_FORMATS.get(localeName));
        datebox.addValueChangeHandler(new ValueChangeHandler<Long>() {
            @Override
            public void onValueChange(ValueChangeEvent<Long> event) {
                calcDuration();
            }
        });
        datebox.getElement().<XElement>cast().setMargins(margins);
        datebox.getElement().getStyle().setProperty("textAlign", "center");
        datebox.setWidth("76px");
        return datebox;
    }

    private void calcDuration() {
        if (startDate.getValue() != null && startTime.getValue() != null && finishDate.getValue() != null && finishTime.getValue() != null && durationTime.getValue() != null) {
            durationTime.setText(getFormatedDuration(":"));
        }
    }

    private String getFormatedDuration(String separator) {
        Period duration = getDuration().toPeriod();
        return NumberFormat.getFormat("00").format(duration.getDays() * 24 + duration.getHours()) + separator +
                NumberFormat.getFormat("00").format(duration.getMinutes()) + separator +
                NumberFormat.getFormat("00").format(duration.getSeconds());
    }

    private Duration getDuration(){
        return new Duration(startDate.getValue() + startTime.getValue(), finishDate.getValue() + finishTime.getValue());
    }

    private void createAndAddLabel(String name, CssFloatLayoutContainer layoutContainer) {
        Label label = new Label(name + ":");
        label.getElement().<XElement>cast().setPadding(new Padding(8, 0, 4, 4));
        layoutContainer.add(label, new CssFloatData(.18));
    }

    private ListStore<String> createStore(String... items) {
        ListStore<String> store = new ListStore<String>(new ModelKeyProvider<String>() {
            @Override
            public String getKey(final String item) {
                return item;
            }
        });
        store.addAll(Arrays.asList(items));
        return store;
    }

    private ComboBox createComboBox(ListStore store) {
        ComboBox comboBox = new ComboBox<String>(store, new LabelProvider<String>() {
            @Override
            public String getLabel(final String item) {
                return item;
            }
        });
        comboBox.setEditable(false);
        comboBox.setAllowBlank(false);
        comboBox.setTriggerAction(ComboBoxCell.TriggerAction.ALL);

        comboBox.setValue(store.get(0), false, true);
        comboBox.getElement().setMargins(new Margins(6, 0, 2, 0));
        return comboBox;
    }

    private void addToolbarToParentContainer(final CssFloatLayoutContainer container) {
        addToolbarToParentContainer(container, new Margins(0, 0, 0, 0));
    }

    private void addToolbarToParentContainer(final CssFloatLayoutContainer container, Margins margins) {
        mainContainer.add(createAndAddToolbarToParentContainer(container), new VerticalLayoutContainer.VerticalLayoutData(1, -1, margins));
    }

    private FramedPanel createAndAddToolbarToParentContainer(final CssFloatLayoutContainer child) {
        final FramedPanel toolbar = new FramedPanel(appearanceFactory.lightFramedPanelAppearance());
        toolbar.setHeaderVisible(false);
        toolbar.setBodyBorder(false);
        toolbar.setBorders(false);
        toolbar.add(child);
        return toolbar;
    }

    @Override
    public void showDialog() {
        dialog.show();
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName.setValue(fileName);
    }

    @Override
    public void setDuration(String duration) {
        this.durationTime.setText(duration);
    }

    @Override
    public void setStartDate(String date) {
        this.startDate.setText(date);
    }

    @Override
    public void setStartTime(String time) {
        this.startTime.setText(time);
    }

    @Override
    public void setFinishDate(String date) {
        this.finishDate.setText(date);
    }

    @Override
    public void setFinishTime(String time) {
        this.finishTime.setText(time);
    }

    @Override
    public void setComment(String comment) { this.comment.setValue(comment); }
}
