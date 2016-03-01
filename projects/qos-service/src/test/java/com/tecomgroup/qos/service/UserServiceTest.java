/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import com.tecomgroup.qos.criterion.CriterionQueryFactory;
import com.tecomgroup.qos.domain.MContactInformation;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.exception.SecurityException;
import com.tecomgroup.qos.exception.SecurityException.Reason;
import com.tecomgroup.qos.modelspace.ModelSpace;
import com.tecomgroup.qos.util.SecurityExceptionMatcher;
import com.tecomgroup.qos.util.SimpleUtils;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author sviyazov.a
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
// Use test common context which will override commonContext.xml
@ContextConfiguration(locations = {
		"classpath:/com/tecomgroup/qos/modelspace/hibernate/dbContext.xml",
		"classpath:/com/tecomgroup/qos/service/serviceContext.xml",
		"classpath:/com/tecomgroup/qos/testCommonContext.xml"})
@ActiveProfiles(AbstractService.TEST_CONTEXT_PROFILE)
@Transactional
public class UserServiceTest {

	@Autowired
	private ModelSpace modelSpace;

	@Autowired
	UserService userService;

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private MUser user;

	@Value("${security.ldap.enabled}")
	private boolean ldapEnabled;

	private final static String NEW_PASSWORD = "123456";

	private final static String VALID_OLD_PASSWORD = "u";

	private final static String INVALID_OLD_PASSWORD = "z";

	@Before
	public void before() {
		user = findUser("User");
		initTestingAuthentication();
	}

	private MUser findUser(final String login) {
		return modelSpace.findUniqueEntity(MUser.class, CriterionQueryFactory
				.getQuery().eq("login", login));
	}

	private void initTestingAuthentication() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		for (final MUser.Role role : user.getRoles()) {
			final GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(
					role.toString());
			authorities.add(grantedAuthority);
		}
		final UserDetails userDetails = new User(user.getLogin(),
				user.getPassword(), authorities);
		final Authentication authentication = new TestingAuthenticationToken(
				userDetails, null);
		authentication.setAuthenticated(true);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}

	@Test
	public void testGetAllContactInformations() {
		final Collection<MContactInformation> allContactInformations = userService
				.getAllContactInformations();

		Assert.assertNotNull(allContactInformations);
		Assert.assertEquals(
				modelSpace.getAll(MContactInformation.class).size(),
				allContactInformations.size());
	}

	@Test
	@Ignore
	public void testGetLdapUsers() {
		final List<MUser> users = userService.getLdapUsers();

		if (ldapEnabled) {
			Assert.assertFalse(users.isEmpty());

			final String usernameToFind = "sviyazov.a";
			MUser foundUser = null;
			for (final MUser user : users) {
				Assert.assertTrue(SimpleUtils.isNotNullAndNotEmpty(user
						.getLogin()));
				if (usernameToFind.equals(user.getLogin())) {
					foundUser = user;
				}
			}

			Assert.assertNotNull(foundUser);
			Assert.assertEquals(usernameToFind + "@tecomgroup.ru",
					foundUser.getEmail());
		} else {
			Assert.assertTrue(users.isEmpty());
		}
	}

	@Test
	public void testSuccessfullUpdatePassword() {
		userService.updatePassword(VALID_OLD_PASSWORD, NEW_PASSWORD);

		final MUser user = findUser("User");
		final PasswordEncoder encoder = new Md5PasswordEncoder();
		final String expectedPassword = encoder.encodePassword(NEW_PASSWORD,
				null);

		Assert.assertEquals(expectedPassword, user.getPassword());
	}

	@Test
	public void testUpdatePasswordWithInvalidOldPassword()
			throws SecurityException {
		thrown.expect(SecurityException.class);
		thrown.expect(new SecurityExceptionMatcher(
				Reason.INCORRECT_OLD_PASSWORD,
				"Reason must be 'invalid old password'"));

		userService.updatePassword(INVALID_OLD_PASSWORD, NEW_PASSWORD);
	}
}
