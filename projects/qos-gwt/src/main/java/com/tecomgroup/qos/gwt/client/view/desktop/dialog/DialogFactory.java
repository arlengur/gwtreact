/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.dialog;

import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.Dialog.DialogMessages;
import com.tecomgroup.qos.gwt.client.i18n.CommonMessages;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.ResultsAnalyticsPresenter.MyView;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.RenameDialog.RenameHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.BaseDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGroupSelectionDialog;

/**
 * Show message dialogs
 * 
 * @author abondin
 * 
 */
public class DialogFactory {
	private final QoSMessages messages;
	private final AppearanceFactory appearanceFactory;

	public final static int DEFAULT_CONFIRMATION_DIALOG_WIDTH = 200;

	@Inject
	public DialogFactory(final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider) {
		super();
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
	}

	public AgentGroupSelectionDialog createAgentGroupSelectionDialog(
			final AgentGisWidget agentGisWidget) {
		final AgentGroupSelectionDialog dialog = new AgentGroupSelectionDialog(
				appearanceFactory, messages, agentGisWidget);
		return dialog;
	}

	public ConfirmationDialog createConfirmationDialog(
			final ConfirmationHandler handler, final String title,
			final String message, final CommentMode commentMode) {
		return createConfirmationDialog(handler, title, message, commentMode,
				null, DEFAULT_CONFIRMATION_DIALOG_WIDTH);
	}

	public ConfirmationDialog createConfirmationDialog(
			final ConfirmationHandler handler, final String title,
			final String message, final CommentMode commentMode,
			final DialogMessages dialogMessages) {
		return createConfirmationDialog(handler, title, message, commentMode,
				dialogMessages, DEFAULT_CONFIRMATION_DIALOG_WIDTH);
	}

	public ConfirmationDialog createConfirmationDialog(
			final ConfirmationHandler handler, final String title,
			final String message, final CommentMode commentMode,
			final DialogMessages dialogMessages, final int dialogWidth) {
		final ConfirmationDialog dialog = new ConfirmationDialog(
				appearanceFactory, messages, handler, title, message,
				commentMode, dialogMessages, dialogWidth);
		return dialog;
	}

	public ConfirmationDialog createConfirmationDialog(
			final ConfirmationHandler handler, final String title,
			final String message, final CommentMode commentMode,
			final int dialogWidth) {
		return createConfirmationDialog(handler, title, message, commentMode,
				null, dialogWidth);
	}

	public BaseDialog<CommonMessages> createErrorDialog(final String message,
			final Throwable error) {
		final BaseDialog<CommonMessages> dialog = new ErrorDialog(
				appearanceFactory, messages, message);
		return dialog;
	}
	public BaseDialog<CommonMessages> createInformationDialog(
			final String message) {
		final BaseDialog<CommonMessages> dialog = new InformationDialog(
				appearanceFactory, messages, message);
		return dialog;
	}

	public RenameDialog createRenameDialog(final RenameHandler handler,
			final String oldName, final MyView view) {
		return new RenameDialog(appearanceFactory, messages, handler, oldName,
				view);
	}

	public WarningDialog createWarningDialog(final String title,
			final String message) {
		return new WarningDialog(title, message, appearanceFactory, messages);
	}

	public WarningDialog createWarningDialog(final String title,
			final String message, final ConfirmationHandler handler) {
		return new WarningDialog(title, message, appearanceFactory, messages,
				handler);
	}

	public WarningDialog createWarningDialog(final String title,
			final String message, final ConfirmationHandler handler,
			final DialogMessages dialogMessages, final int dialogWidth) {
		return new WarningDialog(title, message, appearanceFactory, messages,
				handler, dialogMessages, dialogWidth);
	}
}
