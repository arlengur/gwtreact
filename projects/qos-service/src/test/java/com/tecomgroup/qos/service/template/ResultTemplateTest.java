/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentModule;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MChartSeries;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserResultTemplate;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ResultTemplateTest extends TemplateTest {

	private static final String DEFAULT_TEMPLATE_NAME = "testResultsTemplate";
	private static final TemplateType TEMPLATE_TYPE = BaseTemplateType.RESULT;
	private static final Class<MChartSeries> CHILDREN_TYPE = MChartSeries.class;

	private Set<MChartSeries> createSeries(final int seriesCount) {
		final Set<MChartSeries> series = new HashSet<MChartSeries>();

		for (int i = 0; i < seriesCount; i++) {
			final MChartSeries seria = new MChartSeries();

			seria.setChartName("Chart" + i);
			seria.setTask(task);

			series.add(seria);
		}

		return series;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void expectTemplatesAreDistinct(final TemplateKeeper keeper) {
		final MUserResultTemplate oldTemplate = (MUserResultTemplate) keeper
				.getOld().get(0).getTemplate();
		final MUserResultTemplate actualTemplate = (MUserResultTemplate) keeper
				.getActual().getTemplate();
		final Long idX = oldTemplate.getId();
		final Long idY = actualTemplate.getId();
		final Set<MChartSeries> seriesX = oldTemplate.getSeries();
		final Set<MChartSeries> seriesY = actualTemplate.getSeries();

		Assert.assertTrue(idX != idY);
		Assert.assertTrue(!seriesX.equals(seriesY));
	}

	@Override
	protected Class<? extends MAbstractEntity> getChildrenType() {
		return CHILDREN_TYPE;
	}

	@Override
	protected Collection<MAbstractEntity> getChilds(
			final MUserAbstractTemplate template) {
		Collection<MAbstractEntity> result = null;
		if (template != null) {
			final Set<MChartSeries> series = ((MUserResultTemplate) template)
					.getSeries();
			result = new HashSet<MAbstractEntity>();
			result.addAll(series);
		}

		return result;
	}

	@Override
	protected MUserAbstractTemplate getNewTemplate(final String templateName) {
		return getNewTemplate(templateName, 3);
	}

	private MUserResultTemplate getNewTemplate(final String templateName,
			final int seriesCount) {
		final MUserResultTemplate template = SharedModelConfiguration
				.createUserResultTemplate(templateName);
		template.setUser(user);

		if (seriesCount > 0) {
			template.setSeries(createSeries(seriesCount));
		}

		return template;
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	@Override
	protected boolean hasChilds() {
		return true;
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

		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		chartSeries.add(new MChartSeries(task, null, "Chart1"));
		chartSeries.add(new MChartSeries(taskX, null, "Chart2"));
		chartSeries.add(new MChartSeries(anotherAgentTask, null, "Chart3"));
		template.setSeries(chartSeries);

		modelSpace.save(template);

		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSeries().size());

		agentService.delete(agent);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		chartSeries = templates.iterator().next().getSeries();
		Assert.assertEquals(1, chartSeries.size());
		Assert.assertEquals(anotherAgentTask.getKey(), chartSeries.iterator()
				.next().getTask().getKey());
	}

	@Test
	public void testClearTemplatesWhenDeleteTask() {
		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		final MChartSeries taskChartSeria = new MChartSeries(task, null,
				"Chart1");
		final MChartSeries taskXChartSeria = new MChartSeries(taskX, null,
				"Chart2");
		final MChartSeries taskYChartSeria = new MChartSeries(taskY, null,
				"Chart3");
		chartSeries.add(taskChartSeria);
		chartSeries.add(taskXChartSeria);
		chartSeries.add(taskYChartSeria);
		template.setSeries(chartSeries);
		modelSpace.save(template);

		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSeries().size());
		taskService.delete(task);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		chartSeries = templates.iterator().next().getSeries();
		Assert.assertEquals(2, chartSeries.size());
		Assert.assertTrue(chartSeries.contains(taskXChartSeria));
		Assert.assertTrue(chartSeries.contains(taskYChartSeria));
	}

	@Test
	public void testClearTemplatesWhenDeleteTasks() {
		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		chartSeries.add(new MChartSeries(task, null, "Chart1"));
		chartSeries.add(new MChartSeries(taskX, null, "Chart2"));
		chartSeries.add(new MChartSeries(taskY, null, "Chart3"));
		template.setSeries(chartSeries);

		modelSpace.save(template);
		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSeries().size());

		final Set<String> taskKeys = new HashSet<String>();
		final String taskXKey = taskX.getKey();
		taskKeys.add(task.getKey());
		taskKeys.add(taskY.getKey());

		taskService.deleteTasks(taskKeys);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		chartSeries = templates.iterator().next().getSeries();
		Assert.assertEquals(1, chartSeries.size());
		Assert.assertEquals(taskXKey, chartSeries.iterator().next().getTask()
				.getKey());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteAgent() {
		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		final Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		chartSeries.add(new MChartSeries(task, null, "Chart1"));
		chartSeries.add(new MChartSeries(taskX, null, "Chart2"));
		chartSeries.add(new MChartSeries(taskY, null, "Chart3"));
		template.setSeries(chartSeries);

		modelSpace.save(template);

		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSeries().size());

		agentService.delete(agent);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertTrue(templates.isEmpty());
		Assert.assertTrue(modelSpace.getAll(getChildrenType()).isEmpty());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTask() {
		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		final Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		chartSeries.add(new MChartSeries(task, null, "Chart1"));
		template.setSeries(chartSeries);
		modelSpace.save(template);

		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(1, templates.iterator().next().getSeries().size());
		taskService.delete(task);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertTrue(templates.isEmpty());
		Assert.assertTrue(modelSpace.getAll(getChildrenType()).isEmpty());
	}

	@Test
	public void testDeleteTemplatesWhenDeleteTasks() {
		final MUserResultTemplate template = getNewTemplate(
				DEFAULT_TEMPLATE_NAME, 0);
		final Set<MChartSeries> chartSeries = new HashSet<MChartSeries>();
		chartSeries.add(new MChartSeries(task, null, "Chart1"));
		chartSeries.add(new MChartSeries(taskX, null, "Chart2"));
		chartSeries.add(new MChartSeries(taskY, null, "Chart3"));
		template.setSeries(chartSeries);

		modelSpace.save(template);
		List<MUserResultTemplate> templates = modelSpace
				.getAll(MUserResultTemplate.class);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getSeries().size());

		final Set<String> taskKeys = new HashSet<String>();
		taskX.getKey();
		taskKeys.add(task.getKey());
		taskKeys.add(taskY.getKey());
		taskKeys.add(taskX.getKey());

		taskService.deleteTasks(taskKeys);

		templates = modelSpace.getAll(MUserResultTemplate.class);
		Assert.assertTrue(templates.isEmpty());
		Assert.assertTrue(modelSpace.getAll(getChildrenType()).isEmpty());
	}

	@Test
	public void testSaveTemplateFromExistToExist() {
		final String templateNamePrefix = "testSaveResultsTemplateFromExistToExist";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToExist(nameX, nameY);
	}

	@Test
	public void testSaveTemplateFromExistToNew() {
		final String templateNamePrefix = "testSaveResultsTemplateFromExistToNew";
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
		testTemplateUpdate("testResultsTemplateUpdate");
	}

	@Override
	protected void updateFromTemplate(final MUserAbstractTemplate destination,
			final MUserAbstractTemplate source) {
		final Set<MChartSeries> series = ((MUserResultTemplate) source)
				.getSeries();
		((MUserResultTemplate) destination).setSeries(series);
	}

	@Override
	protected void updateTemplate(final MUserAbstractTemplate template) {
		final Set<MChartSeries> newSeries = new HashSet<MChartSeries>();
		final MChartSeries newSeria = new MChartSeries();
		newSeria.setChartName("Chart 4");
		newSeria.setTask(task);
		newSeries.add(newSeria);
		((MUserResultTemplate) template).setSeries(newSeries);
	}
}
