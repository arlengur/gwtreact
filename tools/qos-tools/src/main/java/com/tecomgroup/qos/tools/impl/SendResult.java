/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.tools.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.message.ResultMessage;
import com.tecomgroup.qos.communication.message.ResultMessage.ResultType;
import com.tecomgroup.qos.communication.result.Result;
import com.tecomgroup.qos.tools.QoSTool;
import com.tecomgroup.qos.tools.impl.util.SendResultConfiguration;
import com.tecomgroup.qos.util.MediaModelConfiguration;
import com.tecomgroup.qos.util.SharedModelConfiguration;

/**
 * 
 * Отсылка результатов на брокер
 * 
 * @author abondin
 * 
 */
@Component
public class SendResult implements QoSTool {
	private final static Logger LOGGER = Logger.getLogger(SendResult.class);

	@Value("${result.send.starttime}")
	private Long startTime;
	@Value("${result.send.endtime}")
	private Long endTime;
	@Value("${result.send.messages.interval}")
	private Long interval;
	@Value("${result.type}")
	private ResultType resultType;
	@Value("${result.send.sleep}")
	private Long sleep;
	@Value("${result.send.mode}")
	private String mode;
	@Value("${result.send.input.file}")
	private String inputFilePath;
	@Value("${result.send.message.count}")
	private Integer sendMessageCount;
	@Value("${result.send.generated.message.count}")
	private Integer sendGeneratedMessageCount;

	@Value("${result.send.program.number}")
	private String programNumber;
	@Value("${result.send.program.display.name}")
	private String programDisplayName;

	@Value("${result.send.agent.name}")
	private String agentName;

	@Value("${task.key}")
	private String[] taskKeys;

	@Value("${result.send.register.agent}")
	private boolean sendResultRegisterAgent = false;

	@Value("${register.agents.count}")
	private Long agentsCount;

	@Autowired
	private AmqpTemplate amqpTemplate;

	@Autowired
	private SendRegisterAgent sendRegisterAgent;

	private final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyyMMddHHmmss");

	private final Map<String, Long> currentResultMap = new HashMap<String, Long>();

	@Override
	public void execute() {
		// Register agent
		if (sendResultRegisterAgent) {
			sendRegisterAgent.execute();
		}

		if (interval < 1l) {
			LOGGER.warn("result.send.messages.interval must be >=1");
			interval = 1l;
		}

		int messageCount = 0;

		mode = mode.toLowerCase();
		if (mode.equals("random")) {
			messageCount = sendGeneratedMessages();
		} else if (mode.equals("linear")) {
			messageCount = sendLinearMessages();
		}else if (mode.equals("file")) {
			messageCount = sendMessagesFromFile();
		} else if (mode.equals("stream")) {
			messageCount = sendMessagesFromInputStream();
		} else {
			LOGGER.error("'"
					+ mode
					+ "' - Unknown mode, please specify 'result.send.mode' property correctly");
		}

		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Done. Total number of sent messages is "
					+ messageCount + " by group of " + sendMessageCount);
		}
	}

	/**
	 * @return the amqpTemplate
	 */
	public AmqpTemplate getAmqpTemplate() {
		return amqpTemplate;
	}

	private long getCurrentMessageTime(final long startTime,
			final int messageIndex) {
		return startTime + messageIndex * interval
				* TimeConstants.MILLISECONDS_PER_SECOND;
	}

	private long getCurrentResult(final String parameterName) {
		Long currentResult = currentResultMap.get(parameterName);
		if (currentResult == null) {
			currentResult = 1l;
		} else {
			currentResult++;
		}
		currentResultMap.put(parameterName, currentResult);

		return currentResult;
	}

	@Override
	public String getDescription() {
		return "Send result messages to the server"
				+ "\nSupported VM arguments:"
				+ "\n\tresult.send.agent.name - Name of the agent. Default 221"
				+ "\n\tresult.send.starttime - first message time in seconds.\n\t\t 0 = now,\n\t\t <0 = (now-n sec),\n\t\t >0 = number of seconds from January 1, 1970, 00:00:00 GMT."
				+ "\n\tresult.send.endtime - first message time in seconds.\n\t\t 0 = now,\n\t\t <0 = (now-n sec),\n\t\t >0 = number of seconds from January 1, 1970, 00:00:00 GMT."
				+ "\n\tresult.send.messages.interval - Message interval in seconds. Minimum value = 1"
				+ "\n\tresult.send.sleep - Thread sleep before send a message. 0 means no sleep"
				+ "\n\tresult.send.mode - Message sending mode. \"random\" (default) to generate messages, \"file\" to read messages from file (needs 'result.send.input.file' property to be defined), \"stream\" to read messages from user input"
				+ "\n\tresult.send.input.file - Path to JSON-formatted file with program arguments and message definitions. Works only in \"file\" mode"
				+ "\n\n\t\"stream\" mode takes strings in following formats:"
				+ "\n\t\t {\"propertyOne\" : 10.0, \"propertyTwo\" : 20.0, \"propertyThree\" : 15.5 (and so on...)}"
				+ "\n\t\t [{\"propertyOne\" : 10.0, \"propertyTwo\" : 20.0, \"propertyThree\" : 15.5}, {\"propertyOne\" : 10.0, \"propertyTwo\" : 20.0, \"propertyThree\" : 15.5} (and so on...) ]"
				+ "\n\t EXAMPLE:"
				+ "\n\t\t {\"signalLevel\" : 60.0, \"videoAudio\" : 15.5, \"signalNoise\" : 15.5, \"nicamLevel\" : 15.5}";

	}

	private long getRealTime(final long time, final long now) {
		if (time < 0) {
			return now + time * TimeConstants.MILLISECONDS_PER_SECOND;
		} else if (time > 0) {
			return time * TimeConstants.MILLISECONDS_PER_SECOND;
		} else {
			return now;
		}
	}

	private int sendGeneratedMessages() {
		final long now = DateTime.now().getMillis();
		final long startTime = getRealTime(this.startTime, now);
		final long endTime = getRealTime(this.endTime, now);
		long currentStartTime = startTime;

		final List<Map<String, Double>> messages = new LinkedList<Map<String, Double>>();
		int messageCount = 0;
		int messageIndex = 1;
		boolean sent = false;
		while (currentStartTime < endTime
				&& getCurrentMessageTime(startTime, messageIndex - 1) < endTime) {
			final Map<String, Double> parameters = new HashMap<String, Double>();
			for (final String parameterName : SharedModelConfiguration.IT09A_MODULE_PARAMETER_LIST) {
				if (parameterName
						.equals(SharedModelConfiguration.FAKE_STRONG_SIGNAL_BOOLEAN)) {
					parameters.put(parameterName,
							1.0 - Math.round(0.45 + Math.random() * 0.55));
				} else {
					parameters.put(parameterName, 1 + Math.random() * 100);
				}
			}
			messages.add(parameters);
			if (messageIndex % sendGeneratedMessageCount == 0) {
				messageCount += sendMessages(currentStartTime, messages);
				currentStartTime += sendGeneratedMessageCount * interval
						* TimeConstants.MILLISECONDS_PER_SECOND;
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(messageIndex + " messages are sent");
				}
				sent = true;
				messages.clear();
			} else {
				sent = false;
			}
			messageIndex++;
		}
		if (!sent) {
			messageCount += sendMessages(currentStartTime, messages);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(messageIndex + " messages are sent");
			}
			messages.clear();
		}
		return messageCount;
	}

	private int sendLinearMessages() {
		final long now = DateTime.now().getMillis();
		final long startTime = getRealTime(this.startTime, now);
		final long endTime = getRealTime(this.endTime, now);
		long currentStartTime = startTime;

		final List<Map<String, Double>> messages = new LinkedList<Map<String, Double>>();
		int messageCount = 0;
		int messageIndex = 1;
		boolean sent = false;
		while (currentStartTime < endTime
				&& getCurrentMessageTime(startTime, messageIndex - 1) < endTime) {
			final Map<String, Double> parameters = new HashMap<String, Double>();
			for (final String parameterName : SharedModelConfiguration.IT09A_MODULE_PARAMETER_LIST) {
				final Long currentResult = getCurrentResult(parameterName);
				if (parameterName
						.equals(SharedModelConfiguration.FAKE_STRONG_SIGNAL_BOOLEAN)) {
					parameters.put(parameterName, (double) (currentResult % 2));
				} else {
					parameters.put(parameterName, (double) currentResult);
				}
			}
			messages.add(parameters);
			if (messageIndex % sendGeneratedMessageCount == 0) {
				messageCount += sendMessages(currentStartTime, messages);
				currentStartTime += sendGeneratedMessageCount * interval
						* TimeConstants.MILLISECONDS_PER_SECOND;
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(messageIndex + " messages are sent");
				}
				sent = true;
				messages.clear();
			} else {
				sent = false;
			}
			messageIndex++;
		}
		if (!sent) {
			messageCount += sendMessages(currentStartTime, messages);
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(messageIndex + " messages are sent");
			}
			messages.clear();
		}
		return messageCount;
	}

	private void sendMessage(final Map<Long, Map<String, Double>> message) {

		final List<Result> results = new LinkedList<Result>();
		int messageIndex = 1;
		for (final Map.Entry<Long, Map<String, Double>> messageEntry : message
				.entrySet()) {
			final Result result = new Result();
			result.setResultDateTime(DATE_FORMAT.print(new DateTime(messageEntry.getKey()).toDateTime(DateTimeZone.UTC)));
			result.setProperties(SharedModelConfiguration
					.createPropertyConfigurationsAsMapWithValues(programNumber,
							programDisplayName));
			result.setParameters(messageEntry.getValue());
			results.add(result);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug((messageIndex++) + " message in group of "
						+ message.size() + ": " + messageEntry.getKey() + " - "
						+ messageEntry.getValue());
			}
		}
		if (sleep > 0) {
			try {
				Thread.sleep(sleep);
			} catch (final InterruptedException e) {
				LOGGER.error(e);
			}
		}
		String agentKey = null;
		for (int index = 1; index <= agentsCount; index++) {
			if (index == 1) {
				agentKey = agentName;
			} else {
				agentKey = agentName + "-" + index;
			}
			for (final String taskKey : taskKeys) {
				final ResultMessage resultMessage = new ResultMessage();
				resultMessage.setTaskKey(MediaModelConfiguration.createTaskKey(
						agentKey, null, taskKey));
				resultMessage.setResults(results);
				resultMessage.setResultType(resultType);
				amqpTemplate.convertAndSend("qos.result", "agent-" + agentKey,
						resultMessage);
			}
		}
	}

	private int sendMessages(final long startTime,
			final List<Map<String, Double>> parametersByMessage) {
		long messageTime = startTime;
		int processedMessageCount = 1;
		int sentMessageCount = 0;

		boolean sent = false;
		final Map<Long, Map<String, Double>> groupMessage = new TreeMap<Long, Map<String, Double>>();
		for (final Map<String, Double> parameters : parametersByMessage) {
			sent = false;
			groupMessage.put(messageTime, parameters);
			if (processedMessageCount % sendMessageCount == 0) {
				sendMessage(groupMessage);
				sentMessageCount++;
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(sentMessageCount
							+ " message is sent by group of "
							+ groupMessage.size());
				}
				sent = true;
				groupMessage.clear();
			}
			processedMessageCount++;
			messageTime += interval * TimeConstants.MILLISECONDS_PER_SECOND;
		}

		if (!sent) {
			sendMessage(groupMessage);
			sentMessageCount++;
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info(sentMessageCount + " message is sent by group of "
						+ groupMessage.size());
			}
			groupMessage.clear();
		}

		return sentMessageCount;
	}

	private int sendMessagesFromFile() {
		int messageCount = 0;
		if (inputFilePath != null && !inputFilePath.isEmpty()) {
			final File inputFile = new File(inputFilePath);
			if (inputFile.exists()) {
				final ObjectMapper mapper = new ObjectMapper();
				try {
					final SendResultConfiguration conf = mapper.reader(
							SendResultConfiguration.class).readValue(inputFile);
					startTime = conf.getStartTimeLong() != null ? conf
							.getStartTimeLong() : startTime;
					endTime = conf.getEndTimeLong() != null ? conf
							.getEndTimeLong() : endTime;
					sleep = conf.getSleep() != null ? conf.getSleep() : sleep;
					taskKeys = conf.getTaskKeys() != null
							? conf.getTaskKeys()
							: taskKeys;
					agentName = conf.getAgent() != null
							? conf.getAgent()
							: agentName;

					final Long interval = conf.getInterval();
					this.interval = interval != null && interval >= 1L
							? interval
							: this.interval;

					messageCount = sendMessages(
							getRealTime(startTime, DateTime.now().getMillis()),
							conf.getParametersByMessage());

				} catch (final JsonProcessingException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			} else {
				LOGGER.warn("Input file specified is not exist: "
						+ inputFilePath);
			}
		}
		return messageCount;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private int sendMessagesFromInputStream() {
		int messageCount = 0;
		final ObjectMapper mapper = new ObjectMapper();
		final Scanner scanner = new Scanner(System.in);

		System.out.println("Type message parameters in JSON format to send message or 'exit' to terminate tool");

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.equals("exit")) {
				break;
			}
			if (!line.isEmpty()) {
				try {
					final Object result = mapper.reader(Object.class)
							.readValue(line);
					if (result instanceof Map) {
						final Map<Long, Map<String, Double>> message = new TreeMap<Long, Map<String, Double>>();
						message.put(DateTime.now().getMillis(), (Map) result);
						sendMessage(message);
						messageCount++;
					} else if (result instanceof List) {
						messageCount += sendMessages(getRealTime(startTime,	DateTime.now().getMillis()), (List) result);
					}
				} catch (final JsonProcessingException e) {
					e.printStackTrace();
				} catch (final IOException e) {
					e.printStackTrace();
				}
			}
			line = scanner.nextLine();
		}
		scanner.close();

		return messageCount;
	}

	/**
	 * @param agentName
	 *            the agentName to set
	 */
	public void setAgentName(final String agentName) {
		this.agentName = agentName;
	}

	/**
	 * @param amqpTemplate
	 *            the amqpTemplate to set
	 */
	public void setAmqpTemplate(final AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	/**
	 * @param endTime
	 *            the endTime to set
	 */
	public void setEndTime(final Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * @param interval
	 *            the interval to set
	 */
	public void setInterval(final Long interval) {
		this.interval = interval;
	}

	/**
	 * @param mode
	 *            the mode to set
	 */
	public void setMode(final String mode) {
		this.mode = mode;
	}

	/**
	 * @param resultType
	 *            the resultType to set
	 */
	public void setResultType(final ResultType resultType) {
		this.resultType = resultType;
	}

	/**
	 * @param sendGeneratedMessageCount
	 *            the sendGeneratedMessageCount to set
	 */
	public void setSendGeneratedMessageCount(
			final Integer sendGeneratedMessageCount) {
		this.sendGeneratedMessageCount = sendGeneratedMessageCount;
	}
	/**
	 * @param sendMessageCount
	 *            the sendMessageCount to set
	 */
	public void setSendMessageCount(final Integer sendMessageCount) {
		this.sendMessageCount = sendMessageCount;
	}

	/**
	 * @param sendResultRegisterAgent
	 *            the sendResultRegisterAgent to set
	 */
	public void setSendResultRegisterAgent(final boolean sendResultRegisterAgent) {
		this.sendResultRegisterAgent = sendResultRegisterAgent;
	}

	/**
	 * @param sleep
	 *            the sleep to set
	 */
	public void setSleep(final Long sleep) {
		this.sleep = sleep;
	}
	/**
	 * @param startTime
	 *            the startTime to set
	 */
	public void setStartTime(final Long startTime) {
		this.startTime = startTime;
	}

	/**
	 * @param taskKeys
	 *            the taskKeys to set
	 */
	public void setTaskKeys(final String[] taskKeys) {
		this.taskKeys = taskKeys;
	}
}
