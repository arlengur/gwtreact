/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.*;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

import org.hibernate.annotations.Type;

import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.UpdatableEntity;
import com.tecomgroup.qos.domain.rbac.UISubject;
import com.tecomgroup.qos.domain.rbac.MRole;

/**
 * Пользователь системы.
 * 
 * @author kunilov.p
 */
@Entity
public class MUser extends MContactInformation
		implements
			UpdatableEntity<MUser>,
			Disabled { 
	/**
	 * Роль пользователя в системе.
	 * 
	 * @author kunilov.p
	 */

	public boolean isPermitted(UISubject page) {
		for(MRole role: this.getRoles()) {
			if(role.isPermitted(page)) {
				return true;
			}
		}
		return false;
	}

	private static final long serialVersionUID = 1348437604616629000L;

	public final static String LOGIN_VALID_PATTERN = "^[\\w\\.\\-_]+$";

	public final static String EMAIL_VALID_PATTERN = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+"
			+ "(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-"
			+ "\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])"
			+ "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|"
			+ "\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?"
			+ "[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-"
			+ "\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

	public final static int LOGIN_MAX_VALID_SIZE = 255;

	/**
	 * @uml.property name="login"
	 */
	@Column(nullable = false, unique = true)
	private String login;

	/**
	 * @uml.property name="division"
	 */
	@OneToOne
	private MDivision division;

	/**
	 * @uml.property name="firstName"
	 */
	private String firstName;

	/**
	 * @uml.property name="lastName"
	 */
	private String lastName;

	/**
	 * @uml.property name="email"
	 */
	private String email;

	/**
	 * @uml.property name="phone"
	 */
	private String phone;

	/**
	 * @uml.property name="position"
	 */
	private String position;

	/**
	 * @uml.property name="disabled"
	 */
	@Column(nullable = false)
	private boolean disabled = false;

	@Embedded
	private UserSettings settings;

	/**
	 * Indicates whether user should be authenticated using LDAP
	 */
	@Column(name = "ldap_authenticated", nullable = false)
	private boolean ldapAuthenticated = false;

	/**
	 * @uml.property name="role"
	 */
	@Type(type="com.tecomgroup.qos.modelspace.hibernate.HibernateMRoleArrayType")
	@Column(name="roles") 
	private List<MRole> roles;

	/**
	 * @uml.property name="secondName"
	 */
	private String secondName;

	private String password;

	public void addRole(final MRole role) {
		if (getRoles() == null) {
			roles = new ArrayList<MRole>();
		}
		getRoles().add(role);
	}

	@Override
	@Transient
	@JsonIgnore
	public Collection<MContactInformation> getContacts() {
		return Collections.unmodifiableCollection(Arrays
				.asList(new MContactInformation[]{this}));
	}

	/**
	 * Getter of the property <tt>division</tt>
	 * 
	 * @return Returns the division.
	 * @uml.property name="division"
	 */
	public MDivision getDivision() {
		return division;
	}

	/**
	 * Getter of the property <tt>email</tt>
	 * 
	 * @return Returns the email.
	 * @uml.property name="email"
	 */
	public String getEmail() {
		return email;
	}

	@Override
	@Transient
	public Collection<String> getEmails() {
		Collection<String> emails = null;
		if (email != null && !email.trim().isEmpty()) {
			emails = Collections.unmodifiableCollection(Arrays
					.asList(new String[]{getEmail()}));
		} else {
			emails = Collections.emptyList();
		}
		return emails;
	}

	/**
	 * Getter of the property <tt>firstName</tt>
	 * 
	 * @return Returns the firstName.
	 * @uml.property name="firstName"
	 */
	public String getFirstName() {
		return firstName;
	}

	@Override
	@Transient
	public String getKey() {
		return login;
	}

	@Transient
	public String getLabel() {
		final StringBuilder fullName = new StringBuilder();

		if (!(lastName == null || lastName.trim().isEmpty())) {
			fullName.append(lastName);
		}

		if (!(firstName == null || firstName.trim().isEmpty())) {
			fullName.append(" ");
			fullName.append(firstName);
		}

		if (!(secondName == null || secondName.trim().isEmpty())) {
			fullName.append(" ");
			fullName.append(secondName);
		}

		String label = login;
		if (fullName.length() > 0) {
			label += " (" + fullName.toString().trim() + ")";
		}
		return label;
	}

	/**
	 * Getter of the property <tt>lastName</tt>
	 * 
	 * @return Returns the lastName.
	 * @uml.property name="lastName"
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Getter of the property <tt>login</tt>
	 * 
	 * @return Returns the login.
	 * @uml.property name="login"
	 */
	public String getLogin() {
		return login;
	}

	@Override
	public UserSettings.NotificationLanguage getNotificationLanguage() {
		return settings == null ? null : settings.getNotificationLanguage();
	}

	@Transient
	@JsonIgnore
	public UserSettings getOrCreateSettings() {
		if (settings == null) {
			settings = new UserSettings();
		}
		return settings;
	}

	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Getter of the property <tt>phone</tt>
	 * 
	 * @return Returns the phone.
	 * @uml.property name="phone"
	 */
	public String getPhone() {
		return phone;
	}

	@Override
	@Transient
	public Collection<String> getPhones() {
		Collection<String> phones = null;
		if (phone != null && !phone.trim().isEmpty()) {
			phones = Collections.unmodifiableCollection(Arrays
					.asList(new String[]{getPhone()}));
		} else {
			phones = Collections.emptyList();
		}
		return phones;
	}

	/**
	 * Getter of the property <tt>position</tt>
	 * 
	 * @return Returns the position.
	 * @uml.property name="position"
	 */
	public String getPosition() {
		return position;
	}

	/**
	 * @return the roles
	 */
	public List<MRole> getRoles() {
		if(roles == null) {
			return new ArrayList<MRole>();
		}
		return roles;
	}

	/**
	 * Getter of the property <tt>fatherName</tt>
	 * 
	 * @return Returns the fatherName.
	 * @uml.property name="secondName"
	 */
	public String getSecondName() {
		return secondName;
	}

	public UserSettings getSettings() {
		return settings;
	}

	public boolean hasRole(final MRole role) {
		return getRoles() == null ? false : getRoles().contains(role);
	}

	/**
	 * @return the disabled
	 */
	@Override
	public boolean isDisabled() {
		return disabled;
	}

	public boolean isLdapAuthenticated() {
		return ldapAuthenticated;
	}

	@Override
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	/**
	 * Setter of the property <tt>division</tt>
	 * 
	 * @param division
	 *            The division to set.
	 * @uml.property name="division"
	 */
	public void setDivision(final MDivision division) {
		this.division = division;
	}

	/**
	 * Setter of the property <tt>email</tt>
	 * 
	 * @param email
	 *            The email to set.
	 * @uml.property name="email"
	 */
	public void setEmail(final String email) {
		this.email = email;
	}

	/**
	 * Setter of the property <tt>firstName</tt>
	 * 
	 * @param firstName
	 *            The firstName to set.
	 * @uml.property name="firstName"
	 */
	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Setter of the property <tt>lastName</tt>
	 * 
	 * @param lastName
	 *            The lastName to set.
	 * @uml.property name="lastName"
	 */
	public void setLastName(final String lastName) {
		this.lastName = lastName;
	}

	public void setLdapAuthenticated(final boolean ldapAuthenticated) {
		this.ldapAuthenticated = ldapAuthenticated;
	}

	/**
	 * Setter of the property <tt>login</tt>
	 * 
	 * @param login
	 *            The login to set.
	 * @uml.property name="login"
	 */
	public void setLogin(final String login) {
		this.login = login;
	}

	/**
	 * @param password
	 *            the password to set
	 */
	public void setPassword(final String password) {
		this.password = password;
	}

	/**
	 * Setter of the property <tt>phone</tt>
	 * 
	 * @param phone
	 *            The phone to set.
	 * @uml.property name="phone"
	 */
	public void setPhone(final String phone) {
		this.phone = phone;
	}

	/**
	 * Setter of the property <tt>position</tt>
	 * 
	 * @param position
	 *            The position to set.
	 * @uml.property name="position"
	 */
	public void setPosition(final String position) {
		this.position = position;
	}

	/**
	 * @param roles
	 *            the roles to set
	 */
	public void setRoles(final List<MRole> roles) {
		this.roles = roles;
	}

	/**
	 * Setter of the property <tt>fatherName</tt>
	 * 
	 * @param secondName
	 *            The second name to set.
	 * @uml.property name="secondName"
	 */
	public void setSecondName(final String secondName) {
		this.secondName = secondName;
	}

	public void setSettings(final UserSettings userSettings) {
		this.settings = userSettings;
	}

	@Override
	public String toString() {
		return getLabel();
	}

	@Override
	public boolean updateSimpleFields(final MUser user) {
		boolean isUpdated = false;

		if (user != null) {

			if (!equals(getLastName(), user.getLastName())) {
				setLastName(user.getLastName());
				isUpdated = true;
			}

			if (!equals(getFirstName(), user.getFirstName())) {
				setFirstName(user.getFirstName());
				isUpdated = true;
			}

			if (!equals(getSecondName(), user.getSecondName())) {
				setSecondName(user.getSecondName());
				isUpdated = true;
			}

			if (!equals(getPosition(), user.getPosition())) {
				setPosition(user.getPosition());
				isUpdated = true;
			}

			if (!equals(getPhone(), user.getPhone())) {
				setPhone(user.getPhone());
				isUpdated = true;
			}

			if (!equals(getEmail(), user.getEmail())) {
				setEmail(user.getEmail());
				isUpdated = true;
			}

			if (getSettings() != null) {
				isUpdated |= getSettings().updateSimpleFields(
						user.getSettings());
			} else if (user.getSettings() != null) {
				setSettings(user.getSettings());
				isUpdated = true;
			}
		}

		return isUpdated;
	}
}
