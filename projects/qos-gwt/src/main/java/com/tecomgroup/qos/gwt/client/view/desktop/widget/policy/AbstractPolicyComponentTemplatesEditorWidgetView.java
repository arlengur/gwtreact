/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget.policy;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.gwtplatform.mvp.client.UiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.Dialog.PredefinedButton;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.SenchaPopupView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractPolicyComponentTemplatesEditorWidgetView<T extends UiHandlers>
		extends
			SenchaPopupView<T> {

	private final QoSDialog dialog;

	private VerticalLayoutContainer verticalLayoutContainer;

	private final AppearanceFactory appearanceFactory;

	protected final QoSMessages messages;

	public AbstractPolicyComponentTemplatesEditorWidgetView(
			final EventBus eventBus) {
		super(eventBus);
		appearanceFactory = AppearanceFactoryProvider.instance();
		messages = AppUtils.getMessages();
		dialog = createDialog();
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	private QoSDialog createDialog() {
		final QoSDialog dialog = new QoSDialog(appearanceFactory, messages) {

			@Override
			protected TextButton createDialogButton(final String text,
					final String itemId) {
				final TextButton textButton = new TextButton(
						new TextButtonCell(
								appearanceFactory
										.<String> buttonCellHugeAppearance()),
						text);
				textButton.setWidth(130);
				textButton.setItemId(itemId);
				return textButton;
			}

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return getDialogTitle();
			}

			@Override
			protected void initializeComponents() {
			}

			@Override
			protected void onButtonPressed(final TextButton button) {
				if (button == getCloseButton()) {
					hide();
				}
			}
		};

		final SimpleContainer container = new SimpleContainer();
		container.setHeight(400);
		container.setWidth(400);
		verticalLayoutContainer = new VerticalLayoutContainer();
		container.add(verticalLayoutContainer);
		dialog.add(container);
		dialog.setPredefinedButtons(PredefinedButton.CLOSE);
		dialog.setButtonAlign(BoxLayoutPack.CENTER);

		return dialog;
	}

	protected abstract String getDialogTitle();

	@Override
	public void setInSlot(final Object slot, final IsWidget content) {
		verticalLayoutContainer.add(content, new VerticalLayoutData(1, 1,
				new Margins(7, 2, 1, 2)));
	}
}
