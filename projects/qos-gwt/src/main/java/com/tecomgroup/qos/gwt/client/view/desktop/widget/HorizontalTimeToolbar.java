/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.dom.XElement;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Composite;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.CssFloatLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;

/**
 * @author ivlev.e
 * 
 */
public class HorizontalTimeToolbar extends Composite {

	public interface UpdateButtonHandler {
		void onUpdateButtonPressed(SelectEvent event);
	}

	interface ViewUiBinder extends UiBinder<Widget, HorizontalTimeToolbar> {
	}

	protected Label syncViewLabel;

	@UiField
	protected CssFloatLayoutContainer timeIntervalToolbarContainer;

	@UiField(provided = true)
	protected TextButton firstTimeButton;

	@UiField(provided = true)
	protected TextButton secondTimeButton;

	@UiField(provided = true)
	protected TextButton thirdTimeButton;

	@UiField(provided = true)
	protected TextButton otherIntervalButton;

	@UiField(provided = true)
	protected DateTimeWidget startDateControl;

	@UiField(provided = true)
	protected DateTimeWidget startTimeControl;

	@UiField(provided = true)
	protected DateTimeWidget endDateControl;

	@UiField(provided = true)
	protected DateTimeWidget endTimeControl;

	@UiField(provided = true)
	protected ComboBox<TimeZoneWrapper> timeZoneControl;

	@UiField(provided = true)
	protected TextButton updateButton;

	@UiField
	protected CssFloatLayoutContainer syncControlContainer;

	protected CheckBox disabledButton;

	@UiField
	protected Label timeDash;

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	protected final DateTimeIntervalWidget dateTimeIntervalWidget;

	protected final AppearanceFactory appearanceFactory;

	protected final QoSMessages messages;

	private UpdateButtonHandler updateButtonHandler;

	@Inject
	public HorizontalTimeToolbar(
			final DateTimeIntervalWidget dateTimeIntervalWidget,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final QoSMessages messages) {
		super();
		this.dateTimeIntervalWidget = dateTimeIntervalWidget;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;
		initialize();
	}

	private void configure() {
		updateButton.setIcon(appearanceFactory.resources().updateButton());
		timeZoneControl.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);

		setMargins(firstTimeButton.getElement(), new Margins(9, 5, 9, 5));
		setToolbarStandardMargins(secondTimeButton.getElement());
		setToolbarStandardMargins(thirdTimeButton.getElement());
		setToolbarStandardMargins(otherIntervalButton.getElement());

		final Margins margins = new Margins(9, 5, 0, 9);
		startDateControl.setMargins(margins);
		startTimeControl.setMargins(margins);
		endDateControl.setMargins(margins);
		endTimeControl.setMargins(margins);
		setToolbarStandardMargins(timeZoneControl.getElement());
		setToolbarStandardMargins(updateButton.getElement());
		setToolbarBigMargins(timeDash.getElement().<XElement> cast());

		timeDash.addStyleName(appearanceFactory.resources().css().timeDash());
	}

	private void createSyncControl() {
		if (disabledButton == null) {

			disabledButton = new CheckBox();
			disabledButton
					.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

						@Override
						public void onValueChange(
								final ValueChangeEvent<Boolean> event) {
							setEnabled(event.getValue());
						}
					});
			setMargins(disabledButton.getElement(), new Margins(10, 0, 0, 1));
			disabledButton.getElement().setWidth(22);
			createSyncViewLabel();
			syncControlContainer.insert(syncViewLabel, 0);
			syncControlContainer.insert(disabledButton, 0);
		}
	}
	private void createSyncViewLabel() {
		syncViewLabel = new Label(messages.syncView());

		syncViewLabel.getElement().<XElement> cast()
				.setMargins(new Margins(8, 0, 0, 0));
		syncViewLabel.getElement().<XElement> cast().setWidth(200);
		syncViewLabel.getElement().getStyle()
				.setTextAlign(Style.TextAlign.LEFT);
		syncViewLabel.getElement().getStyle()
				.setProperty("wordWrap", "break-word");
	}

	@UiHandler(value = {"firstTimeButton", "secondTimeButton",
			"thirdTimeButton"})
	protected void customIntervalDeselected(final SelectEvent e) {
		dateTimeIntervalWidget.disableCustomTimeInterval();
	}

	@UiHandler("otherIntervalButton")
	protected void customIntervalSelected(final SelectEvent e) {
		dateTimeIntervalWidget.enableCustomTimeInterval();
	}

	private TextButton getButtonByInterval(final Type intervalType) {
		TextButton result = null;
		switch (intervalType) {
			case FIFTEEN_MINUTES :
				result = dateTimeIntervalWidget.getLastFifteenMinutesButton();
				break;
			case HOUR :
				result = dateTimeIntervalWidget.getLastHourButton();
				break;
			case DAY :
				result = dateTimeIntervalWidget.getLastDayButton();
				break;
			case WEEK :
				result = dateTimeIntervalWidget.getLastWeekButton();
				break;
			case MONTH :
				result = dateTimeIntervalWidget.getLastMonthButton();
				break;
			default :
				break;
		}
		return result;
	}

	public CssFloatLayoutContainer getContainer() {
		return timeIntervalToolbarContainer;
	}

	/**
	 * @return the dateTimeIntervalWidget
	 */
	public DateTimeIntervalWidget getDateTimeIntervalWidget() {
		return dateTimeIntervalWidget;
	}

	protected void initialize() {
		startDateControl = dateTimeIntervalWidget.getStartDateControl();
		startTimeControl = dateTimeIntervalWidget.getStartTimeControl();
		endDateControl = dateTimeIntervalWidget.getEndDateControl();
		endTimeControl = dateTimeIntervalWidget.getEndTimeControl();
		timeZoneControl = dateTimeIntervalWidget.getTimeZoneControl();
		updateButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellLightAppearance()),
				messages.update());
	}

	public boolean isToolbarEnabled() {
		return disabledButton != null && disabledButton.getValue();
	}

	@UiHandler("updateButton")
	protected void onUpdateButtonSelect(final SelectEvent e) {
		if (updateButtonHandler != null) {
			updateButtonHandler.onUpdateButtonPressed(e);
		}
	}

	public void setDisablable(final boolean disablable) {
		if (disablable) {
			createSyncControl();
			syncControlContainer.show();
		} else {
			syncControlContainer.hide();
		}
	}

	@Override
	public void setEnabled(final boolean enabled) {
		if (Type.CUSTOM.equals(dateTimeIntervalWidget.getTimeIntervalType())) {
			if (enabled) {
				dateTimeIntervalWidget.enableCustomTimeInterval();
			} else {
				dateTimeIntervalWidget.disableCustomTimeInterval();
			}
		}
		dateTimeIntervalWidget.getTimeZoneControl().setEnabled(enabled);
		firstTimeButton.setEnabled(enabled);
		secondTimeButton.setEnabled(enabled);
		thirdTimeButton.setEnabled(enabled);
		otherIntervalButton.setEnabled(enabled);
	}

	private void setMargins(final XElement element, final Margins margins) {
		element.setMargins(margins);
	}

	private void setToolbarBigMargins(final XElement element) {
		setMargins(element, new Margins(9, 10, 9, 8));
	}

	public void setToolbarEnabled(final boolean enabled) {
		if (disabledButton != null) {
			disabledButton.setValue(enabled, true);
		}
	}

	private void setToolbarStandardMargins(final XElement element) {
		setMargins(element, new Margins(9, 5, 9, 0));
	}

	public void setup(final Type firstInterval, final Type secondInterval,
			final Type thirdInterval) {
		if (getWidget() != null) {
			throw new IllegalStateException("setup may only be "
					+ "called once.");
		}

		firstTimeButton = getButtonByInterval(firstInterval);
		secondTimeButton = getButtonByInterval(secondInterval);
		thirdTimeButton = getButtonByInterval(thirdInterval);
		otherIntervalButton = dateTimeIntervalWidget.getOtherIntervalButton();

		// only first initialization of the dateTimeIntervalWidget
		dateTimeIntervalWidget.selectTimeInterval(firstTimeButton, false);
		dateTimeIntervalWidget.setDefaultTimeInterval(firstInterval);
		initWidget(UI_BINDER.createAndBindUi(this));
		configure();
	}

	public void setUpdateButtonHandler(final UpdateButtonHandler handler) {
		updateButtonHandler = handler;
	}
}
