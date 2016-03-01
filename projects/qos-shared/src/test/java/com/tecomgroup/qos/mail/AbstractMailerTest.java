/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.mail;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.tecomgroup.qos.AbstractSender;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractMailerTest {

	@Autowired
	@Qualifier("mailer")
	protected AbstractSender mailer;

	@Value("${mail.smtp.port}")
	private int smtpPort;

	protected GreenMail greenMail;

	protected void assertMessageSubjectAndBody(
			final String expectedMessageSubject,
			final String expectedMessageBody) throws IOException,
			MessagingException {
		final MimeMessage message = greenMail.getReceivedMessages()[0];
		Assert.assertEquals(expectedMessageSubject, message.getSubject());
		Assert.assertTrue(message.getContent() instanceof MimeMultipart);
		final MimeMultipart multipart = (MimeMultipart) message.getContent();
		Assert.assertEquals(1, multipart.getCount());
		Assert.assertTrue(GreenMailUtil.getBody(multipart.getBodyPart(0))
				.contains(expectedMessageBody));
	}

	@Before
	public void setUp() throws Exception {
		greenMail = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		greenMail.start();
	}

	@After
	public void tearDown() throws Exception {
		greenMail.stop();
	}
}
