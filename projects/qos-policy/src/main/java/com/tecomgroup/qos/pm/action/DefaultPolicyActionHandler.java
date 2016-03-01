/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.pm.action;

import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.MAIL_BODY;
import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.MAIL_SUBJECT;
import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.SMS_BODY;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.tecomgroup.qos.domain.MUser;
import org.apache.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.core.TopicExchange;

import com.tecomgroup.qos.AbstractSender;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.communication.message.AlertMessage;
import com.tecomgroup.qos.communication.message.PolicySendActionMessage;
import com.tecomgroup.qos.domain.MAlertIndication;
import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.SpecificReason;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.UserSettings.NotificationLanguage;
import com.tecomgroup.qos.domain.pm.MPolicy;
import com.tecomgroup.qos.domain.pm.MPolicyActionWithContacts;
import com.tecomgroup.qos.domain.pm.MPolicySendAlert;
import com.tecomgroup.qos.domain.pm.MPolicySendEmail;
import com.tecomgroup.qos.domain.pm.MPolicySendSms;
import com.tecomgroup.qos.pm.service.TemplateService;
import com.tecomgroup.qos.util.RussianTransliterator;
import com.tecomgroup.qos.service.PolicyManagerService;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;

/**
 * @author abondin
 * 
 */
public class DefaultPolicyActionHandler implements ActionHandler {

	private static enum SendEmailMode {
		ALL, SEPARATELY
	}

	private AmqpTemplate amqpTemplate;

	private FanoutExchange alertExchange;

	private TopicExchange actionExchange;

	private TemplateService templateService;

	private String emailQueueRoutingKey;

	private String smsQueueRoutingKey;

	private String sendEmailMode;

	private final Logger LOGGER = Logger
			.getLogger(DefaultPolicyActionHandler.class);

	private final DecimalFormat formatter = new DecimalFormat("#0.#####");
	private String alertMessageExpirationTime;

	@Override
	public void doAction(final MPolicy policy,
			final Map<String, Object> outputParameters) {
		final List<MPolicySendEmail> sendEmailActions = policy
				.getSendEmailActions();

		final List<MPolicySendSms> sendSmsActions = policy.getSendSmsActions();

		for (final MPolicySendAlert action : policy.getSendAlertActions()) {
			processSendAlertAction(policy, outputParameters, action);
		}

		if (sendEmailActions.size() > 0) {
			processSendEmailAction(policy, outputParameters, sendEmailActions);
		}

		if (sendSmsActions.size() > 0) {
			processSendSmsAction(policy, outputParameters, sendSmsActions);
		}
	}

	private List<MContactInformation> extractContactsFromActions(
			final List<? extends MPolicyActionWithContacts> actions) {
		final List<MContactInformation> allContacts = new ArrayList<>();
		for (final MPolicyActionWithContacts action : actions) {
			final List<MContactInformation> contacts = action.getContacts();
			if (SimpleUtils.isNotNullAndNotEmpty(contacts)) {
				allContacts.addAll(contacts);
			}
		}
		return allContacts;
	}

	private SendEmailMode getCurrentSendEmailMode() {
		SendEmailMode mode = null;
		if (SendEmailMode.ALL.name().equalsIgnoreCase(sendEmailMode)) {
			mode = SendEmailMode.ALL;
		} else if (SendEmailMode.SEPARATELY.name().equalsIgnoreCase(
				sendEmailMode)) {
			mode = SendEmailMode.SEPARATELY;
		} else {
			LOGGER.warn("Incorrect pm.send.email.mode: " + sendEmailMode
					+ ". Default " + SendEmailMode.SEPARATELY + " is used.");
			mode = SendEmailMode.SEPARATELY;
		}
		return mode;
	}

	private Map<NotificationLanguage, Set<String>> initNotificationLanguageMap() {
		final Map<NotificationLanguage, Set<String>> result = new HashMap<>();
		for (final NotificationLanguage lang : NotificationLanguage.values()) {
			result.put(lang, new HashSet<String>());
		}
		return result;
	}

	/**
	 * Returns new map, where current timestamp date is changed to formatted
	 * date strings. It is needed because Date object in Map<String, Object>
	 * cannot be correctly deserialized from JSON.
	 */
	private Map<String, Object> preprocessOutputParameters(
			final Map<String, Object> outputParameters,
			final NotificationLanguage notificationLanguage) {
		final Locale locale = Locale.forLanguageTag(notificationLanguage
				.getLanguageTag());
		final Map<String, Object> preprocessedOutputParameters = new HashMap<>(
				outputParameters);
		final Date date = (Date) preprocessedOutputParameters
				.get(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP);
		final String formattedDate = DateFormat.getDateTimeInstance(
				DateFormat.SHORT, DateFormat.LONG, locale).format(date);
		preprocessedOutputParameters.put(
				PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP,
				formattedDate);

		if (notificationLanguage == NotificationLanguage.RU_TRANSLIT) {
			for (final Entry<String, Object> entry : preprocessedOutputParameters
					.entrySet()) {
				if (entry.getValue() instanceof String) {
					final String transliteratedValue = RussianTransliterator
							.transliterate((String) entry.getValue());
					entry.setValue(transliteratedValue);
				}
			}
		}

		return preprocessedOutputParameters;
	}

	private void processSendAlertAction(final MPolicy policy,
			final Map<String, Object> outputParameters,
			final MPolicySendAlert action) {
		final PerceivedSeverity severity = (PerceivedSeverity) outputParameters
				.get(OUTPUT_PARAMETER_ALERT_SEVERITY);
		final Date timestamp = (Date) outputParameters
				.get(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_TIMESTAMP);
		String settings = null;
		if (outputParameters.get(OUTPUT_PARAMETER_ALERT_SETTINGS) != null) {
			settings = (String) outputParameters
					.get(OUTPUT_PARAMETER_ALERT_SETTINGS);
		}
		final MAlertIndication alert = new MAlertIndication(
				action.getAlertType(), policy.getSource(),
				Source.getPolicySource(policy.getKey()), settings,
				severity == null ? PerceivedSeverity.CRITICAL : severity,
				timestamp, null);
		alert.setDetectionValue((Double) outputParameters
				.get(PolicyManagerService.OUTPUT_PARAMETER_CURRENT_VALUE));
		alert.setSpecificReason(SpecificReason.NONE);

		if(outputParameters.get(PolicyManagerService.OUTPUT_PARAMETER_THRESHOLD) != null) {
			Double thresholdValue = (Double) outputParameters
					.get(PolicyManagerService.OUTPUT_PARAMETER_THRESHOLD);
			alert.setExtraData(formatter.format(thresholdValue));
		}

		AlertMessage message;
		if (severity == null) {
			message = AlertMessage.clearAlert(alert);
		} else {
			message = AlertMessage.activateAlert(alert);
		}
		amqpTemplate.convertAndSend(alertExchange.getName(), null, message,
				new MessagePostProcessor() {

					@Override
					public Message postProcessMessage(final Message message)
							throws AmqpException {
						message.getMessageProperties().setExpiration(
								alertMessageExpirationTime);
						return message;
					}
				});
	}

	private void processSendEmailAction(final MPolicy policy,
			final Map<String, Object> outputParameters,
			final List<MPolicySendEmail> actions) {
		final List<MContactInformation> contacts = extractContactsFromActions(actions);

		final Map<NotificationLanguage, Set<String>> emailsByLang = initNotificationLanguageMap();

		final NotificationLanguage defaultNotificationLanguage = templateService
				.getDefaultNotificationLanguage();

		for (final MContactInformation contactInformation : contacts) {
			final Collection<MContactInformation> users = contactInformation
					.getContacts();
			for (final MContactInformation user : users) {
				if(user instanceof MUser)
				{
					if(((MUser) user).isDisabled())
					{
						continue;
					}
				}
				NotificationLanguage notificationLanguage = user
						.getNotificationLanguage();
				if (notificationLanguage == null) {
					notificationLanguage = defaultNotificationLanguage;
				}
				for (final String email : user.getEmails()) {
					if (Utils.isEmailValid(email)) {
						emailsByLang.get(notificationLanguage).add(email);
					} else {
						LOGGER.warn("Email address "
								+ email
								+ " of "
								+ user
								+ " is not valid. No attempt to send notification to this email will be made.");
					}
				}
			}
		}

		for (final NotificationLanguage lang : emailsByLang.keySet()) {
			final Set<String> emails = emailsByLang.get(lang);
			if (emails.size() > 0) {
				final String body = templateService
						.getTemplate(lang, MAIL_BODY);
				final String subject = templateService.getTemplate(lang,
						MAIL_SUBJECT);
				final Map<String, Object> preprocessedOutputParams = preprocessOutputParameters(
						outputParameters, lang);
				final SendEmailMode mode = getCurrentSendEmailMode();

				if (mode.equals(SendEmailMode.ALL)) {
					sendEmailMessage(subject, body, emails,
							preprocessedOutputParams);
				} else {
					for (final String email : emails) {
						final Set<String> messageEmails = new HashSet<String>();
						messageEmails.add(email);
						sendEmailMessage(subject, body, messageEmails,
								preprocessedOutputParams);
					}
				}
			}
		}
	}

	private void processSendSmsAction(final MPolicy policy,
			final Map<String, Object> outputParameters,
			final List<MPolicySendSms> actions) {
		final List<MContactInformation> contacts = extractContactsFromActions(actions);

		final Map<NotificationLanguage, Set<String>> phonesByLang = initNotificationLanguageMap();

		final NotificationLanguage defaultNotificationLanguage = templateService
				.getDefaultNotificationLanguage();

		for (final MContactInformation contactInformation : contacts) {
			final Collection<MContactInformation> users = contactInformation
					.getContacts();
			for (final MContactInformation user : users) {
				if(user instanceof MUser)
				{
					if(((MUser) user).isDisabled())
					{
						continue;
					}
				}
				NotificationLanguage notificationLanguage = user
						.getNotificationLanguage();
				if (notificationLanguage == null) {
					notificationLanguage = defaultNotificationLanguage;
				}

				if (notificationLanguage == NotificationLanguage.RU) {
					notificationLanguage = NotificationLanguage.RU_TRANSLIT;
				}

				for (final String phone : user.getPhones()) {
					if (Utils.isPhoneNumberValid(phone)) {
						phonesByLang.get(notificationLanguage).add(phone);
					} else {
						LOGGER.warn("Phone number "
								+ phone
								+ " of "
								+ user
								+ " is not valid. No attempt to send notification to this phone will be made.");
					}
				}
			}
		}

		// Send sms one by one to the message queue
		for (final NotificationLanguage lang : phonesByLang.keySet()) {
			if (phonesByLang.get(lang).size() > 0) {
				final Map<String, Object> preprocessedOutputParams = preprocessOutputParameters(
						outputParameters, lang);
				preprocessedOutputParams.put(
						AbstractSender.UNICODE_TEXT_PARAMETER_NAME,
						lang == NotificationLanguage.RU);
				final String template = templateService.getTemplate(lang,
						SMS_BODY);
				for (final String phone : phonesByLang.get(lang)) {
					final Set<String> messagePhones = new HashSet<>();
					messagePhones.add(phone);
					final PolicySendActionMessage message = new PolicySendActionMessage(
							null, template, messagePhones,
							preprocessedOutputParams);
					amqpTemplate.convertAndSend(actionExchange.getName(),
							smsQueueRoutingKey, message);
				}
			}
		}
	}

	private void sendEmailMessage(final String subject, final String body,
			final Set<String> emails, final Map<String, Object> outputParameters) {
		final PolicySendActionMessage message = new PolicySendActionMessage(
				subject, body, emails, outputParameters);
		amqpTemplate.convertAndSend(actionExchange.getName(),
				emailQueueRoutingKey, message);
	}

	public void setActionExchange(final TopicExchange actionExchange) {
		this.actionExchange = actionExchange;
	}

	public void setAlertExchange(final FanoutExchange alertExchange) {
		this.alertExchange = alertExchange;
	}

	public void setAlertMessageExpirationTime(
			final int alertMessageExpirationTime) {
		this.alertMessageExpirationTime = Long
				.toString(alertMessageExpirationTime
						* TimeConstants.MILLISECONDS_PER_SECOND);
	}

	public void setAmqpTemplate(final AmqpTemplate amqpTemplate) {
		this.amqpTemplate = amqpTemplate;
	}

	public void setEmailQueueRoutingKey(final String emailQueueRoutingKey) {
		this.emailQueueRoutingKey = emailQueueRoutingKey;
	}

	public void setSendEmailMode(final String sendEmailMode) {
		this.sendEmailMode = sendEmailMode;
	}

	public void setSmsQueueRoutingKey(final String smsQueueRoutingKey) {
		this.smsQueueRoutingKey = smsQueueRoutingKey;
	}

	public void setTemplateService(final TemplateService templateService) {
		this.templateService = templateService;
	}
}
