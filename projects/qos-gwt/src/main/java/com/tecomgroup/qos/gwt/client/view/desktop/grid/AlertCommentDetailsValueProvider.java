/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.grid;

import com.sencha.gxt.core.client.ValueProvider;
import com.tecomgroup.qos.domain.MAlertUpdate;
import com.tecomgroup.qos.gwt.client.bean.AlertCommentDetails;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;

/**
 * Creates an object {@link AlertCommentDetails} with alert comment details for
 * provided alertUpdate {@link MAlertUpdate}. It is not possible to use
 * {@link MAlertUpdate} itself as data container because of value processing,
 * which is impossible in qos-appearance project.
 * 
 * @author novohatskiy.r
 * 
 */
public class AlertCommentDetailsValueProvider
		implements
			ValueProvider<MAlertUpdate, AlertCommentDetails> {

	private final AlertUpdateTypeLabelProvider alertUpdateTypeLavelProvider;

	public AlertCommentDetailsValueProvider(final QoSMessages messages) {
		alertUpdateTypeLavelProvider = new AlertUpdateTypeLabelProvider(
				messages);
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public AlertCommentDetails getValue(final MAlertUpdate alertUpdate) {
		final AlertCommentDetails commentDetails = new AlertCommentDetails();
		commentDetails.setDateTime(DateUtils.DATE_TIME_FORMATTER
				.format(alertUpdate.getDateTime()));
		commentDetails.setAuthor(alertUpdate.getUser());
		commentDetails.setComment(alertUpdate.getComment());
		commentDetails.setUpdateType(alertUpdateTypeLavelProvider
				.getLabel(alertUpdate.getUpdateType()));
		return commentDetails;
	}

	@Override
	public void setValue(final MAlertUpdate alertUpdate,
			final AlertCommentDetails commentDetails) {
	}
}
