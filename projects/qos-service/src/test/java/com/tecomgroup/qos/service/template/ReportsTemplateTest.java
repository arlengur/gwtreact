/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserReportsTemplate;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ReportsTemplateTest extends TemplateTest {

	private static final String DEFAULT_TEMPLATE_NAME = "testReportTemplate";
	private static final TemplateType TEMPLATE_TYPE = BaseTemplateType.REPORT;

	@Override
	protected MUserAbstractTemplate getNewTemplate(final String templateName) {
		return getNewTemplate(templateName, new HashSet<String>(Arrays.asList(task.getKey(),
                agent.getKey())));
	}

	protected MUserReportsTemplate getNewTemplate(final String templateName,
			final Set<String> sourceKeys) {
		final MUserReportsTemplate template = SharedModelConfiguration
				.createUserReportTemplate(templateName);
		template.setUser(user);
		template.setSourceKeys(sourceKeys);

		return template;
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	@Test
	public void testClearTemplatesWhenDeleteAgent() {
		final MAgent anotherAgent = SharedModelConfiguration
				.createLightWeightAgent("another-test-agent");
		modelSpace.save(anotherAgent);

		final MAgentModule anotherModule = SharedModelConfiguration
				.createAgentModule(anotherAgent);
		modelSpace.save(anotherModule);

		final MAgentTask anotherAgentTask = SharedModelConfiguration
				.createAgentTask(anotherModule);
		modelSpace.save(anotherAgentTask);

		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey(),
						taskX.getKey(), anotherAgentTask.getKey())));
		modelSpace.save(template);

		List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSourceKeys()
				.size());

		agentService.delete(agent);

		templates = modelSpace.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		final Set<String> sourceKeys = templates.iterator().next()
				.getSourceKeys();
		Assert.assertEquals(1, sourceKeys.size());
		Assert.assertEquals(anotherAgentTask.getKey(), sourceKeys.iterator()
				.next());
	}

	@Test
	public void testClearTemplatesWhenDeleteTask() {
		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey(),
						taskX.getKey(), taskY.getKey())));
		modelSpace.save(template);

		List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSourceKeys()
				.size());
		taskService.delete(task);

		templates = modelSpace.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		final Set<String> sourceKeys = templates.iterator().next()
				.getSourceKeys();
		Assert.assertEquals(2, sourceKeys.size());
		Assert.assertTrue(sourceKeys.contains(taskX.getKey()));
		Assert.assertTrue(sourceKeys.contains(taskY.getKey()));
	}

	@Test
	public void testClearTemplatesWhenDeleteTasks() {
		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey(),
						taskX.getKey(), taskY.getKey())));
		modelSpace.save(template);

		List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSourceKeys()
				.size());

		final Set<String> taskKeys = new HashSet<String>();
		final String taskXKey = taskX.getKey();
		taskKeys.add(task.getKey());
		taskKeys.add(taskY.getKey());

		taskService.deleteTasks(taskKeys);

		templates = modelSpace.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		final Set<String> sourceKeys = templates.iterator().next()
				.getSourceKeys();
		Assert.assertEquals(1, sourceKeys.size());
		Assert.assertEquals(taskXKey, sourceKeys.iterator().next());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteAgent() {
		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey(),
						taskX.getKey(), taskY.getKey())));
		modelSpace.save(template);

		final List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSourceKeys()
				.size());

		agentService.delete(agent);

		Assert.assertTrue(modelSpace.getAll(MUserReportsTemplate.class)
				.isEmpty());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTask() {
		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey())));
		modelSpace.save(template);

		final List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(1, templates.iterator().next().getSourceKeys()
				.size());
		taskService.delete(task);

		Assert.assertTrue(modelSpace.getAll(MUserReportsTemplate.class)
				.isEmpty());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTasks() {
		final MUserReportsTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME,
				new HashSet<String>(Arrays.asList(task.getKey(),
						taskX.getKey(), taskY.getKey())));
		modelSpace.save(template);

		final List<MUserReportsTemplate> templates = modelSpace
				.getAll(MUserReportsTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSourceKeys()
				.size());

		final Set<String> taskKeys = new HashSet<String>();
		taskX.getKey();
		taskKeys.add(task.getKey());
		taskKeys.add(taskY.getKey());
		taskKeys.add(taskX.getKey());

		taskService.deleteTasks(taskKeys);

		Assert.assertTrue(modelSpace.getAll(MUserReportsTemplate.class)
				.isEmpty());
	}

	@Test
	public void testSaveTemplateFromExistToExist() {
		final String templateNamePrefix = "testSaveReportsTemplateFromExistToExist";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToExist(nameX, nameY);
	}

	@Test
	public void testSaveTemplateFromExistToNew() {
		final String templateNamePrefix = "testSaveReportsTemplateFromExistToNew";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToNew(nameX, nameY);
	}

	@Test
	public void testTemplate() {
		testTemplate(DEFAULT_TEMPLATE_NAME);
	}

	@Test
	public void testTemplateUpdate() {
		testTemplate("testReportTemplateUpdate");
	}

	@Override
	protected void updateFromTemplate(final MUserAbstractTemplate destination,
			final MUserAbstractTemplate source) {
		final Order order = ((MUserReportsTemplate) source).getOrder();
		((MUserReportsTemplate) destination).setOrder(order);
	}

	@Override
	protected void updateTemplate(final MUserAbstractTemplate template) {
		((MUserReportsTemplate) template).setOrder(Order.desc("endDateTime"));
	}
}
