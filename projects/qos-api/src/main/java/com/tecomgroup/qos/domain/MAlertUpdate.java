/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.Date;

import javax.persistence.*;

import com.tecomgroup.qos.domain.MAlertType.UpdateType;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author kunilov.p
 * 
 */
@SuppressWarnings("serial")
@Entity
public class MAlertUpdate extends MAbstractEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE,
			generator = "malertupdate_seq")
	@SequenceGenerator(name = "malertupdate_seq",
			sequenceName = "malertupdate_id_seq",allocationSize=1)
	private Long id;

	/**
	 * @uml.property name="alert"
	 */
	@OneToOne(cascade = CascadeType.PERSIST)
	private MAlert alert;

	/**
	 * @uml.property name="dateTime"
	 */
	@Column(nullable = false)
	private Date dateTime;

	/**
	 * @uml.property name="user"
	 */
	@Column(nullable = false, name = "USER_NAME")
	private String user;

	/**
	 * @uml.property name="field"
	 */
	private String field;

	/**
	 * @uml.property name="oldValue"
	 */
	private String oldValue;

	/**
	 * @uml.property name="newValue"
	 */
	private String newValue;

	@Column(length = 1024)
	private String comment;

	/**
	 * @uml.property name="updateType"
	 */
	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private UpdateType updateType;

	public MAlertUpdate() {
		super();
	}

	public MAlertUpdate(final MAlert alert, final UpdateType updateType,
			final Date dateTime, final String ssoToken, final String field,
			final Object oldValue, final Object newValue, final String comment) {
		this();
		setAlert(alert);
		setUpdateType(updateType);
		setDateTime(dateTime);
		setUser(ssoToken);
		setField(field);
		setNewValue(newValue == null ? null : newValue.toString());
		setOldValue(oldValue == null ? null : oldValue.toString());
		setComment(comment);
	}

	/**
	 * @return the alert
	 * @uml.property name="alert"
	 */
	public MAlert getAlert() {
		return alert;
	}

	/**
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * @return the dateTime
	 * @uml.property name="dateTime"
	 */
	public Date getDateTime() {
		return dateTime;
	}

	/**
	 * @return the field
	 * @uml.property name="field"
	 */
	public String getField() {
		return field;
	}

	/**
	 * @return the newValue
	 * @uml.property name="newValue"
	 */
	public String getNewValue() {
		return newValue;
	}

	/**
	 * @return the oldValue
	 * @uml.property name="oldValue"
	 */
	public String getOldValue() {
		return oldValue;
	}

	/**
	 * @return the updateType
	 * @uml.property name="updateType"
	 */
	public UpdateType getUpdateType() {
		return updateType;
	}

	/**
	 * @return the user
	 * @uml.property name="user"
	 */
	public String getUser() {
		return user;
	}
	/**
	 * @param alert
	 *            the alert to set
	 * @uml.property name="alert"
	 */
	public void setAlert(final MAlert alert) {
		this.alert = alert;
	}

	/**
	 * @param comment
	 *            the comment to set
	 */
	public void setComment(final String comment) {
		this.comment = comment;
	}

	/**
	 * @param dateTime
	 *            the dateTime to set
	 * @uml.property name="dateTime"
	 */
	public void setDateTime(final Date dateTime) {
		this.dateTime = dateTime;
	}

	/**
	 * @param field
	 *            the field to set
	 * @uml.property name="field"
	 */
	public void setField(final String field) {
		this.field = field;
	}

	/**
	 * @param newValue
	 *            the newValue to set
	 * @uml.property name="newValue"
	 */
	public void setNewValue(final String newValue) {
		this.newValue = newValue;
	}

	/**
	 * @param oldValue
	 *            the oldValue to set
	 * @uml.property name="oldValue"
	 */
	public void setOldValue(final String oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * @param updateType
	 *            the updateType to set
	 * @uml.property name="updateType"
	 */
	public void setUpdateType(final UpdateType updateType) {
		this.updateType = updateType;
	}

	/**
	 * @param user
	 *            the user to set
	 * @uml.property name="user"
	 */
	public void setUser(final String user) {
		this.user = user;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "{alert=" + alert.getAlertType().getName() + ", updateType="
				+ updateType + ", dateTime=" + dateTime + ", field=" + field
				+ ", newValue=" + newValue + ", oldValue=" + oldValue + "}";
	}
}
