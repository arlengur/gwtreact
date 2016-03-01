/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.BuildInfo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;

/**
 * @author abondin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
public class SystemInformationServiceTest {

	@Autowired
	private SystemInformationService systemInformationService;

	@Test
	public void testGetBuildInfo() {
		final BuildInfo buildInfo = systemInformationService.getBuildInfo();
		final SimpleDateFormat format = new SimpleDateFormat(
				DefaultSystemInformationService.GIT_DATE_FORMAT);

		Assert.assertEquals("super-branch", buildInfo.getBranch());
		Assert.assertEquals("Alexander Bondin", buildInfo.getCommitUserName());
		Assert.assertEquals("09.08.2012 @ 11:04:58 MSK",
				format.format(buildInfo.getCommitTime()));
	}
}
