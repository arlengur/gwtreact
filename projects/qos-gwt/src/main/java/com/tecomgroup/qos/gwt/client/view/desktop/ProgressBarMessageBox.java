/*
 * Copyright (C) 2014 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ProgressBar;
import com.sencha.gxt.widget.core.client.container.BoxLayoutContainer.BoxLayoutPack;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * A <code>MessageBox</code> which displays a {@link ProgressBar}.
 */
public class ProgressBarMessageBox extends QoSDialog {

	private final int DEFAULT_WIDTH = 300;

	private ProgressBar progressBar;
	private String progressText = "";

	private final String title;
	private final String message;

	/**
	 * Creates a message box with {@link ProgressBar} and specified title and
	 * message.
	 * 
	 * @param title
	 * @param message
	 */
	public ProgressBarMessageBox(final String title, final String message) {
		super(AppearanceFactoryProvider.instance(), AppUtils.getMessages());
		this.title = title;
		this.message = message;
		addStyleName(AppearanceFactory.DIALOG_WITH_HEADER);
		setButtonAlign(BoxLayoutPack.CENTER);
	}

	private int getDefaultComponentWidth() {
		return DEFAULT_WIDTH - getFrameSize().getWidth();
	}

	public ProgressBar getProgressBar() {
		return progressBar;
	}

	public String getProgressText() {
		return progressText;
	}

	@Override
	protected String getTitleText(final QoSMessages messages) {
		return title;
	}

	@Override
	protected void initializeComponents() {
		progressBar = initProgressBar();

		final Label label = new Label(message);
		label.setWordWrap(true);
		label.setWidth(getDefaultComponentWidth() + "px");

		final VerticalLayoutContainer container = new VerticalLayoutContainer();
		final VerticalLayoutData margings = new VerticalLayoutData();
		margings.setMargins(new Margins(5, 5, 5, 0));
		container.add(label, margings);
		container.add(progressBar, margings);
		add(container);

		setPredefinedButtons(PredefinedButton.CANCEL);

		setWidth(DEFAULT_WIDTH);
	}

	private ProgressBar initProgressBar() {
		final ProgressBar progressBar = new ProgressBar();

		progressBar.clearSizeCache();
		progressBar.setWidth(getDefaultComponentWidth());

		setFocusWidget(progressBar);

		return progressBar;
	}

	@Override
	protected void onAfterFirstAttach() {
		super.onAfterFirstAttach();
		if (getProgressText() != null) {
			progressBar.updateText(getProgressText());
		}
	}

	@Override
	protected void onResize(final int width, final int height) {
		super.onResize(width, height);
		progressBar.clearSizeCache();
		progressBar.setWidth(width - getFrameSize().getWidth());
	}

	/**
	 * The text to display inside the progress bar.
	 * 
	 * @param progressText
	 *            the progress text
	 */
	public void setProgressText(final String progressText) {
		this.progressText = progressText;
	}

	/**
	 * Updates a progress-style message box's text and progress bar.
	 * 
	 * @param percent
	 *            percentage value from 0 to 100.
	 * @param text
	 *            the progress text to display inside the progress bar or null
	 */
	public void updateProgress(final byte percent, final String text) {
		if (progressBar != null) {
			progressBar.updateProgress(percent
					/ (double) SimpleUtils.MAX_PERCENTAGE_VALUE, text);
		}
		return;
	}
}