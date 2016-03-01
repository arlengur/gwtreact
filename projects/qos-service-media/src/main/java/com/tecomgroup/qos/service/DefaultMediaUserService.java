/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MAgentTask;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MLiveStreamTemplate;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.domain.MRecordedStreamTemplate;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.domain.MStreamTemplate;
import com.tecomgroup.qos.domain.MStreamTemplate.MediaTemplateType;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;

/**
 * @author abondin
 * 
 */
@Service("userService")
@SuppressWarnings({"unchecked"})
public class DefaultMediaUserService extends SpringUserService {

	@Autowired
	private MediaAgentService agentService;

	@Autowired
	private TaskRetriever taskRetriever;

	private final static Logger LOGGER = Logger
			.getLogger(DefaultMediaUserService.class);

	private void clearSourceRelatedStreamTemplates(
			final Class<? extends MStreamTemplate> templateClass,
			final Class<? extends MStreamWrapper> wrapperClass,
			final MSource source) {

		final List<MStreamWrapper> allStreamWrappersToDelete = (List<MStreamWrapper>) modelSpace
				.find(wrapperClass,
						modelSpace.createCriterionQuery().eq("taskKey",
								source.getKey()));

		if (allStreamWrappersToDelete.size() > 0) {
			final List<MStreamTemplate> streamTemplates = (List<MStreamTemplate>) modelSpace
					.find(templateClass,
							createTemplateContainingWrappersCriterion(allStreamWrappersToDelete));

			for (final MStreamTemplate streamTemplate : streamTemplates) {
				final List<? extends MStreamWrapper> streamWrappers = streamTemplate
						.getWrappers();
				if (allStreamWrappersToDelete.containsAll(streamWrappers)) {
					modelSpace.delete(streamTemplate);
				} else {

					final List<MStreamWrapper> templateStreamWrappersToDelete = new ArrayList<>(
							allStreamWrappersToDelete);
					templateStreamWrappersToDelete.retainAll(streamWrappers);

					if (!templateStreamWrappersToDelete.isEmpty()) {
						streamWrappers
								.removeAll(templateStreamWrappersToDelete);
						modelSpace.saveOrUpdate(streamTemplate);

						for (final MStreamWrapper streamWrapper : templateStreamWrappersToDelete) {
							modelSpace.delete(streamWrapper);
						}
					}
				}
			}
		}
	}

	/**
	 * Clear source related wrappers from live and recorded stream templates
	 */
	@Override
	@Transactional
	public void clearSourceRelatedTemplates(final MSource source) {
		super.clearSourceRelatedTemplates(source);

		clearSourceRelatedStreamTemplates(MLiveStreamTemplate.class,
				MLiveStreamWrapper.class, source);
		clearSourceRelatedStreamTemplates(MRecordedStreamTemplate.class,
				MRecordedStreamWrapper.class, source);
	}

	@Override
	protected void clearTaskRelatedWidgets(final MAgentTask task,
			final List<MDashboard> dashboards) {
        super.clearTaskRelatedWidgets(task, dashboards);

		final List<String> widgetsToRemove = new ArrayList<String>();
		for (final MDashboard dashboard : dashboards) {
			for (final Map.Entry<String, DashboardWidget> widgetEntry : dashboard
					.getWidgets().entrySet()) {
				final DashboardWidget widget = widgetEntry.getValue();
				if (widget instanceof LiveStreamWidget) {
					final LiveStreamWidget liveWidget = (LiveStreamWidget) widget;
					if (liveWidget.getTaskKey().equals(task.getKey())) {
						widgetsToRemove.add(liveWidget.getKey());
					}
				}
			}
			if (!widgetsToRemove.isEmpty()) {
				for (final String widgetKey : widgetsToRemove) {
					dashboard.removeWidget(widgetKey);
				}
				updateDashboard(dashboard);
				widgetsToRemove.clear();
			}
		}
	}

	/**
	 * Get search condition which fetches templates if they have at least one
	 * wrapper from condition
	 */
	private Criterion createTemplateContainingWrappersCriterion(
			final List<MStreamWrapper> streamWrappers) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		Criterion result = null;

		for (final MStreamWrapper streamWrapper : streamWrappers) {
			final Criterion criterion = query.collectionContains("wrappers",
					streamWrapper);

			if (result == null) {
				result = criterion;
			} else {
				result = query.or(result, criterion);
			}
		}

		return result;
	}

	@Override
	public MUserAbstractTemplate getTemplate(final TemplateType templateType,
			final Long userId, final String templateName) {
		final MUserAbstractTemplate template = super.getTemplate(templateType,
				userId, templateName);

		if (template instanceof MStreamTemplate) {
			populateStreamTemplate((MStreamTemplate) template);
		}

		return template;
	}

	@Override
	public List<? extends MUserAbstractTemplate> getTemplates(
			final TemplateType templateType, final Long userId) {
		final List<? extends MUserAbstractTemplate> templates = super
				.getTemplates(templateType, userId);

		if (templateType == MediaTemplateType.LIVE_VIDEO
				|| templateType == MediaTemplateType.RECORDED_VIDEO) {
			populateStreamTemplates((List<MStreamTemplate>) templates);
		}

		return templates;
	}

	private <T extends MStreamTemplate> T populateStreamTemplate(
			final T template) {
		for (final MStreamWrapper wrapper : template.getWrappers()) {
			final MAgentTask task = taskRetriever.getTaskByKey(wrapper
					.getTaskKey());
			if (task == null) {
				// FIXME What if task what deleted?
				LOGGER.error("Cannot find task for " + wrapper
						+ ". It could be already deleted.");
			} else {
				final MStream stream = agentService.getStream(task,
						wrapper.getStreamKey());
				if (stream == null) {
					// FIXME What if stream what deleted?
					LOGGER.error("Cannot find stream for "
							+ wrapper
							+ ". It could be deleted during configuration update.");
				}
				wrapper.setStream(stream);
				wrapper.setAgent(task.getModule().getAgent());
			}
		}

		return template;
	}

	private <T extends MStreamTemplate> List<T> populateStreamTemplates(
			final List<T> templates) {
		for (final MStreamTemplate template : templates) {
			populateStreamTemplate(template);
		}
		return templates;
	}

	public void setAgentService(final MediaAgentService agentService) {
		this.agentService = agentService;
	}

	public void setTaskRetriever(final TaskRetriever taskRetriever) {
		this.taskRetriever = taskRetriever;
	}
}
