/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.domain.rbac.PredefinedRoles;
import com.tecomgroup.qos.domain.rbac.MRole;
import com.tecomgroup.qos.exception.QOSException;
import com.tecomgroup.qos.modelspace.ModelSpace;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;

/**
 * @author meleshin.o
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserManagerServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	private TransactionTemplate transactionTemplate;

	@Autowired
	private UserManagerService userManagerService;

	private final static String DEFAULT_USER_LOGIN = "User";

	private final static String DEFAULT_ADMIN_LOGIN = "Admin";

	private final static String TEST_USER_LOGIN = "0123456789-_.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

	private final static String TEST_USER_EMAIL = "test_user@mail.com";

	private final static String TEST_USER_PHONE = "+7 909 (32 - 11) 234-2";

	private final static String TEST_USER_INVALID_FORMATTED_LOGIN = "~!@#$%^&*+=`|\\(){}[]:;\"'<>,?/";

	private final static String TEST_USER_INVALID_FORMATTED_EMAIL = "~!@#$%^&*+=`|\\(){}[]:;\"'<>,?/";

	private final static String TEST_USER_LONG_LOGIN = getPrefilledString('0',
			256);

	private final static String TEST_USER_FIRSTNAME = "test";

	private final static String TEST_USER_LASTNAME = "User";

	private final static String NEW_USER_FIRSTNAME = "New first name";

	private final static String NEW_USER_LASTNAME = "New last name";

	private static String getPrefilledString(final char defaultCharacter,
			final int size) {
		final char[] chars = new char[size];
		Arrays.fill(chars, defaultCharacter);
		return new String(chars);
	}

	private void changeUserDisabledStatus(final String login,
			final boolean disabledStatus) {
		MUser user = findUser(login);
		Assert.assertNotNull(user);

		user.setDisabled(disabledStatus);
		userManagerService.saveOrUpdateUser(user, false);

		user = findUser(login);
		Assert.assertEquals(login, user.getLogin());
		Assert.assertTrue(user.isDisabled() == disabledStatus);
	}

	private MUser createUser(final String login, final String email,
			final String phone) {
		final MUser user = new MUser();
		populateUser(user, login, TEST_USER_FIRSTNAME, TEST_USER_LASTNAME,
				email, phone, Collections.singletonList(PredefinedRoles.ROLE_USER));

		return userManagerService.saveOrUpdateUser(user, false);
	}

	private void expectUserAndAdmin(final List<MUser> users,
			final boolean checkIsNotDisabled) {
		final Map<String, MUser> usersMap = new HashMap<String, MUser>();

		for (final MUser user : users) {
			usersMap.put(user.getLogin(), user);
		}
		Assert.assertTrue(users.size() >= 2);

		Assert.assertTrue(usersMap.containsKey(DEFAULT_ADMIN_LOGIN));
		final MUser admin = usersMap.get(DEFAULT_ADMIN_LOGIN);
		final List<MRole> adminRoles = admin.getRoles();
		Assert.assertEquals(1, adminRoles.size());
		Assert.assertTrue(adminRoles.contains(PredefinedRoles.ROLE_ADMIN));
		if (checkIsNotDisabled) {
			Assert.assertFalse(admin.isDisabled());
		}

		Assert.assertTrue(usersMap.containsKey(DEFAULT_USER_LOGIN));
		final MUser user = usersMap.get(DEFAULT_USER_LOGIN);
		final List<MRole> userRoles = user.getRoles();
		Assert.assertEquals(1, userRoles.size());
		Assert.assertTrue(userRoles.contains(PredefinedRoles.ROLE_USER));
		if (checkIsNotDisabled) {
			Assert.assertFalse(admin.isDisabled());
		}
	}

	private MUser findUser(final String login) {
		return transactionTemplate.execute(new TransactionCallback<MUser>() {

			@Override
			public MUser doInTransaction(final TransactionStatus status) {
				return modelSpace.findUniqueEntity(MUser.class,
						CriterionQueryFactory.getQuery().eq("login", login));
			}
		});
	}

	private void populateUser(final MUser user, final String login,
			final String firstName, final String lastName, final String email,
			final String phone, final List<MRole> roles) {
		user.setLogin(login);
		user.setEmail(email);
		user.setPhone(phone);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setRoles(roles);
	}

	@Test
	public void testChangeUserInformation() {
		MUser user = findUser(DEFAULT_USER_LOGIN);
		populateUser(user, DEFAULT_USER_LOGIN, NEW_USER_FIRSTNAME,
				NEW_USER_LASTNAME, TEST_USER_EMAIL, TEST_USER_PHONE,
				Arrays.asList(PredefinedRoles.ROLE_USER, PredefinedRoles.ROLE_ADMIN));
		userManagerService.saveOrUpdateUser(user, false);

		user = findUser(DEFAULT_USER_LOGIN);

		Assert.assertEquals(DEFAULT_USER_LOGIN, user.getLogin());
		Assert.assertEquals(TEST_USER_EMAIL, user.getEmail());
		Assert.assertEquals(NEW_USER_FIRSTNAME, user.getFirstName());
		Assert.assertEquals(NEW_USER_LASTNAME, user.getLastName());
		final List<MRole> userRoles = user.getRoles();
		Assert.assertTrue(userRoles.contains(PredefinedRoles.ROLE_ADMIN));
		Assert.assertTrue(userRoles.contains(PredefinedRoles.ROLE_USER));
	}

	@Test(expected = QOSException.class)
	public void testCreateUserWithInvalidEmail() {
		createUser(TEST_USER_LOGIN, TEST_USER_INVALID_FORMATTED_EMAIL,
				TEST_USER_PHONE);
	}

	@Test(expected = QOSException.class)
	public void testCreateUserWithInvalidFormattedLogin() {
		createUser(TEST_USER_INVALID_FORMATTED_LOGIN, TEST_USER_EMAIL,
				TEST_USER_PHONE);
	}

	@Test
	public void testCreateUserWithInvalidPhone() {
		Assert.assertFalse(testUserCreation(TEST_USER_LONG_LOGIN,
				TEST_USER_EMAIL, "7909234"));
		Assert.assertFalse(testUserCreation(TEST_USER_LONG_LOGIN,
				TEST_USER_EMAIL, "890932525"));
		Assert.assertFalse(testUserCreation(TEST_USER_LONG_LOGIN,
				TEST_USER_EMAIL, "+7(234(34))234"));
		Assert.assertFalse(testUserCreation(TEST_USER_LONG_LOGIN,
				TEST_USER_EMAIL, "+7 )(234)345"));
		Assert.assertFalse(testUserCreation(TEST_USER_LONG_LOGIN,
				TEST_USER_EMAIL, "+7 text 234"));
	}

	@Test(expected = QOSException.class)
	public void testCreateUserWithLongLogin() {
		createUser(TEST_USER_LONG_LOGIN, TEST_USER_EMAIL, TEST_USER_PHONE);
	}

	@Test
	public void testDisableUser() {
		changeUserDisabledStatus(DEFAULT_USER_LOGIN, true);
	}

	@Test
	public void testEnableUser() {
		changeUserDisabledStatus(DEFAULT_USER_LOGIN, false);
	}

	@Test
	public void testGetAllUsers() {
		final List<MUser> users = userManagerService.getAllUsers();
		expectUserAndAdmin(users, false);
	}

	@Test
	public void testGetUsers() {
		final List<MUser> users = userManagerService.getUsers(
				CriterionQueryFactory.getQuery().eq("disabled", false), null,
				0, 10);
		expectUserAndAdmin(users, true);
	}

	@Test
	public void testGetUsersCount() {
		Assert.assertEquals(2, (long) userManagerService.getUsersCount(null));
	}

	@Test
	public void testSuccessfullCreateUser() {
		int preinstalledUsersCount = 3;

		createUser(TEST_USER_LOGIN, TEST_USER_EMAIL,
				TEST_USER_PHONE);

		final List<MUser> users = userManagerService.getAllUsersNotFiltered();
		expectUserAndAdmin(users, false);
		Assert.assertEquals(preinstalledUsersCount, users.size());
		MUser user = findUser(TEST_USER_LOGIN);

		Assert.assertEquals(TEST_USER_LOGIN, user.getLogin());
		Assert.assertEquals(TEST_USER_EMAIL, user.getEmail());
		Assert.assertEquals(TEST_USER_FIRSTNAME, user.getFirstName());
		Assert.assertEquals(TEST_USER_LASTNAME, user.getLastName());
		Assert.assertEquals(PredefinedRoles.ROLE_USER, user.getRoles().get(0));
	}

	private boolean testUserCreation(final String login, final String email,
			final String phone) {
		boolean creationFailure = true;
		try {
			createUser(login, email, phone);
		} catch (final QOSException e) {
			creationFailure = false;
		}
		return creationFailure;
	}
}
