import React from 'react';
import Row from 'react-bootstrap/Row';
import Col from 'react-bootstrap/Col';
import Immutable from 'immutable';
import _ from 'lodash';
import moment from 'moment';
import DetailsActions from '../../actions/DetailsActions';
import i18nConstants from '../../constants/i18nConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import DetailsStore from '../../stores/ChannelDetailsStore';
import Images from '../../util/Images';
import Misc from '../../util/Misc';
import Player from './VideoPlayer';

var severityUni = function (severity) {
    switch (severity) {
        case 'CRITICAL':
        case 'MAJOR':
            return "CRITICAL";
        case 'WARNING':
        case 'MINOR':
        case 'NOTICE':
        case 'INDETERMINATE':
            return "WARNING";
        default:
            return "";
    }
};

var filterAlerts = function (sourceId) {
    var alerts = getAlerts(sourceId);
    var disabledGroups = DetailsStore.getDisabledGroups();
    var disabledSeverities = DetailsStore.getDisabledSeverities();
    var disabledActivities = DetailsStore.getDisabledActivities();
    var interval = DetailsStore.getSelectedInterval();

    return _.chain(alerts)
        .filter((alert) => !disabledGroups.contains(alert.group))
        .filter((alert) => !disabledSeverities.contains(alert.severity))
        .filter((alert) => (alert.endDateTime == null  && !disabledActivities.contains('ACTIVE')) ||
                           (alert.endDateTime !== null && !disabledActivities.contains('NONACTIVE')))
        .filter((alert) =>  {
            // see http://c2.com/cgi/wiki?TestIfDateRangesOverlap
            var alertEnd = alert.endDateTime || Number.MAX_VALUE;
            return alertEnd >= interval.start && interval.end >= alert.startDateTime;
        })
        .sortBy((alert) => -alert.created) // desc sort
        .groupBy((alert) => alert.group)
        .map((alert) => _.uniq(alert, (item) => severityUni(item.severity)))
        .flatten()
        .value();
};

var getAlerts = function (sourceIds) {
    var alerts = [];
    var alertReports = DetailsStore.getAlertReports();
    alertReports.forEach(function (alertReport) {
        alertReport.alertsHistory.forEach(function (alertItem) {
            if (sourceIds.indexOf(alertItem.alert.sourceId) > -1) {
                var task = DetailsStore.getTaskById(alertItem.alert.sourceId);
                alerts.push({
                    id: alertItem.alertReportId,
                    group: alertReport.group,
                    description: alertItem.alert.description,
                    severity: alertItem.severity,
                    source: alertItem.alert.sourceId,
                    sourceName: alertItem.alert.sourceName,
                    originatorName: alertItem.alert.originatorName,
                    created: alertItem.alert.created,
                    updated: alertItem.alert.updated,
                    cause: alertItem.alert.cause,
                    acknowledged: alertItem.alert.acknowledged,
                    endDateTime: alertItem.endDateTime,
                    startDateTime: alertItem.startDateTime,
                    alertCount: alertItem.alert.alertCount,
                    duration: alertItem.alert.duration,
                    detectionValue: alertItem.alert.detectionValue,
                    thresholdValue: alertItem.alert.thresholdValue,
                    rtmpURL: task.streams.RTMP,
                    hlsURL: task.streams.HLS,
                    downloadURL: task.streams.DOWNLOAD,
                    recordedURL: task.streams.RECORDED,
                    streamId: task.streams.STREAM_ID,
                    relatedTask: task.relatedRecordingTaskId,
                    severityChanged: alertItem.alert.severityChanged,
                    settings: alertItem.alert.settings,
                    //report active only if alert is active and end_date is not set
                    active: alertItem.alert.active && (alertItem.endDateTime == null || alertItem.endDateTime == undefined),
                    alert: {parameterId: alertItem.alert.parameterId},
                    alertId: alertItem.alert.id
                });
            }
        });
    });
    return alerts;
};

var ProbeDetails = React.createClass({
    getInitialState: function () {
        return {
            filteredTabs: [],
            videoUrl: this.props.videoUrl,
            activeIndex: 0,
            channelId: -1,
            isLifeVideo: true,
            probeKey: undefined
        };
    },

    componentDidMount: function () {
        DetailsStore.addChangeListener(this._onChange);
    },

    componentWillUnmount: function () {
        DetailsStore.removeChangeListener(this._onChange);
    },

    _onChange: function () {
        var _probeKey = DetailsStore.getProbeKey();
        if (_probeKey != undefined) {
            var _tasksIds = DetailsStore.getTasksIds();
            var alerts = filterAlerts(_tasksIds);
            var _videoUrl = null;
            var channelId = DetailsStore.getChannelId();
            var streamId = null;
            var isLifeVideo = false;
            var currentTaskName=undefined;
            if(alerts.length > 0) {
                var isNarrowInterval=DetailsStore.getInterval().end!=DetailsStore.getSelectedInterval().end;
                currentTaskName=alerts[0].sourceName;
                if((alerts[0].endDateTime != undefined && alerts[0].endDateTime > 0) || isNarrowInterval) {
                    streamId = alerts[0].streamId;
                    _videoUrl = alerts[0].recordedURL;
                } else {
                    isLifeVideo = true;
                    _videoUrl = alerts[0].rtmpURL;
                }
            } else if (_tasksIds.length > 0) {
                if (_tasksIds[0] != undefined && _tasksIds[0] > 0) {
                    var currentTask = DetailsStore.getTaskById(_tasksIds[0]);
                    if (currentTask != undefined && currentTask != null) {
                        isLifeVideo = true;
                        streamId = currentTask.streams.STREAM_ID;
                        _videoUrl = currentTask.streams.RTMP;
                        currentTaskName = currentTask.name;
                    }
                }
            }
            this.setState({
                filteredTabs: alerts,
                videoUrl: _videoUrl,
                probeKey: _probeKey,
                activeIndex: 0,
                channelId: channelId,
                streamId: streamId,
                isLifeVideo: isLifeVideo,
                currentTaskName: currentTaskName,
                tasksIds: _tasksIds,
                interval: DetailsStore.getInterval(),
                alerts: alerts
            });
        }
    },

    handleActiveCallback: function (index) {
        var _videoUrl = null;
        var isLifeVideo = false;
        var _curTaskName = '';
        if (this.state.filteredTabs.length > 0) {
            _curTaskName = this.state.filteredTabs[index].sourceName;

            if (this.state.filteredTabs[index].endDateTime != undefined && this.state.filteredTabs[index].endDateTime > 0) {
                _videoUrl = this.state.filteredTabs[index].recordedURL;
            } else {
                isLifeVideo = true;
                _videoUrl = this.state.filteredTabs[index].rtmpURL;
            }
        }
        this.setState({
            activeIndex: index,
            videoUrl: _videoUrl,
            isLifeVideo: isLifeVideo,
            currentTaskName: _curTaskName
        })
    },

    render: function () {
        var title = this.state.currentTaskName == undefined ? DetailsStore.getChannelName() : this.state.currentTaskName;
        return <Col xs={12} md={4} className="col-xlg-6 override-padding-0">
            <Col xs={12} className="col-xlg-6 override-padding-0">
                <div style={{marginLeft: '10px'}}>
                    <div className="probes-title">
                        <LiveVideoIndicator isLive={this.state.isLifeVideo} video={this.state.videoUrl}/>
                        <span className="title overflow-ellipsis">{title}</span>
                    </div>
                    <Player url={this.state.videoUrl}
                            channelId={this.state.streamId}
                            interval={DetailsStore.getSelectedInterval()}
                            lifeVideo={this.state.isLifeVideo}/>;
                </div>
            </Col>
            <Col xs={12} className="col-xlg-6 override-padding-0">
                <div style={{marginLeft: '10px'}}>
                    <TabsSwitcher items={this.state.filteredTabs} active={this.state.activeIndex} activeCallback={this.handleActiveCallback}/>
                    <TabsContent items={this.state.filteredTabs} active={this.state.activeIndex} tasksIds={this.state.tasksIds} interval={this.state.interval} alerts={this.state.alerts} probeKey={this.state.probeKey}/>
                </div>
            </Col>
        </Col>;
    }
});

var LiveVideoIndicator = React.createClass({
    render: function () {
        if (this.props.video == undefined || this.props.video == null) {
            return null;
        }

        var title = this.props.isLive ?
            AppUserStore.localizeString(i18nConstants.LIVE_VIDEO)
            : AppUserStore.localizeString(i18nConstants.RECORDED_VIDEO);
        var source = Images.liveRecordedVideo(this.props.isLive);

        return (
            <span className="video-indicator pull-right">
                <span className="live-video">
                    <img src={source}/>
                </span>
                <span className="live-title">{title}</span>
            </span>
        );
    }
});

var TabsSwitcher = React.createClass({
    handleChange: function (index) {
        this.props.activeCallback(index)
    },

    render: function () {
        var active = this.props.active;
        var tabs = this.props.items.map(function (item, index) {
            var href = '#' + item.id;
            var classActive = active === index ? 'active' : '';
            var styleName = {backgroundImage: Images.alertTabBg(item.group, item.severity)};

            return (<li key={index} onClick={this.handleChange.bind(this, index)} className={classActive}>
                <a href={href} style={styleName} data-toggle="tab"><div dangerouslySetInnerHTML={{__html: '&nbsp;'}}/></a>
            </li>);
        }.bind(this));
        if (this.props.items.length > 0) {
            return <ul className="nav nav-tabs" id="myTab">
            {tabs}
            </ul>;
        } else {
            return null;
        }
    }
});

var formatDate = function (date) {
    return moment(date).format('MMM Do, YYYY, hh:mm:ss a');
};

var TabsContent = React.createClass({
    getSeverityRowContent: function (severity) {
        return <div className="severity-row-content">
            <div className={"severity-indicator " + severity.toLowerCase()}/>{severity}</div>
    },
    render: function () {
        var item = this.props.items[this.props.active];
        if (item != undefined) {
            var severity = [<DetailsRow title={AppUserStore.localizeString(i18nConstants.SEVERITY)} content={this.getSeverityRowContent(item.severity)}/>];
            var timeDetails = item.active ?
                [<DetailsRow title={AppUserStore.localizeString(i18nConstants.LAST_UPDATED_AT)} content={formatDate(item.updated)}/>,
                 <DetailsRow title={AppUserStore.localizeString(i18nConstants.SEVERITY_CHANGED_AT)} content={formatDate(item.severityChanged)}/>,
                 <DetailsRow title={AppUserStore.localizeString(i18nConstants.CREATED_AT)} content={formatDate(item.created)}/>]
                :
                [<DetailsRow title={AppUserStore.localizeString(i18nConstants.START_DATE)} content={formatDate(item.startDateTime)}/>,
                 <DetailsRow title={AppUserStore.localizeString(i18nConstants.END_DATE)} content={formatDate(item.endDateTime)}/>];
            var commonDetails = [
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.PROBABLE_CAUSE)} content={item.cause}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.THRESHOLD_VALUE)} content={item.thresholdValue}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.DETECTION_VALUE)} content={item.detectionValue}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.ACKNOWLEDGED)} content={(item.acknowledged == undefined || item.acknowledged == '' || !item.acknowledged) ? 'No' : 'Yes'}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.COUNT)} content={item.alertCount}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.DURATION)} content={item.duration}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.SOURCE)} content={item.sourceName}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.ORIGINATOR)} content={item.originatorName}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.ACTIVE)} content={(item.active == undefined || item.active == '' || item.active != true ) ? 'No' : "Yes"}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.PARAMETERS)} content={item.settings}/>,
                <DetailsRow title={AppUserStore.localizeString(i18nConstants.DESCRIPTION) + ':'} content={item.description}/>];
            return (
                <div className="tab-wrapper">
                    <div className="alert-actions">
                        <div className="alert-action acc"
                             title={AppUserStore.localizeString(i18nConstants.ACKNOWLEDGE)}
                             onClick={()=>{
                                 DetailsActions.openCommentPopup(
                                     i18nConstants.ACKNOWLEDGE,
                                     item.id,
                                     item.alertId);
                             }}/>
                        <div className="alert-action not_acc"
                             title={AppUserStore.localizeString(i18nConstants.UNACKNOWLEDGE)}
                             onClick={()=>{
                                 DetailsActions.openCommentPopup(
                                     i18nConstants.UNACKNOWLEDGE,
                                     item.id,
                                     item.alertId);
                             }}/>
                        <div className="alert-action del"
                             title={AppUserStore.localizeString(i18nConstants.CLEAR)}
                             onClick={()=>{
                                 DetailsActions.openCommentPopup(
                                     i18nConstants.CLEAR,
                                     item.id,
                                     item.alertId);
                             }}/>
                        <div className="delimiter"/>
                        <div className="alert-action comment"
                             title={AppUserStore.localizeString(i18nConstants.COMMENT)}
                             onClick={()=>{
                                 DetailsActions.openCommentPopup(
                                     i18nConstants.COMMENT,
                                     item.id,
                                     item.alertId);
                             }}/>
                        <div className="delimiter"/>
                        <div className="alert-action source"
                             title={AppUserStore.localizeString(i18nConstants.GO_TO_SOURCE)}
                             onClick={function() {
                                 DetailsActions.goSource(this.props.probeKey);
                             }.bind(this)}/>
                        <div className="delimiter"/>
                        <div className="alert-action graph"
                             title={AppUserStore.localizeString(i18nConstants.ANALYSIS)}
                             onClick={function() {
                                 DetailsActions.goTaskAnalysis(item.source, item.alert.parameterId, this.props.interval);
                             }.bind(this)}/>
                        <div className="alert-action table"
                             title={AppUserStore.localizeString(i18nConstants.REPORT)}
                             onClick={function() {
                                 DetailsActions.goReports([item.source], this.props.interval);
                             }.bind(this)}/>
                        <div className="delimiter"/>
                    </div>
                    <div className="tab-content">
                        <div key={item.id} className='tab-pane active' id={item.id}>
                            <div className="container-fluid">
                        {[].concat(severity, timeDetails, commonDetails)}
                            </div>
                        </div>
                    </div>
                </div>);
        } else {
            return null;
        }
    }
});

var DetailsRow = React.createClass({
    render: function () {
        var spanStyle = {color: '#484040'};
        return <Row>
            <Col xs={4} className='override-padding-0'>
                <div className='overflow-hidden'>
                    {this.props.title + " "}
                    <span style={spanStyle}>
                    {_.repeat('.', 200)}
                    </span>
                </div>
            </Col>
            <Col xs={8} className='override-padding-0'>
                {this.props.content}
            </Col>
        </Row>
    }
});

export default ProbeDetails;