/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Arrays;

import com.google.gwt.user.client.ui.HasValue;
import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.widget.core.client.form.SimpleComboBox;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.TimeUnitComboBox.TimeUnits;

/**
 * @author ivlev.e
 * 
 */
public class TimeUnitComboBox extends SimpleComboBox<TimeUnits> {

	public static enum TimeUnits {
		SECONDS, MINUTES, HOURS;

		/**
		 * Returns time in seconds
		 * 
		 * @param field
		 * @param timeUnit
		 * @return
		 */
		public static Long getDuration(final HasValue<TimeUnits> timeUnitField,
				final HasValue<Double> durationField) {
			Double value = durationField.getValue();
			if (value == null) {
				return 0l;
			}
			switch (timeUnitField.getValue()) {
				case HOURS :
					value *= TimeConstants.SECONDS_PER_HOUR;
					break;
				case MINUTES :
					value *= TimeConstants.SECONDS_PER_MINUTE;
					break;
				default :
					break;
			}
			return value.longValue();
		}

		/**
		 * Updates timeUnitField and durationField in accordicance with given
		 * value
		 * 
		 * @param durationField
		 */
		public static void setDuration(final HasValue<TimeUnits> timeUnitField,
				final HasValue<Double> durationField, final Double value) {
			if (value == null) {
				durationField.setValue(value, true);
				timeUnitField.setValue(TimeUnits.SECONDS, true);
			} else if (value >= TimeConstants.SECONDS_PER_HOUR) {
				timeUnitField.setValue(TimeUnits.HOURS);
				durationField.setValue(value / TimeConstants.SECONDS_PER_HOUR);
			} else if (value >= TimeConstants.SECONDS_PER_MINUTE) {
				timeUnitField.setValue(TimeUnits.MINUTES);
				durationField
						.setValue(value / TimeConstants.SECONDS_PER_MINUTE);
			} else {
				timeUnitField.setValue(TimeUnits.SECONDS);
				durationField.setValue(value);
			}
		}

	}

	public TimeUnitComboBox(final QoSMessages messages) {
		super(new LabelProvider<TimeUnits>() {
			@Override
			public String getLabel(final TimeUnits item) {
				String label;
				switch (item) {
					case HOURS :
						label = messages == null ? "hours" : messages
								.hoursShort();
						break;
					case MINUTES :
						label = messages == null ? "minutes" : messages
								.minutesShort();
						break;
					case SECONDS :
						label = messages == null ? "seconds" : messages
								.secondsShort();
						break;
					default :
						label = item.toString();
						break;
				}
				return label;
			}
		});
		setAllowBlank(false);
		setForceSelection(true);
		setTypeAhead(false);
		setTriggerAction(TriggerAction.ALL);
		setEditable(false);
		setWidth(60);
		add(Arrays.asList(TimeUnits.values()));
	}

	public Long getDuration(final HasValue<Double> durationField) {
		return TimeUnits.getDuration(this, durationField);
	}

	@Override
	public void reset() {
		super.reset();
	}

	/**
	 * 
	 * @param durationField
	 */
	public void setDuration(final HasValue<Double> durationField,
			final Double value) {
		TimeUnits.setDuration(this, durationField, value);
	}

}
