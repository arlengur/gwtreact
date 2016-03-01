/**
 * qligentPlayerSettings is an object containing available initialization
 * parameters for player:desc player. getSettings method is invoked to assign it
 * to flashvars object on flash container initialization.
 * 
 * @author Mikhail Yaropolov
 */

var qligentPlayerSettings = {
	// List of player state names:
	// Playback
	NONE_STATE : "none",
	BUFFERING_STATE : "buffering",
	PLAYING_STATE : "playing",
	PAUSED_STATE : "paused",
	STOPPED_STATE : "stopped",
	// Fullscreen on/off
	FULLSCREEN_ON : "fullscreenOn",
	FULLSCREEN_OFF : "fullscreenOff",
	// Control bar view modes
	CONTROL_BAR_LIVE_VIEW : "controlBarLiveView",
	CONTROL_BAR_RECORDED_VIEW : "controlBarRecordedView",

	// Returns player settings which shoud be assigned to flashvars variable.
	getSettings : function(playerId, streamType) {
		var settings = {};
		settings.htmlId = playerId;
		settings.handlerName = "playerStateEventHandler";
		if (streamType == "LIVE") {
			settings.controlBarView = qligentPlayerSettings.CONTROL_BAR_LIVE_VIEW;
		} else if (streamType == "RECORDED") {
			settings.controlBarView = qligentPlayerSettings.CONTROL_BAR_RECORDED_VIEW;
		}
		return settings;
	},

	// Object containing player event handlers. Should be assigned
	// to global variable to let AS3 invoke its methods.
	// In this case handlers object is assigned to global variable
	// playerStateEventHandler which is located at the bottom of this file.
	handlers : {
		noneHandler : function(playerId) {
			console.log("Player '" + playerId + "' state: NONE_STATE");
		},
		bufferingHandler : function(playerId) {
			console.log("Player '" + playerId + "' state: BUFFERING_STATE");
		},
		playingHandler : function(playerId) {
			window.playerStartedCallback(playerId);
			console.log("Player '" + playerId + "' state: PLAYING_STATE");
		},
		pausedHandler : function(playerId) {
			console.log("Player '" + playerId + "' state: PAUSED_STATE");
		},
		stoppedHandler : function(playerId) {
			console.log("Player '" + playerId + "' state: STOPPED_STATE");
		},
		fullscreenOnHandler : function(playerId) {
			window.playerFullScreenOnCallback(playerId);
			console.log("Player '" + playerId
					+ "' fullscreen mode: FULLSCREEN_ON");
		},
		fullscreenOffHandler : function(playerId) {
			window.playerFullScreenOffCallback(playerId);
			console.log("Player '" + playerId
					+ "' fullscreen mode: FULLSCREEN_OFF");
		},
		loadHandler : function(playerId) {
			window.playerSetUpCallback(playerId);
			console.log("Player '" + playerId + "' state mode: LOAD");
		},
		closeHandler : function(playerId) {
			console.log("Player '" + playerId + "' state mode: CLOSE");
		},
		playClickHandler : function(playerId) {
			console.log("Player '" + playerId
					+ "' control bar event: playClick");
		},
		pauseClickHandler : function(playerId) {
			console.log("Player '" + playerId
					+ "' control bar event: pauseClick");
		}
	}
};

var playerStateEventHandler = qligentPlayerSettings.handlers;