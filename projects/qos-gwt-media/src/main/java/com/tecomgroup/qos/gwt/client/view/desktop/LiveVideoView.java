/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.dashboard.LiveStreamWidget;
import com.tecomgroup.qos.domain.MDashboard;
import com.tecomgroup.qos.domain.MLiveStream;
import com.tecomgroup.qos.domain.MLiveStreamWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.presenter.LiveVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.PlayerUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.StreamValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.StreamProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamCell.LiveStreamCellHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.VideoPanel;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author novohatskiy.r
 * 
 */
public class LiveVideoView
		extends
			AbstractMediaPlayerView<MLiveStreamWrapper, LiveVideoPresenter>
		implements
			LiveVideoPresenter.MyView {

	@UiTemplate("MediaPlayerView.ui.xml")
	interface ViewUiBinder extends UiBinder<Widget, LiveVideoView> {
	}

	protected final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	protected final static StreamProperties props = GWT
			.create(StreamProperties.class);

	private MDashboard dashboard;

	/**
	 * @param appearanceFactoryProvider
	 * @param messages
	 */
	@Inject
	public LiveVideoView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory, final QoSMessages messages,
			final HorizontalTimeToolbar timeToolbar) {
		super(appearanceFactoryProvider, dialogFactory, messages, timeToolbar);
		widget = UI_BINDER.createAndBindUi(this);
		afterUiBinder();
	}

	@UiHandler({"addBroadcastingLabel", "addBroadcastButton"})
	protected void addLiveStreamAction(final ClickEvent e) {
		getUiHandlers().openAddVideoDialog();
	}

	@Override
	protected void afterUiBinder() {
		super.afterUiBinder();
		broadcastListLabel.setText(messages.broadcastList());
		addBroadcastingLabel.setText(messages.addBroadcasting());
	}

	@Override
	@UiHandler("applyButton")
	public void applyButtonHandler(final SelectEvent e) {
		getUiHandlers().getDashboard(new AsyncCallback<MDashboard>() {

			@Override
			public void onFailure(final Throwable caught) {
				// exception is logged in presenter.
			}

			@Override
			public void onSuccess(final MDashboard dashboard) {
				LiveVideoView.this.dashboard = dashboard;
				LiveVideoView.super.applyButtonHandler(e);
			}

		});
	}

	@Override
	protected ColumnModel<StreamClientWrapper<MLiveStreamWrapper>> createColumnModel() {
		final List<ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, ?>> list = new ArrayList<ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, ?>>();

		final ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, String> groupColumn = new ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, String>(
				props.displayName());
		final ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, Map<String, String>> info = new ColumnConfig<StreamClientWrapper<MLiveStreamWrapper>, Map<String, String>>(
				new StreamValueProvider(messages));
		final Set<String> excludedProperties = new HashSet<String>();
		excludedProperties.add(LiveStreamBasicCell.PROGRAM_NAME_PROPERTY);
		final LiveStreamCell<Map<String, String>> broadcastCell = new LiveStreamCell<Map<String, String>>(
				appearanceFactory.liveStreamCellAppearance(),
				new LiveStreamCellHandler() {

					@Override
					public void onRemoveButtonPressed(
							final List<String> modelKeys) {
						for (final String key : modelKeys) {
							removeStream(grid.getStore().findModelWithKey(key));
						}
						if(removeVideoToDashboardListener!=null)
						{
							removeVideoToDashboardListener.onRemoveVideoToDashboard();
						}
					}
				}, excludedProperties);
		info.setCell(broadcastCell);
		groupView = new ButtonedGroupingView<StreamClientWrapper<MLiveStreamWrapper>>(
				appearanceFactory.gridAppearance(),
				appearanceFactory.buttonedGroupingViewAppearance());
		groupView.setShowGroupedColumn(false);
		groupView.setForceFit(true);
		groupView.groupBy(groupColumn);

		list.add(groupColumn);
		list.add(info);

		return new ColumnModel<StreamClientWrapper<MLiveStreamWrapper>>(list);
	}

	@Override
	protected DashboardWidget createWidget(final MLiveStreamWrapper stream) {
		final LiveStreamWidget widget = new LiveStreamWidget();
		widget.setStreamKey(stream.getStreamKey());
		widget.setTaskKey(stream.getTaskKey());
		widget.setTitle(stream.getStream().getDisplayName());
		return widget;
	}

	@Override
	public MLiveStream findStreamByUrl(final String url) {
		MLiveStream found = null;
		for (final StreamClientWrapper<MLiveStreamWrapper> stream : grid
				.getStore().getAll()) {
			if (url.equals(stream.getWrapper().getStream().getUrl())) {
				found = stream.getWrapper().getStream();
				break;
			}
		}
		return found;
	}

	@Override
	public TimeInterval getSyncTimeInterval() {
		// live video hasn't sync view feature
		return null;
	}

	@Override
	protected boolean isStreamAddedToDashboard(
			final MLiveStreamWrapper streamWrapper) {
		return dashboard != null
				&& dashboard.hasWidget(streamWrapper.getUniqueKey());
	}

	@Override
	protected boolean isTimeSynchronizationEnabled() {
		return false;
	}

	@Override
	public void markWidgetAsAddedToDashboard(final String widgetKey) {
		switchDashboardWidgetState(widgetKey, true);
	}

	@Override
	public void setUpPlayer(final QoSPlayer player,
			final MLiveStreamWrapper streamWrapper) {
		PlayerUtils.setUpLiveVideoPlayer(player, streamWrapper);
	}

	private void switchDashboardWidgetState(final String widgetKey,
			final boolean addedToDashboard) {
		for (final VideoPanel panel : playerPanels.keySet()) {
			final MLiveStreamWrapper wrapper = playerPanels.get(panel)
					.getWrapper();
			if (widgetKey.equals(wrapper.getUniqueKey())) {
				panel.setAddedToDashboard(addedToDashboard);
				break;
			}
		}
	}

	@Override
	public void unmarkWidgetAsAddedToDashboard(final String widgetKey) {
		switchDashboardWidgetState(widgetKey, false);
	}

	@Override
	public void updateSyncTimeInterval(final TimeInterval interval) {
		// live video hasn't sync view feature
	}
}
