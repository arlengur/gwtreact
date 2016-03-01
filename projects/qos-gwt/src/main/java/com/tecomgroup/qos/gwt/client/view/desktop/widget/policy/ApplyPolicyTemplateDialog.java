/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import java.util.Collection;

import com.sencha.gxt.cell.core.client.form.ComboBoxCell.TriggerAction;
import com.sencha.gxt.data.shared.LabelProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer.HBoxLayoutAlign;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomComboBox;

/**
 * @author sviyazov.a
 *
 */
public class ApplyPolicyTemplateDialog<T> extends QoSDialog {

	public static interface ApplyTemplateHandler<T> {
		void applyTemplate(T template);
	}

	private CustomComboBox<T> templateControl;

	private final ListStore<T> store;

	private ApplyTemplateHandler<T> handler;

	private final LabelProvider<T> comboBoxLabelProvider;

	public ApplyPolicyTemplateDialog(final AppearanceFactory appearanceFactory,
			final QoSMessages messages, final ListStore<T> store,
			final LabelProvider<T> comboBoxLabelProvider) {
		super(appearanceFactory, messages, new BaseDialogMessages(messages) {
			@Override
			public String ok() {
				return messages.apply();
			}
		});
		setPredefinedButtons(PredefinedButton.OK, PredefinedButton.CANCEL);
		setButtonAlign(BoxLayoutPack.CENTER);
		this.store = store;

		this.comboBoxLabelProvider = comboBoxLabelProvider;
	}

	protected TextButton getApplyButton() {
		return (TextButton) getButtonBar().getItemByItemId(
				PredefinedButton.OK.name());
	}

	@Override
	protected TextButton getButtonPressedOnEnter() {
		return getApplyButton();
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return messages.applyTemplate();
	}

	@Override
	protected void initializeComponents() {
		templateControl = new CustomComboBox<T>(store, comboBoxLabelProvider);
		templateControl.setUpdateValueOnSelection(true);
		templateControl.setAllowBlank(false);
		templateControl.setWidth(320);
		templateControl.setEmptyText(messages.emptyTemplateText());
		templateControl.setForceSelection(true);
		templateControl.setValidateOnBlur(false);
		templateControl.setTypeAhead(true);
		templateControl.setTriggerAction(TriggerAction.ALL);
		setFocusWidget(templateControl);

		final HBoxLayoutContainer container = new HBoxLayoutContainer();
		container.addStyleName(appearanceFactory.resources().css()
				.themeLighterBackgroundColor());
		container.setWidth(340);
		container.setHeight(45);
		container.setHBoxLayoutAlign(HBoxLayoutAlign.MIDDLE);
		container.setPack(BoxLayoutPack.CENTER);
		container.add(templateControl);
		add(container, new MarginData(10, 10, 5, 10));
	}

	@Override
	protected void onButtonPressed(final TextButton button) {
		if (button == getCancelButton()) {
			hide();
		} else if (button == getApplyButton()) {
			final T template = templateControl.getValue();
			if (template != null && template != null) {
				handler.applyTemplate(template);
			}
			hide();
		}
	}

	public void setApplyTemplateHandler(final ApplyTemplateHandler<T> handler) {
		this.handler = handler;
	}

	public void setComboBoxData(final Collection<T> data) {
		store.clear();
		store.addAll(data);
	}
}
