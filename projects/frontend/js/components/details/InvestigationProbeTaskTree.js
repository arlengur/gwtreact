import React from 'react';
import {addons} from 'react/addons';
const {update} = addons;
import _ from 'lodash';
import Col from 'react-bootstrap/Col';
import Immutable from 'immutable';
import Images from '../../util/Images';
import DetailsActions from '../../actions/DetailsActions';
import i18nConstants from '../../constants/i18nConstants';
import DetailsStore from '../../stores/ChannelDetailsStore';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Input from '../../components/common/TextInputTypeahead';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import ProbeTaskTree from './ProbeTaskTree';

var InvestigationProbeTaskTree = React.createClass({
    getInitialState: function() {
        return {
            showAll: false,
            filter: ''
        };
    },
    render: function() {
        var alertsByTask = Immutable.Map(
            _.chain(this.props.displayedAlerts)
            // save alert group name inside alert
            .map(function(group) {
                    return update(group,
                        {alertsHistory:
                            {$apply:  function(alerts) {
                                return _.map(alerts, function(alert) {
                                    return update(alert, {$merge: {group: group.group}})
                            })
                        }}}
                    )
                })
            .pluck('alertsHistory')
            .flatten()
            .groupBy(function(report) {return report.alert.sourceId})
            // We need the following step, because JS objects translate Int keys to String keys
            .map(function(v,k) {return [parseInt(k), v]})
            .value());
        var displayedProbes;
        var tasksWithAlerts = Immutable.Set(alertsByTask.keys());
        if(this.state.showAll) {
            displayedProbes = this.props.probes;
        } else {
            displayedProbes = _.chain(this.props.probes)
                .map(function(probe) {
                    return update(probe,
                        {tasks: {$apply: function(tasks) {
                            return _.filter(tasks,
                                function(t) {return tasksWithAlerts.contains(t.id)})
                        }}})})
                .filter(function(p){return p.tasks.length > 0})
                .value();
        }
        if(this.state.filter.length > 0) {
            displayedProbes = TreeCommon.getDisplayedProbes(displayedProbes, this.state.filter);
            displayedProbes = _.map(displayedProbes, function(probe) {
                return update(probe, {tasks: {$apply: function() {
                    return TreeCommon.getDisplayedTasks(probe, this.state.filter)
                }.bind(this)}})
            }.bind(this));
        }
        displayedProbes = _.chain(displayedProbes)
            .sortBy('name')
            .sortBy(function(probe) {
                if(tasksWithAlerts.intersect(_.pluck(probe.tasks, 'id')).size > 0) {
                    return 0
                } else {
                    return 1
                }
            })
            .map(function(probe) {
                return update(probe,
                    {tasks: {$apply: function(tasks) {
                        return _.chain(tasks)
                            .sortBy('name')
                            .sortBy(function(task) {
                                if(tasksWithAlerts.contains(task.id)) {
                                    return 0
                                } else {
                                    return 1
                                }
                            })
                            .value()
                    }}})
            })
            .value();
        var displayedTaskIds = Immutable.Set(
            _.chain(displayedProbes)
                .pluck('tasks')
                .flatten()
                .pluck('id')
                .value());
        var allVisibleTasksSelected = _.every(displayedTaskIds.toJS(),
            function(taskId) {
                 return this.props.selectedTasks.contains(taskId);
            }.bind(this));
        var taskToVideoTask = DetailsStore.getTaskToRecordingTask();
        var selectedVideo = _.chain(this.props.selectedTasks.toJS())
            .map(function(id) {return taskToVideoTask.get(id)})
            .uniq()
            .filter(function(id) {return typeof id != 'undefined' && id != null})
            .value();
        var selectedTasksWithAlerts = _.filter(this.props.selectedTasks.toJS(),
            function(id) {
                var alerts = alertsByTask.get(id);
                return typeof alerts != 'undefined' && alerts != null;
            });
        var videoNavigationStyle = selectedVideo.length > 0 ? "clickable" : "transparent";
        var alertsNavigationStyle = selectedTasksWithAlerts.length > 0 ? "clickable" : "transparent";
        return (
            <Col xs={12} md={8} className="col-xlg-6 override-padding-0 details-tree-panel">
                <div className="probes-find">
                    <input className="probes-find-check" type="checkbox"
                           title={AppUserStore.localizeString(i18nConstants.SELECT_ALL)}
                           checked={allVisibleTasksSelected}
                           onChange={function() {
                                if(allVisibleTasksSelected) {
                                    DetailsActions.treeUnselectAll();
                                } else {
                                    DetailsActions.selectTasks(displayedTaskIds.toJS());
                                }}.bind(this)}/>
                    <img src="img/detailed/GoToRecorded.png"
                         title={AppUserStore.localizeString(i18nConstants.OPEN_RECORDED_VIDEO)}
                         className={videoNavigationStyle}
                         onClick={function() {
                            if(selectedVideo.length > 0) {
                                DetailsActions.goRecordedVideo(selectedVideo, this.props.interval);
                            }
                         }.bind(this)}/>
                    <img src="img/detailed/GoBroadcast.png"
                         title={AppUserStore.localizeString(i18nConstants.OPEN_LIVE_VIDEO)}
                         className={videoNavigationStyle}
                         onClick={function() {
                            if(selectedVideo.length> 0) {
                                DetailsActions.goLiveVideo(selectedVideo, this.props.interval);
                            }
                         }.bind(this)}/>
                    <img src="img/detailed/GoAnalysis.png"
                         title={AppUserStore.localizeString(i18nConstants.BUILD_ANALYSIS_CHART)}
                         className={alertsNavigationStyle}
                         onClick={function() {
                            if(selectedTasksWithAlerts.length > 0) {
                                DetailsActions.goAnalysis(selectedTasksWithAlerts, alertsByTask, this.props.interval);
                            }
                         }.bind(this)}/>
                    <img src="img/detailed/GoReports.png"
                         title={AppUserStore.localizeString(i18nConstants.BUILD_REPORT)}
                         className={alertsNavigationStyle}
                         onClick={function() {
                            if(selectedTasksWithAlerts.length > 0) {
                                DetailsActions.goReports(selectedTasksWithAlerts, this.props.interval);
                            }
                         }.bind(this)}/>
                    <img src={Images.getShowAllTasksImage(this.state.showAll)}
                              className="clickable"
                              title={AppUserStore.localizeString(i18nConstants.SHOW_ALL_TASKS)}
                              onClick={function() {this.setState({showAll: !this.state.showAll})
                              }.bind(this)} />
                    <span title={AppUserStore.localizeString(i18nConstants.FILTERED_PROBES_TOTAL_PROBES)}
                          className="visible_probes">
                        <span style={{color: "#E8E8E8"}}>{displayedProbes.length + '/'}</span>{this.props.probes.length}
                    </span>
                    <Input className="pull-right crud-tree-probe-search placeholder-disappear"
                           placeholder={AppUserStore.localizeString(i18nConstants.SEARCH_TEXT)}
                           onChange={function(val) {this.setState({filter: val})}.bind(this)}/>
                </div>
                <ProbeTaskTree probes={displayedProbes}
                    selectedTasks={this.props.selectedTasks}
                    alerts={alertsByTask}
                    selectionId={this.props.selectionId}
                    filter={this.state.filter}/>
            </Col>
        );
    }
});

export default InvestigationProbeTaskTree;