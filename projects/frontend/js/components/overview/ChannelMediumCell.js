import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import LiveVideoPlayer from './LiveVideoPlayer';
import Store from '../../stores/ChannelStatusStore';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import i18nConstants from '../../constants/i18nConstants';
import {History} from 'react-router';

var ChannelMediumCell = React.createClass({
    mixins: [History],
    render: function() {
        var ch = this.props.channel;
        var background = {background: this.props.colors.background};
        var url = ch.configuration.streams.RTMP;
        var playerId = this.props.cellId;
        return <Col xs={12} sm={4} md={3} className="col-rt-2 col-uhd-1 override-padding-3-2">
            <div className="container-fluid">
                <div className="row player-header">
                    <img src={ch.logo} className="pull-left medium-cell-channel-icon"/>
                    <div>
                        <img src={this.props.colors.other}
                             title={AppUserSettingsStore.localizeString(i18nConstants.OTHER_ALARMS)}
                             className="pull-right"/>
                        <img src={this.props.colors.video}
                             title={AppUserSettingsStore.localizeString(i18nConstants.VIDEO)}
                             className="pull-right"/>
                        <img src={this.props.colors.audio}
                             title={AppUserSettingsStore.localizeString(i18nConstants.AUDIO)}
                             className="pull-right"/>
                        <span className="player-header-title overflow-ellipsis">{ch.channelName}</span>
                    </div>
                </div>
                <Row style={background} className="clickable"
                    onMouseDown={()=>this.history.pushState(null, `/details/${ch.channelId}`, {url: url})}>
                    <LiveVideoPlayer id={playerId} url={url}/>
                </Row>
            </div>
        </Col>;
    }
});

export default ChannelMediumCell;