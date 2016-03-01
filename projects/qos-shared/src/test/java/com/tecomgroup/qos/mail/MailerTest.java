/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.mail;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author novohatskiy.r
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/mail/mailContext.xml"})
public class MailerTest extends AbstractMailerTest {

	private final static List<String> RECIPIENTS = Arrays
			.asList(new String[]{"mail@localhost"});

	private final static String SUBJECT = "Here is a ${PLACEHOLDER}";

	private final static String BODY = "A body with another ${VARIABLE} inside.";

	@Test
	public void testMailSendAndReceive() throws MessagingException, IOException {
		final Map<String, Object> templateParameters = new HashMap<String, Object>();
		templateParameters.put("PLACEHOLDER", "subject");
		templateParameters.put("VARIABLE", "thing");
		mailer.sendTemplatedMessage(RECIPIENTS, SUBJECT, BODY,
				templateParameters);

		assertMessageSubjectAndBody("Here is a subject",
				"A body with another thing inside.");
	}
}
