/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.domain.MAbstractEntity;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MStreamTemplate;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.domain.MTestAgentModule;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.service.AbstractService;
import com.tecomgroup.qos.service.DefaultMediaAgentService;
import com.tecomgroup.qos.service.DefaultMediaUserService;
import com.tecomgroup.qos.service.MediaAgentService;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/testServiceContext.xml"})
@ActiveProfiles(profiles = {AbstractService.TEST_MEDIA_CONTEXT_PROFILE}, inheritProfiles = false)
@Transactional
public abstract class MediaTemplateTest extends TemplateTest {

	@SuppressWarnings("unchecked")
	@Override
	protected void expectTemplatesAreDistinct(final TemplateKeeper keeper) {
		final MStreamTemplate oldTemplate = (MStreamTemplate) keeper.getOld()
				.get(0).getTemplate();
		final MStreamTemplate actualTemplate = (MStreamTemplate) keeper
				.getActual().getTemplate();
		final Long idX = oldTemplate.getId();
		final Long idY = actualTemplate.getId();
		final List<MStreamWrapper> wrappersX = (List<MStreamWrapper>) oldTemplate
				.getWrappers();
		final List<MStreamWrapper> wrappersY = (List<MStreamWrapper>) actualTemplate
				.getWrappers();
		Assert.assertTrue(idX != idY);
		Assert.assertTrue(!wrappersX.equals(wrappersY));
	}

	@Override
	protected abstract Class<? extends MAbstractEntity> getChildrenType();

	@Override
	@SuppressWarnings("unchecked")
	protected Collection<MAbstractEntity> getChilds(
			final MUserAbstractTemplate template) {
		Collection<MAbstractEntity> result = null;
		if (template != null) {
			final List<MStreamWrapper> wrappers = (List<MStreamWrapper>) ((MStreamTemplate) template)
					.getWrappers();
			result = new ArrayList<MAbstractEntity>();
			result.addAll(wrappers);
		}

		return result;
	}

	@Override
	protected MUserAbstractTemplate getNewTemplate(final String templateName) {
		return getNewTemplate(templateName, agent, task, 3);
	}

	@SuppressWarnings("unchecked")
	private MStreamTemplate getNewTemplate(final String templateName,
			final MAgent agent, final MAgentTask task, final int wrappersCount) {
		final MStreamTemplate template = (MStreamTemplate) getTemplateInstance(
				templateName, agent, task, wrappersCount);
		template.setUser(user);

		return template;
	}

	protected abstract MStreamWrapper getStreamWrapperInstance(
			final MAgent agent, final MAgentTask task);

	protected abstract MUserAbstractTemplate getTemplateInstance(
			String templateName, final MAgent agent, final MAgentTask task,
			final int wrappersCount);

	@Override
	protected boolean hasChilds() {
		return true;
	}

	@Override
	protected void initAgentService() {
		final DefaultMediaAgentService mediaAgentService = new DefaultMediaAgentService();

		mediaAgentService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		mediaAgentService.setTransactionTemplate(transactionTemplate);
		mediaAgentService
				.setPolicyConfigurationService(policyConfigurationService);
		mediaAgentService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		mediaAgentService.setInternalEventBroadcaster(internalEventBroadcaster);
		mediaAgentService.setModelSpace(modelSpace);
		mediaAgentService.setPropertyUpdater(propertyUpdater);

		agentService = mediaAgentService;
	}

	@Override
	protected void initDependencies() {
		super.initDependencies();

		((DefaultMediaUserService) userService)
				.setAgentService((MediaAgentService) agentService);
		((DefaultMediaUserService) userService).setTaskRetriever(taskService);
	}

	@Override
	protected void initModule() {
		module = MediaModelConfiguration.createMediaAgentModule(agent);
		modelSpace.save(module);
	}

	@Override
	protected void initUserService() {
		final DefaultMediaUserService mediaUserService = new DefaultMediaUserService();

		mediaUserService.setModelSpace(modelSpace);
		mediaUserService.setTransactionTemplate(transactionTemplate);
		mediaUserService
				.setReadOnlyTransactionTemplate(readOnlyTransactionTemplate);
		mediaUserService.setEventBroadcastDispatcher(eventBroadcastDispatcher);
		mediaUserService.setInternalEventBroadcaster(internalEventBroadcaster);
		mediaUserService.setAgentService((MediaAgentService) agentService);
		mediaUserService.setTaskRetriever(taskService);
		userService = mediaUserService;
	}

	protected void testClearTemplatesWhenDeleteAgent(final String templateName,
			final Class<? extends MStreamTemplate> templateClass) {
		final MStreamTemplate template = (MStreamTemplate) getTemplateInstance(
				templateName, null, null, 0);

		final MAgent anotherAgent = SharedModelConfiguration
				.createLightWeightAgent("test-agent1");
		modelSpace.save(anotherAgent);

		final MTestAgentModule anotherModule = SharedModelConfiguration
				.createAgentModule(anotherAgent);
		modelSpace.save(anotherModule);

		final MAgentTask anotherTask = SharedModelConfiguration
				.createAgentTask(anotherModule);
		modelSpace.save(anotherTask);

		final List<MStreamWrapper> streamWrappers = new ArrayList<MStreamWrapper>();
		streamWrappers.add(getStreamWrapperInstance(agent, task));
		streamWrappers.add(getStreamWrapperInstance(agent, taskX));
		streamWrappers.add(getStreamWrapperInstance(agent, taskY));
		streamWrappers.add(getStreamWrapperInstance(anotherAgent, anotherTask));
		template.setUser(user);
		template.setWrappers(streamWrappers);

		modelSpace.save(template);

		List<MStreamTemplate> templates = (List<MStreamTemplate>) modelSpace
				.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(4, templates.iterator().next().getWrappers().size());

		agentService.delete(agent);

		templates = (List<MStreamTemplate>) modelSpace.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		final List<MStreamWrapper> templateWrappers = (List<MStreamWrapper>) templates
				.iterator().next().getWrappers();
		Assert.assertEquals(1, templateWrappers.size());
		Assert.assertEquals(anotherTask.getKey(), templateWrappers.iterator()
				.next().getTaskKey());
	}

	protected void testClearTemplatesWhenDeleteTasks(final String templateName,
			final Class<? extends MStreamTemplate> templateClass) {
		final MStreamTemplate template = (MStreamTemplate) getTemplateInstance(
				templateName, null, null, 0);
		final List<MStreamWrapper> streamWrappers = new ArrayList<MStreamWrapper>();
		streamWrappers.add(getStreamWrapperInstance(agent, task));
		streamWrappers.add(getStreamWrapperInstance(agent, taskX));
		streamWrappers.add(getStreamWrapperInstance(agent, taskY));
		template.setUser(user);
		template.setWrappers(streamWrappers);

		modelSpace.save(template);
		List<MStreamTemplate> templates = (List<MStreamTemplate>) modelSpace
				.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getWrappers().size());

		final Set<String> taskKeys = new HashSet<String>();
		final String taskXKey = taskX.getKey();
		taskKeys.add(task.getKey());
		taskKeys.add(taskY.getKey());

		taskService.deleteTasks(taskKeys);

		templates = (List<MStreamTemplate>) modelSpace.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		final List<MStreamWrapper> templateWrappers = (List<MStreamWrapper>) templates
				.iterator().next().getWrappers();
		Assert.assertEquals(1, templateWrappers.size());
		Assert.assertEquals(taskXKey, templateWrappers.iterator().next()
				.getTaskKey());
	}

	protected void testDeleteTemplatesWhenDeleteAgent(
			final String templateName,
			final Class<? extends MStreamTemplate> templateClass) {
		final MStreamTemplate template = (MStreamTemplate) getTemplateInstance(
				templateName, null, null, 0);
		final List<MStreamWrapper> streamWrappers = new ArrayList<MStreamWrapper>();
		streamWrappers.add(getStreamWrapperInstance(agent, task));
		streamWrappers.add(getStreamWrapperInstance(agent, taskX));
		streamWrappers.add(getStreamWrapperInstance(agent, taskY));
		template.setUser(user);
		template.setWrappers(streamWrappers);

		modelSpace.save(template);

		List<MStreamTemplate> templates = (List<MStreamTemplate>) modelSpace
				.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getWrappers().size());

		agentService.delete(agent);

		templates = (List<MStreamTemplate>) modelSpace.getAll(templateClass);
		Assert.assertTrue(templates.isEmpty());
	}

	@SuppressWarnings("unchecked")
	protected void testDeleteTemplatesWhenDeleteTask(final String templateName,
			final Class<? extends MStreamTemplate> templateClass) {
		final MStreamTemplate template = getNewTemplate(templateName, agent,
				task, 3);
		modelSpace.save(template);

		List<MStreamTemplate> templates = (List<MStreamTemplate>) modelSpace
				.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(3, templates.iterator().next().getWrappers().size());
		taskService.delete(task);

		templates = (List<MStreamTemplate>) modelSpace.getAll(templateClass);
		Assert.assertTrue(templates.isEmpty());
	}

	@SuppressWarnings("unchecked")
	protected void testDeleteTemplatesWhenDeleteTasks(
			final String templateName,
			final Class<? extends MStreamTemplate> templateClass) {
		// TODO
		final MStreamTemplate template = (MStreamTemplate) getTemplateInstance(
				templateName, null, null, 0);
		final List<MStreamWrapper> streamWrappers = new ArrayList<MStreamWrapper>();
		streamWrappers.add(getStreamWrapperInstance(agent, taskX));
		streamWrappers.add(getStreamWrapperInstance(agent, taskY));
		template.setUser(user);
		template.setWrappers(streamWrappers);
		modelSpace.save(template);

		List<MStreamTemplate> templates = (List<MStreamTemplate>) modelSpace
				.getAll(templateClass);
		Assert.assertEquals(1, templates.size());
		Assert.assertEquals(2, templates.iterator().next().getWrappers().size());

		final Set<String> taskKeys = new HashSet<String>();
		taskKeys.add(taskX.getKey());
		taskKeys.add(taskY.getKey());

		taskService.deleteTasks(taskKeys);

		templates = (List<MStreamTemplate>) modelSpace.getAll(templateClass);
		Assert.assertTrue(templates.isEmpty());
	}

	@SuppressWarnings("unchecked")
	protected void testEmptyTemplate(final String templateName) {
		final TemplateKeeper keeper = new TemplateKeeper(getNewTemplate(
				templateName, null, null, 0));

		saveTemplate(keeper);
		expectTemplate(keeper);

		removeTemplate(keeper);
		expectTemplate(null);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void updateFromTemplate(final MUserAbstractTemplate destination,
			final MUserAbstractTemplate source) {
		final List<MStreamWrapper> wrappers = (List<MStreamWrapper>) ((MStreamTemplate) source)
				.getWrappers();
		((MStreamTemplate) destination).setWrappers(wrappers);
	}

	@Override
	protected void updateTemplate(final MUserAbstractTemplate template) {
		final List<MStreamWrapper> newWrappers = new ArrayList<MStreamWrapper>();
		final MStreamWrapper newWrapper = getStreamWrapperInstance(agent, task);
		newWrappers.add(newWrapper);
		((MStreamTemplate) template).setWrappers(newWrappers);
	}

}
