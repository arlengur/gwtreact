/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer.BorderLayoutData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;

/**
 * Abstract dialog with player and close button.
 * 
 * @author kunilov.p
 * 
 */
public abstract class PlayerDialog extends QoSDialog {

	private static final String DIALOG_PLAYER_ID = "qos.dialog.player";

	private static final short VERTICAL_MARGIN = 10;

	private static final short HORIZONTAL_MARGIN = 4;

	private final int dialogWidth;

	private final int dialogHeight;

	protected final RecordedPlayerWidget playerWidget;

	public PlayerDialog(final PlayerFactory playerFactory,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(appearanceFactory, messages);

		playerWidget = new RecordedPlayerWidget(DIALOG_PLAYER_ID,
				playerFactory, appearanceFactory, messages);

		// don't make dialog draggable otherwise there will be the issue with
		// player reloading after dragging
		setDraggable(false);

		dialogWidth = PlayerWidget.VIDEO_PANEL_WIDTH + 2 * HORIZONTAL_MARGIN
				+ 12;
		dialogHeight = PlayerWidget.VIDEO_PANEL_HEIGHT + 2 * VERTICAL_MARGIN
				+ 66;
		setWidth(dialogWidth);
		setHeight(dialogHeight);
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return messages.video();
	}

	@Override
	protected void initializeComponents() {
		final BorderLayoutData playerWidgetLayout = new BorderLayoutData();
		playerWidgetLayout.setMinSize(dialogWidth);
		playerWidgetLayout.setMaxSize(dialogWidth);
		playerWidgetLayout.setFloatable(false);
		playerWidgetLayout.setMargins(new Margins(VERTICAL_MARGIN,
				HORIZONTAL_MARGIN, VERTICAL_MARGIN, HORIZONTAL_MARGIN));
		add(playerWidget.getPlayerContainer(), playerWidgetLayout);
	}

	@Override
	protected void onButtonPressed(final TextButton button) {
		if (button == getCloseButton()) {
			hide();
			playerWidget.destroyPlayer();
		}
	}
}
