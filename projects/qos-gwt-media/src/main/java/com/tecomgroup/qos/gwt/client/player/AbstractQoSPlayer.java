/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */

package com.tecomgroup.qos.gwt.client.player;

import java.util.Date;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tecomgroup.qos.exception.NotInitializedException;
import com.tecomgroup.qos.gwt.client.QoSServlets;
import com.tecomgroup.qos.gwt.client.i18n.QoSMessages;
import com.tecomgroup.qos.gwt.client.player.listener.AfterPlayerSetUpListener;
import com.tecomgroup.qos.gwt.client.player.listener.FullScreenOffPlayerListener;
import com.tecomgroup.qos.gwt.client.player.listener.FullScreenOnPlayerListener;
import com.tecomgroup.qos.gwt.client.player.listener.PlayerListener;
import com.tecomgroup.qos.gwt.client.player.listener.PlayerListenerContainer;

/**
 * @author kunilov.p
 * 
 */
public abstract class AbstractQoSPlayer extends PlayerListenerContainer
		implements
			QoSPlayer {

	protected enum StreamType {
		LIVE, RECORDED
	}

	protected static Logger LOGGER = Logger.getLogger(AbstractQoSPlayer.class
			.getName());

	/**
	 * Shows whether player component is initialized and starts to load.
	 */
	private boolean initialized = false;

	/**
	 * Shows whether player is loaded and ready to perform operations.
	 */
	private boolean loaded = false;

	protected PlayerState state;

	protected final String playerId;
	protected final QoSMessages messages;

	protected SimplePanel playerWidget;
	protected String playerWidgetHtmlElement;

	protected String baseUrl;
	protected String downloadUrl;
	protected StreamType streamType;

	// stream parameters
	protected String urlPostfix;
	// channel parameters
	protected String recordedStreamIdentifier;
	protected Date startDateTime;
	protected Date endDateTime;

	protected static final String WRAPPER_PREFIX = "wrapper_";

	/**
	 * Creates abstract player with provided playerId.
	 * 
	 * @param playerId
	 */
	public AbstractQoSPlayer(final String playerId, final QoSMessages messages) {
		super();
		this.playerId = playerId;
		this.messages = messages;
		initPlayerWidget();
		initPlayerListeners();
	}

	/**
	 * Creates player for regular file or stream.
	 * 
	 * @param playerId
	 * @param baseUrl
	 *            - base URL to play all videos
	 * @param urlPostfix
	 *            - It can be name of video file or name of stream.
	 */
	public AbstractQoSPlayer(final String playerId, final String baseUrl,
			final String urlPostfix, final QoSMessages messages) {
		this(playerId, messages);
		setLiveStreamParameters(baseUrl, urlPostfix);
	}

	/**
	 * Creates player for channel.
	 * 
	 * @param playerId
	 * @param baseUrl
	 *            - base URL to play all videos
	 * @param downloadUrl
	 *            - base URL to download video. Filename must be appended to
	 *            this URL in order to get full URL to download specific file
	 * @param recordedStreamIdentifier
	 *            - some identifier of recorded stream
	 * @param startDateTime
	 *            - start time of the requested video
	 * @param endDateTime
	 *            - end time of the requested video
	 */
	public AbstractQoSPlayer(final String playerId, final String baseUrl,
			final String downloadUrl, final String recordedStreamIdentifier,
			final Date startDateTime, final Date endDateTime,
			final QoSMessages messages) {
		this(playerId, messages);
		setRecordedStreamParameters(baseUrl, downloadUrl,
				recordedStreamIdentifier, startDateTime, endDateTime);
	}

	@Override
	public void addAfterPlayerSetUpListener(
			final AfterPlayerSetUpListener listener) {
		addPlayerListener(AfterPlayerSetUpListener.class, listener);
	}

	/**
	 * Not required steps after initialization. Can be called even if player is
	 * already initialized.
	 */
	protected abstract void afterInitialization();

	/**
	 * Returns the container of the player.
	 * 
	 * @return
	 */
	@Override
	public Widget asWidget() {
		return playerWidget;
	}

	protected void checkInitializationState() {
		if (!isInitialized()) {
			throw new NotInitializedException(
					"Player is not initialized. Invoke method initialize before calling any other methods");
		}
	}

	@Override
	public void clearState() {
		state = null;
	}

	@Override
	public void close() {
		initialized = false;
		loaded = false;
	}

	@Override
	public String getPlayerId() {
		return playerId;
	}

	@Override
	public String getPlaylistDownloadUrl() {
		checkInitializationState();

		if (StreamType.RECORDED.equals(streamType)) {
			return downloadUrl + QoSServlets.searchVideoFilesServlet
					+ "?recordedStreamIdentifier=" + recordedStreamIdentifier
					+ "&startDateTime=" + startDateTime.getTime()
					+ "&endDateTime=" + endDateTime.getTime();
		} else {
			throw new UnsupportedOperationException(
					"Playlist is only supported for recorded video");
		}
	}

	/**
	 * Gets immutable instance of {@link PlayerState}.
	 */
	protected PlayerState getState() {
		return state;
	}

	/**
	 * Returns id of player wrapper html-element
	 * 
	 * @return
	 */
	protected String getWrapperId() {
		return WRAPPER_PREFIX + playerId;
	}

	protected boolean hasState() {
		return state != null;
	}

	@Override
	final public void initialize() {
		if (streamType == null) {
			throw new NotInitializedException(
					"Stream or channel parameters are not set");
		}
		if (StreamType.RECORDED.equals(streamType)) {
			validateRecordedStreamParameters();
		} else if (StreamType.LIVE.equals(streamType)) {
			validateLiveStreamParameters();
		}

		if (!isInitialized()) {
			try {
				loaded = false;
				initPlayer();
				initialized = true;
			} catch (final Exception ex) {
				throw new NotInitializedException(ex.getMessage(), ex);
			}
		}
		try {
			afterInitialization();
		} catch (final Exception ex) {
			LOGGER.log(Level.INFO,
					"After player initialization steps are failed: player="
							+ toString() + ": " + ex.getMessage(), ex);
		}
	}

	protected abstract void initPlayer();

	private void initPlayerListeners() {
		addAfterPlayerSetUpListener(new AfterPlayerSetUpListener() {
			@Override
			public void handle(final QoSPlayer player) {
				loadState();
				loaded = true;
			}
		});
		addPlayerListener(FullScreenOnPlayerListener.class,
				new FullScreenOnPlayerListener() {

					@Override
					public void handle(final QoSPlayer currentPlayer) {
						final Set<String> playerIds = PlayerRegistry
								.getInstance().getPlayerIds();
						for (final String playerId : playerIds) {
							if (!playerId.equals(currentPlayer.getPlayerId())) {
								final QoSPlayer player = PlayerRegistry
										.getInstance().getPlayer(playerId);
								player.saveState();
								player.pause();
							}
						}

					}
				});
		addPlayerListener(FullScreenOffPlayerListener.class,
				new FullScreenOffPlayerListener() {

					@Override
					public void handle(final QoSPlayer currentPlayer) {
						final Set<String> playerIds = PlayerRegistry
								.getInstance().getPlayerIds();
						for (final String playerId : playerIds) {
							final AbstractQoSPlayer player = (AbstractQoSPlayer) PlayerRegistry
									.getInstance().getPlayer(playerId);
							if (!playerId.equals(currentPlayer.getPlayerId())
									&& player.hasState()
									&& !player.getState().isPaused()) {
								player.resume();
							}
						}
					}
				});
	}

	private void initPlayerWidget() {
		playerWidget = new SimplePanel();
		playerWidget.getElement().setId(getWrapperId());
		final SimplePanel internalWidget = new SimplePanel();
		internalWidget.getElement().setId(playerId);
		playerWidgetHtmlElement = internalWidget.getElement().getString();
		playerWidget.add(internalWidget);
	}

	/**
	 * Shows whether player component is initialized and starts to load.
	 * 
	 * @return true if player is initialized otherwise false.
	 */
	public boolean isInitialized() {
		return initialized;
	}

	/**
	 * Shows whether player is loaded and ready to perform operations.
	 * 
	 * @return true if player is loaded otherwise false.
	 */
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void loadState() {
		checkInitializationState();
		if (hasState()) {
			if (state.isMuted()) {
				mute();
			} else {
				setVolume(state.getVolume());
			}
			if (state.isPaused()) {
				pause();
			} else {
				play();
			}
			setCurrentTime(state.getCurrentTime());
		}
	}

	@Override
	public void notifyListeners(
			final Class<? extends PlayerListener> listenerType) {
		notifyListeners(this, listenerType);
	}

	@Override
	public void saveState() {
		checkInitializationState();
		if (isLoaded()) {
			state = new PlayerState(isPaused(), getCurrentTime(), getVolume());
		}
	}

	@Override
	public void setDefaultState(final PlayerState state) {
		if (!hasState()) {
			this.state = state;
		}
	}

	@Override
	public void setLiveStreamParameters(final String baseUrl,
			final String urlPostfix) {
		streamType = StreamType.LIVE;
		this.baseUrl = baseUrl;
		this.urlPostfix = urlPostfix;
	}

	@Override
	public void setRecordedStreamParameters(final String baseUrl,
			final String downloadUrl, final String recordedStreamIdentifier,
			final Date startDateTime, final Date endDateTime) {
		streamType = StreamType.RECORDED;
		this.baseUrl = baseUrl;
		this.downloadUrl = downloadUrl;
		this.recordedStreamIdentifier = recordedStreamIdentifier;
		this.startDateTime = startDateTime;
		this.endDateTime = endDateTime;
	}

	@Override
	public String toString() {
		String toString = "{playerId=" + playerId;
		if (StreamType.LIVE.equals(streamType)) {
			toString += ", liveStream={baseUrl=" + baseUrl + ", urlPostfix="
					+ urlPostfix + "}";
		} else if (StreamType.RECORDED.equals(streamType)) {
			toString += ", recordedStream={baseUrl=" + baseUrl
					+ ", recordedStreamIdentifier=" + recordedStreamIdentifier
					+ ", startDateTime=" + startDateTime + ", endDateTime="
					+ endDateTime + "}";
		}
		toString += "}";
		return toString;
	}

	private boolean validateCommonParameters() {
		boolean result = false;

		if (baseUrl == null || baseUrl.isEmpty()) {
			throw new NotInitializedException("Base url is not set");
		} else {
			result = true;
		}

		return result;
	}

	private boolean validateLiveStreamParameters() {
		boolean result = validateCommonParameters();

		if (urlPostfix == null || urlPostfix.isEmpty()) {
			throw new NotInitializedException("Url postfix is not set");
		} else {
			result = true;
		}

		return result;
	}

	private boolean validateRecordedStreamParameters() {
		boolean result = validateCommonParameters();

		if (recordedStreamIdentifier == null
				|| recordedStreamIdentifier.isEmpty()) {
			throw new NotInitializedException(
					"Recorded stream identifier is not set");
		} else if (startDateTime == null) {
			throw new NotInitializedException("Start play time is not set");
		} else if (endDateTime == null) {
			throw new NotInitializedException("End play time is not set");
		} else {
			result = true;
		}

		return result;
	}
}
