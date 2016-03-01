/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain;

import java.util.Date;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Formula;

import com.tecomgroup.qos.domain.MAlertType.PerceivedSeverity;
import com.tecomgroup.qos.domain.MAlertType.UpdateType;

/**
 * A report for {@link MAlert}. It starts when {@link MAlertUpdate} with
 * {@link UpdateType#NEW} or {@link UpdateType#SEVERITY_CHANGE} comes. It ends
 * when {@link MAlertUpdate} with {@link UpdateType#AUTO_CLEARED},
 * {@link UpdateType#OPERATOR_CLEARED}, {@link UpdateType#OPERATOR_DELETED} or
 * {@link UpdateType#SEVERITY_CHANGE} comes.
 * 
 * <b>IMPORTANT</b> <br/>
 * Only one opened report (report with NULL as {@link #endDateTime}) is possible
 * for every alert. Before an addition another opened report, it is obligatory
 * to close previous one.
 * 
 * 
 * 
 * @author kunilov.p
 * 
 */
@Entity
public class MAlertReport extends MAbstractEntity {
	private static final long serialVersionUID = -1966730438329603611L;

	@Column(nullable = false)
	private Date startDateTime;

	@Column(nullable = true)
	private Date endDateTime;

	@Column(nullable = false)
	@Enumerated(value = EnumType.STRING)
	private PerceivedSeverity perceivedSeverity;

	@OneToOne(cascade = {CascadeType.PERSIST, CascadeType.REFRESH,
			CascadeType.MERGE})
	private MAlert alert;

	/**
	 * This property allows duration sorting via hql, it returns dateTime
	 * interval as string. It is not possible to convert dateTime interval to
	 * long equally for all dbs because of limited hibernate support.
	 * 
	 * TODO: Convert dateTime interval to Long or implement other solution to
	 * calculate duration as long in sql or hql.
	 */
	@JsonIgnore
	@Formula("case when (endDateTime is null) then (now() - startDateTime) else (endDateTime - startDateTime) end")
	private String duration;

	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	@JsonIgnore
	private Long id;

	/**
	 * @return the alert
	 */
	public MAlert getAlert() {
		return alert;
	}

	/**
	 * Calculates duration on the fly.
	 * 
	 * @see {@link #duration}
	 * 
	 * @return the duration
	 */
	@Transient
	@JsonIgnore
	public Long getDuration() {
		final Long duration;
		if (endDateTime == null) {
			duration = System.currentTimeMillis() - startDateTime.getTime();
		} else {
			duration = endDateTime.getTime() - startDateTime.getTime();
		}
		return duration;
	}

	/**
	 * @return the endDateTime
	 */
	public Date getEndDateTime() {
		return endDateTime;
	}

	/**
	 * @return the perceivedSeverity
	 */
	public PerceivedSeverity getPerceivedSeverity() {
		return perceivedSeverity;
	}

	/**
	 * @return the startDateTime
	 */
	public Date getStartDateTime() {
		return startDateTime;
	}

	/**
	 * @param alert
	 *            the alert to set
	 */
	public void setAlert(final MAlert alert) {
		this.alert = alert;
	}

	/**
	 * @param endDateTime
	 *            the endDateTime to set
	 */
	public void setEndDateTime(final Date endDateTime) {
		this.endDateTime = endDateTime;
	}

	/**
	 * @param perceivedSeverity
	 *            the perceivedSeverity to set
	 */
	public void setPerceivedSeverity(final PerceivedSeverity perceivedSeverity) {
		this.perceivedSeverity = perceivedSeverity;
	}

	/**
	 * @param startDateTime
	 *            the startDateTime to set
	 */
	public void setStartDateTime(final Date startDateTime) {
		this.startDateTime = startDateTime;
	}

	@Override
	public String toString() {
		return "{ id = " + getId() + ", startDateTime = " + startDateTime
				+ ", endDateTime = " + endDateTime + ", perceivedSeverity = "
				+ perceivedSeverity + ", alert = " + alert + " }";
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
