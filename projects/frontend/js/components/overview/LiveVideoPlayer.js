import React from 'react';
import Swf from 'react-swf';

//enum from getState()  -  http://flash.flowplayer.org/documentation/api/player.html
var PlayerState={
    UNLOADED:-1,
    LOADED:0,
    UNSTARTED:1,
    BUFFERING:2,
    PLAYING:3,
    PAUSED:4,
    ENDED:5
};

var playerInterval;

var LiveVideoPlayer = React.createClass({
    render: function () {
        return <div className="player-frame">
                 <div className="player-wrapper">
                     <div id={this.props.id}></div>
                <div className="player-overlay"/>
                </div>
            </div>;
    },
    componentDidMount: function() {
        this.initPlayer(this.props);
    },
    shouldComponentUpdate: function(nextProps,nextState) {
        if(nextProps.url!==this.props.url)
        {
            return true;
        }else{
            return false;
        }
        return true;
    },
    componentDidUpdate: function() {
        this.initPlayer(this.props);
    },
    componentWillUnmount: function() {
        var player=$f(this.props.id);
        if(playerInterval!=undefined) {
            clearInterval(playerInterval);
        }
        if(player!=undefined) {
            player.close();
            player.unload();
        }
    },
    initPlayer: function(props) {
        var targetClip;
        var isValidURL=true;
        var url = props.url;
        if ((typeof url !== 'string') || url == '') {
            targetClip= {
                url: '/qos/img/no_broadcast.png',
                scaling: 'orig'
            };
            isValidURL=false;
        }else{
            targetClip= {
                url: url,
                live:true,
                provider : "rtmp"
            };
        }
        var player = $f(props.id, "swf/flowplayer-3.2.18.swf", {
            // Uncomment for debug
            // log: {level: "debug", filter: "org.flowplayer.rtmp.*"},
            showErrors: true,
            onError: function (errorCode, errorMessage) {
                if (isValidURL) {
                    console.log("Player: " + props.id + " ERROR, will try to reconnect." + errorMessage + " : " + errorCode);
                    this.close().stopBuffering();
                    this.stop();
                }
            },
            onLoad:function()
            {
                var currentPlayer=this;
                if (isValidURL) {
                    playerInterval = setInterval(function () {
                        if ((currentPlayer.getState() === PlayerState.UNSTARTED || currentPlayer.getState() === PlayerState.ENDED)) {
                            console.log("Resume playing in " + currentPlayer.id() + " State:" + currentPlayer.getState());
                            currentPlayer.play(0);
                            currentPlayer.mute(true);
                        }
                    }, 20000);
                }
            },
            plugins: {
                rtmp: {url: "swf/flowplayer.rtmp-3.2.13.swf"}
            },
            clip:targetClip,
            play : {opacity: 0},
            muted: true
        });
    }
});

export default LiveVideoPlayer;