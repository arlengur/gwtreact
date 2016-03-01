/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.modelspace.hibernate;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.CriterionQuery;
import com.tecomgroup.qos.domain.MFakeTask;
import com.tecomgroup.qos.domain.MFakeTask.MFakeTaskAttribute;
import com.tecomgroup.qos.domain.Source;
import com.tecomgroup.qos.domain.Source.Type;
import com.tecomgroup.qos.exception.ModelSpaceException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.projection.ProjectionFactory;
import org.hibernate.collection.spi.PersistentCollection;
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
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author abondin
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class HibernateModelspaceTest {

	@Autowired
	private ModelSpace hibernateModelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	private MFakeTask[] tasks;

	private MFakeTask createFakeTask(final String taskKey,
			final String sourceKey) {
		final MFakeTask task = new MFakeTask();
		task.setName(taskKey);
		task.setSource(Source.getTaskSource(sourceKey));
		return task;
	}

	private MFakeTask[] populateData() {
		final List<MFakeTask> result = new ArrayList<MFakeTask>();

		final MFakeTask task1 = createFakeTask("Task", "source1");

		final MFakeTaskAttribute superAttribute = new MFakeTaskAttribute();
		superAttribute.setName("super");
		superAttribute.setValue("value");
		task1.setSuperAttribute(superAttribute);

		final MFakeTaskAttribute attribute1 = new MFakeTaskAttribute();
		attribute1.setName("art1");
		attribute1.setValue("val1");
		final MFakeTaskAttribute attribute2 = new MFakeTaskAttribute();
		attribute2.setName("art2");
		attribute2.setValue("val2");

		task1.setAttributes(Arrays.asList(attribute1, attribute2));

		hibernateModelSpace.save(task1);
		result.add(task1);

		final MFakeTask task2 = createFakeTask("Task", "source2");
		hibernateModelSpace.save(task2);
		result.add(task2);

		final MFakeTask task3 = createFakeTask("Task3", "source3");
		hibernateModelSpace.save(task3);
		result.add(task3);

		final MFakeTask task4 = createFakeTask("Task4", "source1");
		hibernateModelSpace.save(task4);
		result.add(task4);

		return result.toArray(new MFakeTask[0]);
	}

	@Before
	public void setup() {
		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				tasks = populateData();

			}
		});
	}

	@Test
	@Transactional
	public void testFind() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		Criterion cr = query.eq("name", "Task");
		List<MFakeTask> result = hibernateModelSpace.find(MFakeTask.class, cr);
		Assert.assertEquals(2, result.size());

		cr = query.or(query.like("name", "Task"), query.like("name", "Task3"));
		result = hibernateModelSpace.find(MFakeTask.class, cr);
		Assert.assertEquals(3, result.size());

		cr = query.not(query.eq("name", "Task"));
		result = hibernateModelSpace.find(MFakeTask.class, cr);
		Assert.assertEquals(2, result.size());
	}

	@Test
	@Transactional
	public void testFindEmbededProperties() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		Criterion cr = query.in("source.key",
				Arrays.asList("source3", "source1"));
		cr = query.and(cr, query.eq("source.type", Type.TASK));
		final List<MFakeTask> result = hibernateModelSpace.find(
				MFakeTask.class, cr);
		Assert.assertEquals(3, result.size());
	}

	@Test
	@Transactional
	public void testFindGroupCriterion() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		Criterion cr = query.eq("name", "Task");

		List<MFakeTask> result = hibernateModelSpace.find(MFakeTask.class,
				query.and(cr, query.eq("superAttribute.name", "super")));
		Assert.assertEquals(1, result.size());

		result = hibernateModelSpace.find(MFakeTask.class,
				query.and(cr, query.eq("superAttribute.name", "not super")));
		Assert.assertEquals(0, result.size());

		cr = query.eq("name", "Task3");
		result = hibernateModelSpace.find(MFakeTask.class,
				query.or(cr, query.eq("name", "Task4")));
		Assert.assertEquals(2, result.size());
	}

	@Test
	@Transactional
	public void testFindInCriterion() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		final Criterion cr = query.in("name", Arrays.asList("Task3", "Task4"));
		final List<MFakeTask> result = hibernateModelSpace.find(
				MFakeTask.class, cr);
		Assert.assertEquals(2, result.size());
	}

	@Test
	@Transactional
	public void testFindNestedProperties() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		final Criterion cr = query.eq("superAttribute.name", "super");
		final List<MFakeTask> result = hibernateModelSpace.find(
				MFakeTask.class, cr);
		Assert.assertEquals(1, result.size());
	}

	@Test
	@Transactional
	public void testFindProperty() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		final Criterion cr = query.eq("name", "Task3");
		List<?> result = hibernateModelSpace.findProperties(MFakeTask.class,
				cr, "name", "id");
		Assert.assertEquals("Task3", ((Object[]) result.iterator().next())[0]);
		Assert.assertEquals(tasks[2].getId(), ((Object[]) result.iterator()
				.next())[1]);

		result = hibernateModelSpace
				.findProperties(MFakeTask.class, cr, "name");
		Assert.assertEquals("Task3", result.iterator().next());

		result = hibernateModelSpace.findProperties(MFakeTask.class, null,
				ProjectionFactory.max("modificationDateTime"));
		Assert.assertEquals(tasks[3].getModificationDateTime(), result.get(0));
	}

	@Test
	@Transactional
	public void testLoad() {
		final MFakeTask task = createFakeTask("Task", "source1");
		final Long id = hibernateModelSpace.save(task);
		Assert.assertNotNull(id);

		final MFakeTask loadedTask = hibernateModelSpace.get(MFakeTask.class,
				id);
		Assert.assertEquals(task.getName(), loadedTask.getName());
	}

	@Test
	@Transactional
	public void testNotCriterion() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		Criterion cr = query.eq("name", "Task3");
		List<?> result = hibernateModelSpace.find(MFakeTask.class, cr);
		Assert.assertEquals(1, result.size());

		cr = query.not(cr);
		result = hibernateModelSpace.find(MFakeTask.class, cr);
		Assert.assertTrue(result.size() > 1);
	}

	@Test(expected = ModelSpaceException.class)
	@Transactional
	public void testNotUniqueResult() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		hibernateModelSpace.findUniqueEntity(MFakeTask.class,
				query.eq("name", "Task"));
	}

	@Test
	@Transactional
	public void testSave() {
		final MFakeTask task = createFakeTask("Task", "source1");
		final Long id = hibernateModelSpace.save(task);
		Assert.assertNotNull(id);
	}

	@Test
	public void testSaveOrUpdate() {
		final MFakeTask task = createFakeTask("Task", "source1");

		final MFakeTaskAttribute attribute1 = new MFakeTaskAttribute();
		attribute1.setName("Attr1");

		final List<MFakeTaskAttribute> attributes = new ArrayList<>();
		attributes.add(attribute1);
		task.setAttributes(attributes);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				hibernateModelSpace.saveOrUpdate(task);
			}
		});
		HibernateEntityConverter.convertHibernateCollections(task,
				PersistentCollection.class);

		final MFakeTaskAttribute attribute2 = new MFakeTaskAttribute();
		attribute2.setName("Attr2");
		task.getAttributes().add(attribute2);

		transactionTemplate.execute(new TransactionCallbackWithoutResult() {

			@Override
			protected void doInTransactionWithoutResult(
					final TransactionStatus status) {
				hibernateModelSpace.saveOrUpdate(task);
			}
		});
		Assert.assertNotNull(task.getAttributes().get(1).getId());
	}

	@Test
	@Transactional
	public void testUniqueResult() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		final MFakeTask task = hibernateModelSpace.findUniqueEntity(
				MFakeTask.class, query.eq("name", "Task3"));
		Assert.assertEquals(tasks[2], task);
	}

	@Test
	@Transactional
	public void testUniqueResultNull() {
		final CriterionQuery query = hibernateModelSpace.createCriterionQuery();
		final MFakeTask task = hibernateModelSpace.findUniqueEntity(
				MFakeTask.class, query.eq("name", "Task8"));
		Assert.assertNull(task);
	}
}
