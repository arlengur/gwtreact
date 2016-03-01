import React from 'react';
import Col from 'react-bootstrap/Col';
import {History} from 'react-router';
import Store from '../../stores/ChannelStatusStore';

var ChannelSmallCell = React.createClass({
    mixins: [History],

    render: function() {
        var ch = this.props.channel;
        var url = ch.configuration.streams.RTMP;
        var background = {background: this.props.colors.background};
        return <Col xs={6} sm={4} md={3} lg={2} className="col-rt-1 override-padding-3-2">
            <div className="container-fluid channel-small-wrapper clickable" style={background}
                 onClick={()=>this.history.pushState(null, `/details/${ch.channelId}`, {url: url})}>
                <img src={ch.logo} className="pull-left" height="26 px" width="26 px"/>
                <span className="channel-small-cell overflow-ellipsis">{ch.channelName}</span>
            </div>
        </Col>
    }
});

export default ChannelSmallCell;