/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.gwt.client.view.desktop.widget.chart;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer.CssFloatData;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.form.NumberField;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.DoublePropertyEditor;
import com.sencha.gxt.widget.core.client.form.NumberPropertyEditor.IntegerPropertyEditor;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.widget.chart.AddBitrateToDashboarddWidgetPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AddNamedWidgetToDashboardView;

/**
 * @author sviyazov.a
 */
public class AddBitrateToDashboardView extends AddNamedWidgetToDashboardView
        implements
        AddBitrateToDashboarddWidgetPresenter.MyView {

    private final static int DIALOG_HEIGHT = 253;

    private final static int DEFAULT_DIALOG_WIDTH = 340;

    private ComboBox<String> typeControl;

    private CheckBox updateIntervalCheckbox;

    private NumberField<Integer> minutesField;

    private NumberField<Integer> secondsField;

    private NumberField<Integer> capacityField;
    private String dialogTitle;

    @Inject
    public AddBitrateToDashboardView(final EventBus eventBus,
                                       final AppearanceFactoryProvider appearanceFactoryProvider,
                                       final QoSMessages messages) {
        super(eventBus, appearanceFactoryProvider, messages);
    }

    private void addToolbarToParentContainer(final Widget toolbar) {
        final VerticalLayoutData layoutData = new VerticalLayoutData(1, -1,
                new Margins(1, 5, 0, 5));
        mainContainer.add(toolbar, layoutData);
    }

    @Override
    protected boolean areUserFieldsValid() {
        return super.areUserFieldsValid() && getUpdateInterval() >= 0;
    }

    /**
     *
     * @return selected interval in seconds, 0 if "update" checkbox is not set,
     * or -1 if checkbox is set, but no interval is provided by user
     */
    private int getUpdateInterval() {
        if (!updateIntervalCheckbox.getValue()) {
            return 0;
        }
        else {
            int interval = 0;

            Integer min = minutesField.getValue();
            Integer sec = secondsField.getValue();

            if (min != null) {
                interval += min * TimeConstants.SECONDS_PER_MINUTE;
            }
            if (sec != null) {
                interval += sec;
            }

            if (interval == 0) {
                // no data provided by user
                return -1;
            }
            else {
                return interval;
            }
        }
    }

    private int getCapacity() {
            Integer capacity = capacityField.getValue();

            if (capacity != null && capacity != 0) {
                return capacity;
            } else {
                // no data provided by user
                return -1;
            }
    }

    private void createAndAddToolbarToParentContainer(
            final CssFloatLayoutContainer child) {
        final FramedPanel toolbar = new FramedPanel(
                appearanceFactory.lightFramedPanelAppearance());
        toolbar.setHeaderVisible(false);
        toolbar.setBodyBorder(false);
        toolbar.setBorders(false);
        toolbar.add(child);

        addToolbarToParentContainer(toolbar);
    }

    @Override
    protected void createWidget() {
        getUiHandlers().<AddBitrateToDashboarddWidgetPresenter> cast()
                .actionCreateWidget(getUpdateInterval(), getCapacity());
    }
    @Override
    protected int getDialogHeight() {
        return DIALOG_HEIGHT;
    }

    @Override
    protected int getDialogWidth() {
        return DEFAULT_DIALOG_WIDTH;
    }

    @Override
    protected String getDialogTitle() {
        return dialogTitle;
    }

    @Override
    public void initialize() {
        super.initialize();
        initializeChartTypeComboBox();
        initUpdateIntervalControls();
        initUpdateCapacityControl();
    }

    @Override
    public void setTitle(String taskName) {
        this.dialogTitle = messages.newWidget() + "(" + taskName + ")";
    }

    private void initializeChartTypeComboBox() {
        final ListStore<String> store = new ListStore<String>(
                new ModelKeyProvider<String>() {

                    @Override
                    public String getKey(final String item) {
                        return item;
                    }
                });
        store.add("Pie");

        typeControl = new ComboBox<String>(store,
                new LabelProvider<String>() {

                    @Override
                    public String getLabel(final String item) {
                        return messages.pieChart();
                    }
                });
        typeControl.setEditable(false);
        typeControl.setAllowBlank(false);
        typeControl.setTriggerAction(TriggerAction.ALL);

        typeControl.setValue("Pie", false, true);
        typeControl.getElement().setMargins(new Margins(6, 0, 2, 6));

        final CssFloatLayoutContainer topContainer = new CssFloatLayoutContainer();
        topContainer.getElement().setMargins(
                new Margins(0, 0, 4, 0));
        typeControl.setWidth(150);

        final Label label = new Label(messages.analyticsChartType() + ":");
        label.getElement().<XElement> cast()
                .setMargins(new Margins(8, 0, 4, 5));
        topContainer.add(label, new CssFloatData());

        topContainer.add(typeControl, new CssFloatData());

        createAndAddToolbarToParentContainer(topContainer);
    }

    private void initUpdateIntervalControls() {
        final CssFloatLayoutContainer layout = new CssFloatLayoutContainer();
        layout.getElement().setMargins(
                new Margins(0, 0, 4, 0));

        Label updateLabel = new Label(messages.updateEvery() + ":");
        Label minLabel = new Label(messages.minutesShort());
        Label secLabel = new Label(messages.secondsShort());

        updateIntervalCheckbox = new CheckBox();
        updateIntervalCheckbox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                boolean enabled = !updateIntervalCheckbox.getValue(); // value before change
                minutesField.setEnabled(enabled);
                secondsField.setEnabled(enabled);
            }
        });
        minutesField = new NumberField<Integer>(new IntegerPropertyEditor());
        minutesField.setWidth(25);
        secondsField = new NumberField<Integer>(new IntegerPropertyEditor());
        secondsField.setWidth(25);
        layout.add(updateIntervalCheckbox, new CssFloatData());
        layout.add(updateLabel, new CssFloatData());
        layout.add(minutesField, new CssFloatData());
        layout.add(minLabel, new CssFloatData());
        layout.add(secondsField, new CssFloatData());
        layout.add(secLabel, new CssFloatData());

        updateIntervalCheckbox.getElement().setMargins(new Margins(8, 0, 4, 5));
        minutesField.getElement().setMargins(new Margins(6, 0, 2, 6));
        secondsField.getElement().setMargins(new Margins(6, 0, 2, 6));

        updateLabel.getElement().<XElement> cast()
                .setMargins(new Margins(8, 4, 4, 2));
        minLabel.getElement().<XElement> cast()
                .setMargins(new Margins(8, 0, 4, 5));
        secLabel.getElement().<XElement> cast()
                .setMargins(new Margins(8, 0, 4, 5));

        minutesField.setEnabled(false);
        secondsField.setEnabled(false);

        createAndAddToolbarToParentContainer(layout);
    }

    private void initUpdateCapacityControl() {
        final CssFloatLayoutContainer layout = new CssFloatLayoutContainer();
        layout.getElement().setMargins(
                new Margins(0, 0, 4, 0));

        Label capacityLabel = new Label(messages.capacity() + ":");
        Label mBitSLabel = new Label(messages.mBitS());
        capacityField = new NumberField<Integer>(new IntegerPropertyEditor());
        capacityField.setWidth(45);

        layout.add(capacityLabel, new CssFloatData());
        layout.add(capacityField, new CssFloatData());
        layout.add(mBitSLabel, new CssFloatData());

        capacityLabel.getElement().<XElement> cast()
                .setMargins(new Margins(8, 0, 4, 5));
        mBitSLabel.getElement().<XElement> cast()
                .setMargins(new Margins(8, 0, 4, 5));
        capacityField.getElement().setMargins(new Margins(6, 0, 2, 6));

        createAndAddToolbarToParentContainer(layout);
    }
}
