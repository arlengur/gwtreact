/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */
package com.tecomgroup.qos.pm.amqp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.tecomgroup.qos.communication.message.PolicySendActionMessage;
import com.tecomgroup.qos.mail.AbstractMailerTest;

/**
 * @author sviyazov.a
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:/com/tecomgroup/qos/mail/mailContext.xml"})
public class SendEmailActionMessageListenerTest extends AbstractMailerTest {

	private static final String TEST_USER_EMAIL = "mail@localhost";

	private static final String TEST_MAIL_SUBJECT = "Subject";
	private static final String TEST_MAIL_BODY = "Body";

	private PolicyActionMessageListener actionMessageListener;

	private Set<String> createContacts() {
		return new HashSet<String>(Arrays.asList(new String[]{TEST_USER_EMAIL}));
	}

	private PolicySendActionMessage createSendEmailActionMessage() {
		return new PolicySendActionMessage(TEST_MAIL_SUBJECT, TEST_MAIL_BODY,
				createContacts(), new HashMap<String, Object>());
	}

	@Override
	@Before
	public void setUp() throws Exception {
		super.setUp();
		actionMessageListener = new PolicyActionMessageListener();
		ReflectionTestUtils.setField(actionMessageListener, "sender", mailer);
	}

	@Test
	public void testSendEmailAction() throws MessagingException, IOException {
		actionMessageListener.handleQosMessage(createSendEmailActionMessage());
		assertMessageSubjectAndBody(TEST_MAIL_SUBJECT, TEST_MAIL_BODY);
	}

}
