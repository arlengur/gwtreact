/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Time;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.apache.log4j.spi.LoggingEvent;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hsqldb.lib.StringInputStream;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.message.AlertMessage;
import com.tecomgroup.qos.communication.message.AlertMessage.AlertAction;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.tools.ToolsUtil;
import com.tecomgroup.qos.tools.impl.util.SendAlertFrame;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * Отсылка алёртов на брокер
 * 
 * @author novohatskiy.r
 * 
 */
@Component
public class SendAlert implements QoSTool {
	private final static Logger LOGGER = Logger.getLogger(SendAlert.class);
	public static AlertMessage createAlert(final AlertAction alertAction,
			final String alertType, final Source source,
			final Source originator, final String settings,
			final PerceivedSeverity perceivedSeverity,
			final SpecificReason reason) {
		String validatedSettings = null;
		if (SimpleUtils.isNotNullAndNotEmpty(settings)) {
			validatedSettings = settings;
		}
		final MAlertIndication alert = new MAlertIndication(alertType, source,
				originator, validatedSettings, perceivedSeverity, new Date(),
				UpdateType.NEW);
		alert.setDetectionValue(10.0 + Math.random() * 10.0);
		alert.setSpecificReason(reason);
		if (alertAction == AlertAction.ACTIVATE) {
			return AlertMessage.activateAlert(alert);
		} else if (alertAction == AlertAction.CLEAR) {
			alert.setIndicationType(UpdateType.AUTO_CLEARED);
			return AlertMessage.clearAlert(alert);
		}
		return null;
	}

	@Value("${alert.send.sleep}")
	private Long sleep;
	@Value("${alert.send.mode}")
	private String mode;
	@Value("${alert.send.source.key}")
	private String sourceKey;
	@Value("${alert.send.type}")
	private String aType;

	@Value("${alert.send.input.file}")
	private String inputFilePath;
	@Value("${alert.send.gui.default.action}")
	private String defaultAction;
	@Value("${alert.send.gui.default.settings}")
	private String defaultSettings;
	@Value("${alert.send.gui.default.perceived.severity}")
	private String defaultPerceivedSeverity;
	@Value("${alert.send.gui.default.specific.reason}")
	private String defaultSpecificReason;

	@Value("${result.send.agent.name}")
	private String agentKey;

	@Value("${task.key}")
	private String taskKey;

	@Value("${amqp.qos.alert.exchange}")
	private String alertExchange;

	private String alertMessageExpirationTime;

	@Autowired
	private AmqpTemplate amqpTemplate;

	private volatile long index = 0;
	@Override
	public void execute() {

		int alertCount = 0;

		if (mode.equalsIgnoreCase("gui")) {
			alertCount = sendAlertsFromGUI();
		} else if (mode.equalsIgnoreCase("file")) {
			alertCount = sendAlertsFromFile();
		} else if (mode.equalsIgnoreCase("stream")) {
			alertCount = sendAlertsFromInputStream();
		} else if (mode.equalsIgnoreCase("single")) {
			for(String tk : taskKey.split(",")) {
				for(String severity : defaultPerceivedSeverity.split(",")) {
					alertCount = sendAlerts(Arrays.asList(createAlert(AlertAction.valueOf(defaultAction.toUpperCase()),
																	  aType,
																	  Source.getTaskSource(SharedModelConfiguration
																						   .createTaskKey(agentKey,
																										  SharedModelConfiguration.IT09A_MODULE_KEY, 
																										  tk)),
																	  Source.getPolicySource(sourceKey),
																	  defaultSettings, 
																	  PerceivedSeverity.valueOf(severity.toUpperCase()),
																	  SpecificReason.valueOf(defaultSpecificReason.toUpperCase()))));
				}
			}
		} else {
			LOGGER.error("'"
					+ mode
					+ "' - Unknown mode, please specify 'alert.send.mode' property correctly");
		}

		LOGGER.debug("Done. Total number of sent alerts is " + alertCount);
	}

	@Override
	public String getDescription() {
		return "Send alerts to the server"
				+ "\nSupported VM arguments:"
				+ "\n\talert.send.sleep - Thread sleep before send a message. 0 means no sleep"
				+ "\n\talert.send.mode - Message sending mode. \"gui\" (default) to use graphical "
				+ "user interace, \"file\" to read messages from file (needs 'alert.send.input.file' property to be defined), "
				+ "\"stream\" to read messages from user input"
				+ "\n\talert.send.input.file - Path to JSON-formatted file with program arguments and message definitions. Works in \"file\" and \"gui\" mode"
				+ "\n\nalert.send.gui.default.action - default action in GUI mode"
				+ "\nalert.send.gui.default.alert.type - default alert type in GUI mode"
				+ "\nalert.send.gui.default.source.type - default source type in GUI mode"
				+ "\nalert.send.gui.default.source - default source in GUI mode"
				+ "\nalert.send.gui.default.settings - default settings in GUI mode"
				+ "\nalert.send.gui.default.perceived.severity - default perceived severity in GUI mode"
				+ "\nalert.send.gui.default.specific.reason - default specific reason in GUI mode"
				+ "\n\n\t Example string for \"stream\" mode"
				+ "\n\t\t [ { \"action\" : \"ACTIVATE\", "
				+ "\"alert\" : { \"alertType\" : { "
				+ "\"name\" : \"qos.it09a.signalLevel\" }, "
				+ "\"sourceType\" : \"TASK\", "
				+ "\"perceivedSeverity\" : \"CRITICAL\", "
				+ "\"specificReason\" : \"NONE\", "
				+ "\"source\" : \"384\" } } ]";
	}

	public List<AlertMessage> readAlertsFromFile(final File inputFile) {
		if (inputFile.exists()) {
			try {
				LOGGER.debug("Loading file: " + inputFile.getAbsolutePath());
				return ToolsUtil.parseListFromJsonFile(new FileInputStream(
						inputFile), AlertMessage.class);
			} catch (final Exception e) {
				LOGGER.error("Error parsing input file", e);
			}
		} else {
			LOGGER.warn("Specified input file does not exist: " + inputFilePath);
		}
		return new ArrayList<AlertMessage>();
	}

	private void sendAlert(final AlertMessage message, final int messageIndex) {

		message.getAlert().setDateTime(
				new Date(System.currentTimeMillis() + (index++)
						* TimeConstants.MILLISECONDS_PER_SECOND));
		if (message.getAction() == AlertAction.ACTIVATE) {
			message.getAlert().setIndicationType(UpdateType.NEW);
		} else if (message.getAction() == AlertAction.CLEAR) {
			message.getAlert().setIndicationType(UpdateType.AUTO_CLEARED);
		}
		if (sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		amqpTemplate.convertAndSend(alertExchange, null, message,
				new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(final Message message)
							throws AmqpException {
						message.getMessageProperties().setExpiration(
								alertMessageExpirationTime);
						return message;
					}
				});
		LOGGER.debug("Sent: " + message.getAction() + " "
				+ message.getAlert().getPerceivedSeverity() + " "
				+ message.getAlert().getAlertType().getName());
	}

	public int sendAlerts(final List<AlertMessage> messages) {
		int messageCount = 0;
		for (final AlertMessage message : messages) {
			sendAlert(message, messageCount);
			messageCount++;
		}
		return messageCount;
	}

	private int sendAlertsFromFile() {
		int alertCount = 0;

		if (inputFilePath != null && !inputFilePath.isEmpty()) {
			for(int i=0; i < 100; i++ ) {
				final File inputFile = new File(inputFilePath);
				alertCount = sendAlerts(readAlertsFromFile(inputFile));
			}
		} else {
			LOGGER.warn("File path specified is null or empty");
		}

		return alertCount;
	}

	private int sendAlertsFromGUI() {
		final SendAlertFrame frame = new SendAlertFrame(this);

		frame.setAgentKey(agentKey);
		frame.setDefaultAction(AlertAction.valueOf(defaultAction.toUpperCase()));
		frame.setDefaultAlertType(SharedModelConfiguration.IT09A_ALERT_TYPE_NAME);
		frame.setDefaultSource(Source.getTaskSource(SharedModelConfiguration
				.createTaskKey(agentKey,
						SharedModelConfiguration.IT09A_MODULE_KEY, taskKey)));
		frame.setDefaultSettings(defaultSettings);
		frame.setDefaultPerceivedSeverity(PerceivedSeverity
				.valueOf(defaultPerceivedSeverity.toUpperCase()));
		frame.setDefaultSpecificReason(SpecificReason
				.valueOf(defaultSpecificReason.toUpperCase()));

		frame.restoreDefaults();

		if (!inputFilePath.isEmpty()) {
			frame.setInput(new File(inputFilePath));
		}

		LOGGER.addAppender(new WriterAppender() {
			@Override
			public void append(final LoggingEvent event) {
				frame.log("[" + new Time(event.getTimeStamp()) + "] "
						+ event.getMessage() + "\n");
			}
		});

		if (amqpTemplate instanceof RabbitTemplate) {
			final ConnectionFactory connectionFactory = ((RabbitTemplate) amqpTemplate)
					.getConnectionFactory();
			final String hostPort = connectionFactory.getHost() + ":"
					+ connectionFactory.getPort();
			frame.setTitle(frame.getTitle() + " (" + hostPort + ")");
		}

		frame.setVisible(true);

		while (frame.isVisible()) {
			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}

		return 0;
	}

	private int sendAlertsFromInputStream() {
		int alertCount = 0;

		final Scanner scanner = new Scanner(System.in);

		System.out
				.println("Type alert parameters in JSON format to send alert or 'exit' to terminate tool");
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			if (line.equals("exit")) {
				break;
			}
			if (!line.isEmpty()) {
				List<AlertMessage> alerts;
				try {
					alerts = ToolsUtil.parseListFromJsonFile(
							new StringInputStream(line), AlertMessage.class);
					alertCount += sendAlerts(alerts);
				} catch (final IOException e) {
					LOGGER.error(e);
				}

			}
		}
		scanner.close();

		return alertCount;
	}

	@Value("${amqp.qos.alarm.message.expiration.time.in.sec}")
	public void setAlertMessageExpirationTime(
			final int alertMessageExpirationTime) {
		this.alertMessageExpirationTime = Long
				.toString(alertMessageExpirationTime
						* TimeConstants.MILLISECONDS_PER_SECOND);
	}

	public void writeAlertsToFile(final File file,
			final List<AlertMessage> alerts) {
		if (!file.exists()) {
			LOGGER.warn("File specified is not exist, creating: "
					+ file.getAbsolutePath());
			try {
				file.createNewFile();
			} catch (final IOException e) {
				e.printStackTrace();
			}
		}
		final ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, alerts);
		} catch (final JsonGenerationException e) {
			e.printStackTrace();
		} catch (final JsonMappingException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		LOGGER.debug("Alerts were successfully saved to: "
				+ file.getAbsolutePath());
	}
}
