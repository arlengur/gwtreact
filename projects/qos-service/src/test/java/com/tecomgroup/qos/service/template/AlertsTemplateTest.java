/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service.template;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUserAbstractTemplate;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.BaseTemplateType;
import com.tecomgroup.qos.domain.MUserAbstractTemplate.TemplateType;
import com.tecomgroup.qos.domain.MUserAlertsTemplate;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AlertsTemplateTest extends TemplateTest {

	public static final TemplateType TEMPLATE_TYPE = BaseTemplateType.ALERT;

	@Override
	protected MUserAbstractTemplate getNewTemplate(final String templateName) {
		final MUserAlertsTemplate template = new MUserAlertsTemplate(
				templateName);
		template.setCriterion(modelSpace.createCriterionQuery().le(
				"creationDateTime", new Date()));
		template.setOrder(Order.asc("creationDateTime"));
		template.setHiddenColumns(new String[]{"settings", "acknowledged"});
		template.setUser(user);

		return template;
	}

	@Override
	protected TemplateType getTemplateType() {
		return TEMPLATE_TYPE;
	}

	@Test
	public void testSaveTemplateFromExistToExist() {
		final String templateNamePrefix = "testSaveAlertsTemplateFromExistToExist";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToExist(nameX, nameY);
	}

	@Test
	public void testSaveTemplateFromExistToNew() {
		final String templateNamePrefix = "testSaveAlertsTemplateFromExistToNew";
		final String nameX = templateNamePrefix + "-X";
		final String nameY = templateNamePrefix + "-Y";

		testSaveTemplateFromExistToNew(nameX, nameY);
	}

	@Test
	public void testTemplate() {
		testTemplate("testAlertsTemplate");
	}

	@Test
	public void testTemplateUpdate() {
		testTemplateUpdate("testAlertsTemplateUpdate");
	}

	@Override
	protected void updateFromTemplate(final MUserAbstractTemplate destination,
			final MUserAbstractTemplate source) {
		final Order order = ((MUserAlertsTemplate) source).getOrder();
		((MUserAlertsTemplate) destination).setOrder(order);
	}

	@Override
	protected void updateTemplate(final MUserAbstractTemplate template) {
		((MUserAlertsTemplate) template).setOrder(Order
				.desc("creatingDateTime"));
	}

}
