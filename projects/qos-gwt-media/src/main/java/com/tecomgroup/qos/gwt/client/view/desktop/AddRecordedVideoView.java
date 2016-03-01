/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.ValidationException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.IdentityValueProvider;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.widget.core.client.Dialog;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VBoxLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.event.SelectEvent.SelectHandler;
import com.sencha.gxt.widget.core.client.form.ComboBox;
import com.sencha.gxt.widget.core.client.grid.CheckBoxSelectionModel;
import com.sencha.gxt.widget.core.client.grid.ColumnConfig;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GridView;
import com.tecomgroup.qos.TimeConstants;
import com.tecomgroup.qos.TimeInterval;
import com.tecomgroup.qos.TimeInterval.TimeZoneType;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.TimeZoneWrapper;
import com.tecomgroup.qos.domain.MAgent;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.presenter.AddRecordedVideoPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;
import com.tecomgroup.qos.gwt.client.view.desktop.grid.StreamBaseValueProvider;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AbstractAgentDialogView;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.AgentSelectorWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.CustomCheckBoxSelectionModel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeIntervalWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.DateTimeWidget;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.LiveStreamBasicCell;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.gis.AgentGisWidget;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;
import com.tecomgroup.qos.util.SimpleUtils;

/**
 * @author novohatskiy.r
 * 
 */
public class AddRecordedVideoView extends AbstractAgentDialogView
		implements
			AddRecordedVideoPresenter.MyView {

	protected class UIFieldHolder {

		@UiField(provided = true)
		public VerticalLayoutContainer scrollableVerticalLayoutContainer;

		@UiField(provided = true)
		public HBoxLayoutContainer dateTimeControlsContainer;

		@UiField(provided = true)
		public Grid<StreamClientWrapper<MRecordedStreamWrapper>> grid;

		@UiField(provided = true)
		public TextButton okButton;

		@UiField(provided = true)
		protected TextButton lastFifteenMinutesButton;

		@UiField(provided = true)
		protected TextButton lastHourButton;

		@UiField(provided = true)
		protected TextButton lastDayButton;

		@UiField(provided = true)
		protected TextButton otherIntervalButton;

		@UiField(provided = true)
		protected DateTimeWidget startDateControl;

		@UiField(provided = true)
		protected DateTimeWidget startTimeControl;

		@UiField(provided = true)
		protected DateTimeWidget endDateControl;

		@UiField(provided = true)
		protected DateTimeWidget endTimeControl;

		@UiField(provided = true)
		protected ComboBox<TimeZoneWrapper> timeZoneControl;

		@UiField
		protected Label timeDash;

		@UiHandler(value = {"lastFifteenMinutesButton", "lastHourButton",
				"lastDayButton"})
		protected void customIntervalDeselected(final SelectEvent e) {
			setCustomTimeIntervalEnabled(false);
		}

		@UiHandler("otherIntervalButton")
		protected void customIntervalSelected(final SelectEvent e) {
			setCustomTimeIntervalEnabled(true);
		}

	}

	interface ViewUiBinder extends UiBinder<VBoxLayoutContainer, UIFieldHolder> {

	}

	final ModelKeyProvider<StreamClientWrapper<MRecordedStreamWrapper>> keyProvider = new ModelKeyProvider<StreamClientWrapper<MRecordedStreamWrapper>>() {

		@Override
		public String getKey(
				final StreamClientWrapper<MRecordedStreamWrapper> item) {
			return item.getWrapper().getStream().getComplexStreamSource()
					.getKey();
		}
	};

	private final static ViewUiBinder UI_BINDER = GWT
			.create(ViewUiBinder.class);

	private final QoSMessages messages;
	private final AppearanceFactory appearanceFactory;

	private final DateTimeIntervalWidget dateTimeIntervalWidget;

	private MAgent selectedAgent;

	private final VBoxLayoutContainer leftContainer;

	private final UIFieldHolder uiFieldHolder = new UIFieldHolder();

	private final long maxVideoLength;

	public static final String MAX_VIDEO_LENGTH_SEC = "client.max.video.length.in.sec";

	@Inject
	public AddRecordedVideoView(
			final EventBus eventBus,
			final QoSMessages messages,
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final AgentSelectorWidget agentSelectorWidget,
			final AgentGisWidget agentGisWidget,
			final DateTimeIntervalWidget dateTimeIntervalWidget,
			@Named("clientProperties") final Map<String, Object> clientProperties) {
		super(eventBus, appearanceFactoryProvider, agentSelectorWidget,
				agentGisWidget);
		this.messages = messages;
		this.appearanceFactory = appearanceFactoryProvider.get();
		this.dateTimeIntervalWidget = dateTimeIntervalWidget;

		maxVideoLength = Long.parseLong(((String) clientProperties
				.get(MAX_VIDEO_LENGTH_SEC)).trim())
				* TimeConstants.MILLISECONDS_PER_SECOND;

		uiFieldHolder.scrollableVerticalLayoutContainer = new VerticalLayoutContainer();
		uiFieldHolder.scrollableVerticalLayoutContainer
				.setStyleName(appearanceFactory.resources().css()
						.addRecordedVideoLeftPanel());
		uiFieldHolder.scrollableVerticalLayoutContainer.getScrollSupport()
				.setScrollMode(ScrollMode.AUTOY);

		uiFieldHolder.dateTimeControlsContainer = new HBoxLayoutContainer();
		uiFieldHolder.dateTimeControlsContainer.setEnableOverflow(false);

		uiFieldHolder.okButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()));
		uiFieldHolder.okButton.setText(messages.actionDone());

		uiFieldHolder.lastFifteenMinutesButton = dateTimeIntervalWidget
				.getLastFifteenMinutesButton();
		uiFieldHolder.lastHourButton = dateTimeIntervalWidget
				.getLastHourButton();
		uiFieldHolder.lastDayButton = dateTimeIntervalWidget.getLastDayButton();
		uiFieldHolder.otherIntervalButton = dateTimeIntervalWidget
				.getOtherIntervalButton();

		uiFieldHolder.startDateControl = dateTimeIntervalWidget
				.getStartDateControl();
		uiFieldHolder.startTimeControl = dateTimeIntervalWidget
				.getStartTimeControl();
		uiFieldHolder.endDateControl = dateTimeIntervalWidget
				.getEndDateControl();
		uiFieldHolder.endTimeControl = dateTimeIntervalWidget
				.getEndTimeControl();
		uiFieldHolder.timeZoneControl = dateTimeIntervalWidget
				.getTimeZoneControl();
		uiFieldHolder.timeZoneControl
				.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH);

		initializeGrid(appearanceFactory);

		leftContainer = UI_BINDER.createAndBindUi(uiFieldHolder);

		uiFieldHolder.timeDash.addStyleName(appearanceFactory.resources().css()
				.timeDash());

		dateTimeIntervalWidget.selectTimeInterval(uiFieldHolder.lastDayButton);

		uiFieldHolder.timeZoneControl.setValue(DateUtils.createLocalTimeZone());
		dateTimeIntervalWidget
				.setDefaultTimeInterval(TimeInterval.Type.FIFTEEN_MINUTES);

		setCustomTimeIntervalEnabled(false);

		setLeftContent(leftContainer);
		initializeListeners();

		if (AppUtils.isDemoMode()) {
			dateTimeIntervalWidget.applyDemoMode(Type.FIFTEEN_MINUTES);
		}
	}

	@Override
	public Dialog asWidget() {
		return getDialog();
	}

	private ColumnModel<StreamClientWrapper<MRecordedStreamWrapper>> createColumnModel(
			final AppearanceFactory af,
			final CheckBoxSelectionModel<StreamClientWrapper<MRecordedStreamWrapper>> sm) {
		final List<ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, ?>> list = new ArrayList<ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, ?>>();

		final ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, Map<String, String>> info = new ColumnConfig<StreamClientWrapper<MRecordedStreamWrapper>, Map<String, String>>(
				new StreamBaseValueProvider(messages));
		final LiveStreamBasicCell<Map<String, String>> broadcastCell = new LiveStreamBasicCell<Map<String, String>>(
				af.liveStreamBasicCellAppearance(),
				LiveStreamBasicCell.PROGRAM_NAME_PROPERTY);
		info.setCell(broadcastCell);
		list.add(sm.getColumn());
		list.add(info);

		return new ColumnModel<StreamClientWrapper<MRecordedStreamWrapper>>(
				list);
	}

	private TimeInterval getTimeInterval() {
		final TimeInterval timeInterval = dateTimeIntervalWidget
				.getTimeInterval();
		if (!SimpleUtils.isNotNullAndNotEmpty(timeInterval.getTimeZone())
				&& TimeZoneType.AGENT.equals(dateTimeIntervalWidget
						.getTimeZoneType())) {
			timeInterval.setTimeZone(selectedAgent.getTimeZone());
		}
		return timeInterval;
	}

	private void initializeGrid(final AppearanceFactory af) {
		final IdentityValueProvider<StreamClientWrapper<MRecordedStreamWrapper>> identity = new IdentityValueProvider<StreamClientWrapper<MRecordedStreamWrapper>>();
		final CheckBoxSelectionModel<StreamClientWrapper<MRecordedStreamWrapper>> selectionModel = new CustomCheckBoxSelectionModel<StreamClientWrapper<MRecordedStreamWrapper>>(
				identity,
				af.<StreamClientWrapper<MRecordedStreamWrapper>> checkBoxColumnAppearance());
		selectionModel.setSelectionMode(SelectionMode.SIMPLE);
		uiFieldHolder.grid = new Grid<StreamClientWrapper<MRecordedStreamWrapper>>(
				new ListStore<StreamClientWrapper<MRecordedStreamWrapper>>(
						keyProvider), createColumnModel(af, selectionModel),
				new GridView<StreamClientWrapper<MRecordedStreamWrapper>>(af
						.gridAppearance()));
		uiFieldHolder.grid.addStyleName(ClientConstants.QOS_GRID_STYLE);
		uiFieldHolder.grid.addStyleName(ClientConstants.QOS_GRID_VIDEO_STYLE);
		uiFieldHolder.grid.setSelectionModel(selectionModel);
		uiFieldHolder.grid.setHideHeaders(true);
		uiFieldHolder.grid.getView().setStripeRows(true);
	}

	private void initializeListeners() {
		uiFieldHolder.okButton.addSelectHandler(new SelectHandler() {

			@Override
			public void onSelect(final SelectEvent event) {

				try {
					dateTimeIntervalWidget.validate();
					final TimeInterval interval = getTimeInterval();
					final long intervalDuration = (interval.getEndDateTime()
							.getTime() - interval.getStartDateTime().getTime());

					if (intervalDuration > maxVideoLength) {
						AppUtils.showErrorMessage(messages
								.maxVideoLengthExceeded(DateUtils
										.formatDuration(maxVideoLength,
												messages)));
					} else {
						getUiHandlers().<AddRecordedVideoPresenter> cast()
								.addStreams(
										uiFieldHolder.grid.getSelectionModel()
												.getSelectedItems(), interval);
						hide();
					}
				} catch (final ValidationException e) {
					AppUtils.showErrorMessage(messages
							.invalidDateTimeInterval() + "\n" + e.getMessage());
				}
			}
		});
	}

	@Override
	public void select(final MAgent agent) {
		selectedAgent = agent;
		super.select(agent);
		getUiHandlers().agentSelected(agent);
	}

	private void setCustomTimeIntervalEnabled(final boolean value) {
		uiFieldHolder.startDateControl.setEnabled(value);
		uiFieldHolder.startTimeControl.setEnabled(value);
		uiFieldHolder.endDateControl.setEnabled(value);
		uiFieldHolder.endTimeControl.setEnabled(value);
	}

	@Override
	public void setSelectedAgent(final MAgent agent) {
		selectedAgent = agent;
		dateTimeIntervalWidget.enableAgentTimeZone(agent.getTimeZone());
	}

	@Override
	public void setStreams(
			final List<StreamClientWrapper<MRecordedStreamWrapper>> streams) {
		uiFieldHolder.grid.getStore().clear();
		uiFieldHolder.grid.getStore().addAll(streams);
	}

}
