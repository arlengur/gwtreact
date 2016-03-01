/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.Collection;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import com.tecomgroup.qos.HasUniqueKey;

/**
 * Entity that contains contact information.
 * 
 * @author novohatskiy.r
 * 
 */
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class MContactInformation extends MAbstractEntity
		implements
			HasUniqueKey {

	private static final long serialVersionUID = 5533227468114631605L;
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * @return unmodifiable collection of nested contacts or collection with
	 *         only itself. I.e: group will return all nested users (group
	 *         itself is excluded), user will return himself wrapped in a
	 *         collection
	 */
	@Transient
	@JsonIgnore
	public abstract Collection<MContactInformation> getContacts();

	/**
	 * @return unmodifiable collection of emails.
	 */
	@Transient
	@JsonIgnore
	public abstract Collection<String> getEmails();

	@Override
	@JsonIgnore
	@Transient
	public abstract String getKey();

	/**
	 * @return unmodifiable collection of phone numbers.
	 */
	@Transient
	@JsonIgnore
	public abstract Collection<String> getPhones();

    @Transient
    @JsonIgnore
    public abstract UserSettings.NotificationLanguage getNotificationLanguage();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
