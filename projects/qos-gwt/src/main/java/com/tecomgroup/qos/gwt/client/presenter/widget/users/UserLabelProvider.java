/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.presenter.widget.users;

import com.sencha.gxt.data.shared.LabelProvider;
import com.tecomgroup.qos.domain.MUser;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;

/**
 * @author ivlev.e
 * 
 */
public class UserLabelProvider implements LabelProvider<MUser> {

	public static String toLabel(final MUser user) {
		return user.toString();
	}

	private QoSMessages messages;

	public UserLabelProvider() {
		super();
	}

	public UserLabelProvider(final QoSMessages messages) {
		this();
		this.messages = messages;
	}

	@Override
	public String getLabel(final MUser user) {
		String label = UserLabelProvider.toLabel(user);

		if (messages != null && user.isDisabled()) {
			label += " - (" + messages.disabledUser() + ")";
		}

		return label;
	}

	public void setMessages(final QoSMessages messages) {
		this.messages = messages;
	}
}
