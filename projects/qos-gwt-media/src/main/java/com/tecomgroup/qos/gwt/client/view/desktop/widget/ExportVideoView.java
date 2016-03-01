package com.tecomgroup.qos.gwt.client.view.desktop.widget;

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
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.ExportVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;


/**
 * @author galin.a on 05.01.2016.
 */
public class ExportVideoView extends SenchaPopupView<ExportVideoPresenter> implements ExportVideoPresenter.MyView {

    private final AppearanceFactory appearanceFactory;
    private final QoSMessages messages;
    private QoSDialog dialog;
    private final static int DIALOG_WIDTH = 590;
    private final static int DIALOG_HEIGHT = 262;

    private final VerticalLayoutContainer mainContainer;
    private TextField fileName;
    private TextField startDate;
    private TextField startTime;
    private TextField finishDate;
    private TextField finishTime;
    private TextField durationTime;
    private ComboBox<String> qualityControl;
    private ComboBox<String> destinationControl;


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
                } else if (button == getButtonById(PredefinedButton.OK.name())) {
                    String quality = qualityControl.getValue().equals(messages.labelDefault()) ? "default" : qualityControl.getValue();
                    getUiHandlers().downloadRequest(quality, destinationControl.getValue());
                    hide();
                }
            }
        };
        dialog.setPredefinedButtons(Dialog.PredefinedButton.OK, Dialog.PredefinedButton.CANCEL);
        dialog.setWidth(DIALOG_WIDTH);
        dialog.setHeight(DIALOG_HEIGHT);

        return dialog;
    }

    private void initializeTextField() {
        final CssFloatLayoutContainer fileNameContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.fileName(), fileNameContainer);

        fileName = createTextField();
        fileNameContainer.add(fileName, new CssFloatData(.82));

        addToolbarToParentContainer(fileNameContainer, new Margins(6, 0, 0, 0));
    }

    private void initializeStartTextFields() {
        final CssFloatLayoutContainer startContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.start(), startContainer);

        startDate = createTextField();
        startTime = createTextField(new Margins(6, 0, 2, 6));
        startContainer.add(startDate, new CssFloatData());
        startContainer.add(startTime, new CssFloatData());

        addToolbarToParentContainer(startContainer);
    }

    private void initializeFinishTextFields() {
        final CssFloatLayoutContainer finishContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.finish(), finishContainer);

        finishDate = createTextField();
        finishTime = createTextField(new Margins(6, 0, 2, 6));
        finishContainer.add(finishDate, new CssFloatData());
        finishContainer.add(finishTime, new CssFloatData());

        addToolbarToParentContainer(finishContainer);
    }

    private void initializeDurationTextFields() {
        final CssFloatLayoutContainer durationContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.duration(), durationContainer);

        durationTime = createTextField();
        durationContainer.add(durationTime, new CssFloatData());

        addToolbarToParentContainer(durationContainer);
    }

    private void initializeQualityComboBox() {
        final CssFloatLayoutContainer qualityContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.quality(), qualityContainer);

        qualityControl = createComboBox(createStore(messages.labelDefault(), messages.label240p(), messages.label360p()));
        qualityContainer.add(qualityControl, new CssFloatData());

        addToolbarToParentContainer(qualityContainer);
    }

    private void initializeDestinationComboBox() {
        final CssFloatLayoutContainer destinationContainer = new CssFloatLayoutContainer();

        createAndAddLabel(messages.destination(), destinationContainer);

        destinationControl = createComboBox(createStore(messages.linkLocal(), messages.linkFTP()));
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
        for (String item : items) {
            store.add(item);
        }
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
        this.durationTime.setValue(duration);
    }

    @Override
    public void setStartDate(String date) {
        this.startDate.setValue(date);
    }

    @Override
    public void setStartTime(String time) {
        this.startTime.setValue(time);
    }

    @Override
    public void setFinishDate(String date) {
        this.finishDate.setValue(date);
    }

    @Override
    public void setFinishTime(String time) {
        this.finishTime.setValue(time);
    }
}
