import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import _ from 'lodash';
import {severityToNum, severityTo3Colors, severityToLineY} from '../../util/Alerts';

var Timeline = React.createClass({

    normalize: function(x, start, end) {
        if(x == null) {return 100;}
        else if(x < start) {return 0;}
        else if(x > end) {return 100;}
        else {return Math.round((x - start)*100/(end-start));}
    },

    renderReport: function(report) {
        var key = "" + report.severity + report.alertReportId + report.startDateTime + report.endDateTime;
        var color = severityTo3Colors(report.severity);
        var height = severityToLineY(report.severity);
        var intEnd = this.props.endDate;
        var intStart = this.props.startDate;
        var x_start = this.normalize(report["startDateTime"], intStart, intEnd);
        var x_end = this.normalize(report["endDateTime"], intStart, intEnd);
        if(x_end == x_start) {
            if(x_start != 100) {
                x_end = x_start + 1;
            } else {
                x_start = x_end - 1;
            }
        }
        var points = x_start+",5 "+x_end+",5 "+x_end+","+height+" "+x_start+","+height;
        return <polygon key={key} fill={color} strokeWidth="0" points={points}></polygon>
    },

    render: function() {
        var sorted = _.sortBy(this.props.reports, function(report){
            return severityToNum(report.severity)
        });
        return <Row key={this.props.key}>
            <div className="container-fluid override-padding-0">
                <img src={this.props.img} className="pull-left"/>
                <div className="timeline-wrapper">
                    <svg width="100%" height="30px" viewBox="0 0 100 5" preserveAspectRatio="none">
                        <polygon fill="#00c200" strokeWidth="0" points="0,5 100,5 100,4 0,4"></polygon>
                        {_.map(sorted, this.renderReport)}
                    </svg>
                </div>
            </div>
        </Row>
    }
});

export default Timeline;