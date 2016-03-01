/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.util.MediaModelConfiguration;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class RecordedVideoTemplateTest extends MediaTemplateTest {

	private static final String STREAM_TEMPLATE_DEFAULT_NAME = "testRecordedStreamTemplate";

	private static final MediaTemplateType TEMPLATE_TYPE = MediaTemplateType.RECORDED_VIDEO;

	private static final Class<? extends MStreamWrapper> CHILDREN_TYPE = MRecordedStreamWrapper.class;

	@Override
	protected Class<? extends MAbstractEntity> getChildrenType() {
		return CHILDREN_TYPE;
	}

	@Override
	protected MStreamWrapper getStreamWrapperInstance(final MAgent agent,
			final MAgentTask task) {
		return MediaModelConfiguration.createRecordedStreamWrapper(agent, task);
	}

	@Override
	protected MUserAbstractTemplate getTemplateInstance(
			final String templateName, final MAgent agent,
			final MAgentTask task, final int wrappersCount) {
		return MediaModelConfiguration.createRecordedStreamTemplate(
				templateName, agent, task, wrappersCount);
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	@Test
	public void testClearTemplatesWhenDeleteAgent() {
		testClearTemplatesWhenDeleteAgent(STREAM_TEMPLATE_DEFAULT_NAME,
				MRecordedStreamTemplate.class);
	}

	@Test
	public void testClearTemplatesWhenDeleteTasks() {
		testClearTemplatesWhenDeleteTasks(STREAM_TEMPLATE_DEFAULT_NAME,
				MRecordedStreamTemplate.class);
	}

	@Test
	public void testDeleteTemplatesWhenDeleteAgent() {
		testDeleteTemplatesWhenDeleteAgent(STREAM_TEMPLATE_DEFAULT_NAME,
				MRecordedStreamTemplate.class);
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTask() {
		testDeleteTemplatesWhenDeleteTask(STREAM_TEMPLATE_DEFAULT_NAME,
				MRecordedStreamTemplate.class);
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTasks() {
		testDeleteTemplatesWhenDeleteTasks(STREAM_TEMPLATE_DEFAULT_NAME,
				MRecordedStreamTemplate.class);
	}

	@Test
	public void testEmptyTemplate() {
		testEmptyTemplate("testEmptyLiveVideoTemplate");
	}

	@Test
	public void testSaveTemplateFromExistToExist() {
		final String templateNamePrefix = "testRecordedVideoSaveTemplateFromExistToExist";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToExist(nameX, nameY);
	}

	@Test
	public void testSaveTemplateFromExistToNew() {
		final String templateNamePrefix = "testRecordedVideoSaveTemplateFromExistToNew";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToNew(nameX, nameY);
	}

	@Test
	public void testTemplate() {
		testTemplate(STREAM_TEMPLATE_DEFAULT_NAME);
	}

	@Test
	public void testTemplateUpdate() {
		testTemplateUpdate("testRecordedVideoTemplateUpdate");
	}

}
