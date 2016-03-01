/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.dom.XDOM;
import com.sencha.gxt.core.client.dom.XElement;
import com.tecomgroup.qos.gwt.client.event.video.CloseVideoPanelEvent;
import com.tecomgroup.qos.gwt.client.event.video.CloseVideoPanelEvent.CloseVideoPanelEventHandler;
import com.tecomgroup.qos.gwt.client.event.video.ExportVideoEvent;
import com.tecomgroup.qos.gwt.client.player.PlayerRegistry;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.style.common.VideoPanelAppearance;
import com.tecomgroup.qos.gwt.client.style.theme.base.panel.VideoPanelBaseAppearance;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;

/**
 * @author ivlev.e
 * 
 */
public class VideoPanel extends ComplexPanel {

	public static interface AddPlayerToDahsboardEventHanlder {
		void onEvent(VideoPanel panel, boolean isAlreadyAddedToDashboard);
	}

	private final Widget content;

	private final VideoPanelAppearance appearance;

	private CloseVideoPanelEventHandler closeVideoPanelEventHandler;

	private final String playerId;

	private final QoSPlayer player;

	private String taskKey = "";

	private String taskDisplayName = "";

	private AddPlayerToDahsboardEventHanlder addPlayerToDahsboardEventHanlder;

	public VideoPanel(final QoSPlayer player,
					  final String title,
					  final boolean closable,
					  final boolean hasDownloadButton,
					  final String downloadButtonTitle,
					  final boolean hasAddToDashboardButton,
					  final boolean isAddedToDashboard,
					  final int offsetWidth,
					  final int offsetHeight) {
		this(player, title, (VideoPanelBaseAppearance) GWT
				.create(VideoPanelBaseAppearance.class), closable,
				hasDownloadButton, downloadButtonTitle, hasAddToDashboardButton, isAddedToDashboard,
				offsetWidth, offsetHeight);
	}

	public VideoPanel(final QoSPlayer player, final String title,
			final VideoPanelAppearance appearance, final boolean closable,
			final boolean hasDownloadButton,
					  final String downloadButtonTitle,
			final boolean hasAddToDashboardButton,
			final boolean isAddedToDashboard, final int width, final int height) {
		this.player = player;
		this.content = player.asWidget();
		this.playerId = player.getPlayerId();
		this.appearance = appearance;

		final int contentWidth = getContentWidth(width);
		final int contentHeight = getContentHeight(height);
		content.setPixelSize(contentWidth, contentHeight);

		final SafeHtmlBuilder sb = new SafeHtmlBuilder();
		appearance.render(sb, title, closable, hasDownloadButton, downloadButtonTitle,
				hasAddToDashboardButton, isAddedToDashboard);
		setElement(XDOM.create(sb.toSafeHtml()));

		DOM.appendChild(getContainerElement(), content.getElement());
		adopt(content);
		getContainerElement().<XElement> cast().setSize(contentWidth,
				contentHeight);
		setPixelSize(width, height);

		initializeListeners();
	}

	public Element getContainerElement() {
		return appearance.getContentElement(getElement().<XElement> cast());
	}

	public Widget getContent() {
		return content;
	}

	protected int getContentHeight(final int offsetHeight) {
		return offsetHeight - appearance.getTopBorderHeight()
				- appearance.getBottomBorderHeight();
	}

	protected int getContentWidth(final int offsetWidth) {
		return offsetWidth - appearance.getLeftBorderWidth()
				- appearance.getRightBorderWidth();
	}

	public String getPlayerId() {
		return playerId;
	}

	private void handleAddToDashboardButtonClick(final XElement target) {
		if(appearance.isAddedToDashboard(target)) {
			addPlayerToDahsboardEventHanlder.onEvent(VideoPanel.this, true);
		} else {
			if (appearance.isAddToDashboardButtonPressed(target)) {
				addPlayerToDahsboardEventHanlder.onEvent(VideoPanel.this, false);
			}
		}
	}

	private void handleCloseButtonClick(final XElement target) {
		if ((closeVideoPanelEventHandler != null)
				&& (appearance.isCloseButtonPressed(target))) {
			// should be called first before the removal of panel
			PlayerRegistry.getInstance().removePlayer(playerId);
			closeVideoPanelEventHandler.onClose(new CloseVideoPanelEvent(
					VideoPanel.this));
		}
	}

	private void handleDownloadButtonClick(final XElement target) {
		if (appearance.isDownloadButtonPressed(target)) {
			AppUtils.getEventBus().fireEvent(new ExportVideoEvent(player.getPlaylistDownloadUrl(), player.getPlayerId(), taskKey, taskDisplayName));
		}
	}

	private void initializeListeners() {
		sinkEvents(Event.ONCLICK);
		addDomHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				final XElement target = Element.as(
						event.getNativeEvent().getEventTarget())
						.<XElement> cast();
				handleAddToDashboardButtonClick(target);
				handleCloseButtonClick(target);
				handleDownloadButtonClick(target);
			}
		}, ClickEvent.getType());
	}

	public void setAddedToDashboard(final boolean isAddedToDashboard) {
		appearance.setAddedToDashboard(getElement().<XElement> cast(),
				isAddedToDashboard);
	}

	/**
	 * @param addPlayerToDahsboardEventHanlder
	 *            the addPlayerToDahsboardEventHanlder to set
	 */
	public void setAddPlayerToDahsboardEventHanlder(
			final AddPlayerToDahsboardEventHanlder addPlayerToDahsboardEventHanlder) {
		this.addPlayerToDahsboardEventHanlder = addPlayerToDahsboardEventHanlder;
	}

	public void setCloseVideoPanelEventHandler(
			final CloseVideoPanelEventHandler handler) {
		this.closeVideoPanelEventHandler = handler;
	}

	public void setTaskKey(String taskKey) {
		this.taskKey = taskKey;
	}

	public void setTaskDisplayName(String taskDisplayName) {
		this.taskDisplayName = taskDisplayName;
	}
}
