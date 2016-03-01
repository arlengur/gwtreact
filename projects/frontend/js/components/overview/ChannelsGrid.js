import React from 'react';
import Row from 'react-bootstrap/Row';
import ChannelLargeCell from './ChannelLargeCell';
import ChannelMediumCell from './ChannelMediumCell';
import ChannelSmallCell from './ChannelSmallCell';
import {severityToNum, currentSeverity, severityTo3Colors} from '../../util/Alerts';
import Images from '../../util/Images';
import Actions from '../../actions/OverviewActions';
import _ from 'lodash';


var ChannelsGrid = React.createClass({
    calculateColors: function(reportGroups, endTime){
        var videoGroup = _.find(reportGroups, (group) => group.group == "VIDEO");
        var videoSeverity = typeof videoGroup=='undefined'?"NONE":currentSeverity(videoGroup.alertsHistory, endTime);
        var audioGroup = _.find(reportGroups, (group) => group.group == "AUDIO");
        var audioSeverity = typeof audioGroup=='undefined'?"NONE":currentSeverity(audioGroup.alertsHistory, endTime);
        var otherGroups = _.filter(reportGroups, (group) => group.group != "VIDEO" && group.group != "AUDIO");
        var otherReports = _.flatten(_.pluck(otherGroups, "alertsHistory"));
        var otherSeverity = currentSeverity(otherReports, endTime);
        var maxSeverity = _.max([audioSeverity, videoSeverity, otherSeverity], severityToNum);
        return {
            audio: Images.audio(audioSeverity),
            video: Images.video(videoSeverity),
            other: Images.other(otherSeverity),
            background: severityTo3Colors(maxSeverity)
        }
    },
    componentDidMount: function () {
        var gridRect = React.findDOMNode(this.refs.grid).getBoundingClientRect();
        Actions.setGridSize(gridRect.width, gridRect.height);
    },
    render: function() {
        return <div ref="grid" className="channels-grid container-fluid flex-11a">
            <Row>
                {_.map(this.props.channels, (d) => {
                    var colors = this.calculateColors(d.parameterStates, d.endDate);
                    var cellId = d.channelName+"_"+d.channelId;
                    var key=cellId;
                    if(this.props.view == 1) {
                        return <ChannelSmallCell key={key} cellId={cellId} channel={d} colors={colors}/>
                    } else if(this.props.view == 2){
                        return <ChannelMediumCell key={key} cellId={cellId} channel={d} colors={colors}/>
                    } else if(this.props.view == 3) {
                        return <ChannelLargeCell key={key} cellId={cellId} channel={d} colors={colors}/>
                    }
                })}
            </Row>
        </div>;
    }
});

export default ChannelsGrid;