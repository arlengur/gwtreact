import React from 'react';
import _ from 'lodash';
import Immutable from 'immutable';
import DetailsActions from '../../actions/DetailsActions';
import AppConstants from '../../constants/AppConstants';
import i18nConstants from '../../constants/i18nConstants';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import AppDispatcher from '../../dispatcher/AppDispatcher';
import AppUserStore from '../../stores/AppUserSettingsStore';
import {groupToNum, severityToNum} from '../../util/Alerts';
import Images from '../../util/Images';
import Misc from '../../util/Misc';

var TreeActions = {
    treeClick: function (probeEntityKey, tasksIds, selectionId) {
        AppDispatcher.dispatch({
            actionType: AppConstants.TREE_CLICK,
            probeKey: probeEntityKey,
            tasksIds: tasksIds,
            selectionId: selectionId
        })
    }
};

var getAlertTreeImages = function(alerts) {
    if(typeof alerts == 'undefined' || alerts.length == 0) {
        return []
    } else {
        return _.chain(alerts)
            .groupBy(function (alert) {
                return alert.endDateTime == null;
            })
            // For every status (active/inactive) and every group we find max severity
            .map(function (alerts) {
                return _.chain(alerts)
                    .groupBy('group')
                    .map(function (alerts) {
                        return _.max(alerts, function (alert) {
                            return severityToNum(alert.severity)
                        })
                    })
                    .flatten()
                    .value()
            })
            .flatten()
            .sortBy(function (alert) {
                return groupToNum(alert.group)
            })
            .sortBy(function (alert) {
                return alert.endDateTime == null ? -1 : 1
            })
            .map(function (alert) {
                var transparency = alert.endDateTime == null ? "" : " transparent";
                var alertTreeTitle = Images.alertTreeIconPrefix(alert.group) + AppUserStore.localizeString(i18nConstants.ERRORS);
                return <img key={alert.group + transparency}
                            title={alertTreeTitle}
                            src={Images.alertTreeIcon(alert.group, alert.severity)}
                            className={"pull-left tree-group-icon" + transparency}/>
            })
            .value()
    }
};

var TaskNode = React.createClass({
    render: function () {
        var hasVideo = typeof this.props.task.streams.STREAM_ID != 'undefined';
        var selectedClass = this.props.treeSelected ? " details-tree-selected" : "";
        return <li className={"details-tree-task" + selectedClass}
                   onClick={function() {
                      TreeActions.treeClick(this.props.probeEntityKey, [this.props.task.id], this.props.task.id);
                   }.bind(this)}>
            <div className="container-fluid override-padding-0-5">
                <img src={Images.treeLine(this.props.last)} className="pull-left"/>
                <input type="checkbox" className="pull-left" style={{marginLeft: '5px', marginRight: '3px'}}
                    checked={this.props.checkboxSelected}
                    onChange={function() {
                        if(this.props.checkboxSelected) {
                            DetailsActions.unselectTasks([this.props.task.id]);
                        } else {
                            DetailsActions.selectTasks([this.props.task.id]);
                        }
                    }.bind(this)}
                    onClick={function(event) {event.stopPropagation()}}/>
                <div className={"pull-left " + Images.taskGroup(this.props.task.parameterGroup)}/>
                <div className="tree-play-icon-section pull-right">
                    {(hasVideo && this.props.treeSelected) ?
                        <img src="img/detailed/tree/video_playing.png" style={{marginLeft: '8px'}}/>
                        : ""}
                </div>
                <div className="tree-video-icon-section pull-right">
                    {hasVideo ?
                        <img src="img/detailed/tree/video_available.png" style={{marginLeft: '15px'}}/>
                        : ""}
                </div>
                <div className="tree-alerts-section pull-right">
                    {getAlertTreeImages(this.props.alerts)}
                </div>
                <div className="small-text overflow-ellipsis crud-tree-text">
                    {TreeCommon.getHighlightedName(this.props.task.name, this.props.filter)}
                </div>
            </div>
        </li>
    }
});

var ProbeNode = React.createClass({
    render: function () {
        var probe = this.props.probe;
        var taskNum = probe.tasks.length;
        var taskIds = _.pluck(probe.tasks, 'id');
        var probeAlerts = _.chain(taskIds)
            .map(function(id) {return this.props.alerts.get(id)}.bind(this))
            .filter(function(alerts) {return typeof alerts != 'undefined'})
            .flatten()
            .value();
        var hasVideo = _.some(probe.tasks,
            function(task) {
                return typeof task.streams.STREAM_ID != 'undefined';
            });
        var selected = probe.id == this.props.selectionId;
        var selectedClass = selected ? " details-tree-selected" : "";
        var allTasksChecked = _.every(taskIds,
            function(id) {return this.props.selectedTasks.contains(id)}.bind(this));
        return (
            <li className="details-tree-probe">
                <div className="row-content-wrapper"
                     onClick={function() {
                          TreeActions.treeClick(probe.entityKey, taskIds, probe.id);
                     }.bind(this)}>
                    <div className={"container-fluid override-padding-0-5" + selectedClass}>
                        <input type="checkbox" className="pull-left" style={{marginRight: '10px'}}
                               checked={allTasksChecked}
                               onChange={function() {
                                   if(allTasksChecked) {
                                       DetailsActions.unselectTasks(taskIds);
                                   } else {
                                       DetailsActions.selectTasks(taskIds);
                                   }
                               }.bind(this)}
                               onClick={function(event) {event.stopPropagation()}}/>
                        <img src={Images.detailsProbeExpand(this.props.expanded)}
                             className="pull-left crud-tree-image-left"
                             onClick={function(event) {
                                event.stopPropagation();
                                this.props.nodeClick(probe.id);
                             }.bind(this)}/>
                        <img src="img/tree_probe_23_no_line.png" className="pull-left crud-tree-image-left"/>
                        <div className="tree-play-icon-section pull-right">
                            {(hasVideo && selected) ?
                                <img src="img/detailed/tree/video_playing.png" style={{marginLeft: '8px'}}/>
                                : ""}
                        </div>
                        <div className="tree-video-icon-section pull-right">
                            {hasVideo ?
                                <img src="img/detailed/tree/video_available.png" style={{marginLeft: '15px'}}/>
                                : ""}
                        </div>
                        <div className="tree-alerts-section pull-right">
                            {getAlertTreeImages(probeAlerts)}
                        </div>
                        <div className="small-text overflow-ellipsis crud-tree-text">
                            {TreeCommon.getHighlightedName(probe.name, this.props.filter)}
                        </div>
                    </div>
                </div>
                <ul className="override-margin-0 override-padding-0">
                {this.props.expanded ?
                    _.map(probe.tasks,
                        function(task, ix) {
                            return <TaskNode
                                key={task.id}
                                task={task}
                                probeEntityKey={probe.entityKey}
                                checkboxSelected={this.props.selectedTasks.contains(task.id)}
                                alerts={this.props.alerts.get(task.id)}
                                treeSelected={this.props.selectionId == task.id}
                                filter={this.props.filter}
                                last={ix==taskNum-1}/>
                        }.bind(this))
                    : ""}
                </ul>
            </li>
        );
    }
});

var ProbeTaskTree = React.createClass({
    getInitialState: function() {
        return {
            collapsed: Immutable.Set()
        }
    },

    nodeClick: function(probeId) {
        this.setState({
            collapsed: Misc.toggle(this.state.collapsed, probeId)
        });
    },

    render: function () {
        return <div className="details-tree details-tree-background">
                <ul className="details-tree-background override-margin-0 override-padding-0">
                    {_.map(this.props.probes, function (probe) {
                       return <ProbeNode
                           key={probe.id}
                           probe={probe}
                           selectedTasks={this.props.selectedTasks}
                           alerts={this.props.alerts}
                           selectionId={this.props.selectionId}
                           expanded={!this.state.collapsed.contains(probe.id)}
                           nodeClick={this.nodeClick}
                           filter={this.props.filter}/>
                   }.bind(this))}
                </ul>
            </div>;
    }
});

export default ProbeTaskTree;