/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.mail;

import java.io.StringWriter;
import java.util.*;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import com.sun.mail.smtp.SMTPAddressFailedException;
import com.tecomgroup.qos.AbstractSender;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author novohatskiy.r
 * 
 */
public class DefaultMailer implements AbstractSender {

	private final static Logger LOGGER = Logger.getLogger(DefaultMailer.class);

	private static final String VELOCITY_LOG_TAG = "TemplateMailMessage";

	private static final String UTF8 = "UTF-8";

	@Value("${mail.from}")
	private String from;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private VelocityEngine velocityEngine;

	private String processTemplate(final Context context, final String template) {
		final StringWriter output = new StringWriter();
		velocityEngine.evaluate(context, output, VELOCITY_LOG_TAG, template);
		return output.toString();
	}

	private Status sendMail(final Collection<String> contacts,
			final String subject, final String body) {
		Status status = null;
		if (SimpleUtils.isNotNullAndNotEmpty(contacts)) {
			final MimeMessage message = mailSender.createMimeMessage();
			try {
				final MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8);
				String email = contacts.iterator().next();
				final Set<String> ccEmails = new HashSet<>();
				ccEmails.addAll(contacts);
				ccEmails.remove(email);
				helper.setTo(email);
				helper.setCc(ccEmails.toArray(new String[ccEmails.size()]));
				helper.setFrom(from);
				helper.setSubject(subject);
				helper.setText(body, true);
				mailSender.send(message);
				LOGGER.info("Mail has been successfully sent to " + contacts);
				status = Status.SUCCESS;
			} catch (final MailException | MessagingException e) {
				LOGGER.error("Failed to send mail message to " + contacts, e);
				status = Status.SOLVABLE_PROBLEM_OCCURED;
				if (e instanceof MailSendException) {
					final Exception[] messageExceptions = ((MailSendException) e).getMessageExceptions();

					for (Exception messageException : messageExceptions) {
						if (messageException instanceof SendFailedException) {
							final SendFailedException sendFailedException = (SendFailedException) messageException;
							final Exception nextException = sendFailedException.getNextException();
							if (nextException instanceof SMTPAddressFailedException) {

								final Address[] validUnsentAddresses = sendFailedException.getValidUnsentAddresses();
								if (SimpleUtils.isNotNullAndNotEmpty(validUnsentAddresses)) {
									for (final Address address : validUnsentAddresses) {
										if (address instanceof InternetAddress) {
											final List<String> unsentAddresses = new ArrayList<>();
											unsentAddresses.add(((InternetAddress) address).getAddress());
											status = sendMail(unsentAddresses, subject, body);
										}
									}
								} else {
									status = Status.INCORRECT_INPUT;
								}
							}
						}
					}
				}
			}
		} else {
			LOGGER.warn("Failed to send mail message: no recipients defined.");
			status = Status.INCORRECT_INPUT;
		}
		return status;
	}

	@Override
	public Status sendTemplatedMessage(final Collection<String> contacts,
			final String subject, final String body,
			final Map<String, Object> templateParameters) {
		final VelocityContext context = new VelocityContext(templateParameters);
		String processedSubject = null;
		String processedBody = null;
		try {
			processedSubject = processTemplate(context, subject);
			processedBody = processTemplate(context, body);
		} catch (final Exception e) {
			LOGGER.warn("Failed to process mail template. ", e);
			return Status.INCORRECT_INPUT;
		}
		return sendMail(contacts, processedSubject, processedBody);
	}

}
