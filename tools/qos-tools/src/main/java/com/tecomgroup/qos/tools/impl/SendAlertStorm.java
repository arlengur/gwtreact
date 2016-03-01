/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.message.AlertMessage;
import com.tecomgroup.qos.communication.message.AlertMessage.AlertAction;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Отправка большого кол-ва аварийных сообщений
 * 
 * <ul>
 * <li>Использую разные settings, посылает алёрты на задачу(задачи)</li>
 * <li>Увеличивает важность (warning->critical)</li>
 * <li>Уменьшает важность (critical->warning)</li>
 * <li>Увеличивает важность (warning->critical)</li>
 * <li>Сбрасывает алёрты</li>
 * <li>Переходит к шагу по посылке алёртов</li>
 * </ul>
 * 
 * alert.storm.alerts.counts - число алёртов (по умолчанию 100)
 * alert.storm.iterations.counts -число повторов (по умолчанию 100)
 * 
 * @author abondin
 */
@Component
public class SendAlertStorm implements QoSTool {

	private interface BulkAction {
		public void execute(int iterationNumber, int alertNumber);
	}

	private final static Logger LOGGER = Logger.getLogger(SendAlertStorm.class);

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Value("${result.send.agent.name}")
	private String agentName;

	@Value("${alert.storm.alerts.counts}")
	private Integer alerts;
	@Value("${alert.storm.iterations.counts}")
	private Integer iterations;

	@Value("${alert.send.gui.default.alert.type}")
	private String defaultAlertType;
	@Value("${amqp.qos.alert.exchange}")
	private String alertExchange;

	private String alertMessageExpirationTime;

	@Value("${task.key}")
	private String[] taskKeys;

	private final static Long ALERT_INDICATION_PERIOD = TimeUnit.MINUTES
			.toMillis(1);

	private Date alertDateTime;

	private void bulkAction(final int iteration, final BulkAction action) {
		for (int alertNumber = 1; alertNumber <= alerts; alertNumber++) {
			action.execute(iteration, alertNumber);
		}
	}

	@Override
	public void execute() {
		// Send new alert indication every minute
		// 5 - number of actions with one alert (send, sev_up, sev_down, sev_up,
		// clear)
		final long alertDateTimeShift = alerts * iterations * 5
				* ALERT_INDICATION_PERIOD;
		alertDateTime = new Date(System.currentTimeMillis()
				- alertDateTimeShift);

		for (int iteration = 1; iteration <= iterations; iteration++) {
			storm(iteration);
		}
	}

	@Override
	public String getDescription() {
		return "Send alerts to the server"
				+ "\nSupported VM arguments:"
				+ "\n\talert.storm.alerts.counts - Number of alerts to send (100 by default)"
				+ "\n\talert.storm.iterations.counts - Number of iterations (100 by default)";
	}

	private void sendAlert(final AlertAction action, final String settings,
			final PerceivedSeverity severity, final UpdateType updateType) {
		String validatedSettings = null;
		if (SimpleUtils.isNotNullAndNotEmpty(settings)) {
			validatedSettings = settings;
		}
		if (taskKeys.length > 0) {
			final String taskKey = MediaModelConfiguration.createTaskKey(
					agentName, null, taskKeys[0]);
			final MAlertIndication alert = new MAlertIndication(
					defaultAlertType, Source.getTaskSource(taskKey),
					Source.getTaskSource(taskKey), validatedSettings, severity,
					alertDateTime, UpdateType.NEW);
			alert.setDetectionValue(10.0 + Math.random() * 10.0);
			alert.setSpecificReason(SpecificReason.NONE);

			final AlertMessage alertMessage = new AlertMessage();
			alertMessage.setAction(action);
			alertMessage.setAlert(alert);

			amqpTemplate.convertAndSend(alertExchange, null, alertMessage,
					new MessagePostProcessor() {

						@Override
						public Message postProcessMessage(final Message message)
								throws AmqpException {
							message.getMessageProperties().setExpiration(
									alertMessageExpirationTime);
							return message;
						}
					});

			alertDateTime = new Date(alertDateTime.getTime()
					+ ALERT_INDICATION_PERIOD);
		} else {
			LOGGER.warn("taskKeys array is empty");
		}

	}

	@Value("${amqp.qos.alarm.message.expiration.time.in.sec}")
	public void setAlertMessageExpirationTime(
			final int alertMessageExpirationTime) {
		this.alertMessageExpirationTime = Long
				.toString(alertMessageExpirationTime
						* TimeConstants.MILLISECONDS_PER_SECOND);
	}

	/**
	 * @param iteration
	 */
	private void storm(final int iteration) {
		LOGGER.info("--- Send new alerts. Iteration #" + iteration);
		bulkAction(iteration, new BulkAction() {
			@Override
			public void execute(final int iterationNumber, final int alertNumber) {
				sendAlert(AlertAction.ACTIVATE, "Al" + alertNumber,
						PerceivedSeverity.WARNING, MAlertType.UpdateType.NEW);
			}
		});

		LOGGER.info("Upgrade severity. Iteration #" + iteration);
		bulkAction(iteration, new BulkAction() {
			@Override
			public void execute(final int iterationNumber, final int alertNumber) {
				sendAlert(AlertAction.ACTIVATE, "Al" + alertNumber,
						PerceivedSeverity.CRITICAL, MAlertType.UpdateType.NEW);
			}
		});

		LOGGER.info("Degrade severity. Iteration #" + iteration);
		bulkAction(iteration, new BulkAction() {
			@Override
			public void execute(final int iterationNumber, final int alertNumber) {
				sendAlert(AlertAction.ACTIVATE, "Al" + alertNumber,
						PerceivedSeverity.WARNING, MAlertType.UpdateType.NEW);
			}
		});

		LOGGER.info("Upgrade severity. Iteration #" + iteration);
		bulkAction(iteration, new BulkAction() {
			@Override
			public void execute(final int iterationNumber, final int alertNumber) {
				sendAlert(AlertAction.ACTIVATE, "Al" + alertNumber,
						PerceivedSeverity.CRITICAL, MAlertType.UpdateType.NEW);
			}
		});

		LOGGER.info("Clear alerts. Iteration #" + iteration);
		bulkAction(iteration, new BulkAction() {
			@Override
			public void execute(final int iterationNumber, final int alertNumber) {
				sendAlert(AlertAction.CLEAR, "Al" + alertNumber,
						PerceivedSeverity.WARNING,
						MAlertType.UpdateType.AUTO_CLEARED);
			}
		});
	}
}
