import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import _ from 'lodash';
import Timeline from './Timeline';
import {group8toGroup5, currentSeverity} from '../../util/Alerts';
import Images from '../../util/Images';

var TimelineBar = React.createClass({
    render: function() {
        var groups = group8toGroup5(this.props.parameterStates);
        return <div className="container-fluid timeline-bar">
            {_.map(
                ["RF_IP", "TS", "VIDEO", "AUDIO", "EPG_CC_DATA"],
                function(group){
                    var reportsGroup =  _.find(groups, function(param){
                        return param["group"] == group;
                    });
                    var reports = (typeof reportsGroup != 'undefined')? reportsGroup["alertsHistory"] : [];
                    var severity = currentSeverity(reports, this.props.endDate);
                    var iconUrl = Images.timelineIcon(group, severity);
                    return <Timeline reports={reports} img={iconUrl} key={group}
                                     endDate={this.props.endDate}
                                     startDate={this.props.startDate}/>
                }.bind(this))}
        </div>
    }
});

export default TimelineBar;