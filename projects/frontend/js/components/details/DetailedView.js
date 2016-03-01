import React from 'react';
import {addons} from 'react/addons';
const {update} = addons;
import Immutable from 'immutable';
import _ from 'lodash';
import GroupFilter from './GroupFilter';
import SeverityFilter from './SeverityFilter';
import ActivityFilter from './ActivityFilter';
import Tree from './InvestigationProbeTaskTree';
import ProbeDetails from './ProbeDetails';
import DetailedTimelines from './DetailedTimeline';
import CommentPopup from './CommentPopup';
import NavMenu from '../../components/common/NavMenu';
import ChannelCrud from '../../components/crud/ChannelCrud';
import LoadingProgressBar from '../../components/common/LoadingProgressBar';
import DetailsStore from '../../stores/ChannelDetailsStore';
import AppUserStore from '../../stores/AppUserSettingsStore';
import DetailsActions from '../../actions/DetailsActions';
import CrudActions from '../../actions/ChannelCrudActions';
import {ALL_GROUPS, severityToNum, filterAlerts, filterAlertsByInterval} from '../../util/Alerts';
import i18nConstants from 'constants/i18nConstants';
import {History} from 'react-router';

var DetailedView = React.createClass({
    mixins: [History],

    getInitialState: function () {
        return {
            details: DetailsStore.getDetails(),
            alerts: DetailsStore.getAlertReports(),
            disabledGroups: DetailsStore.getDisabledGroups(),
            disabledSeverities: DetailsStore.getDisabledSeverities(),
            disabledActivities: DetailsStore.getDisabledActivities(),
            interval: DetailsStore.getInterval(),
            selectedInterval: DetailsStore.getSelectedInterval(),
            title: DetailsStore.getChannelName(),
            probes: DetailsStore.getProbes(),
            taskToProbe: DetailsStore.getTaskToProbe(),
            selectedTasks: DetailsStore.getSelectedTasks(),
            selectionId: DetailsStore.getSelectionId(),
            dataLoaded: DetailsStore.isDataLoaded()
        };
    },
    componentDidMount: function () {
        DetailsStore.addChangeListener(this._onChange);
        DetailsActions.clearDataLoadIndicator();
        DetailsActions.loadChannelData(this.props.params.channelId);
    },
    componentWillUnmount: function () {
        DetailsStore.removeChangeListener(this._onChange);
    },
    _onChange: function () {
        this.setState({
            details: DetailsStore.getDetails(),
            alerts: DetailsStore.getAlertReports(),
            disabledGroups: DetailsStore.getDisabledGroups(),
            disabledSeverities: DetailsStore.getDisabledSeverities(),
            disabledActivities: DetailsStore.getDisabledActivities(),
            interval: DetailsStore.getInterval(),
            selectedInterval: DetailsStore.getSelectedInterval(),
            title: DetailsStore.getChannelName(),
            probes: DetailsStore.getProbes(),
            taskToProbe: DetailsStore.getTaskToProbe(),
            selectedTasks: DetailsStore.getSelectedTasks(),
            selectionId: DetailsStore.getSelectionId(),
            dataLoaded: DetailsStore.isDataLoaded()
        });
    },

    render: function () {
        var availableGroups = Immutable.Set(this.state.details.groups);
        var maxSeverities = Immutable.Map(
            _.map(ALL_GROUPS, function (grp) {
                if (!availableGroups.contains(grp)) {
                    return [grp, undefined];
                }
                var severities = _.chain(this.state.alerts)
                    .filter('group', grp)
                    .pluck('alertsHistory')
                    .flatten()
                    .filter(function(alert) {return alert.alert.active && alert.endDateTime == null})
                    .pluck('severity')
                    .uniq()
                    .value();
                if (severities.length == 0) {
                    return [grp, 'NONE'];
                } else {
                    return [grp, _.max(severities, severityToNum)];
                }
            }.bind(this))
        );
        var displayedAlerts = filterAlerts(this.state.alerts, this.state.disabledGroups,
                                           this.state.disabledSeverities, this.state.disabledActivities);
        var alertsInInterval = displayedAlerts.map(function(group){
            return update(group, {alertsHistory:
                {$apply: function(alerts) {
                    return filterAlertsByInterval(alerts, this.state.selectedInterval);
                }.bind(this)}})
        }.bind(this));
        var pageTitle = AppUserStore.localizeString(i18nConstants.MULTIPLE_CHANNEL_VIEW_TITLE);
        if(this.state.title !== undefined){
            pageTitle = this.state.title + ' - ' + AppUserStore.localizeString(i18nConstants.CHANNEL_DETAILS_TITLE);
        }
        var alertsTotalNum = _.chain(this.state.alerts)
            .pluck('alertsHistory')
            .flatten()
            .value()
            .length;
        var alertsDisplayedNum = _.chain(alertsInInterval)
            .pluck('alertsHistory')
            .flatten()
            .value()
            .length;
        return (
            <div className="detailed-view container-fluid">
                <NavMenu className="row" title={pageTitle}/>
                <div className="row button-toolbar">
                    <button type="button" className="btn btn-primary"
                            onClick={() => this.history.pushState(null, '/overview')}>
                        <span className="glyphicon glyphicon-chevron-left"></span>
                        {' ' + AppUserStore.localizeString(i18nConstants.BACK_BUTTON)}
                    </button>
                    <button type="button" className="pull-right btn btn-primary"
                        onClick={()=> {
                            // TODO: confirm delete, make a separate action
                            CrudActions.removeChannel(DetailsStore.getChannelId());
                            this.history.pushState(null, '/overview');}}>
                        <span className="glyphicon glyphicon-remove"></span>
                        {' ' + AppUserStore.localizeString(i18nConstants.REMOVE_BUTTON)}
                    </button>
                    <button type="button" className="pull-right btn btn-primary" style={{marginRight: "5px"}}
                        onClick={function(){
                            CrudActions.getChannelConfig(DetailsStore.getChannelId())}}>
                        <span className="glyphicon glyphicon-cog"></span>
                        {' ' + AppUserStore.localizeString(i18nConstants.EDIT_BUTTON)}
                    </button>
                </div>
                <div className="row filter-toolbar">
                    <div className="pull-left container-fluid">
                        <GroupFilter available={availableGroups}
                            disabled={this.state.disabledGroups}
                            maxSeverities={maxSeverities}/>
                    </div>
                    <div className="pull-left container-fluid">
                        <SeverityFilter disabled={this.state.disabledSeverities}/>
                    </div>
                    <div className="pull-left container-fluid">
                        <ActivityFilter disabled={this.state.disabledActivities}/>
                    </div>
                    <div className="pull-right container-fluid">
                        <div className="visible_alerts">
                            <span style={{color: "#E8E8E8"}}>{alertsDisplayedNum}/</span>{alertsTotalNum}
                        </div>
                    </div>
                </div>
                <div className="row graph-toolbar">
                    <DetailedTimelines
                        displayedAlerts={displayedAlerts}
                        maxSeverities={maxSeverities}
                        interval={this.state.interval}
                        selectedInterval={this.state.selectedInterval}/>
                </div>
                <div className="row tree-toolbar">
                    <Tree probes={this.state.probes}
                        taskToProbe={this.state.taskToProbe}
                        selectedTasks={this.state.selectedTasks}
                        displayedAlerts={alertsInInterval}
                        interval={this.state.selectedInterval}
                        selectionId={this.state.selectionId}/>
                    <ProbeDetails videoUrl={this.props.location.url}/>
                    <CommentPopup/>
                </div>
                <ChannelCrud/>
                <LoadingProgressBar dataLoaded={this.state.dataLoaded}/>
            </div>

        );
    }
});

export default DetailedView;