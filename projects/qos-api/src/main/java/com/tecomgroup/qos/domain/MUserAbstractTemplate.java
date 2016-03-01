/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("serial")
@MappedSuperclass
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "user_id"})})
public abstract class MUserAbstractTemplate extends MAbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public enum BaseTemplateType implements TemplateType {
		RESULT(MUserResultTemplate.class.getName()), ALERT(
				MUserAlertsTemplate.class.getName()), REPORT(
				MUserReportsTemplate.class.getName());

		private final String className;

		private BaseTemplateType(final String className) {
			this.className = className;
		}

		@Override
		public String getTemplateClassName() {
			return className;
		}
	}

	public interface TemplateType {
		String getTemplateClassName();
	}

	@ManyToOne
	@JoinColumn(name = "user_id")
	protected MUser user;

	protected String name;

	public MUserAbstractTemplate() {
		super();
	}

	public MUserAbstractTemplate(final MUserAbstractTemplate abstractTemplate) {
		this();
		setName(abstractTemplate.getName());
		setUser(abstractTemplate.getUser());
	}

	public MUserAbstractTemplate(final String name) {
		this();
		setName(name);
	}

	public abstract MUserAbstractTemplate copy();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the user
	 */
	public MUser getUser() {
		return user;
	}

	@Transient
	@JsonIgnore
	public boolean isValid() {
		return true;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(final MUser user) {
		this.user = user;
	}
}
