/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.util;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.log4j.Logger;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * Provides way to run lingering tasks only as long as any third party is
 * interested in them. <br/>
 * "Interest is shown through {@link #checkStatus(long, boolean)} method calls
 * with parameter updateLastTimeChecked=true. <br/>
 * 
 * When a task is not checked for longer than "uncheckedTimeLimit" it is
 * cancelled. <br/>
 * <br/>
 * There is also possibility to explicitly cancel task via
 * {@link #cancelTask(long)}.
 * 
 * @author sviyazov.a
 * 
 */
public class LingeringTaskExecutor<R> {

	private static class TaskInfo<R> {

		private final Future<R> future;
		private final IdentifiableTask<R> task;
		private volatile long lastTimeChecked;

		public TaskInfo(final Future<R> future, final IdentifiableTask<R> task) {
			this.future = future;
			this.task = task;
			lastTimeChecked = System.currentTimeMillis();
		}

		public Future<R> getFuture() {
			return future;
		}

		public long getLastTimeChecked() {
			return lastTimeChecked;
		}

		public IdentifiableTask<R> getTask() {
			return task;
		}

		public void updateLastTimeChecked() {
			lastTimeChecked = System.currentTimeMillis();
		}
	}

	/**
	 * Periodically (taking break for {@link #examinationInterval}) examines all
	 * tasks and cancels those, which are not checked by interested party for
	 * longer, than {@link #uncheckedTimeLimit}.
	 * 
	 * @param <R>
	 */
	private static class TaskObserver<R> implements Runnable {

		private final Map<Long, TaskInfo<R>> tasks;

		private final long uncheckedTimeLimit;

		private final long examinationInterval;

		public TaskObserver(final Map<Long, TaskInfo<R>> tasks,
				final long uncheckedTimeLimit, final long examinationInterval) {
			this.tasks = tasks;
			this.examinationInterval = examinationInterval;
			this.uncheckedTimeLimit = uncheckedTimeLimit;
		}

		@Override
		public void run() {
			while (!Thread.currentThread().isInterrupted()) {
				final Iterator<Map.Entry<Long, TaskInfo<R>>> iterator = tasks
						.entrySet().iterator();
				while (iterator.hasNext()) {
					final Map.Entry<Long, TaskInfo<R>> entry = iterator.next();
					final TaskInfo<R> taskInfo = entry.getValue();
					final long currentTime = System.currentTimeMillis();
					if (currentTime - taskInfo.getLastTimeChecked() > uncheckedTimeLimit) {
						// if it's done, we wait for client to collect results,
						// otherwise cancel or clears
						if (!taskInfo.getFuture().isDone()) {
							taskInfo.getFuture().cancel(true);
							tasks.remove(entry.getKey());
							LOGGER.info("Task "
									+ taskInfo.getTask()
									+ " exceeded unchecked time limit and was cancelled by the observer.");
						}
					}
				}
				try {
					Thread.sleep(examinationInterval);
				} catch (final InterruptedException e) {
					Thread.currentThread().interrupt();
					return;
				}
			}
		}
	}

	private final static Logger LOGGER = Logger
			.getLogger(LingeringTaskExecutor.class);

	private final AtomicLong taskIdCounter = new AtomicLong();

	private final Map<Long, TaskInfo<R>> tasks = new ConcurrentHashMap<>();

	private final ThreadPoolExecutor executorService;

	private final Thread observer;

	/**
	 * @param taskExecutorName
	 *            general name of created task executor which is in the same
	 *            time the pefix of created thread names.
	 * @param threadCountBase
	 *            base thread count of nested {@link ExecutorService}
	 * @param threadCountMax
	 *            maximal thread count of nested {@link ExecutorService}
	 * @param uncheckedTimeLimit
	 *            maximum time, for which a task could be not checked by
	 *            interested party, before it's cancelled.
	 * @param examinationInterval
	 *            pause between tasks examinations by
	 *            {@link com.tecomgroup.qos.util.LingeringTaskExecutor.TaskObserver}
	 */
	public LingeringTaskExecutor(final String taskExecutorName,
			final int threadCountBase, final int threadCountMax,
			final long uncheckedTimeLimit, final long examinationInterval) {
		executorService = new ThreadPoolExecutor(threadCountBase,
				threadCountMax, 10L, TimeUnit.MINUTES,
				new LinkedBlockingQueue<Runnable>(),
				new CustomizableThreadFactory(taskExecutorName + "-thread-"));
		observer = new Thread(new TaskObserver<R>(tasks, uncheckedTimeLimit,
				examinationInterval));
		observer.setName(taskExecutorName + "-observer");
	}

	/**
	 * Cancels execution of a task. Harmless if task already completed.
	 * 
	 * @param taskId
	 * @throws NoSuchElementException
	 *             if no task with provided id is found
	 */
	public void cancelTask(final long taskId) throws NoSuchElementException {
		final TaskInfo<R> taskInfo = getTaskInfo(taskId);

		taskInfo.getFuture().cancel(true);
		// Necessary to clear task from executor queue, if it hasn't been
		// started yet.
		// Otherwise un-executable cancelled task will block other tasks from
		// execution.
		executorService.purge();
		LOGGER.info("Explicitly cancel task: " + taskInfo.getTask());
	}

	/**
	 * 
	 * @param taskId
	 * @param updateLastTimeChecked
	 *            whether to inform that caller is still interested in the task
	 *            and wants to prolong it's run.
	 * @return current status of the task
	 * @throws NoSuchElementException
	 *             if no task with provided id is found
	 */
	public TaskStatusInfo checkStatus(final long taskId,
			final boolean updateLastTimeChecked) throws NoSuchElementException {
		final TaskInfo<R> taskInfo = getTaskInfo(taskId);
		final Future<R> taskFuture = taskInfo.getFuture();

		TaskStatus status = null;
		if (!taskFuture.isDone()) {
			status = TaskStatus.RUNNING;
		} else {
			try {
				taskFuture.get();
				status = TaskStatus.COMPLETED;
			} catch (final InterruptedException e) {
				// thread was interrupted, we don't longer need result as we are
				// shutting down
				taskFuture.cancel(true);
				status = TaskStatus.CANCELLED;
				Thread.currentThread().interrupt();
			} catch (final ExecutionException e) {
				LOGGER.error("Task execution exception", e);
				status = TaskStatus.ERROR;
			} catch (final CancellationException e) {
				status = TaskStatus.CANCELLED;
			}
		}

		if (updateLastTimeChecked && status == TaskStatus.RUNNING) {
			taskInfo.updateLastTimeChecked();
		}

		return new TaskStatusInfo(status, taskInfo.getTask().getPercentDone());
	}
	/**
	 * Removes all information about task. It's possible only if task is
	 * completed or cancelled.
	 * 
	 * @param taskId
	 * @throws NoSuchElementException
	 *             if no task with provided id is found
	 * @throws IllegalStateException
	 *             if task is still running.
	 */
	public void clearTask(final long taskId) throws NoSuchElementException,
			IllegalStateException {
		final TaskInfo<R> taskInfo = getTaskInfo(taskId);

		if (taskInfo.getFuture().isDone()) {
			tasks.remove(taskId);
		} else {
			throw new IllegalStateException(
					"Task "
							+ taskId
							+ " is running. It has to be finished or canceled before clearing.");
		}
	}

	private TaskInfo<R> getTaskInfo(final long taskId)
			throws NoSuchElementException {
		final TaskInfo<R> taskInfo = tasks.get(taskId);

		if (taskInfo == null) {
			throw new NoSuchElementException("There is no such task: " + taskId);
		}
		return taskInfo;
	}

	/**
	 * Returns {@link java.util.concurrent.Callable} task result. Will block
	 * execution if task is still running.
	 * 
	 * @param taskId
	 * @return
	 * @throws NoSuchElementException
	 *             if no task with provided id is found
	 * @throws ExecutionException
	 *             if there is nested exception in
	 *             {@link java.util.concurrent.Callable} task
	 * @throws CancellationException
	 *             if task was cancelled
	 */
	public R getTaskResult(final long taskId) throws NoSuchElementException,
			ExecutionException, CancellationException {
		final TaskInfo<R> taskInfo = getTaskInfo(taskId);
		R result = null;
		try {
			result = taskInfo.getFuture().get();
		} catch (final InterruptedException e) {
			// thread was interrupted, we don't longer need result as we are
			// shutting down
			Thread.currentThread().interrupt();
		}
		return result;
	}

	/**
	 * 
	 * @param taskId
	 * @return String representation of task with provided id. Empty string if
	 *         task with such id does not exist.
	 */
	public String getTaskStringRepresentation(final long taskId) {
		String taskStringId = taskId + "";
		try {
			final TaskInfo<R> taskInfo = getTaskInfo(taskId);
			taskStringId = taskInfo.getTask().toString();
		} catch (final NoSuchElementException e) {
			// just return empty string
		}
		return taskStringId;
	}

	/**
	 * Starts new thread to observe tasks and cancel those, that are not checked
	 * for longer than "uncheckedTimeLimit"
	 */
	public void startObserving() {
		observer.start();
	}

	/**
	 * Once observing is stopped, it cannot be restarted.
	 */
	public void stopObserving() {
		observer.interrupt();
	}

	/**
	 * Immediately submit provided {@link CallableTask} task for execution.
	 * 
	 * @param task
	 * @return id assigned to created task
	 */
	public long submitTask(final CallableTask<R> task) {
		assert (task != null);
		final long taskId = taskIdCounter.getAndIncrement();

		final IdentifiableTask<R> identifiableTask = new IdentifiableTask<>(
				task, taskId);
		LOGGER.info("Submit task: " + identifiableTask);

		tasks.put(taskId,
				new TaskInfo<>(executorService.submit(identifiableTask),
						identifiableTask));
		return taskId;
	}
}
