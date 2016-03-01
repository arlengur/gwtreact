import React from 'react';
import _ from 'lodash';
import AlertIntervals from '../../util/AlertIntervals';
import {severityToNum, severityTo3Colors} from '../../util/Alerts';

var OverviewLine = React.createClass({
    render: function() {
        var height = this.props.alertGroups.length * 2 - 1;
        var end = this.props.interval.end;
        var start = this.props.interval.start;
        var length = end - start;
        return <div className="TlScr-preview-wrapper">
            <svg width="100%" height="100%" viewBox={"0 0 1000 "+height}
                 preserveAspectRatio="none" style={{position: 'absolute', top: 0, left: 0}}>
                {_.flatten(_.map(this.props.alertGroups, function(group, ix) {
                    var merged = AlertIntervals.merge(group.alertsHistory, start, end);
                    return _.chain(merged)
                        .sortBy(function(report) {
                            return severityToNum(report.severity)
                        })
                        .map(function(report) {
                            var xStart = Math.floor((report.startDateTime - start)*1000/length);
                            var xEnd= Math.ceil((report.endDateTime - start)*1000/length);
                            if(xEnd == xStart) {
                                xEnd = xStart + 1;
                            }
                            var yStart = ix*2;
                            var yEnd = yStart + 1;
                            return <polygon key={report.severity+report.id+"y"+yStart+"s"+xStart+"e"+xEnd}
                                            fill={severityTo3Colors(report.severity)}
                                            strokeWidth="0"
                                            points={""+xStart+","+yStart+" "+
                                                       xEnd+","+yStart+" "+
                                                       xEnd+","+yEnd+" "+
                                                       xStart+","+yEnd}/>
                        })
                        .value();
                }.bind(this)))}
                {_.map(this.props.separators, function(timestamp) {
                    var x = Math.round((timestamp - start)*1000/length);
                    return <line key={x+"sep"} x1={x} y1="0" x2={x} y2={height} strokeWidth="0.5" stroke="#808080"/>;
                })}
            </svg>
        </div>
    }
});

export default OverviewLine;