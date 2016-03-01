/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;

import org.codehaus.jackson.annotate.JsonIgnore;

import com.tecomgroup.qos.Deleted;
import com.tecomgroup.qos.Disabled;
import com.tecomgroup.qos.domain.MSource;
import com.tecomgroup.qos.domain.Source;

/**
 * @author kunilov.p
 * 
 */
@Entity
@SuppressWarnings("serial")
public abstract class MPolicySharedData extends MSource
		implements
			Disabled,
			Deleted {

	@OneToOne(cascade = CascadeType.ALL, optional = false)
	private MPolicyCondition condition;

	@OneToMany(cascade = CascadeType.ALL)
	@JoinTable(uniqueConstraints = @UniqueConstraint(columnNames = {"actions_id"}))
	private List<MPolicyAction> actions;

	@Column(nullable = false)
	@Embedded
	private Source source;

	@Column(nullable = false)
	private boolean disabled = false;

	@Column(nullable = false)
	private boolean deleted = false;

	public MPolicySharedData() {
		super();
	}

	public MPolicySharedData(final MPolicySharedData policy) {
		this();
		this.condition = policy.getCondition().copy();
		this.actions = new ArrayList<MPolicyAction>();
		for (final MPolicyAction policyAction : policy.getActions()) {
			this.actions.add(policyAction.copy());
		}
		this.source = policy.getSource();
		this.displayName = policy.getDisplayName();
	}

	@Transient
	public void addAction(final MPolicyAction action) {
		if (actions == null) {
			actions = new ArrayList<MPolicyAction>();
		}
		actions.add(action);
	}

	public List<MPolicyAction> getActions() {
		return actions;
	}

	@Transient
	@JsonIgnore
	public List<MPolicyActionWithContacts> getActionsWithContacts() {
		final List<MPolicyActionWithContacts> actionsWithContacts = new ArrayList<MPolicyActionWithContacts>();
		for (final MPolicyAction policyAction : actions) {
			if (policyAction instanceof MPolicyActionWithContacts) {
				actionsWithContacts
						.add((MPolicyActionWithContacts) policyAction);
			}
		}
		return actionsWithContacts;
	}

	public MPolicyCondition getCondition() {
		return condition;
	}

	@Transient
	@JsonIgnore
	public List<MPolicySendAlert> getSendAlertActions() {
		final List<MPolicySendAlert> sendAlertActions = new ArrayList<MPolicySendAlert>();
		for (final MPolicyAction policyAction : actions) {
			if (policyAction instanceof MPolicySendAlert) {
				sendAlertActions.add((MPolicySendAlert) policyAction);
			}
		}
		return sendAlertActions;
	}

	@Transient
	@JsonIgnore
	public List<MPolicySendEmail> getSendEmailActions() {
		final List<MPolicySendEmail> sendEmailActions = new ArrayList<MPolicySendEmail>();
		for (final MPolicyAction policyAction : actions) {
			if (policyAction instanceof MPolicySendEmail) {
				sendEmailActions.add((MPolicySendEmail) policyAction);
			}
		}
		return sendEmailActions;
	}

	@Transient
	@JsonIgnore
	public List<MPolicySendSms> getSendSmsActions() {
		final List<MPolicySendSms> sendSmsActions = new ArrayList<MPolicySendSms>();
		for (final MPolicyAction policyAction : actions) {
			if (policyAction instanceof MPolicySendSms) {
				sendSmsActions.add((MPolicySendSms) policyAction);
			}
		}
		return sendSmsActions;
	}

	public Source getSource() {
		return source;
	}

	@Override
	public boolean isDeleted() {
		return deleted;
	}

	@Override
	public boolean isDisabled() {
		return disabled;
	}

	@Transient
	public boolean removeAction(final MPolicyAction action) {
		boolean result = false;
		if (actions != null) {
			result = actions.remove(action);
		}
		return result;
	}

	public void setActions(final List<MPolicyAction> actions) {
		this.actions = actions;
	}

	public void setCondition(final MPolicyCondition condition) {
		this.condition = condition;
	}

	@Override
	public void setDeleted(final boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public void setDisabled(final boolean disabled) {
		this.disabled = disabled;
	}

	public void setSource(final Source source) {
		this.source = source;
	}

	@Override
	public String toString() {
		return "{key = " + getKey() + ", source = " + getSource()
				+ ", displayName = " + getDisplayName() + "}";
	}

	@Override
	public boolean updateSimpleFields(final MSource source) {
		boolean isUpdated = super.updateSimpleFields(source);
		if (source instanceof MPolicySharedData) {
			isUpdated |= condition
					.updateSimpleFields(((MPolicySharedData) source)
							.getCondition());
		}
		return isUpdated;
	}
}
