/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.probestatus.MExportVideoEvent;
import com.tecomgroup.qos.domain.probestatus.MEventProperty;
import com.tecomgroup.qos.domain.probestatus.MProbeEvent;
import com.tecomgroup.qos.modelspace.jdbc.dao.probestatus.ProbeEventServiceDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by uvarov.m on 03.02.2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ProbeEventsJdbcTest {

	@Autowired
	protected ProbeEventServiceDao provider;

	protected MProbeEvent populateData(String salt, MExportVideoEvent.FIELD excludeField) {

		MProbeEvent event = new MProbeEvent();
		event.setAgentKey("testAgentKey" + salt);
		event.setEventType(MExportVideoEvent.class.getCanonicalName());
		event.setTimestamp(new Date());
		event.setUserLogin("testUser" + salt);
		event.setStatus(MProbeEvent.STATUS.IN_PROGRESS);
		event.setKey(UUID.randomUUID().toString());
		event.setCreatedTimestamp(new Date());
		event.setPropertyList(new ArrayList<MEventProperty>());

		for(MExportVideoEvent.FIELD field: MExportVideoEvent.FIELD.values()) {
			if(excludeField == field) {
				continue;
			}
			MEventProperty property = new MEventProperty();
			property.setKey(field.name());
			property.setValue(field.name() + salt);
			event.getPropertyList().add(property);
		}
		return event;
	}

	private void addAgentDisplayProperty(MProbeEvent event) {
		MEventProperty agentDisplayName = new MEventProperty(
				MProbeEvent.FIELD.AGENT_DISPLAY_NAME.name(),
				null);

		event.getPropertyList().add(agentDisplayName);
	}

	@Before
	public void setup() {
	}

	@Test
	public void testCreateProbeEvent() {
		MProbeEvent event = populateData("-create", MExportVideoEvent.FIELD.ERROR_CODE);
		String initialKey = event.getKey();

		Long eventId = provider.createProbeEvent(event);
		Assert.assertNotNull(eventId);

		List<MProbeEvent> foundEvents = provider.getEventsByKey(initialKey);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(1, foundEvents.size());

		MProbeEvent foundEvent = foundEvents.get(0);
		Assert.assertEquals(event, foundEvent);
		Assert.assertEquals(event.getPropertyList().size(), foundEvent.getPropertyList().size());

		for(MExportVideoEvent.FIELD field: MExportVideoEvent.FIELD.values()) {
			if(MExportVideoEvent.FIELD.ERROR_CODE == field) {
				continue;
			}
			Assert.assertNotNull(foundEvent.getProperty(field.name()));
		}
	}

	@Test
	public void testUpdateProbeEvent() {
		final String errorMessage = "something gone wrong";
		MProbeEvent event = populateData("-update", null);
		String initialKey = event.getKey();

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		MProbeEvent foundEvent = provider.getLastEventByKey(initialKey);
		Assert.assertNotNull(foundEvent);
		Assert.assertEquals(event, foundEvent);

		List<MEventProperty> props = new ArrayList<>();
		props.add(new MEventProperty(MExportVideoEvent.FIELD.ERROR_CODE.name(), errorMessage));
		provider.updateEvent(foundEvent, MProbeEvent.STATUS.OK, new Date(), props);

		MProbeEvent updatedEvent = provider.getLastEventByKey(initialKey);
		Assert.assertNotNull(updatedEvent);
		Assert.assertNotEquals(event, updatedEvent);
		Assert.assertEquals(MProbeEvent.STATUS.OK, updatedEvent.getStatus());

		Assert.assertEquals(errorMessage, updatedEvent.getPropertyValue(MExportVideoEvent.FIELD.ERROR_CODE.name()));
		Assert.assertEquals(
				event.getPropertyValue(MExportVideoEvent.FIELD.URL.name()),
				updatedEvent.getPropertyValue(MExportVideoEvent.FIELD.URL.name()));

	}

	@Test
	public void testGetEventsByAgent() {
		MProbeEvent event = populateData("-create", null);
		MProbeEvent event2 = populateData("-create", null);
		String agentKey = event.getAgentKey();

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByAgent(agentKey);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(2, foundEvents.size());

		MProbeEvent foundEvent = foundEvents.get(0);
		Assert.assertEquals(event, foundEvent);
		Assert.assertEquals(event.getPropertyList().size(), foundEvent.getPropertyList().size());

		foundEvent = foundEvents.get(1);
		Assert.assertEquals(event2, foundEvent);
		Assert.assertEquals(event2.getPropertyList().size(), foundEvent.getPropertyList().size());
	}

	@Test
	public void testGetEventsByUser() {
		MProbeEvent event = populateData("-create", null);
		MProbeEvent event2 = populateData("-create", null);
		String login = event.getUserLogin();

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByUser(login);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(2, foundEvents.size());

		MProbeEvent foundEvent = foundEvents.get(0);
		Assert.assertEquals(event, foundEvent);
		Assert.assertEquals(event.getPropertyList().size(), foundEvent.getPropertyList().size());

		foundEvent = foundEvents.get(1);
		Assert.assertEquals(event2, foundEvent);
		Assert.assertEquals(event2.getPropertyList().size(), foundEvent.getPropertyList().size());
	}

	@Test
	public void testGetEventsByUserAndType() {
		MProbeEvent event = populateData("-create", null);
		MProbeEvent event2 = populateData("-create", null);
		String login = event.getUserLogin();
		String type = event.getEventType();

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByUserAndType(login, type);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(2, foundEvents.size());

		MProbeEvent foundEvent = foundEvents.get(0);
		Assert.assertEquals(event, foundEvent);
		Assert.assertEquals(event.getPropertyList().size(), foundEvent.getPropertyList().size());

		foundEvent = foundEvents.get(1);
		Assert.assertEquals(event2, foundEvent);
		Assert.assertEquals(event2.getPropertyList().size(), foundEvent.getPropertyList().size());
	}

	@Test
	public void testGetEventsByProperty() {
		MProbeEvent event = populateData("-create", null);
		MProbeEvent event2 = populateData("-create", MExportVideoEvent.FIELD.URL);

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByProperty(
				event.getEventType(),
				MExportVideoEvent.FIELD.QUALITY.name(),
				event.getPropertyValue(MExportVideoEvent.FIELD.QUALITY.name()));
		Assert.assertEquals(2, foundEvents.size());

		MProbeEvent foundEvent = foundEvents.get(0);
		Assert.assertNotNull(foundEvent.getProperty(MExportVideoEvent.FIELD.QUALITY.name()));

		foundEvents = provider.getEventsByProperty(
				event.getEventType(),
				MExportVideoEvent.FIELD.URL.name(),
				event.getPropertyValue(MExportVideoEvent.FIELD.URL.name()));
		Assert.assertEquals(1, foundEvents.size());

		foundEvent = foundEvents.get(0);
		Assert.assertNotNull(foundEvent.getProperty(MExportVideoEvent.FIELD.URL.name()));
	}

	@Test
	public void testDeleteProbeEvent() {
		MProbeEvent event = populateData("-delete", null);
		MProbeEvent event2 = populateData("-delete", null);
		String initialKey = event.getKey();
		event2.setKey(initialKey);
		event2.setStatus(MProbeEvent.STATUS.OK);

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByKey(initialKey);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(2, foundEvents.size());

		Long removedId = provider.removeProbeEvent(eventId);
		Assert.assertNotNull(removedId);

		foundEvents = provider.getEventsByKey(initialKey);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(1, foundEvents.size());

		removedId = provider.removeProbeEvent(eventId2);
		Assert.assertNotNull(removedId);

		foundEvents = provider.getEventsByKey(initialKey);
		Assert.assertEquals(new ArrayList<>(), foundEvents);
	}

	@Test
	public void testDeleteProbeEventByKey() {
		MProbeEvent event = populateData("-delete", null);
		MProbeEvent event2 = populateData("-delete", null);
		String initialKey = event.getKey();
		event2.setKey(initialKey);
		event2.setStatus(MProbeEvent.STATUS.OK);

		Long eventId = provider.createProbeEvent(event);
		addAgentDisplayProperty(event);
		Assert.assertNotNull(eventId);

		Long eventId2 = provider.createProbeEvent(event2);
		addAgentDisplayProperty(event2);
		Assert.assertNotNull(eventId2);

		List<MProbeEvent> foundEvents = provider.getEventsByKey(initialKey);
		Assert.assertNotNull(foundEvents);
		Assert.assertEquals(2, foundEvents.size());

		provider.removeProbeEventByKey(initialKey);

		foundEvents = provider.getEventsByKey(initialKey);
		Assert.assertEquals(new ArrayList<>(), foundEvents);
	}
}
