/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.core.client.GXT;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.loader.ListLoadConfig;
import com.sencha.gxt.data.shared.loader.ListLoadResult;
import com.sencha.gxt.data.shared.loader.ListLoader;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.gwt.client.QoSServlets;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.model.PlaylistItem;
import com.tecomgroup.qos.gwt.client.presenter.DownloadVideoPresenterWidget;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.utils.UserAgentUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.base.QoSDialog;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DateValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.DurationValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.PlaylistItemDurationValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.PlaylistItemProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.Anchor;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomGridView;

/**
 * @author meleshin.o
 * 
 */
public class DownloadVideoView
		extends
			SenchaPopupView<DownloadVideoPresenterWidget>
		implements
			DownloadVideoPresenterWidget.MyView {

	private final AppearanceFactory appearanceFactory;

	private final QoSMessages messages;

	private PlaylistItemProperties properties;

	private Grid<PlaylistItem> grid;

	private QoSDialog dialog;

	private final static int DIALOG_WIDTH = 700;

	private final static int DIALOG_HEIGHT = 300;

	private final static String CLIPBOARD_BUTTON_ID = "copyToClipboardButton";

	private final static String CLIPBOARD_SOURCE_ID = "clipboardSourceElement";

	/**
	 * Element with this id should contain data which will be copied to
	 * clipboard
	 */
	private final static String DATA_CLIPBOARD_TARGET = "data-clipboard-target";

	private String baseUrl;

	@Inject
	public DownloadVideoView(final EventBus eventBus,
			final AppearanceFactory appearanceFactory,
			final QoSMessages messages) {
		super(eventBus);
		this.messages = messages;
		this.appearanceFactory = appearanceFactory;
	}

	@Override
	public Widget asWidget() {
		return dialog;
	}

	@Override
	public void copyToClipboard(final List<PlaylistItem> items) {
		AppUtils.setElementValue(CLIPBOARD_SOURCE_ID, getDownloadList(items));
	}

	private ColumnModel<PlaylistItem> getColumnModel() {
		final ColumnConfig<PlaylistItem, String> name = new ColumnConfig<PlaylistItem, String>(
				properties.name(), 200, messages.video());
		final ColumnConfig<PlaylistItem, Date> startDateTime = new ColumnConfig<PlaylistItem, Date>(
				getStartDateValueProvider(), 100, messages.startDateTime());
		final ColumnConfig<PlaylistItem, Date> endDateTime = new ColumnConfig<PlaylistItem, Date>(
				getEndDateValueProvider(), 100, messages.endDateTime());
		final ColumnConfig<PlaylistItem, String> duration = new ColumnConfig<PlaylistItem, String>(
				getDurationValueProvider(), 75, messages.duration());

		final List<ColumnConfig<PlaylistItem, ?>> list = new ArrayList<ColumnConfig<PlaylistItem, ?>>();
		name.setCell(new AbstractCell<String>() {

			@Override
			public void render(final Context context, final String value,
					final SafeHtmlBuilder sb) {
				final Anchor anchor = new Anchor(getFullDownloadUrl(baseUrl,
						value), value, "_blank");
				sb.append(SafeHtmlUtils.fromTrustedString(anchor.getElement()
						.getString()));
			}
		});
		startDateTime.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));
		endDateTime.setCell(new DateCell(DateUtils.DATE_TIME_FORMATTER));

		list.add(name);
		list.add(startDateTime);
		list.add(endDateTime);
		list.add(duration);

		return new ColumnModel<PlaylistItem>(list);
	}

	private QoSDialog getDialog() {
		final QoSDialog dialog = new QoSDialog(appearanceFactory, messages) {

			private TextButton copyToClipboardButton;

			@Override
			protected void createButtons() {
				super.createButtons();
				if (UserAgentUtils.isDesktop()) {
					copyToClipboardButton = createDialogButton(
							messages.copyToClipboard(), CLIPBOARD_BUTTON_ID);
					copyToClipboardButton.getElement().setId(
							CLIPBOARD_BUTTON_ID);
					copyToClipboardButton.getElement().setAttribute(
							DATA_CLIPBOARD_TARGET, CLIPBOARD_SOURCE_ID);
					copyToClipboardButton.setWidth(150);
					buttonBar.insert(copyToClipboardButton, 0);
				}
			}

			@Override
			protected TextButton getButtonPressedOnEnter() {
				return copyToClipboardButton;
			}

			@Override
			protected String getTitleText(final QoSMessages messages) {
				return messages.downloadingFiles();
			}

			@Override
			protected void initializeComponents() {
				add(grid);
			}

			@Override
			protected void onAfterFirstAttach() {
				super.onAfterFirstAttach();
				if (UserAgentUtils.isDesktop()) {
					initClipboard(messages.copiedToClipboard());
				}
			}
		};
		dialog.setWidth(DIALOG_WIDTH);
		dialog.setHeight(DIALOG_HEIGHT);

		return dialog;
	}

	private String getDownloadList(final List<PlaylistItem> items) {
		final StringBuilder sb = new StringBuilder("");
		final String EOL = GXT.isWindows() ? " \r\n" : " \n";
		for (final PlaylistItem playlistItem : items) {
			sb.append(getFullDownloadUrl(baseUrl, playlistItem.getName()));
			sb.append(EOL);
		}
		return sb.toString();
	}

	private DurationValueProvider<PlaylistItem> getDurationValueProvider() {
		return new PlaylistItemDurationValueProvider(messages);
	}

	private ValueProvider<PlaylistItem, Date> getEndDateValueProvider() {
		return new DateValueProvider<PlaylistItem>(properties.endTime()
				.getPath()) {

			@Override
			protected Long getTimestamp(final PlaylistItem item) {
				return item.getEndTime();
			}

		};
	}

	private String getFullDownloadUrl(final String baseUrl,
			final String fileName) {
		return baseUrl + fileName;
	}

	private ValueProvider<PlaylistItem, Date> getStartDateValueProvider() {
		return new DateValueProvider<PlaylistItem>(properties.startTime()
				.getPath()) {

			@Override
			protected Long getTimestamp(final PlaylistItem item) {
				return item.getStartTime();
			}

		};
	}

	private ListStore<PlaylistItem> getStore() {
		return getUiHandlers().getStore();
	}

	@Override
	public void initClipboard(final String message) {
		AppUtils.initClipboard(CLIPBOARD_BUTTON_ID, CLIPBOARD_SOURCE_ID,
				message);
	}

	@Override
	public void initialize() {
		final GridView<PlaylistItem> gridView = new CustomGridView<PlaylistItem>(
				appearanceFactory.gridStandardAppearance(),
				appearanceFactory.columnHeaderAppearance());

		properties = getUiHandlers().getPlaylistItemProperties();
		gridView.setStripeRows(true);
		grid = new Grid<PlaylistItem>(getStore(), getColumnModel(), gridView);
		grid.addStyleName(ClientConstants.QOS_GRID_STANDARD_STYLE);
		grid.addStyleName(appearanceFactory.resources().css().textMainColor());
		grid.setAllowTextSelection(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		grid.getView().setAutoFill(true);
		grid.setLoadMask(false);
		grid.setBorders(true);

		dialog = getDialog();
	}

	@Override
	public void setLoader(
			final ListLoader<ListLoadConfig, ListLoadResult<PlaylistItem>> loader) {
		grid.setLoader(loader);
	}

	@Override
	public void setUrl(final String url) {
		baseUrl = url.substring(0,
				url.indexOf(QoSServlets.searchVideoFilesServlet));
	}

	@Override
	public void showDialog() {
		dialog.show();
	}

}
