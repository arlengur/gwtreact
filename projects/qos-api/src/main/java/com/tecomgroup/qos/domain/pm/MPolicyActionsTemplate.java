/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.domain.pm;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * A template group of {@link MPolicyAction}. Can be applied to a policy.
 * 
 * @author novohatskiy.r
 * 
 */
@Entity
public class MPolicyActionsTemplate extends MPolicyComponentTemplate {

	private static final long serialVersionUID = -2264578740233344991L;

	@OneToMany(cascade = CascadeType.ALL)
	private List<MPolicyActionWithContacts> actions;

	public MPolicyActionsTemplate() {
		super();
	}

	public MPolicyActionsTemplate(final MPolicyActionsTemplate actionsTemplate) {
		super(actionsTemplate);

		final List<MPolicyActionWithContacts> templateActions = actionsTemplate
				.getActions();
		if (templateActions != null) {
			actions = new ArrayList<MPolicyActionWithContacts>();
			for (final MPolicyActionWithContacts action : templateActions) {
				actions.add(action.copy());
			}
		}
	}

	public MPolicyActionsTemplate copy() {
		return new MPolicyActionsTemplate(this);
	}

	/**
	 * @return the actions
	 */
	public List<MPolicyActionWithContacts> getActions() {
		return actions;
	}

	/**
	 * @param actions
	 *            the actions to set
	 */
	public void setActions(final List<MPolicyActionWithContacts> actions) {
		this.actions = actions;
	}
}
