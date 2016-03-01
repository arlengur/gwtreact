/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.service;

import java.util.*;

import com.tecomgroup.qos.service.rbac.AuthorizeService;
import com.tecomgroup.qos.util.AuditLogger;
import com.tecomgroup.qos.util.AuditLogger.SyslogActionStatus;
import com.tecomgroup.qos.util.AuditLogger.SyslogCategory;
import org.apache.log4j.Logger;
import org.hibernate.collection.spi.PersistentCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;

import com.tecomgroup.qos.criterion.Criterion;
import com.tecomgroup.qos.criterion.Order;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.exception.QOSException;
import com.tecomgroup.qos.exception.ServiceException;
import com.tecomgroup.qos.exception.UserValidationException;
import com.tecomgroup.qos.modelspace.hibernate.HibernateEntityConverter;
import com.tecomgroup.qos.util.SimpleUtils;
import com.tecomgroup.qos.util.Utils;

/**
 * @author meleshin.o
 * 
 */
@Service("userManagerService")
public class DefaultUserManagerService extends AbstractService
		implements
			UserManagerService {

	private static Logger LOGGER = Logger
			.getLogger(DefaultUserManagerService.class);

	@Autowired
	private InternalPolicyConfigurationService policyConfigurationService;

	@Autowired
	private UserService userService;

	@Autowired
	private AuthorizeService authorizeService;

	private void changeUsersStatus(final Set<String> userKeys,
			final boolean isDisabled) {
		final List<MUser> users = executeInTransaction(true,
				new TransactionCallback<List<MUser>>() {

					@Override
					public List<MUser> doInTransaction(
							final TransactionStatus status) {
						return modelSpace.find(MUser.class, modelSpace
								.createCriterionQuery().in("login", userKeys));
					}
				});
		if (SimpleUtils.isNotNullAndNotEmpty(users)) {
			for (final MUser user : users) {
				executeInTransaction(false, new TransactionCallback<MUser>() {

					@Override
					public MUser doInTransaction(final TransactionStatus status) {
						changeUserStatus(user, isDisabled);
						return user;
					}
				});
			}
		}
		policyConfigurationService
				.updatePolicyConfigurationsByUsers(users);
	}

	private void changeUserStatus(final MUser user, final boolean isDisabled) {
		if (user.isDisabled() != isDisabled) {
			user.setDisabled(isDisabled);
			try {
				final String message = isDisabled ? "Disable" : "Enable";
				modelSpace.saveOrUpdate(user);
				if (LOGGER.isInfoEnabled()) {
					LOGGER.info(message + " user: " + user);
				}
			} catch (final Exception ex) {
				throw new ServiceException("Unable to change status for user:"
						+ user.getId(), ex);
			}
		}
	}

	@Override
	public void disableUsers(final Set<String> userKeys) {
		changeUsersStatus(userKeys, true);
		AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, "Users {} disabled", Arrays.toString(userKeys.toArray(new String[userKeys.size()])));
	}

	@Override
	public void enableUsers(final Set<String> userKeys) {
		changeUsersStatus(userKeys, false);
		AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, "Users {} enabled", Arrays.toString(userKeys.toArray(new String[userKeys.size()])));
	}

	@Override
	@Transactional(readOnly = true)
	public List<MUser> getAllUsersNotFiltered() {
		return modelSpace.getAll(MUser.class);
	}

	@Override
	@Transactional(readOnly = true)
	public List<MUser> getAllUsers() {
		return filterUsersByAccess(modelSpace.getAll(MUser.class));
	}

	private List<MUser> filterUsersByAccess(List<MUser> users) {
		MUser currentUser = userService.getCurrentUser();
		if (currentUser != null && currentUser.hasRole(MUser.Role.ROLE_SUPER_ADMIN)) {
			return users;
		}

		List<MUser> result = new ArrayList<>();
		for (MUser user : users) {
			if (!user.hasRole(MUser.Role.ROLE_SUPER_ADMIN)) {
				if(authorizeService.isSubordinate(user.getLogin())) {
					result.add(user);
				}
			}
		}
		return result;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MUser> getUsers(final Criterion criterion, final Order order,
			final Integer startPosition, final Integer size) {
		return filterUsersByAccess(modelSpace.find(MUser.class, criterion, order, startPosition,
				size));
	}

	@Override
	@Transactional(readOnly = true)
	public Long getUsersCount(final Criterion criterion) {
		List<MUser> users = filterUsersByAccess(modelSpace.find(MUser.class, criterion));
		return new Long(users.size());
	}

	@Override
	public MUser saveOrUpdateUser(final MUser user, final boolean updatePassword)
			throws QOSException {
		MUser result = null;
		try {
			if (validateUser(user)) {
				if (updatePassword) {
					final PasswordEncoder encoder = new Md5PasswordEncoder();
					user.setPassword(encoder.encodePassword(user.getPassword(),
							null));
				}
				boolean isNewUser=user.getId()==null?true:false;
				result = executeInTransaction(false,
						new TransactionCallback<MUser>() {

							@Override
							public MUser doInTransaction(
									final TransactionStatus status) {

								if (user.getId() != null) {
									final MUser existingUser = modelSpace.get(
											MUser.class, user.getId());

									modelSpace.evict(existingUser);
									modelSpace.saveOrUpdate(user);

									if (!Objects.equals(
											existingUser.getEmail(),
											user.getEmail())
											|| !Objects.equals(
													existingUser.getPhone(),
													user.getPhone())
											|| !Objects.equals(
													existingUser.isDisabled(),
													user.isDisabled())) {
										policyConfigurationService
												.updatePolicyConfigurationsByUser(user);
									}
								} else {
									modelSpace.saveOrUpdate(user);

								}
								return user;
							}
						});
				HibernateEntityConverter.convertHibernateCollections(result,
						PersistentCollection.class);
				if(isNewUser)
				{
					AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, " User {} created", user.toString());
				}else{
					AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.OK, " User {} updated", user.toString());
				}
			}
		} catch (UserValidationException e) {
			AuditLogger.critical(SyslogCategory.USER, SyslogActionStatus.NOK, "Unable to update or create user {}, reason : {}", user.toString(),e.getMessage());
			throw e;
		}

		return result;
	}

	private boolean validateUser(final MUser user) {
		final String email = user.getEmail();
		final String phone = user.getPhone();
		boolean isUserValid = true;
		UserValidationException.Reason reason = null;
		String errorMessage = null;

		if (!Utils.isUserLoginValid(user.getLogin())) {
			isUserValid = false;
			reason = UserValidationException.Reason.INCORRECT_LOGIN_FORMAT;
			errorMessage = "Login may contain uppercase or lowercase latin alphabet's characters, "
					+ " digits or nonalphanumeric characters \"_\", \".\", \"-\"."
					+ " Its size must be equals or less than "
					+ MUser.LOGIN_MAX_VALID_SIZE + " characters";
		} else if (SimpleUtils.isNotNullAndNotEmpty(email)
				&& !Utils.isEmailValid(email)) {
			isUserValid = false;
			reason = UserValidationException.Reason.INCORRECT_EMAIL_FORMAT;
			errorMessage = "Email format is incorrect";
		} else if (SimpleUtils.isNotNullAndNotEmpty(phone)
				&& !SimpleUtils.isPhoneNumberValid(phone)) {
			isUserValid = false;
			reason = UserValidationException.Reason.INCORRECT_PHONE_NUMBER_FORMAT;
			errorMessage = "Phone number format is incorrect";
		}

		if (!isUserValid) {
			throw new UserValidationException(reason, errorMessage);
		}

		return isUserValid;
	}
}
