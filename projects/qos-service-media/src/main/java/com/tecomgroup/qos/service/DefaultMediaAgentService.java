/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tecomgroup.qos.domain.*;
import org.apache.log4j.Logger;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.domain.MProperty.PropertyType;
import com.tecomgroup.qos.exception.ServiceException;

/**
 * @author kunilov.p
 * 
 */
@Transactional(readOnly = true)
public class DefaultMediaAgentService extends DefaultAgentService
		implements
			MediaAgentService,StreamsInfoService {

	private PropertyUpdater propertyUpdater;

	private final static Logger LOGGER = Logger
			.getLogger(DefaultMediaAgentService.class);

	@Override
	public List<MLiveStream> getAgentLiveStreams(final String agentName) {
		return getAgentStreams(agentName, MLiveStream.class);
	}

	@Override
	public List<MRecordedStream> getAgentRecordedStreams(final String agentName) {
		return getAgentStreams(agentName, MRecordedStream.class);
	}

    @Override
    public List<MLiveStreamWrapper> getTasksLiveStreams(final List<Long> taskIds) {
        final List<MAgentTask> tasks = taskService.getTasksByIds(taskIds);
        final List<MLiveStream> streams = getTasksStreams(tasks, MLiveStream.class);

        final List<MLiveStreamWrapper> wrappers = new ArrayList<>();

        for (MLiveStream stream : streams) {
            MLiveStreamWrapper wrapper = new MLiveStreamWrapper();
            wrapper.setStream(stream);

            for (MAgentTask task : tasks) {
                if (task.getKey().equals(stream.getSource().getKey())) {
                    wrapper.setAgent(task.getModule().getAgent());
                    break;
                }
            }
            wrappers.add(wrapper);
        }
        return wrappers;
    }

    @Override
    public List<MRecordedStreamWrapper> getTasksRecordedStreams(final List<Long> taskIds) {
        final List<MAgentTask> tasks = taskService.getTasksByIds(taskIds);
        final List<MRecordedStream> streams = getTasksStreams(tasks, MRecordedStream.class);

        final List<MRecordedStreamWrapper> wrappers = new ArrayList<>();

        for (MRecordedStream stream : streams) {
            MRecordedStreamWrapper wrapper = new MRecordedStreamWrapper();
            wrapper.setStream(stream);

            for (MAgentTask task : tasks) {
                if (task.getKey().equals(stream.getSource().getKey())) {
                    wrapper.setAgent(task.getModule().getAgent());
                    break;
                }
            }
            wrappers.add(wrapper);
        }
        return wrappers;
    }

    private <T extends MStream> List<T> getTasksStreams(final List<MAgentTask> tasks,
                                                        final Class<T> streamType) {

        final List<T> streams = new LinkedList<T>();
        for (final MAgentTask task : tasks) {
            streams.addAll(getStreams(task, streamType));
        }
        return streams;
    }

	@SuppressWarnings("unchecked")
	private <T extends MStream> List<T> getAgentStreams(final String agentKey,
			final Class<T> streamType) {
		final CriterionQuery query = modelSpace.createCriterionQuery();
		final Criterion criterion = query.and(
				createModuleNotDeletedCriterionByAgentKey(agentKey, query),
				query.isNotEmpty("templateStreams"));

		final List<String> mediaModuleKeys = (List<String>) modelSpace
				.findProperties(MMediaAgentModule.class, criterion, "key");
		final List<MAgentTask> tasksWithLiveStreams = taskService
				.getAgentTasks(agentKey, new HashSet<String>(mediaModuleKeys),
						null, null);

		final List<T> streams = new LinkedList<T>();
		for (final MAgentTask task : tasksWithLiveStreams) {
			streams.addAll(getStreams(task, streamType));
		}
		
		Collections.sort(streams,
						 new Comparator<T>() {
							 public int compare(T itemA,
												T itemB) {
								 return itemA.getDisplayName().compareTo(itemB.getDisplayName());
							 }
							 
							 public boolean equals(Object other) {
								 return this.equals(other);
							 }
						 });
		
		return streams;
	}

	@Override
	public MMediaAgentModule getMediaModule(final String agentKey,
			final String moduleKey) {
		return modelSpace
				.findUniqueEntity(
						MMediaAgentModule.class,
						createModuleCriterionByAgentKeyAndModuleKey(agentKey,
								moduleKey));
	}

	@Override
	public MRecordedStream getRelatedStream(final MAlert alert) {
		MRecordedStream stream = null;
		final String relatedRecordingTaskKey = alert
				.getRelatedRecordingTaskKey();
		if (relatedRecordingTaskKey != null) {
			final MAgentTask task = taskService
					.getTaskByKey(relatedRecordingTaskKey);
			if (task != null) {
				// FIXME We support only one stream for task
				final List<MRecordedStream> streams = getStreams(task,
						MRecordedStream.class);
				if (!streams.isEmpty()) {
					stream = streams.iterator().next();
				}
			}
		}
		return stream;
	}

	@Override
	public MStream getStream(final MAgentTask task, final String streamKey) {
		for (final MStream stream : ((MMediaAgentModule) task.getModule())
				.getTemplateStreams()) {
			if (stream.getKey().equals(streamKey)) {
				return instantiateStreamFromTemplate(stream.getClass(), stream,
						task);
			}
		}
		return null;
	}

	public <T extends MStream> List<T> getStreams(final MAgentTask task,
			final Class<T> streamType) {
		final List<T> streams = new ArrayList<>();
		final MAgentModule module = task.getModule();
		if (module instanceof MMediaAgentModule) {
			final MMediaAgentModule mediaModule = (MMediaAgentModule) module;
			streams.addAll(instantiateStreamsFromTemplates(streamType,
					mediaModule.getTemplateStreams(), task));
		}
		return streams;
	}

	private <T extends MStream> T instantiateStreamFromTemplate(
			final Class<T> streamType, final MStream template) {
		T castedStream = null;
		if (streamType.isInstance(template)) {
			try {
				castedStream = streamType.newInstance();
			} catch (final Exception ex) {
				throw new ServiceException(
						"Unable to instantiate stream by type: " + streamType,
						ex);
			}
			if (castedStream != null) {
				template.copyTo(castedStream);
			}
		}
		return castedStream;
	}

	private <T extends MStream> T instantiateStreamFromTemplate(
			final Class<T> streamType, final MStream template,
			final MAgentTask task) {
		final T stream = instantiateStreamFromTemplate(streamType, template);
		if (stream != null) {
			stream.createTaskRelatedFields(task);
		}
		return stream;
	}

	private <T extends MStream> List<T> instantiateStreamsFromTemplates(
			final Class<T> streamType, final Collection<MStream> templates,
			final MAgentTask task) {
		final List<T> streams = new ArrayList<T>();
		for (final MStream template : templates) {
			final T stream = instantiateStreamFromTemplate(streamType,
					template, task);
			if (stream != null) {
				streams.add(stream);
			}
		}
		return streams;
	}

	/**
	 * @param propertyUpdater
	 *            the propertyUpdater to set
	 */
	public void setPropertyUpdater(final PropertyUpdater propertyUpdater) {
		this.propertyUpdater = propertyUpdater;
	}

	@Override
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	public void updateModules(final String agentName,
			final List<MAgentModule> updatedModules) {
		super.updateModules(agentName, updatedModules);

		for (final MAgentModule module : updatedModules) {
			if (module instanceof MMediaAgentModule) {
				final MMediaAgentModule updatedMediaModule = (MMediaAgentModule) module;
				final MMediaAgentModule oldMediaModule = executeInTransaction(
						true, new TransactionCallback<MMediaAgentModule>() {

							@Override
							public MMediaAgentModule doInTransaction(
									final TransactionStatus status) {
								return getMediaModule(agentName,
										updatedMediaModule.getKey());
							}
						});
				if (oldMediaModule != null) {
					updateStreams(oldMediaModule, updatedMediaModule);
				}
			}
		}
	}

	private void updateStreams(final MMediaAgentModule oldModule,
			final MMediaAgentModule updatedModule) {

		final Map<String, MStream> oldStreamMap = new HashMap<String, MStream>();
		final List<MStream> oldTemplateStreams = oldModule.getTemplateStreams();
		if (oldTemplateStreams != null) {
			for (final MStream oldStream : oldTemplateStreams) {
				oldStreamMap.put(oldStream.getKey(), oldStream);
			}
		}

		final Map<String, MStream> updatedStreamMap = new HashMap<String, MStream>();
		final List<MStream> updatedTemplateStreams = updatedModule
				.getTemplateStreams();
		if (updatedTemplateStreams != null) {
			for (final MStream updatedStream : updatedTemplateStreams) {
				updatedStreamMap.put(updatedStream.getKey(), updatedStream);
			}
		}

		// remove old stream
		final Set<String> removedStreams = new HashSet<String>(
				oldStreamMap.keySet());
		removedStreams.removeAll(updatedStreamMap.keySet());
		for (final String streamKey : removedStreams) {
			final MStream oldStream = oldStreamMap.get(streamKey);
			executeInTransaction(false, new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					oldModule.removeTemplateStream(oldStream);
					modelSpace.saveOrUpdate(oldModule);
					modelSpace.delete(oldStream);
				}
			});
			LOGGER.info("Remove stream: " + oldStream);
		}

		// add new stream
		final Set<String> newStreams = new HashSet<String>(
				updatedStreamMap.keySet());
		newStreams.removeAll(oldStreamMap.keySet());
		for (final String streamKey : newStreams) {
			final MStream newStream = updatedStreamMap.get(streamKey);
			executeInTransaction(false, new TransactionCallbackWithoutResult() {

				@Override
				protected void doInTransactionWithoutResult(
						final TransactionStatus status) {
					modelSpace.save(newStream);
					oldModule.addTemplateStream(newStream);
					modelSpace.saveOrUpdate(oldModule);
				}
			});
			LOGGER.info("Register new stream: " + newStream);
		}

		// update stream
		final Set<String> updatedStreams = new HashSet<String>(
				oldStreamMap.keySet());
		updatedStreams.retainAll(updatedStreamMap.keySet());
		for (final String streamKey : updatedStreams) {
			final MStream oldStream = oldStreamMap.get(streamKey);
			final MStream updatedStream = updatedStreamMap.get(streamKey);

			// update stream without properties
			boolean streamIsUpdated = oldStream
					.updateSimpleFields(updatedStream);
			// update properties
			streamIsUpdated |= propertyUpdater.updateProperties(oldStream,
					updatedStream, PropertyType.ALL);

			if (streamIsUpdated) {
				executeInTransaction(false,
						new TransactionCallbackWithoutResult() {

							@Override
							protected void doInTransactionWithoutResult(
									final TransactionStatus status) {
								modelSpace.saveOrUpdate(oldStream);
							}
						});
				LOGGER.info("Update stream: " + oldStream);
			}
		}
	}
}
