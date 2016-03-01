/*
 * Copyright (C) 2015 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.recording.Event;
import com.tecomgroup.qos.domain.recording.Schedule;
import com.tecomgroup.qos.modelspace.jdbc.dao.recording.RecordingSchedulerServiceDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;

/**
 * Created by uvarov.m on 12.12.2015.
 */

@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RecordingSchedulerJdbcTest {

	@Autowired
	protected RecordingSchedulerServiceDao provider;

	protected Schedule populateData(String salt) {
		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd'T'HHmmss");
		Schedule schedule = new Schedule();
		schedule.setAgentKey("testAgentKey" + salt);
		schedule.setTaskKey("testTaskKey" + salt);
		schedule.setName("testSchedule" + salt);
		schedule.setEventList(new ArrayList<Event>());

		DateTime dateStart = DateTime.now();
		DateTime dateEnd = dateStart.plusMinutes(10);

		for(int i=0; i < 10; i++) {
			Event event = new Event();
			event.setComment("textComment");
			event.setStartDateTime(formatter.print(dateStart));
			event.setEndDateTime(formatter.print(dateEnd));
			dateStart = dateEnd;
			dateEnd = dateEnd.plusMinutes(10);
			schedule.getEventList().add(event);
		}
		return schedule;
	}

	@Before
	public void setup() {

	}

	@Test
	public void testCreateRecordingSchedule() {
		Schedule schedule = populateData("-create");

		Long scheduleId = provider.createSchedule(schedule);
		Assert.assertNotNull(scheduleId);

		Schedule found = provider.getSchedule(scheduleId);
		Assert.assertNotNull(found);
		Assert.assertNotNull(found.getId());
		Assert.assertEquals(schedule, found);
	}

	@Test
	public void testUpdateRecordingSchedule() {
		Schedule schedule = populateData("-update");

		Long scheduleId = provider.createSchedule(schedule);
		Assert.assertNotNull(scheduleId);

		Schedule found = provider.getSchedule(scheduleId);
		Assert.assertNotNull(found);
		Assert.assertNotNull(found.getId());
		Assert.assertEquals(schedule, found);

		found.setName("modified-name");
		found.setTimeZone("my-time-zone");
		found.getEventList().remove(0);
		found.getEventList().remove(1);

		Long updatedId = provider.updateSchedule(found);
		Schedule foundUpdated = provider.getSchedule(updatedId);
		Assert.assertNotNull(foundUpdated);
		Assert.assertNotNull(foundUpdated.getId());
		Assert.assertEquals(found, foundUpdated);
	}

	@Test
	public void testCreateRecordingScheduleForTasks() {
		Schedule schedule = populateData("-create");
		Map<String, String> map = new HashMap<>();
		map.put("task-1", "agent-1");
		map.put("task-2", "agent-1");
		map.put("task-3", "agent-2");
		map.put("task-4", "agent-2");

		Set<String> result = provider.createScheduleForTasks(schedule, map);
		Assert.assertEquals(2, result.size());
		Assert.assertTrue(result.contains("agent-1"));
		Assert.assertTrue(result.contains("agent-2"));

		Schedule foundA1T1 = provider.getScheduleByAgentAndTask("agent-1", "task-1");
		Assert.assertNotNull(foundA1T1);
		Assert.assertNotNull(foundA1T1.getId());
		Assert.assertNotEquals(schedule, foundA1T1);
		Assert.assertEquals(schedule.getEventList().size(), foundA1T1.getEventList().size());

		Schedule foundA1T2 = provider.getScheduleByAgentAndTask("agent-1", "task-2");
		Assert.assertNotNull(foundA1T2);
		Assert.assertNotNull(foundA1T2.getId());
		Assert.assertNotEquals(schedule, foundA1T2);
		Assert.assertEquals(schedule.getEventList().size(), foundA1T2.getEventList().size());

		Schedule foundA2T3 = provider.getScheduleByAgentAndTask("agent-2", "task-3");
		Assert.assertNotNull(foundA2T3);
		Assert.assertNotNull(foundA2T3.getId());
		Assert.assertNotEquals(schedule, foundA2T3);
		Assert.assertEquals(schedule.getEventList().size(), foundA2T3.getEventList().size());

		Schedule foundA2T4 = provider.getScheduleByAgentAndTask("agent-2", "task-4");
		Assert.assertNotNull(foundA2T4);
		Assert.assertNotNull(foundA2T4.getId());
		Assert.assertNotEquals(schedule, foundA2T4);
		Assert.assertEquals(schedule.getEventList().size(), foundA2T4.getEventList().size());
	}



}
