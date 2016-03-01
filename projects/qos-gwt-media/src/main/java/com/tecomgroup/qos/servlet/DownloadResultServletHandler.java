/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.HttpRequestHandler;

import com.tecomgroup.qos.ExportResultsWrapper;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.gwt.client.QoSServlets;
import com.tecomgroup.qos.gwt.client.RequestParams;
import com.tecomgroup.qos.service.ResultRetriever;
import com.tecomgroup.qos.util.LingeringTaskExecutor;
import com.tecomgroup.qos.util.RrdUtil;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.TaskStatus;
import com.tecomgroup.qos.util.TaskStatusInfo;

/**
 * Downloads results in .xlsx file using provided url parameters.
 * 
 * @author kunilov.p
 * 
 * @author kshnyakin.m
 * 
 */
public class DownloadResultServletHandler implements HttpRequestHandler {

	private final static Logger LOGGER = Logger
			.getLogger(DownloadResultServletHandler.class);

	private final static String RESULTS_EXPORT_TASK = "resultsExportTask_";

	private final static String RESULTS_EXPORT_TASK_EXECUTOR_NAME = "Results-export-executor";

	private ResultRetriever resultRetriever;

	private final DateTimeFormatter nameFormatter;

	private int exportResultsThreadCountBase;

	private int exportResultsThreadCountMax;

	private int exportResultsUncheckedTimeLimitInSec;

	private int exportResultsExaminationIntervalInSec;

	private int exportResultsBatchSize;

	private LingeringTaskExecutor<File> taskExecutor;

	public DownloadResultServletHandler() {
		nameFormatter = DateTimeFormat.forPattern("dd-MM-yyyy HH-mm");
	}

	private void attachTaskToSession(final HttpSession session,
			final long taskId, final String resultFileName) {
		session.setAttribute(RESULTS_EXPORT_TASK + taskId, resultFileName);
	}

	/**
	 * @param session
	 * @param taskId
	 * @return true if task was created by this session
	 */
	private boolean checkTaskOwnership(final HttpSession session,
			final long taskId) {
		return session.getAttribute(RESULTS_EXPORT_TASK + taskId) != null;
	}

	private void clearExistingTask(final long taskId)
			throws NoSuchElementException, ExecutionException {
		final TaskStatusInfo statusInfo = taskExecutor.checkStatus(taskId,
				false);
		if (statusInfo.getStatus() == TaskStatus.COMPLETED) {
			taskExecutor.clearTask(taskId);
			taskExecutor.getTaskResult(taskId).delete();
		} else {
			taskExecutor.cancelTask(taskId);
			taskExecutor.clearTask(taskId);
		}
	}

	private String getErrorCodeCommonDescription(final int code) {
		String description = "";
		switch (code) {
			case 400 :
				description = "The request is not correct. ";
				break;
			case 401 :
			case 403 :
				description = "You are not authorized to perform this action. ";
				break;
			case 404 :
				description = "Export task is not found. ";
				break;
			case 500 :
				description = "Some execution error occurred. ";
				break;
		}
		return description;
	}

	private ExportResultsWrapper getExportResultsWrapper(
			final BufferedReader reader) {
		String data = "";
		try {
			data = FileCopyUtils.copyToString(reader);
		} catch (final Exception ex) {
			LOGGER.error("Unable to read json from request");
		}
		return resultRetriever.deserializeBean(data.toString());
	}

	private void handleCancelTaskRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final HttpSession session = request.getSession(false);
		if (session != null) {
			final String taskIdParameter = request
					.getParameter(RequestParams.exportResultsTaskParameterName);
			Long taskId = null;
			try {
				taskId = Long.parseLong(taskIdParameter);
				if (checkTaskOwnership(session, taskId)) {
					clearExistingTask(taskId);
					response.setStatus(200);
				} else {
					send401ErrorResponse(response, taskIdParameter, null);
				}
			} catch (final NumberFormatException ex) {
				send400ErrorResponse(response, taskIdParameter, null, ex);
			} catch (final NoSuchElementException ex) {
				send404ErrorResponse(response, taskIdParameter, null, ex);
			} catch (final ExecutionException ex) {
				final String errorMessage = "An error occurred while cancelling export task ("
						+ taskExecutor.getTaskStringRepresentation(taskId)
						+ "). ";
				send500ErrorResponse(response, taskIdParameter, errorMessage,
						ex);
			} finally {
				response.flushBuffer();
			}
		}
	}

	@Override
	public void handleRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws ServletException,
			IOException {
		if ("POST".equalsIgnoreCase(request.getMethod())) {
			handleStartTaskRequest(request, response);
		} else if ("DELETE".equalsIgnoreCase(request.getMethod())) {
			handleCancelTaskRequest(request, response);
		} else if ("GET".equalsIgnoreCase(request.getMethod())) {
			if (("/" + QoSServlets.downloadResultServletStatusUrl)
					.equalsIgnoreCase(request.getPathInfo())) {
				handleStatusRequest(request, response);
			} else if (("/" + QoSServlets.downloadResultServletResultUrl)
					.equalsIgnoreCase(request.getPathInfo())) {
				handleResultRequest(request, response);
			} else {
				send404ErrorResponse(response, null, "Invalid request url. ");
			}
		}
	}

	private void handleResultRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final HttpSession session = request.getSession(false);
		if (session != null) {
			final String taskIdParameter = request
					.getParameter(RequestParams.exportResultsTaskParameterName);
			Long taskId = null;
			try {
				taskId = Long.parseLong(taskIdParameter);

				if (checkTaskOwnership(session, taskId)) {
					final TaskStatusInfo statusInfo = taskExecutor.checkStatus(
							taskId, false);
					if (statusInfo.getStatus() == TaskStatus.COMPLETED) {
						final File exportedResultsFile = taskExecutor
								.getTaskResult(taskId);

						if (exportedResultsFile != null) {
							final String fileName = (String) session
									.getAttribute(RESULTS_EXPORT_TASK + taskId);

							response.setCharacterEncoding("UTF-8");
							response.setContentType("application/vnd.ms-excel");
							response.setHeader("Content-Disposition",
									"attachment; filename=\"" + fileName + "\"");

							try (InputStream input = new FileInputStream(
									exportedResultsFile)) {
								IOUtils.copy(input, response.getOutputStream());
								LOGGER.info("Sent to client export results of the task: "
										+ taskExecutor
												.getTaskStringRepresentation(taskId));
							} catch (final Exception ex) {
								final String errorMessage = "An error occurred while sending export results of task ("
										+ taskExecutor
												.getTaskStringRepresentation(taskId)
										+ ") to client. ";
								send500ErrorResponse(response, taskIdParameter,
										errorMessage, ex);
							} finally {
								exportedResultsFile.delete();
								taskExecutor.clearTask(taskId);
							}
						} else {
							final String errorMessage = "An error occurred while executing the task ("
									+ taskExecutor
											.getTaskStringRepresentation(taskId)
									+ "). File with results is null. ";
							send500ErrorResponse(response, taskIdParameter,
									errorMessage);
						}
					}
				} else {
					send403ErrorResponse(response, taskIdParameter, null);
				}
			} catch (final NumberFormatException ex) {
				send400ErrorResponse(response, taskIdParameter, null, ex);
			} catch (final NoSuchElementException ex) {
				send404ErrorResponse(response, taskIdParameter, null, ex);
			} catch (final ExecutionException ex) {
				send500ErrorResponse(response,
						taskExecutor.getTaskStringRepresentation(taskId), null,
						ex);
			} catch (final CancellationException ex) {
				final String errorMessage = "The requested task was already canceled and deleted ("
						+ taskExecutor.getTaskStringRepresentation(taskId)
						+ "). ";
				send404ErrorResponse(response, taskIdParameter, errorMessage,
						ex);
			} finally {
				response.flushBuffer();
			}
		}
	}

	private void handleStartTaskRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final ExportResultsWrapper exportResultsWrapper = getExportResultsWrapper(request
				.getReader());
		final HttpSession session = request.getSession(true);

		final ExportTaskClientInfo taskClientInfo = new ExportTaskClientInfo(
				session.getId(), request.getRemoteAddr());

		final ExportResultsTask task = new ExportResultsTask(taskClientInfo,
				resultRetriever, exportResultsWrapper, exportResultsBatchSize);

		final long taskId = taskExecutor.submitTask(task);
		final String fileName = RrdUtil.createExportResultsFileName(
				nameFormatter.print(new DateTime(exportResultsWrapper.startDateTime)),
				nameFormatter.print(new DateTime(exportResultsWrapper.endDateTime)),
				exportResultsWrapper.timeZone, exportResultsWrapper.rawData)
				+ ".xlsx";
		task.setTaskIdentifier(taskExecutor.getTaskStringRepresentation(taskId));
		attachTaskToSession(session, taskId, fileName);
		response.getWriter().print(taskId);
		response.flushBuffer();
	}

	private void handleStatusRequest(final HttpServletRequest request,
			final HttpServletResponse response) throws IOException {
		final HttpSession session = request.getSession(false);

		if (session != null) {
			final String taskIdParameter = request
					.getParameter(RequestParams.exportResultsTaskParameterName);
			try {
				final long taskId = Long.parseLong(taskIdParameter);
				final TaskStatusInfo statusInfo = taskExecutor.checkStatus(
						taskId, true);
				final TaskStatus status = statusInfo.getStatus();
				response.getWriter().print(status.name());
				if (status == TaskStatus.RUNNING) {
					response.getWriter().print(
							":" + statusInfo.getPercentDone());
				} else if (status == TaskStatus.ERROR) {
					try {
						taskExecutor.getTaskResult(taskId);
					} catch (final ExecutionException e) {
						LOGGER.error(
								"Error occurred while exporting task: "
										+ taskExecutor
												.getTaskStringRepresentation(taskId),
								e);
						final String message = e.getCause() != null ? e
								.getCause().getLocalizedMessage() : e
								.getLocalizedMessage();
						response.getWriter().print(":" + message);
					}
					taskExecutor.clearTask(taskId);
				}
			} catch (final NumberFormatException e) {
				send400ErrorResponse(response, taskIdParameter, null);
				return;
			} catch (final NoSuchElementException e) {
				send404ErrorResponse(response, taskIdParameter, null);
			} finally {
				response.flushBuffer();
			}
		}
	}

	public void init() {
		taskExecutor = new LingeringTaskExecutor<>(
				RESULTS_EXPORT_TASK_EXECUTOR_NAME,
				exportResultsThreadCountBase, exportResultsThreadCountMax,
				exportResultsUncheckedTimeLimitInSec
						* TimeConstants.MILLISECONDS_PER_SECOND,
				exportResultsExaminationIntervalInSec
						* TimeConstants.MILLISECONDS_PER_SECOND);
		taskExecutor.startObserving();
	}

	private void send400ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage) throws IOException {
		send400ErrorResponse(response, taskId, errorMessage, null);
	}

	private void send400ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage,
			final Throwable throwable) throws IOException {

		String defaultErrorMessage = errorMessage;
		if (!SimpleUtils.isNotNullAndNotEmpty(defaultErrorMessage)) {
			defaultErrorMessage = "Incorrect export task id (" + taskId + "). ";
		}
		if (throwable != null) {
			LOGGER.error(defaultErrorMessage, throwable);
		} else {
			LOGGER.error(defaultErrorMessage);
		}
		response.sendError(403, getErrorCodeCommonDescription(403)
				+ errorMessage);
	}

	private void send401ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage) throws IOException {
		send401ErrorResponse(response, taskId, errorMessage, null);
	}

	private void send401ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage,
			final Throwable throwable) throws IOException {

		String defaultErrorMessage = errorMessage;
		if (!SimpleUtils.isNotNullAndNotEmpty(defaultErrorMessage)) {
			defaultErrorMessage = "Export task with provided id (" + taskId
					+ ") not found. ";
		}
		if (throwable != null) {
			LOGGER.error(defaultErrorMessage, throwable);
		} else {
			LOGGER.error(defaultErrorMessage);
		}
		response.sendError(401, getErrorCodeCommonDescription(401)
				+ defaultErrorMessage);
	}

	private void send403ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage) throws IOException {
		send403ErrorResponse(response, taskId, errorMessage, null);
	}

	private void send403ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage,
			final Throwable throwable) throws IOException {

		String defaultErrorMessage = errorMessage;
		if (!SimpleUtils.isNotNullAndNotEmpty(defaultErrorMessage)) {
			defaultErrorMessage = "Export task with provided id (" + taskId
					+ ") not found. ";
		}
		if (throwable != null) {
			LOGGER.error(defaultErrorMessage, throwable);
		} else {
			LOGGER.error(defaultErrorMessage);
		}
		response.sendError(403, getErrorCodeCommonDescription(403)
				+ defaultErrorMessage);
	}

	private void send404ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage) throws IOException {
		send404ErrorResponse(response, taskId, errorMessage, null);
	}

	private void send404ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage,
			final Throwable throwable) throws IOException {

		String defaultErrorMessage = errorMessage;
		if (!SimpleUtils.isNotNullAndNotEmpty(defaultErrorMessage)) {
			defaultErrorMessage = "Export task with provided id (" + taskId
					+ ") not found. ";
		}
		if (throwable != null) {
			LOGGER.error(defaultErrorMessage, throwable);
		} else {
			LOGGER.error(defaultErrorMessage);
		}
		response.sendError(404, getErrorCodeCommonDescription(404)
				+ defaultErrorMessage);
	}

	private void send500ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage) throws IOException {
		send500ErrorResponse(response, taskId, errorMessage, null);
	}

	private void send500ErrorResponse(final HttpServletResponse response,
			final String taskId, final String errorMessage,
			final Throwable throwable) throws IOException {

		String defaultErrorMessage = errorMessage;
		if (!SimpleUtils.isNotNullAndNotEmpty(defaultErrorMessage)) {
			defaultErrorMessage = "An error occurred while executing the task ("
					+ taskId + "). ";
		}
		if (throwable != null) {
			defaultErrorMessage += throwable.getMessage();
			LOGGER.error(defaultErrorMessage, throwable);
		} else {
			LOGGER.error(defaultErrorMessage);
		}
		response.sendError(500, getErrorCodeCommonDescription(500)
				+ defaultErrorMessage);
	}

	public void setExportResultsBatchSize(final int exportResultsBatchSize) {
		this.exportResultsBatchSize = exportResultsBatchSize;
	}

	public void setExportResultsExaminationIntervalInSec(
			final int exportResultsExaminationIntervalInSec) {
		this.exportResultsExaminationIntervalInSec = exportResultsExaminationIntervalInSec;
	}

	public void setExportResultsThreadCountBase(
			final int exportResultsThreadCountBase) {
		this.exportResultsThreadCountBase = exportResultsThreadCountBase;
	}

	public void setExportResultsThreadCountMax(
			final int exportResultsThreadCountMax) {
		this.exportResultsThreadCountMax = exportResultsThreadCountMax;
	}

	public void setExportResultsUncheckedTimeLimitInSec(
			final int exportResultsUncheckedTimeLimitInSec) {
		this.exportResultsUncheckedTimeLimitInSec = exportResultsUncheckedTimeLimitInSec;
	}

	public void setResultRetriever(final ResultRetriever resultRetriever) {
		this.resultRetriever = resultRetriever;
	}
}
