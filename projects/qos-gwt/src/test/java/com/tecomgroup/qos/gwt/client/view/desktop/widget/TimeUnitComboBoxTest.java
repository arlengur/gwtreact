/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.user.client.ui.HasValue;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.TimeUnitComboBox.TimeUnits;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author abondin
 * 
 */
public class TimeUnitComboBoxTest {

	private HasValue<TimeUnits> timeUnitField;

	private HasValue<Double> durationField;

	@Before
	public void before() {
		timeUnitField = new SimpleValueContainer<TimeUnits>();
		durationField = new SimpleValueContainer<Double>();
	}

	@Test
	public void testSetDuration1Hours30Minutes() {
		final Double value = 2.0 * 60 * 60 + 30 * 60;
		TimeUnits.setDuration(timeUnitField, durationField, value);
		Assert.assertEquals(TimeUnits.HOURS, timeUnitField.getValue());
		Assert.assertEquals(Double.valueOf(2.5), durationField.getValue());
	}

	@Test
	public void testSetDuration2Hours() {
		final Double value = 2.0 * 60 * 60 + 30 * 60;
		TimeUnits.setDuration(timeUnitField, durationField, value);
		Assert.assertEquals(TimeUnits.HOURS, timeUnitField.getValue());
		Assert.assertEquals(Double.valueOf(2.5), durationField.getValue());
	}

	@Test
	public void testSetDuration2Minutes6sec() {
		final Double value = 2.0 * 60 + 6;
		TimeUnits.setDuration(timeUnitField, durationField, value);
		Assert.assertEquals(TimeUnits.MINUTES, timeUnitField.getValue());
		Assert.assertEquals(Double.valueOf(2.1), durationField.getValue());
	}
	@Test
	public void testSetDuration30Minutes() {
		final Double value = 30.0 * 60;
		TimeUnits.setDuration(timeUnitField, durationField, value);
		Assert.assertEquals(TimeUnits.MINUTES, timeUnitField.getValue());
		Assert.assertEquals(Double.valueOf(30.0), durationField.getValue());
	}
}
