/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import com.tecomgroup.qos.domain.MContactInformation;

/**
 * @author kunilov.p
 * 
 */
@Entity
public abstract class MPolicyActionWithContacts extends MPolicyAction {

	private static final long serialVersionUID = 91516113114940105L;

	@ManyToMany
	protected List<MContactInformation> contacts;

	public MPolicyActionWithContacts() {
		super();
		contacts = new ArrayList<MContactInformation>();
	}

	public MPolicyActionWithContacts(
			final MPolicyActionWithContacts policyActionWithContacts) {
		super(policyActionWithContacts);
		final List<MContactInformation> originalContacts = policyActionWithContacts
				.getContacts();
		if (originalContacts != null) {
			contacts = new ArrayList<MContactInformation>(originalContacts);
		}
	}

	@Override
	public abstract MPolicyActionWithContacts copy();

	public List<MContactInformation> getContacts() {
		return contacts;
	}

	public void setContacts(final List<MContactInformation> contacts) {
		this.contacts = contacts;
	}
}
