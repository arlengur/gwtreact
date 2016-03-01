/*
 * Copyright (C) 2013 Tecomgroup.
 * All Rights Reserved.
 */
/**
 * @author kunilov.p
 * 
 */

var qligentPlayerModule = (function() {

	return {

		getPlayer : function(playerId) {
			return document.getElementById(playerId);
		},

		playLiveStream : function(playerId, baseUrl, urlPostfix) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.play(baseUrl, urlPostfix);
		},

		setVolume : function(playerId, volume) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.setVolume(volume);
		},
		
		getVolume : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			return qligentPlayer.getVolume();
		},

		close : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
            if(qligentPlayer != null) {
			    qligentPlayer.close();
            }
		},
		
		removePlayer : function(playerId, wrapperId, originalHtmlElement) {
		    swfobject.removeSWF(playerId);
		    wrapperId = '#' + wrapperId;
		    wrapperId = wrapperId.replace(/\./g , '\\.');
		    $(wrapperId).prepend(originalHtmlElement);
		},
		
		getCurrentTime : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			return qligentPlayer.getCurrentTime();
		},
		
		setCurrentTime : function(playerId, positionInMilliseconds) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			return qligentPlayer.setCurrentTime(positionInMilliseconds);
		},
		
		setPlayerPanelText : function(playerId, text) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.setCustomLabelText(text);
		},
		
		isPaused : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			return qligentPlayer.isPaused();
		},

		playRecordedStream : function(playerId, baseUrl, channelIdentifier,
				startTimestamp, endTimestamp) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.playChannel(baseUrl, channelIdentifier,
					new Date(startTimestamp), new Date(endTimestamp));
		},

		mute : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.mute();
		},

		pause : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.pause();
		},

		resume : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.resume();
		},

		togglePause : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.togglePause();
		},

		unmute : function(playerId) {
			var qligentPlayer = qligentPlayerModule.getPlayer(playerId);
			qligentPlayer.unmute();
		},

		createPlayer : function(playerId, streamType) {
			// For version detection, set to min. required Flash Player version,
			// or 0 (or 0.0.0), for no version detection.
			var swfVersionStr = "11.1.0";
			// To use express install, set to playerProductInstall.swf,
			// otherwise the empty string.
			var xiSwfUrlStr = "dependencies/qligentPlayerProductInstall.swf";
			
			var playerSettings = qligentPlayerSettings.getSettings(playerId, streamType);
			var playerParameters = {};
			playerParameters.quality = "high";
			playerParameters.bgcolor = "#ffffff";
			playerParameters.allowscriptaccess = "sameDomain";
			playerParameters.allowfullscreen = "true";
			playerParameters.scale = "noscale";
			playerParameters.wmode = "opaque";
			
			var playerAttributes = {};
			playerAttributes.id = playerId;
			playerAttributes.name = playerId;
			swfobject.embedSWF(
					"DesktopQoSMedia/player/dependencies/qligentPlayer.swf",
					playerId, "100%", "100%", swfVersionStr, xiSwfUrlStr,
					playerSettings, playerParameters, playerAttributes);
		}
	};
}());