/*
 * Copyright (C) 2012 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.view.desktop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.validation.ValidationException;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.dom.client.Style.Float;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.ViewWithUiHandlers;
import com.sencha.gxt.cell.core.client.TextButtonCell;
import com.sencha.gxt.core.client.Style.SelectionMode;
import com.sencha.gxt.core.client.dom.ScrollSupport.ScrollMode;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.Point;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.fx.client.FxElement;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent;
import com.sencha.gxt.fx.client.animation.AfterAnimateEvent.AfterAnimateHandler;
import com.sencha.gxt.fx.client.animation.Fx;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;
import com.sencha.gxt.widget.core.client.container.CenterLayoutContainer;
import com.sencha.gxt.widget.core.client.container.FlowLayoutContainer;
import com.sencha.gxt.widget.core.client.container.MarginData;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.grid.ColumnModel;
import com.sencha.gxt.widget.core.client.grid.Grid;
import com.sencha.gxt.widget.core.client.grid.GroupingView;
import com.tecomgroup.qos.TimeInterval.Type;
import com.tecomgroup.qos.dashboard.DashboardWidget;
import com.tecomgroup.qos.domain.MRecordedStreamWrapper;
import com.tecomgroup.qos.domain.MStreamWrapper;
import com.tecomgroup.qos.gwt.client.event.video.CloseVideoPanelEvent;
import com.tecomgroup.qos.gwt.client.event.video.CloseVideoPanelEvent.CloseVideoPanelEventHandler;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.PlayerFactory;
import com.tecomgroup.qos.gwt.client.player.PlayerRegistry;
import com.tecomgroup.qos.gwt.client.player.PlayerState;
import com.tecomgroup.qos.gwt.client.player.QoSPlayer;
import com.tecomgroup.qos.gwt.client.presenter.MediaPlayerPresenter;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.theme.AppearanceFactory;
import com.tecomgroup.qos.gwt.client.utils.AppUtils;
import com.tecomgroup.qos.gwt.client.utils.ClientConstants;
import com.tecomgroup.qos.gwt.client.utils.StyleUtils;
import com.tecomgroup.qos.gwt.client.view.MediaPlayerView;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.DialogFactory;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.GroupingGrid;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.HorizontalTimeToolbar.UpdateButtonHandler;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.VideoPanel;
import com.tecomgroup.qos.gwt.client.view.desktop.widget.VideoPanel.AddPlayerToDahsboardEventHanlder;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.CommentMode;
import com.tecomgroup.qos.gwt.client.view.desktop.dialog.ConfirmationDialog.ConfirmationHandler;
import com.tecomgroup.qos.gwt.client.wrapper.StreamClientWrapper;

/**
 * @author abondin
 * 
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractMediaPlayerView<W extends MStreamWrapper, P extends MediaPlayerPresenter>
		extends
			ViewWithUiHandlers<P> implements MediaPlayerView<W> {

	public static interface AddVideoToDashboardListener {
		public void onAddVideoToDashboard(DashboardWidget widget);
	}

	public static interface RemoveVideoToDashboardListener {
		public void onRemoveVideoToDashboard();
	}

	protected Widget widget;

	@UiField(provided = true)
	protected BorderLayoutContainer borderLayoutContainer;

	@UiField(provided = true)
	protected FramedPanel westPanel;

	@UiField
	protected VerticalPanel broadcastTableHeader;

	@UiField
	protected Label broadcastListLabel;

	@UiField
	protected Label loadedTemplateLabel;

	@UiField
	protected HorizontalPanel addBroadcastPanel;

	@UiField
	protected Label addBroadcastingLabel;

	@UiField(provided = true)
	protected Image addBroadcastButton;

	@UiField(provided = true)
	protected Grid<StreamClientWrapper<W>> grid;

	protected GroupingView<StreamClientWrapper<W>> groupView;

	@UiField
	protected HorizontalPanel templateBar;

	@UiField(provided = true)
	protected TextButton applyButton;

	@UiField(provided = true)
	protected FramedPanel centerFramePanel;

	@UiField(provided = true)
	protected FlowLayoutContainer centerContainer;

	@UiField(provided = true)
	protected FramedPanel toolbarFramePanel;

	@UiField(provided = true)
	protected HorizontalTimeToolbar timeToolbar;

	protected final AppearanceFactory appearanceFactory;

	private PlayerFactory playerFactory;

	protected BorderLayoutContainer bigTvContainer;

	protected Map<VideoPanel, StreamClientWrapper<W>> playerPanels = new HashMap<VideoPanel, StreamClientWrapper<W>>();

	protected final QoSMessages messages;

	protected Logger LOGGER = Logger.getLogger(AbstractMediaPlayerView.class
			.getName());

	public static final Point VIDEO_SIZE = new Point(320, 240);

	public static final String FLOW_CONTAINER_STYLE_NAME = "videoFlowContainer";

	protected final static int FADE_DURATION = 500;

	public final static int VIDEO_PANEL_OFFSET_WIDTH = 354;

	public final static int VIDEO_PANEL_OFFSET_HEIGHT = 290;

	protected RemoveVideoToDashboardListener removeVideoToDashboardListener;

	protected final CloseVideoPanelEventHandler closePlayerHandler = new CloseVideoPanelEventHandler() {

		@Override
		public void onClose(final CloseVideoPanelEvent eventClose) {
			final Fx fx = new Fx(FADE_DURATION);
			fx.addAfterAnimateHandler(new AfterAnimateHandler() {

				@Override
				public void onAfterAnimate(final AfterAnimateEvent event) {
					final VideoPanel videoPanel = eventClose.getVideoPanel();
					final StreamClientWrapper<W> stream = playerPanels
							.get(videoPanel);
					removeStream(stream);
				}
			});
			eventClose.getVideoPanel().getElement().<FxElement> cast()
					.fadeToggle(fx);
			if(removeVideoToDashboardListener!=null) {
				removeVideoToDashboardListener.onRemoveVideoToDashboard();
			}
		}
	};

	private AddVideoToDashboardListener addVideoToDashboardListener;

	protected final AddPlayerToDahsboardEventHanlder addPlayerToDahsboardEventHanlder = new AddPlayerToDahsboardEventHanlder() {
		@Override
		public void onEvent(final VideoPanel panel, boolean isAlreadyAddedToDashboard) {
			if( isAlreadyAddedToDashboard) {
				AppUtils.showInfoWithConfirmMessage(messages.widgetAlreadyExists());
				return;
			}

			if (addVideoToDashboardListener != null) {
				final W stream = playerPanels.get(panel).getWrapper();
				addVideoToDashboardListener
						.onAddVideoToDashboard(createWidget(stream));
			}
		}
	};

	protected final DialogFactory dialogFactory;

	public AbstractMediaPlayerView(
			final AppearanceFactoryProvider appearanceFactoryProvider,
			final DialogFactory dialogFactory, final QoSMessages messages,
			final HorizontalTimeToolbar timeToolbar) {
		appearanceFactory = appearanceFactoryProvider.get();
		this.messages = messages;
		this.dialogFactory = dialogFactory;
		this.timeToolbar = timeToolbar;
		this.timeToolbar.setup(Type.FIFTEEN_MINUTES, Type.HOUR, Type.DAY);
		this.timeToolbar.setEnabled(false);
		initialize();
	}

	@Override
	public void addStream(final StreamClientWrapper<W> clientStreamWrapper) {
		if (grid.getStore().findModel(clientStreamWrapper) == null) {
			grid.getStore().add(clientStreamWrapper);
		}
	}

	@Override
	public void addStreams(
			final List<StreamClientWrapper<W>> clientStreamWrappers) {
		for (final StreamClientWrapper<W> clientStreamWrapper : clientStreamWrappers) {
			addStream(clientStreamWrapper);
			AppUtils.logToConsole(clientStreamWrapper.getWrapper().toString());
		}
	}

	@Override
	public void activateAgentTimeZone() {
		List<String> timeZones = new ArrayList<String>();
		for (final StreamClientWrapper<W> clientStreamWrapper : grid.getStore().getAll()) {
			String timeZone = clientStreamWrapper.getWrapper().getAgent().getTimeZone();
			if (!timeZones.contains(timeZone)) {
				timeZones.add(timeZone);
			}
		}
		if (timeZones.size() == 1) {
			timeToolbar.getDateTimeIntervalWidget().enableAgentTimeZone(timeZones.get(0));
		} else {
			timeToolbar.getDateTimeIntervalWidget().disableAgentTimeZone();
		}
	}

	protected void afterUiBinder() {
		broadcastTableHeader.addStyleName(appearanceFactory.resources().css()
				.gridPanelHeader());
		broadcastTableHeader.getElement().getStyle()
				.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH, Unit.PX);
		loadedTemplateLabel.addStyleName(appearanceFactory.resources().css()
				.templateLabel());
		loadedTemplateLabel.getElement().getStyle()
				.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH - 15, Unit.PX);

		templateBar.addStyleName(appearanceFactory.resources().css()
				.templateBar());
		templateBar.getElement().getStyle()
				.setWidth(ClientConstants.DEFAULT_FIELD_WIDTH, Unit.PX);

		addBroadcastPanel.addStyleName(appearanceFactory.resources().css()
				.addBroadcastPanel());
		addBroadcastingLabel.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());

		final Image loadButton = AbstractImagePrototype.create(
				appearanceFactory.resources().loadTemplateButton())
				.createImage();
		loadButton.setTitle(messages.templateLoadingHeader());
		loadButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openLoadTemplateDialog();
			}
		});
		loadButton.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());
		final Image saveButton = AbstractImagePrototype.create(
				appearanceFactory.resources().saveTemplateButton())
				.createImage();
		saveButton.setTitle(messages.tempalteSavingHeader());
		saveButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openSaveTemplateDialog();
			}
		});
		saveButton.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());

		final Image cleanButton = AbstractImagePrototype.create(
				appearanceFactory.resources().clearSeriesButton())
				.createImage();
		cleanButton.setTitle(messages.clearVideoSeriesTemplateName());
		cleanButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					if (grid.getStore().getAll().size() > 0) {
						dialogFactory
							.createConfirmationDialog(new ConfirmationHandler() {
									@Override
									public void onCancel() {
									}
									
									@Override
									public void onConfirm( final String comment) {
										getUiHandlers().cleanupTemplate();
										clearStreams();
									}
								}, 
								messages.clearVideoSeriesDialogName(),
								messages.clearVideoSeriesConfirmation(),
								CommentMode.DISABLED).show();
						}
				}
			});
		cleanButton.addStyleName(appearanceFactory.resources().css()
								 .cursorPointer());

		templateBar.add(loadButton);
		templateBar.add(saveButton);
		templateBar.add(cleanButton);
		templateBar.setCellWidth(loadButton, loadButton.getWidth() + "px");
		templateBar.setCellWidth(saveButton, saveButton.getWidth() + "px");
		cleanButton.getElement().getStyle().setFloat(Float.RIGHT);
		cleanButton.getElement().getStyle().setMarginRight(10, Unit.PX);

		if (!isTimeSynchronizationEnabled()) {
			timeToolbar.removeFromParent();
		}
	}

	/**
	 * Use {@link UiHandler} annotation in descendants of the class in order to
	 * prevent handling of two events from the same button
	 */
	@Override
	public void applyButtonHandler(final SelectEvent e) {
		setUpPlayers();
	}

	@Override
	public Widget asWidget() {
		return widget;
	}

	private void clearRegistry() {
		final PlayerRegistry playerRegistry = PlayerRegistry.getInstance();
		// remove only players from playerPanels, not all players
		for (final Entry<VideoPanel, StreamClientWrapper<W>> entry : playerPanels
				.entrySet()) {
			final String playerId = entry.getKey().getPlayerId();

			playerRegistry.removePlayer(playerId);
		}
	}

	@Override
	public void clearStreams() {
		// should be called first before the removal of panels
		clearRegistry();
		for (final Entry<VideoPanel, StreamClientWrapper<W>> entry : playerPanels
				.entrySet()) {
			entry.getKey().removeFromParent();
			grid.getStore().remove(entry.getValue());
		}
		playerPanels.clear();
		// if we didn't select apply button on view
		grid.getStore().clear();
		// refresh grid view
		refreshGridView();
	}

	@Override
	public void closePlayers() {
		for (final Entry<VideoPanel, StreamClientWrapper<W>> entry : playerPanels
				.entrySet()) {
			final String playerId = entry.getKey().getPlayerId();
			final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
					playerId);
			if (player != null) {
				player.close();
			}
		}
	}

	protected abstract ColumnModel<StreamClientWrapper<W>> createColumnModel();

	protected FramedPanel createFramedPanel() {
		final FramedPanel panel = new FramedPanel(
				appearanceFactory.framedPanelAppearance());
		panel.setHeaderVisible(false);
		panel.setBorders(false);
		panel.setBodyBorder(false);

		return panel;
	}

	protected FramedPanel createLightFramedPanel() {
		final FramedPanel panel = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		panel.setHeaderVisible(false);
		panel.setBorders(false);
		panel.setBodyBorder(false);

		return panel;
	}

	private void createVideoContent(final String playerId,
			final StreamClientWrapper<W> clientStreamWrapper,
			final double volume, final int index) {
		final QoSPlayer player = playerFactory.createPlayerDependentOnPlatform(
				playerId, messages, new PlayerState(true, 0, 1),
				new PlayerState(false, 0, volume));
		PlayerRegistry.getInstance().addPlayer(player);

		createVideoPanel(player, clientStreamWrapper, index);
		setUpPlayer(player, clientStreamWrapper.getWrapper());
	}

	protected void createVideoPanel(final QoSPlayer player,
			final StreamClientWrapper<W> clientStreamWrapper, final int index) {
		final boolean hasDownloadButton = clientStreamWrapper
				.isStreamCanBeDownloaded();
		// FIXME get value from streamWrapper of somewhere
		final boolean hasAddToDashboardButton = !hasDownloadButton;
		boolean isAddedToDashboard = false;

		if (hasAddToDashboardButton) {
			isAddedToDashboard = isStreamAddedToDashboard(clientStreamWrapper
					.getWrapper());
		}

		final VideoPanel videoPanel = new VideoPanel(player,
				clientStreamWrapper.getWrapper().getStream().getDisplayName(),
				true, hasDownloadButton, messages.downloadVideo(), hasAddToDashboardButton,
				isAddedToDashboard, VIDEO_PANEL_OFFSET_WIDTH,
				VIDEO_PANEL_OFFSET_HEIGHT);
		videoPanel.setCloseVideoPanelEventHandler(closePlayerHandler);
		videoPanel
				.setAddPlayerToDahsboardEventHanlder(addPlayerToDahsboardEventHanlder);
		videoPanel.setTaskKey(clientStreamWrapper.getWrapper().getTaskKey());
		videoPanel.setTaskDisplayName(clientStreamWrapper.getWrapper().getStream().getTaskDisplayName());
		playerPanels.put(videoPanel, clientStreamWrapper);
		centerContainer.insert(videoPanel, index);
	}

	protected abstract DashboardWidget createWidget(W stream);

	public VideoPanel findVideoPanelByStream(final StreamClientWrapper<W> stream) {
		VideoPanel found = null;
		for (final Entry<VideoPanel, StreamClientWrapper<W>> entry : playerPanels
				.entrySet()) {
			if (entry.getValue().equals(stream)) {
				found = entry.getKey();
				break;
			}
		}
		return found;
	}

	@Override
	public List<W> getStreamWrappers() {
		final List<W> wrappers = new ArrayList<W>();
		for (final StreamClientWrapper<W> clientWrapper : grid.getStore()
				.getAll()) {
			wrappers.add(clientWrapper.getWrapper());
		}
		return wrappers;
	}

	protected void initialize() {
		borderLayoutContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		westPanel = createFramedPanel();
		centerFramePanel = createLightFramedPanel();

		centerContainer = new FlowLayoutContainer();
		centerContainer.setScrollMode(ScrollMode.AUTOY);
		centerContainer.addStyleName(FLOW_CONTAINER_STYLE_NAME);

		addBroadcastButton = AbstractImagePrototype.create(
				appearanceFactory.resources().addRectangleButton())
				.createImage();
		addBroadcastButton.addStyleName(appearanceFactory.resources().css()
				.addBroadcastButton());
		addBroadcastButton.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());

		final ColumnModel<StreamClientWrapper<W>> colModel = createColumnModel();

		final ModelKeyProvider<StreamClientWrapper<W>> keyProvider = new ModelKeyProvider<StreamClientWrapper<W>>() {

			@Override
			public String getKey(final StreamClientWrapper<W> item) {
				final W wrapper = item.getWrapper();
				String key = wrapper.getUniqueKey();
				if (wrapper instanceof MRecordedStreamWrapper) {
					key += ((MRecordedStreamWrapper) wrapper).getIntervalType()
							.toString();
				}
				return key;
			}
		};
		grid = new GroupingGrid<StreamClientWrapper<W>>(
				new ListStore<StreamClientWrapper<W>>(keyProvider), colModel,
				groupView) {
		};
		grid.addStyleName("qosGridStyle");
		grid.setHideHeaders(true);
		grid.getView().setAutoFill(true);
		grid.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

		applyButton = new TextButton(new TextButtonCell(
				appearanceFactory.<String> buttonCellHugeAppearance()),
				messages.apply());

		bigTvContainer = new BorderLayoutContainer(
				appearanceFactory.borderLayoutAppearance());
		bigTvContainer.addStyleName(ClientConstants.QOS_CONTAINER_LIGTH_STYLE);
		final CenterLayoutContainer center = new CenterLayoutContainer();
		final Image bigTvButton = AbstractImagePrototype.create(
				appearanceFactory.resources().bigTvButton()).createImage();
		bigTvButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(final ClickEvent event) {
				getUiHandlers().openAddVideoDialog();
			}
		});
		bigTvButton.addStyleName(appearanceFactory.resources().css()
				.cursorPointer());
		final MarginData centerData = new MarginData(new Margins(0));
		center.add(bigTvButton);
		bigTvContainer.setCenterWidget(center, centerData);

		toolbarFramePanel = new FramedPanel(
				appearanceFactory.lightFramedPanelAppearance());
		StyleUtils.configureNoHeaders(toolbarFramePanel);

		if (isTimeSynchronizationEnabled()) {
			timeToolbar.getElement().getStyle().setMarginLeft(16, Unit.PX);
			timeToolbar.setDisablable(true);
			timeToolbar.getDateTimeIntervalWidget().disableCustomTimeInterval();
			timeToolbar.setUpdateButtonHandler(new UpdateButtonHandler() {

				@Override
				public void onUpdateButtonPressed(final SelectEvent event) {
					try {
						if (timeToolbar.isToolbarEnabled()) {
							timeToolbar.getDateTimeIntervalWidget().validate();
						}
						closePlayers();
						applyButtonHandler(event);
					} catch (final ValidationException ex) {
						AppUtils.showErrorMessage(messages.invalidDateTimeInterval() + "\n" + ex.getMessage());
					}
				}
			});
		}
	}

	abstract protected boolean isStreamAddedToDashboard(W streamWrapper);

	abstract protected boolean isTimeSynchronizationEnabled();

	@Override
	public void refreshGridView() {
		grid.getView().refresh(true);
	}

	@Override
	public void removeBigTvButton() {
		if (bigTvContainer.isAttached()) {
			bigTvContainer.removeFromParent();
			centerFramePanel.add(centerContainer);
			centerFramePanel.forceLayout();
		}
	}

	@Override
	public void removeStream(final StreamClientWrapper<W> stream) {
		final VideoPanel panel = findVideoPanelByStream(stream);
		if (panel != null) {
			PlayerRegistry.getInstance().removePlayer(panel.getPlayerId());
			panel.removeFromParent();
			playerPanels.remove(panel);
		}
		grid.getStore().remove(stream);
		refreshGridView();
		if (grid.getStore().size() == 0) {
			setBigTvButton();
		}
	}

	@Override
	public void savePlayerStates() {
		for (final Entry<VideoPanel, StreamClientWrapper<W>> entry : playerPanels
				.entrySet()) {
			final String playerId = entry.getKey().getPlayerId();
			final QoSPlayer player = PlayerRegistry.getInstance().getPlayer(
					playerId);
			if (player != null) {
				player.saveState();
			}
		}
	}

	@Override
	public void setAddVideoToDashboardListener(
			final AddVideoToDashboardListener addVideoToDashboardListener) {
		this.addVideoToDashboardListener = addVideoToDashboardListener;
	}

	@Override
	public void setRemoveVideoToDashboardListener(
			final RemoveVideoToDashboardListener removeVideoToDashboardListener) {
		this.removeVideoToDashboardListener = removeVideoToDashboardListener;
	}

	@Override
	public void setBigTvButton() {
		if (!bigTvContainer.isAttached()) {
			centerContainer.removeFromParent();
			centerFramePanel.add(bigTvContainer);
			centerFramePanel.forceLayout();
		}
	}

	@Inject
	public void setPlayerFactory(final PlayerFactory playerFactory) {
		this.playerFactory = playerFactory;
	}

	@Override
	public void setTemplateLabel(final String templateName) {
		loadedTemplateLabel.setText(templateName);
		westPanel.forceLayout();
	}

	@Override
	public void setUpPlayers() {
		final int playerCount = grid.getStore().getAll().size();
		if (playerCount > 0) {
			removeBigTvButton();

			final List<StreamClientWrapper<W>> clientStreamWrappers = grid
					.getStore().getAll();
			for (int playerIndex = 0; playerIndex < playerCount; playerIndex++) {
				final StreamClientWrapper<W> clientStreamWrapper = clientStreamWrappers
						.get(playerIndex);
				try {
					final W streamWrapper = clientStreamWrapper.getWrapper();
					final String playerId = PlayerRegistry
							.createPlayerId(streamWrapper.getUniqueKey());
					final boolean videoContentWasCreated = playerPanels
							.containsValue(clientStreamWrapper);

					if (!videoContentWasCreated) {
						final double volume = playerCount > 1 ? 0 : 1;
						createVideoContent(playerId, clientStreamWrapper,
								volume, playerIndex);
					} else {
						final QoSPlayer player = PlayerRegistry.getInstance()
								.getPlayer(playerId);
						if (player != null) {
							try {
								setUpPlayer(player, streamWrapper);
							} catch (final ValidationException e) {
								AppUtils.showErrorMessage(messages
										.invalidDateTimeInterval()
										+ "\n"
										+ e.getMessage());
								removeStream(clientStreamWrapper);
							}
						}
					}
				} catch (final Exception e) {
					AppUtils.showErrorMessage(
							"Cannot load video player: " + e.getMessage(), e);
				}
			}
		}
	}

}
