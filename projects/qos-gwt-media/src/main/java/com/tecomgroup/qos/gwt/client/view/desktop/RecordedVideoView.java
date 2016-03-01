/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.MProperty;
import com.tecomgroup.qos.domain.MRecordedStream;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStream;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.presenter.RecordedVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.RecordedStreamValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.properties.StreamProperties;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.ButtonedGroupingView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeIntervalWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedStreamCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.RecordedStreamCell.RecordedStreamCellHandler;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.util.SimpleUtils;

import javax.validation.ValidationException;
import java.util.*;
import java.util.logging.Level;

/**
 * @author novohatskiy.r
 * 
 */
public class RecordedVideoView
		extends
			AbstractMediaPlayerView<MRecordedStreamWrapper, RecordedVideoPresenter>
		implements
			RecordedVideoPresenter.MyView {

	@UiTemplate("MediaPlayerView.ui.xml")
	interface ViewUiBinder extends UiBinder<Widget, RecordedVideoView> {
	}

	protected final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	protected final static StreamProperties props = GWT
			.create(StreamProperties.class);

	private final long maxVideoLength;

	public static final String MAX_VIDEO_LENGTH_SEC = "client.max.video.length.in.sec";

	/**
	 * @param appearanceFactoryProvider
	 * @param messages
	 */
	@Inject
	public RecordedVideoView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory,
			final QoSMessages messages,
			final HorizontalTimeToolbar timeToolbar,
			@Named("clientProperties") final Map<String, Object> clientProperties) {
		super(appearanceFactoryProvider, dialogFactory, messages, timeToolbar);
		widget = UI_BINDER.createAndBindUi(this);
		afterUiBinder();
		this.timeToolbar.getDateTimeIntervalWidget().setTimeZone(
				TimeZoneType.LOCAL, null);

		maxVideoLength = Long.parseLong(((String) clientProperties
				.get(MAX_VIDEO_LENGTH_SEC)).trim())
				* TimeConstants.MILLISECONDS_PER_SECOND;
	}

	@UiHandler({"addBroadcastingLabel", "addBroadcastButton"})
	protected void addRecordedStreamAction(final ClickEvent e) {
		getUiHandlers().openAddVideoDialog();
	}

	@Override
	protected void afterUiBinder() {
		super.afterUiBinder();
		broadcastListLabel.setText(messages.recordingList());
		addBroadcastingLabel.setText(messages.addRecording());
	}

	@Override
	@UiHandler("applyButton")
	public void applyButtonHandler(final SelectEvent e) {
		try {
			if (timeToolbar.isToolbarEnabled()) {
				timeToolbar.getDateTimeIntervalWidget().validate();
			}
			super.applyButtonHandler(e);
		} catch (final ValidationException ex) {
			AppUtils.showErrorMessage(messages.invalidDateTimeInterval() + "\n" + ex.getMessage());
		}
	}

	@Override
	protected ColumnModel<StreamClientWrapper<MRecordedStreamWrapper>> createColumnModel() {
		final List<ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, ?>> list = new ArrayList<ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, ?>>();

		final ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, String> groupColumn = new ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, String>(
				props.displayName());
		final ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, Map<String, String>> info = new ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, Map<String, String>>(
				new RecordedStreamValueProvider(messages));

		final Set<Set<String>> keysByGroups = new LinkedHashSet<Set<String>>();

		final Set<String> firstGroup = new LinkedHashSet<String>();
		firstGroup.add(messages.probeShort());
		firstGroup.add(messages.timezone());
		keysByGroups.add(firstGroup);

		final Set<String> secondGroup = new LinkedHashSet<String>();
		secondGroup.add(messages.recordedVideoFrom());
		secondGroup.add(messages.recordedVideoTo());
		secondGroup.add(RecordedStreamCell.VIDEOS_TIMEZONE);
		keysByGroups.add(secondGroup);

		final Set<String> thirdGroup = new LinkedHashSet<String>();
		thirdGroup.add(messages.codec());
		thirdGroup.add(messages.size());
		thirdGroup.add(messages.videobitrate());
		thirdGroup.add(messages.fps());
		keysByGroups.add(thirdGroup);

		final RecordedStreamCell<Map<String, String>> broadcastCell = new RecordedStreamCell<Map<String, String>>(
				appearanceFactory.recordedStreamCellAppearance(),
				new RecordedStreamCellHandler() {

					@Override
					public void onRemoveButtonPressed(final List<String> modelKeys) {
						for (final String key : modelKeys) {
							removeStream(grid.getStore().findModelWithKey(key));
						}
						activateAgentTimeZone();
						if(removeVideoToDashboardListener!=null) {
							removeVideoToDashboardListener.onRemoveVideoToDashboard();
						}
					}
				}, keysByGroups);
		info.setCell(broadcastCell);
		groupView = new ButtonedGroupingView<StreamClientWrapper<MRecordedStreamWrapper>>(
				appearanceFactory.gridAppearance(),
				appearanceFactory.buttonedGroupingViewAppearance());
		groupView.setShowGroupedColumn(false);
		groupView.setForceFit(true);
		groupView.groupBy(groupColumn);

		list.add(groupColumn);
		list.add(info);

		return new ColumnModel<StreamClientWrapper<MRecordedStreamWrapper>>(
				list);
	}

	@Override
	protected DashboardWidget createWidget(final MRecordedStreamWrapper stream) {
		throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public TimeInterval getSyncTimeInterval() {
		TimeInterval interval = null;
		if (timeToolbar != null && timeToolbar.isToolbarEnabled()) {
			interval = timeToolbar.getDateTimeIntervalWidget()
					.getTimeInterval();
		}
		return interval;
	}

	@Override
	protected boolean isStreamAddedToDashboard(
			final MRecordedStreamWrapper streamWrapper) {
		// recorded streams can't be added to dashboard
		return false;
	}

	@Override
	protected boolean isTimeSynchronizationEnabled() {
		return true;
	}

	@Override
	public void setUpPlayer(final QoSPlayer player,
			final MRecordedStreamWrapper streamWrapper) throws ValidationException {
		final MRecordedStream stream = streamWrapper.getStream();
		final String streamUrl = stream.getStreamUrl();

		final MProperty recordedFilePrefix = stream.getProperty(MStream.RECORDED_FILE_PREFIX);
		if (SimpleUtils.isNotNullAndNotEmpty(streamUrl)
				&& recordedFilePrefix != null) {
			Date startDateTime = streamWrapper.getStartDateTime();
			Date endDateTime = streamWrapper.getEndDateTime();
			if (timeToolbar.isToolbarEnabled()) {
				DateTimeIntervalWidget toolbarIntervalWidget = timeToolbar.getDateTimeIntervalWidget();
				toolbarIntervalWidget.validate();
				final TimeInterval synchControlTimeInterval = toolbarIntervalWidget.getTimeInterval();
				startDateTime = synchControlTimeInterval.getStartDateTime();
				endDateTime = synchControlTimeInterval.getEndDateTime();
				if (!SimpleUtils.isNotNullAndNotEmpty(synchControlTimeInterval.getTimeZone())
						&& TimeZoneType.AGENT == toolbarIntervalWidget.getTimeZoneType()) {
					synchControlTimeInterval.setTimeZone(streamWrapper.getAgent().getTimeZone());
				}
			}

			if (endDateTime.getTime() - startDateTime.getTime() > maxVideoLength) {
				AppUtils.showErrorMessage(messages
						.maxVideoLengthExceeded(DateUtils.formatDuration(
								maxVideoLength, messages)));
			} else {
				player.setRecordedStreamParameters(streamUrl,
						stream.getDownloadUrl(),
						recordedFilePrefix.getValue(),
						startDateTime,
						endDateTime);
				player.initialize();
			}
		} else {
			LOGGER.log(Level.SEVERE,
					"Recorded stream player cannot be initialized: "
							+ MStream.RECORDED_FILE_PREFIX
							+ " property of the stream is null or empty");
		}
	}

	@Override
	public void updateSyncTimeInterval(final TimeInterval timeInterval) {
		if (timeToolbar != null) {
			final boolean enabled = timeInterval != null ? true : false;
			timeToolbar.setToolbarEnabled(enabled);
			if (enabled) {
				final DateTimeIntervalWidget dateTimeIntervalWidget = timeToolbar
						.getDateTimeIntervalWidget();
				dateTimeIntervalWidget.setTimeZone(
						timeInterval.getTimeZoneType(),
						timeInterval.getTimeZone());
				dateTimeIntervalWidget.setTimeIntervalType(timeInterval
						.getType());
				if (Type.CUSTOM.equals(timeInterval.getType())) {
					dateTimeIntervalWidget.setTimeInterval(
							timeInterval.getStartDateTime(),
							timeInterval.getEndDateTime());
				}
			}
		}
	}
}
