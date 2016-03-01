import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import LiveVideoPlayer from './LiveVideoPlayer';
import TimelineBar from './TimelineBar';
import Images from '../../util/Images';
import Clock from './Clock';
import FavouriteControl from './FavouriteControl';
import Store from '../../stores/ChannelStatusStore';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import {History} from 'react-router';

var ChannelLargeCell = React.createClass({
    mixins: [History],

    render: function() {
        var ch = this.props.channel;
        var url = ch.configuration.streams.RTMP;
        var background = {background: this.props.colors.background};
        var playerId = this.props.cellId;
        return <Col xs={12} md={6} className="col-xlg-4 col-rt-2 col-uhd-1 override-padding-3-2">
            <div className="container-fluid">
                <Row className="detailed-player-header">
                    <img src={ch.logo} className="pull-left medium-cell-channel-icon"/>
                    <div>
                        <FavouriteControl enabled={ch.configuration.isFavourite} channelId={ch.channelId}/>
                        <Clock time={ch.configuration.interval} locale={AppUserSettingsStore.getLocale()}/>
                        <span className="player-header-title overflow-ellipsis">{ch.channelName}</span>
                    </div>
                </Row>
                <Row className="clickable"
                     onMouseDown={()=>this.history.pushState(null, `/details/${ch.channelId}`, {url: url})}>
                    <Col sm={6} xs={12} className="col-rt-12 override-padding-0" style={background}>
                        <LiveVideoPlayer id={playerId} url={url}/>
                    </Col>
                    <Col sm={6} xs={12} className="col-rt-12 override-padding-0" >
                        <TimelineBar parameterStates={ch.parameterStates}
                            startDate={ch.startDate} endDate={ch.endDate}/>
                    </Col>
                </Row>
            </div>
        </Col>;
    }
});

export default ChannelLargeCell;
