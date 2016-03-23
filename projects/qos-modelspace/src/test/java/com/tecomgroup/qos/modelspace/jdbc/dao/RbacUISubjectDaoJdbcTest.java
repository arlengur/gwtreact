package com.tecomgroup.qos.modelspace.jdbc.dao;

import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.modelspace.jdbc.dao.RbacUISubjectServiceDao;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.dao.DataAccessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * Created by kiselev.a on Tue Mar  1 11:27:47 2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RbacUISubjectDaoJdbcTest {
	@Autowired
	protected RbacUISubjectServiceDao provider;
	
	@Autowired
	DataSource ds;
	

	@Before
	public void setup() {
		return;
	}

	@Test
	public void successfullGetUISubject() {
		UISubject testUIS = provider.getSubject("MAIN");
		List<UISubject> testUISList = provider.getSubjectById(testUIS.getId());
		Assert.assertTrue(testUIS.equals(testUISList.get(0)));
	}

	@Test
	public void successfullCreateUISubject() {
		UISubject test = new UISubject(101L,"testPageCreate");
		Assert.assertNotNull(provider.insertSubject(test));

		List<UISubject> testUISList = provider.getSubjectById(test.getId());
		Assert.assertTrue(test.equals(testUISList.get(0)));
	}

	@Test
	public void successfullUpdateUISubject() {
		UISubject test = new UISubject(102L, "testPageUpdate");
		Assert.assertNotNull(provider.insertSubject(test));
		test.setName("testPageUpdate1");
		provider.updateSubject(test);

		List<UISubject> testUISList = provider.getSubjectById(test.getId());
		Assert.assertTrue(test.equals(testUISList.get(0)));
	}
}
