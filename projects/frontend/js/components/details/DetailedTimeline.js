import React from 'react';
import moment from 'moment';
import _ from 'lodash';
import {severityToNum, filterAlertsByInterval, group8toGroup5} from '../../util/Alerts';
import Images from '../../util/Images';
import AppUserStore from '../../stores/AppUserSettingsStore';
import TimelineScrollbar from './TimelineScrollbar';

var SEPARATOR_INTERVALS = [
    //[thin(ms), thick(ms), labelFormat]
    [1000, 10*1000, "LTS"],
    [2*1000, 20*1000, "LTS"],
    [5*1000, 60*1000, "LTS"],
    [20*1000, 2*60*1000, "LTS"],
    [30*1000, 5*60*1000, "LTS"],
    [60*1000, 10*60*1000, "LTS"],
    [2*60*1000, 20*60*1000, "LT"],
    [5*60*1000, 60*60*1000, "LT"],
    [15*6*1000, 2*60*60*1000, "l LT"],
    [30*60*1000, 3*60*60*1000, "l LT"],
    [60*60*1000, 6*60*60*1000, "l LT"],
    [2*60*60*1000, 12*60*60*1000, "l"],
    [4*60*60*1000, 24*60*60*1000, "l"],
    [6*60*60*1000, 2*24*60*60*1000, "l"],
    [12*60*60*1000, 3*24*60*60*1000, "l"]
];

var DetailedTimeline = React.createClass({
    getRectClassName: function (severity) {
        if (severityToNum(severity) > 4) {
            return "rect crit-line";
        }
        else {
            return "rect warn-line";
        }
    },
    render: function () {
        var rectOffset = 16;
        var minTime = this.props.interval.start;
        var maxTime = this.props.interval.end;
        var alerts = filterAlertsByInterval(this.props.alerts, {start: minTime, end: maxTime});

        var tlSize = maxTime - minTime;

        var rectStyle = {
            bottom: rectOffset,
            left: 0,
            width: "100%"
        };
        var noAlertRect = <div style={rectStyle} className="no-alert-line"></div>;

        var warnAlerts = [];
        var critAlerts = [];

        alerts.map(function (alert) {
            var rectX = (Math.max(alert.startDateTime, minTime) - minTime) / tlSize;

            var alertEndTime = alert.endDateTime || maxTime;
            var rectWidth = (Math.min(alertEndTime, maxTime) - minTime) / tlSize - rectX;
            var rectClassName = this.getRectClassName(alert.severity);
            var rectStyle = {
                bottom: rectOffset,
                left: rectX * 100 + "%",
                width: rectWidth * 100 + "%"
            };

            var alertRect = (<div
                key={alert.alertReportId}
                data-start={alert.startDateTime}
                data-end={alert.endDateTime || maxTime}
                style={rectStyle}
                className={rectClassName}
            ></div>);
            if (severityToNum(alert.severity) <= 4) {
                warnAlerts.push(alertRect);
            }
            else { // critical
                critAlerts.push(alertRect);
            }

        }.bind(this));

        var alertCount = alerts.length;
        var iconUrl = Images.timelineIcon(this.props.type, this.props.severity);

        var className = "detailed-timeline " + (this.props.noBorder ? "no-border" : "");
        return <div>
            <div className="timeline-info">
                <img src={iconUrl}/>
                <span>x{alertCount}</span>
            </div>
            <div>
                <div className={className}>
                    { noAlertRect }
                    { warnAlerts }
                    { critAlerts }
                </div>
            </div>
        </div>;
    }
});

var DetailedTimelineList = React.createClass({
    getSeparatorIntervals: function(interval) {
        var length = interval.end - interval.start;
        return _.dropWhile(SEPARATOR_INTERVALS, function(intervals) {
            return intervals[0]*100 < length;
        })[0];
    },
    getSeparatorPositions: function(start, end, interval) {
        var offset = start % interval;
        return _.range(start-offset+interval, end, interval);
    },
    getIntervalOffset: function(timestamp) {
        return (timestamp - this.props.selectedInterval.start) / (this.props.selectedInterval.end - this.props.selectedInterval.start);
    },
    render: function () {
        var groups = group8toGroup5(this.props.displayedAlerts);
        var maxSeverities8 = this.props.maxSeverities;
        var maxSeverities5 = {
            RF_IP: _.max([maxSeverities8.get('RF'), maxSeverities8.get('IP')], severityToNum),
            TS: maxSeverities8.get('TS'),
            VIDEO: maxSeverities8.get('VIDEO'),
            AUDIO: maxSeverities8.get('AUDIO'),
            EPG_CC_DATA: _.max([maxSeverities8.get('EPG'), maxSeverities8.get('CC'), maxSeverities8.get('DATA')], severityToNum)
        };
        var overviewIntervals = this.getSeparatorIntervals(this.props.interval);
        var overviewSep = this.getSeparatorPositions(this.props.interval.start, this.props.interval.end, overviewIntervals[0]);
        var intervals = this.getSeparatorIntervals(this.props.selectedInterval);
        var thinSepPos = this.getSeparatorPositions(this.props.selectedInterval.start, this.props.selectedInterval.end, intervals[0]);
        var thickSepPos = this.getSeparatorPositions(this.props.selectedInterval.start, this.props.selectedInterval.end, intervals[1]);
        return <div className="detailed-timeline-list">
            <div className="timeline-background">
                <div className="timeline-separators-thin">
                    <svg width="100%" height="100%" viewBox="0 0 1000 100" preserveAspectRatio="none">
                        {_.map(thinSepPos, function(pos) {
                            var x = Math.round(this.getIntervalOffset(pos)*1000);
                            return <line key={pos+"thin"} x1={x} y1="0" x2={x} y2="100" strokeWidth="1" stroke="#4f4b4b"></line>;
                        }.bind(this))}
                    </svg>
                </div>
                {groups.map(function (group) {
                    return (<DetailedTimeline
                        key={group.group}
                        type={group.group}
                        alerts={group.alertsHistory}
                        interval={this.props.selectedInterval}
                        severity={maxSeverities5[group.group]}
                    />);
                }.bind(this))}
                <div className="timeline-separators-thick">
                    <svg width="100%" height="100%" viewBox="0 0 1000 100" preserveAspectRatio="none">
                        {_.map(thickSepPos, function(pos) {
                            var x = Math.round(this.getIntervalOffset(pos)*1000);
                            return <line key={pos+"thick"} x1={x} y1="0" x2={x} y2="100" strokeWidth="1" stroke="#808080"></line>;
                        }.bind(this))}
                    </svg>
                </div>
                {_.map(thickSepPos, function(pos) {
                    var offsetLeft = this.getIntervalOffset(pos)*100;
                    return <div key={pos+"text"}className="timeline-separator-text" style={{left: ''+offsetLeft+'%'}}>
                        {moment(pos).format(intervals[2])}
                    </div>
                }.bind(this))}
            </div>
            <TimelineScrollbar interval={this.props.interval} alertGroups={groups} separators={overviewSep}/>
        </div>;
    }
});

export default DetailedTimelineList;