package com.tecomgroup.qos.modelspace.hibernate;

import com.tecomgroup.qos.domain.rbac.*;
import com.tecomgroup.qos.modelspace.ModelSpace;

import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by kiselev.a on Fri Mar 11 16:01:25 2016.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HibernateRbacTest {
	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	private ModelSpace hibernateModelSpace;

	@Before
	public void setup() {
		return;
	}

	@Test
	@Transactional
	public void testUISubjectSaveLoad() {
		UISubject subj = new UISubject("testPage"); // TODO: need to save extensions
		Long id = (Long) sessionFactory
			.getCurrentSession()
			.save(subj);
		Assert.assertNotNull(id);
		
		UISubject loaded = (UISubject)sessionFactory
			.getCurrentSession()
			.get(UISubject.class, id);
		Assert.assertEquals(subj, loaded);
	}

	@Test
	@Transactional
	public void testMRoleSaveLoad() {
		MRole role = new MRole("testUser",
							   new UISubject("testPage100"),
							   new UISubject("testPage101"));
							   
		Long id = (Long) sessionFactory
			.getCurrentSession()
			.save(role);
		Assert.assertNotNull(id);

		MRole loaded = (MRole)sessionFactory
			.getCurrentSession()
			.get(MRole.class, id);
		Assert.assertEquals(role, loaded);
	}
}
