/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * A group to combine users and to perform group operations.
 * 
 * @author kunilov.p
 * 
 */
@Entity
public class MUserGroup extends MContactInformation {

	private static final long serialVersionUID = 4316577228269961656L;

	@Column(nullable = false, unique = true)
	private String name;

	@ManyToMany
	private List<MUser> users;

	@Override
	@Transient
	@JsonIgnore
	public Collection<MContactInformation> getContacts() {
		final List<MContactInformation> contacts = new ArrayList<MContactInformation>();
		for (final MUser user : users) {
			contacts.add(user);
		}
		return Collections.unmodifiableCollection(contacts);
	}

	@Override
	@Transient
	public Collection<String> getEmails() {
		final Set<String> userEmails = new HashSet<String>();

		for (final MUser user : users) {
			if(!user.isDisabled()) {
				userEmails.addAll(user.getEmails());
			}
		}

		return Collections.unmodifiableCollection(userEmails);
	}

	@Override
	@Transient
	public String getKey() {
		return name;
	}

	public String getName() {
		return name;
	}

	@Override
	@Transient
	public Collection<String> getPhones() {
		final Set<String> userPhones = new HashSet<String>();

		for (final MUser user : users) {
			if(!user.isDisabled()) {
				userPhones.add(user.getPhone());
			}
		}

		return Collections.unmodifiableCollection(userPhones);
	}

    @Override
    public UserSettings.NotificationLanguage getNotificationLanguage() {
        return null;
    }

    public List<MUser> getUsers() {
		return users;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public void setUsers(final List<MUser> users) {
		this.users = users;
	}

	@Override
	public String toString() {
		return "{ name = " + name + ", users = " + users + " }";
	}
}
