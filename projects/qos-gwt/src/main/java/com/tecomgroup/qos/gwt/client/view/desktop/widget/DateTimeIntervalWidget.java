/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.event.logical.shared.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.inject.Inject;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.core.client.resources.CommonStyles;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;

import javax.validation.ValidationException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @author ivlev.e
 * 
 */
public class DateTimeIntervalWidget extends Composite
		implements
			HasValueChangeHandlers<TimeInterval> {

	protected static final int DEFAULT_FIELD_WIDTH = 100;

	private TextButton lastFifteenMitutesButton;

	private TextButton lastHourButton;

	private TextButton lastDayButton;

	private TextButton lastWeekButton;

	private TextButton lastMonthButton;

	private TextButton otherIntervalButton;

	private TextButton selectedTimeControl;

	private DateTimeWidget startDateControl;

	private DateTimeWidget startTimeControl;

	private DateTimeWidget endDateControl;

	private DateTimeWidget endTimeControl;

	private CustomComboBox<TimeZoneWrapper> timeZoneControl;

	private final QoSMessages messages;

	private final AppearanceFactory appearanceFactory;

	private final Map<String, TimeZoneWrapper> timeZoneWrappers;

	private static Logger LOGGER = Logger
			.getLogger(DateTimeIntervalWidget.class.getName());

	@Inject
	public DateTimeIntervalWidget(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		timeZoneWrappers = new LinkedHashMap<String, TimeZoneWrapper>();
		initializeUI();
		initializeListeners();
		initializeTimeZones();
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<TimeInterval> handler) {
		return addHandler(handler, ValueChangeEvent.getType());
	}

	public void applyDemoMode(final Type permissibleIntervalType) {
		final Map<Type, TextButton> intervalTypeMap = new HashMap<Type, TextButton>();
		final String newDisabledStyleName = appearanceFactory.resources().css()
				.disabledLinkButton();
		final String oldDisabledStyleName = CommonStyles.get().disabled();

		intervalTypeMap.put(Type.FIFTEEN_MINUTES, lastFifteenMitutesButton);
		intervalTypeMap.put(Type.HOUR, lastHourButton);
		intervalTypeMap.put(Type.WEEK, lastWeekButton);
		intervalTypeMap.put(Type.MONTH, lastMonthButton);
		intervalTypeMap.put(Type.DAY, lastDayButton);
		intervalTypeMap.put(Type.CUSTOM, otherIntervalButton);

		final TextButton intervalButtonForDemoMode = intervalTypeMap
				.get(permissibleIntervalType);
		for (final Map.Entry<Type, TextButton> entry : intervalTypeMap
				.entrySet()) {
			if (entry.getKey() != permissibleIntervalType) {
				final TextButton button = entry.getValue();

				button.setEnabled(false);
				button.addStyleName(newDisabledStyleName);
				button.removeStyleName(oldDisabledStyleName);
			}
		}

		selectTimeInterval(intervalButtonForDemoMode);
		if (permissibleIntervalType != Type.CUSTOM) {
			disableCustomTimeInterval();
		}
	}

	private void deselectAll() {
		final String selected = appearanceFactory.resources().css().selected();
		lastFifteenMitutesButton.removeStyleName(selected);
		lastHourButton.removeStyleName(selected);
		lastDayButton.removeStyleName(selected);
		lastWeekButton.removeStyleName(selected);
		lastMonthButton.removeStyleName(selected);
		otherIntervalButton.removeStyleName(selected);
	}

	public void disableCustomTimeInterval() {
		disableCustomTimeInterval(true);
	}

	private void disableCustomTimeInterval(final boolean disabeIfSelected) {
		if ((selectedTimeControl != otherIntervalButton) || disabeIfSelected) {
			startDateControl.setEnabled(false);
			startTimeControl.setEnabled(false);
			endDateControl.setEnabled(false);
			endTimeControl.setEnabled(false);
		}
	}

	/**
	 * Adds agent time zone in time zone control at the beginning of the list
	 * and sets it as a current value.
	 */
	public void enableAgentTimeZone(final String timeZoneId) {
		disableAgentTimeZone();
		TimeZoneWrapper wrapper=timeZoneWrappers.get(timeZoneId);
		// Case special to determinate errors like in Bug qos-7525
		if (wrapper == null) {
			LOGGER.warning("Unable to determinate probe time zone. Please check time zone spelling");
			AppUtils.showInfoMessage(messages.invalidTimeZone());
		} else {
			final TimeZoneWrapper agentTimeZoneWrapper = new TimeZoneWrapper(
					timeZoneId, messages.timeAgent(), wrapper.getOffset());
			timeZoneWrappers.put(messages.timeAgent(), agentTimeZoneWrapper);
			timeZoneControl.getStore().add(0, agentTimeZoneWrapper);
		}
	}

	public void disableAgentTimeZone() {
		final TimeZoneWrapper agentTimeZoneWrapper = timeZoneControl.getStore().get(0);
		if (agentTimeZoneWrapper.getTimeZoneLabel().equals(messages.timeAgent())) {
			timeZoneWrappers.remove(agentTimeZoneWrapper);
			timeZoneControl.getStore().remove(agentTimeZoneWrapper);
			timeZoneControl.redraw();
			timeZoneControl.setValue(DateUtils.createLocalTimeZone());
		}
	}

	public void enableCustomTimeInterval() {
		startDateControl.setEnabled(true);
		startTimeControl.setEnabled(true);
		endDateControl.setEnabled(true);
		endTimeControl.setEnabled(true);
	}

	/**
	 * Fires {@link ValueChangeEvent} if current TimeInteval is valid
	 */
	private void fireValueChangeIfValid() {
		final TimeInterval newTimeInterval = getTimeInterval();
		if (newTimeInterval.isValid()) {
			ValueChangeEvent.fire(DateTimeIntervalWidget.this, newTimeInterval);
		}
	}

	/**
	 * Gets end date and time directly from controls.
	 * 
	 * @return
	 */
	private Date getCustomEndDateTime() {
		Date result = null;

		final Date endDate = endDateControl.getValue();
		final Date endTime = endTimeControl.getValue();
		if (endDate != null && endTime != null) {
			StringBuilder dateTime = new StringBuilder().append(startDateControl.getStringValue(endDate))
														.append(" ")
														.append(startTimeControl.getStringValue(endTime));
			result = DateTimeWidget.parseFullDate(dateTime.toString(), getTimeZoneOffset());
		}
		return result;
	}

	/**
	 * Gets start date and time directly from controls.
	 * 
	 * @return
	 */
	private Date getCustomStartDateTime() {
		Date result = null;

		final Date startDate = startDateControl.getValue();
		final Date startTime = startTimeControl.getValue();
		if (startDate != null && startTime != null) {
			StringBuilder dateTime = new StringBuilder().append(startDateControl.getStringValue(startDate))
														.append(" ")
														.append(startTimeControl.getStringValue(startTime));
			result = DateTimeWidget.parseFullDate(dateTime.toString(), getTimeZoneOffset());
		}
		return result;
	}

	/**
	 * Gets effective end date taking into account selected
	 * {@link TimeInterval.Type}.
	 * 
	 * <b> IMPORTANT: TimeZone is not supported </b>
	 * 
	 * This method is deprecated because of lack of time zone support.
	 * 
	 * Use {@link DateTimeIntervalWidget#getTimeInterval()} instead of this
	 * method to get exact timeInterval with time zone support.
	 * 
	 * @return
	 */
	@Deprecated
	public Date getEndDate() {
		Date endDate = null;
		final Type intervalType = getTimeIntervalType();
		if (Type.CUSTOM.equals(intervalType)) {
			endDate = getCustomEndDateTime();
		}
		return TimeInterval.getEndDate(endDate, intervalType);
	}

	/**
	 * @return the endDateControl
	 */
	public DateTimeWidget getEndDateControl() {
		return endDateControl;
	}

	/**
	 * @return the endTimeControl
	 */
	public DateTimeWidget getEndTimeControl() {
		return endTimeControl;
	}

	public TextButton getLastDayButton() {
		return lastDayButton;
	}

	public TextButton getLastFifteenMinutesButton() {
		return lastFifteenMitutesButton;
	}

	public TextButton getLastHourButton() {
		return lastHourButton;
	}

	public TextButton getLastMonthButton() {
		return lastMonthButton;
	}

	public TextButton getLastWeekButton() {
		return lastWeekButton;
	}

	public TextButton getOtherIntervalButton() {
		return otherIntervalButton;
	}

	public TextButton getSelectedTimeControl() {
		return selectedTimeControl;
	}

	/**
	 * Gets effective start date taking into account selected
	 * {@link TimeInterval.Type}.
	 * 
	 * This method is depricated because of mistiming between
	 * {@link DateTimeIntervalWidget#getStartDate()} and
	 * {@link DateTimeIntervalWidget#getEndDate()}.
	 * 
	 * 1) Use {@link DateTimeIntervalWidget#getTimeInterval()} instead of this
	 * method to get exact timeInterval.
	 * 
	 * 2) Use {@link DateTimeIntervalWidget#getStartDate(Date)} instead of this
	 * method to synchronize start with end date manually.
	 * 
	 * <b> IMPORTANT: TimeZone is not supported </b>
	 * 
	 * @return
	 */
	@Deprecated
	public Date getStartDate() {
		return getStartDate(new Date());

	}

	/**
	 * Gets effective start date synchronized with end date taking into account
	 * selected {@link TimeInterval.Type}.
	 * 
	 * <b> IMPORTANT: TimeZone is not supported </b>
	 * 
	 * This method is depricated because of lack of time zone support.
	 * 
	 * Use {@link DateTimeIntervalWidget#getTimeInterval()} instead of this
	 * method to get exact timeInterval with time zone support.
	 * 
	 * @param endDateTime
	 * 
	 * @return startDateTime
	 */
	@Deprecated
	public Date getStartDate(final Date endDateTime) {
		return getStartDate(endDateTime, getTimeIntervalType());
	}

	private Date getStartDate(final Date endDateTime, final Type intervalType) {
		Date startDate = null;
		if (Type.CUSTOM.equals(intervalType)) {
			startDate = getCustomStartDateTime();
		}

		return TimeInterval.getStartDate(startDate, endDateTime, intervalType);
	}

	/**
	 * @return the startDateControl
	 */
	public DateTimeWidget getStartDateControl() {
		return startDateControl;
	}

	/**
	 * @return the startTimeControl
	 */
	public DateTimeWidget getStartTimeControl() {
		return startTimeControl;
	}

    /**
     * Gets current {@link TimeInterval}.
     *
     * @return {@link TimeInterval}
     */
    public TimeInterval getTimeInterval() {
        Date endDateTime = getEndDate();
        Date startDateTime = getStartDate(endDateTime);
		return TimeInterval.get(getTimeIntervalType(), startDateTime,
                endDateTime, getTimeZoneType(), getTimeZone(),
                DateUtils.getCurrentTimeZoneAsString());
    }

	/**
	 * @return selected {@link TimeInterval.Type}.
	 */
	public Type getTimeIntervalType() {
		Type type = Type.CUSTOM;
		if (selectedTimeControl != null) {
			if (lastFifteenMitutesButton.equals(selectedTimeControl)) {
				type = Type.FIFTEEN_MINUTES;
			} else if (lastHourButton.equals(selectedTimeControl)) {
				type = Type.HOUR;
			} else if (lastDayButton.equals(selectedTimeControl)) {
				type = Type.DAY;
			} else if (lastWeekButton.equals(selectedTimeControl)) {
				type = Type.WEEK;
			} else if (lastMonthButton.equals(selectedTimeControl)) {
				type = Type.MONTH;
			}
		}
		return type;
	}

    /**
     * Gets current time zone. Agent time zone is not supported.
     *
     * @return time zone or empty string in case of agent time zone.
     */
    public String getTimeZone() {
        return timeZoneControl.getValue().getTimeZoneId();
    }

    /**
     * @return the timeZoneOffset
     */
    public int getTimeZoneOffset() {
        return timeZoneControl.getValue().getOffset() / 1000 / 60;
    }

	/**
	 * @return the timeZoneControl
	 */
	public ComboBox<TimeZoneWrapper> getTimeZoneControl() {
		return timeZoneControl;
	}

	/**
	 * Gets current time zone label from control. For local time it returns
	 * local timezone ID
	 * 
	 * @return
	 */
	public String getTimeZoneLabel() {
		String label = timeZoneControl.getValue().getTimeZoneLabel();
		if (label.equals(messages.timeLocal())) {
			label = timeZoneControl.getValue().getTimeZoneId();
		}
		return label;
	}

	public TimeZoneType getTimeZoneType() {
		TimeZoneType result = TimeZoneType.CUSTOM;

		final String selectedTimeZoneLabel = timeZoneControl.getValue()
				.getTimeZoneLabel();
		if (selectedTimeZoneLabel.equals(messages.timeLocal())) {
			result = TimeZoneType.LOCAL;
		} else if (selectedTimeZoneLabel.equals(messages.timeAgent())) {
			result = TimeZoneType.AGENT;
		}
		return result;
	}

	private void initializeListeners() {
		final SelectHandler selectHandler = new SelectHandler() {
			@Override
			public void onSelect(final SelectEvent event) {
				selectTimeInterval((TextButton) event.getSource());
				fireValueChangeIfValid();
			}
		};

		lastFifteenMitutesButton.addSelectHandler(selectHandler);
		lastHourButton.addSelectHandler(selectHandler);
		lastDayButton.addSelectHandler(selectHandler);
		lastWeekButton.addSelectHandler(selectHandler);
		lastMonthButton.addSelectHandler(selectHandler);
		otherIntervalButton.addSelectHandler(selectHandler);

		timeZoneControl
				.addSelectionHandler(new SelectionHandler<TimeZoneWrapper>() {
					@Override
					public void onSelection(
							final SelectionEvent<TimeZoneWrapper> event) {
						fireValueChangeIfValid();
					}
				});
	}

	private void initializeTimeZones() {
		final TimeZoneWrapper localTimeZoneWrapper = DateUtils
				.createLocalTimeZone();

		timeZoneWrappers.put(localTimeZoneWrapper.getTimeZoneId(),
				localTimeZoneWrapper);
		timeZoneWrappers.putAll(DateUtils.getServerTimeZones());

		timeZoneControl.getStore().addAll(timeZoneWrappers.values());
		timeZoneControl.setValue(localTimeZoneWrapper, true, true);
	}

	private void initializeUI() {

		lastFifteenMitutesButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalFifteenMinutes());

		lastHourButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalHour());

		lastDayButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalDay());

		lastWeekButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalWeek());

		lastMonthButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalMonth());

		otherIntervalButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHyperlinkAppearance()),
				messages.timeIntervalOther());

		startDateControl = new DateTimeWidget(DateTimeWidget.Mode.DATE);
		startDateControl.setWidth(DEFAULT_FIELD_WIDTH);

		endDateControl = new DateTimeWidget(DateTimeWidget.Mode.DATE);
		endDateControl.setWidth(DEFAULT_FIELD_WIDTH);

		startTimeControl = new DateTimeWidget(DateTimeWidget.Mode.TIME);
		startTimeControl.setWidth(DEFAULT_FIELD_WIDTH);

		endTimeControl = new DateTimeWidget(DateTimeWidget.Mode.TIME);
		endTimeControl.setWidth(DEFAULT_FIELD_WIDTH);

		timeZoneControl = new CustomComboBox<TimeZoneWrapper>(
				new ListStore<TimeZoneWrapper>(
						new ModelKeyProvider<TimeZoneWrapper>() {

							@Override
							public String getKey(final TimeZoneWrapper item) {
								return item.getTimeZoneId();
							}
						}), new LabelProvider<TimeZoneWrapper>() {

					@Override
					public String getLabel(final TimeZoneWrapper item) {
						return item.getTimeZoneLabel();
					}
				}, appearanceFactory.triggerFieldAppearance());
		timeZoneControl.setWidth(DEFAULT_FIELD_WIDTH);
		timeZoneControl.setAllowBlank(false);
		timeZoneControl.setForceSelection(true);
		timeZoneControl.setTypeAhead(true);
		timeZoneControl.setTriggerAction(TriggerAction.ALL);
		timeZoneControl.setEditable(true);
		timeZoneControl.setUpdateValueOnSelection(true);
	}

	public boolean isValid() {
		return getTimeInterval().isValid();
	}

	public void selectTimeInterval(final TextButton selection) {
		selectTimeInterval(selection, true);
	}

	public void selectTimeInterval(final TextButton selection,
			final boolean overrideExistingTimeInterval) {
		if ((this.selectedTimeControl == null) || overrideExistingTimeInterval) {
			this.selectedTimeControl = selection;
			deselectAll();
			selection.addStyleName(appearanceFactory.resources().css()
					.selected());
		}
	}

	/**
	 * Sets start and end dates taking into account selected
	 * {@link TimeInterval.Type}.
	 * 
	 * @return
	 */
	public void setDefaultTimeInterval(final Type timeIntervalType) {
		final Date endDateTime = new Date();
		setTimeInterval(getStartDate(endDateTime, timeIntervalType),
				endDateTime);
	}

	public void setTimeInterval(final Date start, final Date end) {
		startDateControl.setValue(start);
		endDateControl.setValue(end);
		startTimeControl.setValue(start);
		endTimeControl.setValue(end);
	}

	public void setTimeIntervalType(final Type timeIntervalType) {
		switch (timeIntervalType) {
			case FIFTEEN_MINUTES :
				selectTimeInterval(lastFifteenMitutesButton);
				break;
			case HOUR :
				selectTimeInterval(lastHourButton);
				break;
			case DAY :
				selectTimeInterval(lastDayButton);
				break;
			case WEEK :
				selectTimeInterval(lastWeekButton);
				break;
			case MONTH :
				selectTimeInterval(lastMonthButton);
				break;
			case CUSTOM :
				selectTimeInterval(otherIntervalButton);
				break;
			default :
				throw new UnsupportedOperationException("Type "
						+ timeIntervalType + "is not supported");
		}

		if (timeIntervalType == Type.CUSTOM) {
			enableCustomTimeInterval();
		} else {
			disableCustomTimeInterval();
		}
	}

	public void setTimeZone(final TimeZoneType timeZoneType,
			final String timeZone) {
		switch (timeZoneType) {
			case LOCAL :
				timeZoneControl.setValue(timeZoneWrappers.get(DateUtils
						.getCurrentTimeZoneAsString()));
				break;
			case AGENT :
				timeZoneControl.setValue(timeZoneWrappers.get(messages
						.timeAgent()));
				break;
			case CUSTOM :
				timeZoneControl.setValue(timeZoneWrappers.get(timeZone));
				break;
			default :
				break;
		}
	}

	/**
	 * Sets equal width to all time controls
	 * 
	 * @param width
	 */
	public void setWidthToAll(final int width) {
		startDateControl.setWidth(width);
		startTimeControl.setWidth(width);
		endDateControl.setWidth(width);
		endTimeControl.setWidth(width);
		timeZoneControl.setWidth(width);
	}

	public void validate() throws ValidationException {
		if (!isValid()) {
			String errorMessage = "";
			final DateTimeFormat dateFormat = DateTimeWidget
					.getCurrentLocaleDateFormat();
			final DateTimeFormat timeFormat = DateTimeWidget
					.getCurrentLocaleTimeFormat();

			if (startDateControl.getValue() == null) {
				errorMessage += messages.invalidDate(
						startDateControl.getRawValue(),
						dateFormat.getPattern(), dateFormat.format(new Date()))
						+ "\n";
			}

			if (endDateControl.getValue() == null) {
				errorMessage += messages.invalidDate(
						endDateControl.getRawValue(), dateFormat.getPattern(),
						dateFormat.format(new Date())) + "\n";
			}

			if (startTimeControl.getValue() == null) {
				errorMessage += messages.invalidTime(
						startTimeControl.getRawValue(),
						timeFormat.getPattern(), timeFormat.format(new Date()))
						+ "\n";
			}

			if (endTimeControl.getValue() == null) {
				errorMessage += messages.invalidTime(
						endTimeControl.getRawValue(), timeFormat.getPattern(),
						timeFormat.format(new Date())) + "\n";
			}

			throw new ValidationException(errorMessage);
		}
	}
}
