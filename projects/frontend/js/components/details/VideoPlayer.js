import React from 'react';
import Swf from 'react-swf';
import Settings from '../../qligentPlayerSettings';

var PlayerCell = React.createClass({
    getPlayer: function() {
        return React.findDOMNode(this.refs.player);
    },
    getId: function() {
        return 'player_' + this.props.name;
    },
    playStream: function() {
        var url = this.props.url;
        if(typeof url == 'string' && url != '') {
            if(this.props.lifeVideo){
                    var delimeterIx = url.lastIndexOf("/");
                    var base = url.substring(0,delimeterIx);
                    var urlPostfix = url.substring(delimeterIx+1);
                    this.getPlayer().play(base,urlPostfix);
            } else {
                var channelId = typeof(this.props.channelId) == 'undefined' ? "defaultchannel" : this.props.channelId;
                this.getPlayer().playChannel(url, channelId,
                    new Date(this.props.interval.start), new Date(this.props.interval.end));
            }
       }

    },
    pause: function() {
        this.getPlayer().qligentPlayer.pause();
    },
    resume: function() {
        this.getPlayer().qligentPlayer.resume();
    },
    togglePause: function() {
        this.getPlayer().togglePause();
    },
    isPaused: function() {
        return this.getPlayer().isPaused();
    },
    getCurrentTime: function() {
        return this.getPlayer().getCurrentTime();
    },
    setCurrentTime: function(positionInMilliseconds) {
        this.getPlayer().setCurrentTime(positionInMilliseconds);
    },
    setPlayerPanelText: function(text) {
        this.getPlayer().setCustomLabelText(text);
    },
    mute : function() {
        this.getPlayer().mute();
    },
    unmute: function() {
        this.getPlayer().unmute();
    },
    setVolume: function(volume) {
        this.getPlayer().setVolume(volume);
    },
    getVolume: function() {
        return this.getPlayer().getVolume();
    },
    close: function() {
        if(this.getPlayer() != null) {
            this.getPlayer().close();
        }
    },
    componentWillMount: function() {
        Settings.registerLoadCallback(this.getId(), function() {
            this.playStream();
            this.mute();
        }.bind(this));
    },
    componentDidUpdate: function() {
        this.playStream();
    },
    componentWillUnmount: function() {
        Settings.unregisterLoadCallback(this.getId());
        this.close();
    },
    shouldComponentUpdate: function(nextProps, nextState) {
        //don't update player if it is Live video , and url did not changed
        if(this.props.url != nextProps.url) {
            return true;
        }
        //if it's recorded with the same interval, did not update
        if(!nextProps.lifeVideo) {
            return (this.props.interval.start != nextProps.interval.start ||
                    this.props.interval.end   != nextProps.interval.end);
        }
        return false;
    },
    render: function() {
        // Unique component key, to re-mount it on different recorded video tasks, to clean "Info panel" on player ("No video files found")
        // @TODO add clean info panel method to player. there is still bug when files didn't exist for selected time
        var key = "pl-"+(this.props.lifeVideo ? "live": "recorded-"+this.props.channelId);
        if (typeof this.props.url !== 'string' || this.props.url == '') {
            return <div key={key} className="player-wrapper" style={{marginBottom: '4px'}}>
                <div className="no_video_stream"></div>;
            </div>
        }
        var playerId = this.getId();
        var playerFlashVars;
        if(this.props.lifeVideo){
            playerFlashVars = Settings.getSettings(playerId, "LIVE");
        }else{
            playerFlashVars = Settings.getSettings(playerId, "RECORDED");
        }
        return <div key={key} className="player-wrapper" style={{marginBottom: '4px'}}>
            <Swf src="swf/qligentPlayer.swf"
                 ref="player"
                 id={playerId}
                 flashVars={playerFlashVars}
                 allowFullScreen="true"
                 scale="noscale"
                 quality="high"
                 width="300"
                 height="200"
                 wmode="opaque"
                 bgcolor="#ffffff"/>
        </div>;
    }
});

export default PlayerCell;