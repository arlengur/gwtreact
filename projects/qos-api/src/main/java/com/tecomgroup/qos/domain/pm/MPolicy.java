/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Key object for policy manager
 *
 * @author abondin
 *
 */
@Entity
public class MPolicy extends MPolicySharedData {

	private static final long serialVersionUID = -8392572224117094755L;

	@JsonIgnore
	@ManyToOne
	private MPolicyActionsTemplate actionsTemplate;

	@JsonIgnore
	@ManyToOne
	private MPolicyConditionsTemplate conditionsTemplate;

	public MPolicy() {
		super();
	}

	public MPolicy(final MPolicy policy) {
		super(policy);
		key = policy.getKey();
	}

	public MPolicyConditionsTemplate getConditionsTemplate() {
		return conditionsTemplate;
	}

	public MPolicyActionsTemplate getActionsTemplate() {
		return actionsTemplate;
	}

	public void setConditionsTemplate(
			final MPolicyConditionsTemplate conditionsTemplate) {
		this.conditionsTemplate = conditionsTemplate;
	}

	public void setActionsTemplate(
			final MPolicyActionsTemplate notificationTemplate) {
		this.actionsTemplate = notificationTemplate;
	}

}
