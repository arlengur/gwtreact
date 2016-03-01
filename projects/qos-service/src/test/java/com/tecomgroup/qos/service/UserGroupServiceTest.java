/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.MUserGroup;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SharedModelConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author kunilov.p
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@Transactional
public class UserGroupServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private UserGroupService userGroupService;

	private MUserGroup group;

	@Before
	public void setUp() {
		group = SharedModelConfiguration
				.createGroupWithDefaultUsers("defaultGroup");

		for (final MUser user : group.getUsers()) {
			modelSpace.saveOrUpdate(user);
		}
		modelSpace.saveOrUpdate(group);
	}

	@Test
	public void testClearGroupUsersWithEmptyList() {
		final MUserGroup existingGroup = modelSpace.findUniqueEntity(
				MUserGroup.class,
				modelSpace.createCriterionQuery().eq("name", group.getName()));
		Assert.assertNotNull(existingGroup);

		existingGroup.setUsers(new ArrayList<MUser>());

		final MUserGroup updatedGroup = userGroupService
				.saveOrUpdateGroup(existingGroup);
		Assert.assertNotNull(updatedGroup);
		Assert.assertEquals(0, updatedGroup.getUsers().size());

		final MUserGroup foundGroup = modelSpace.get(MUserGroup.class,
				updatedGroup.getId());
		Assert.assertNotNull(foundGroup);
		Assert.assertEquals(0, foundGroup.getUsers().size());
	}

	@Test
	public void testClearGroupUsersWithNull() {
		final MUserGroup existingGroup = modelSpace.findUniqueEntity(
				MUserGroup.class,
				modelSpace.createCriterionQuery().eq("name", group.getName()));
		Assert.assertNotNull(existingGroup);

		existingGroup.setUsers(null);

		final MUserGroup updatedGroup = userGroupService
				.saveOrUpdateGroup(existingGroup);
		Assert.assertNotNull(updatedGroup);
		Assert.assertNull(updatedGroup.getUsers());

		final MUserGroup foundGroup = modelSpace.get(MUserGroup.class,
				updatedGroup.getId());
		Assert.assertNotNull(foundGroup);
		Assert.assertNull(foundGroup.getUsers());
	}

	@Test
	public void testCreateNewGroup() {
		final List<MUser> allUsers = modelSpace.getAll(MUser.class);

		final MUserGroup newGroup = new MUserGroup();
		final String groupName = "newGroup";
		newGroup.setName(groupName);
		newGroup.setUsers(allUsers);

		final MUserGroup updatedGroup = userGroupService
				.saveOrUpdateGroup(newGroup);
		Assert.assertNotNull(updatedGroup);
		Assert.assertEquals(groupName, updatedGroup.getName());
		Assert.assertEquals(allUsers.size(), updatedGroup.getUsers().size());

		final MUserGroup foundGroup = modelSpace.get(MUserGroup.class,
				updatedGroup.getId());
		Assert.assertNotNull(foundGroup);
		Assert.assertEquals(groupName, foundGroup.getName());
		Assert.assertEquals(allUsers.size(), foundGroup.getUsers().size());
	}

	@Test
	public void testDoesGroupExist() {
		Assert.assertTrue(userGroupService.doesGroupExist(group.getName()));
		Assert.assertFalse(userGroupService.doesGroupExist("fakeGroup"));
	}

	@Test
	public void testGetAllGroups() {
		final Collection<MUserGroup> allGroups = userGroupService
				.getAllGroups();

		Assert.assertNotNull(allGroups);
		Assert.assertEquals(modelSpace.getAll(MUserGroup.class).size(),
				allGroups.size());
	}

	@Test
	public void testGetGroupByName() {
		Assert.assertNotNull(userGroupService.getGroupByName(group.getName()));
		Assert.assertNull(userGroupService.getGroupByName("fakeGroup"));
	}

	@Test
	public void testGetGroups() {
		final Collection<MUserGroup> groups = userGroupService
				.getGroups(modelSpace.createCriterionQuery().like("name",
						"default%"));

		Assert.assertNotNull(groups);
		Assert.assertEquals(1, groups.size());
	}

	@Test
	public void testRemoveGroup() {
		final Collection<MUserGroup> previousAllGroups = userGroupService
				.getAllGroups();

		userGroupService.removeGroup(group.getName());

		final Collection<MUserGroup> currentAllGroups = userGroupService
				.getAllGroups();

		Assert.assertEquals(previousAllGroups.size() - 1,
				currentAllGroups.size());
		Assert.assertNull(userGroupService.getGroupByName(group.getName()));
	}

	@Test
	public void testRemoveGroups() {
		final Collection<MUserGroup> allGroups = userGroupService
				.getAllGroups();

		final List<String> allGroupNames = new ArrayList<>();
		for (final MUserGroup group : allGroups) {
			allGroupNames.add(group.getName());
		}

		userGroupService.removeGroups(allGroupNames);

		final Collection<MUserGroup> currentAllGroups = userGroupService
				.getAllGroups();

		Assert.assertEquals(0, currentAllGroups.size());
	}

	@Test
	public void testUpdateExistingGroup() {
		final MUserGroup existingGroup = modelSpace.findUniqueEntity(
				MUserGroup.class,
				modelSpace.createCriterionQuery().eq("name", group.getName()));
		Assert.assertNotNull(existingGroup);

		final int usersSizeOfTheExistingGroup = existingGroup.getUsers().size();
		final String groupName = "newNameOfTheExistingGroup";

		existingGroup.setName(groupName);
		existingGroup.getUsers().remove(0);

		final MUserGroup updatedGroup = userGroupService
				.saveOrUpdateGroup(existingGroup);
		Assert.assertNotNull(updatedGroup);
		Assert.assertEquals(groupName, updatedGroup.getName());
		Assert.assertEquals(usersSizeOfTheExistingGroup - 1, updatedGroup
				.getUsers().size());

		final MUserGroup foundGroup = modelSpace.get(MUserGroup.class,
				updatedGroup.getId());
		Assert.assertNotNull(foundGroup);
		Assert.assertEquals(groupName, foundGroup.getName());
		Assert.assertEquals(usersSizeOfTheExistingGroup - 1, foundGroup
				.getUsers().size());
	}
}
