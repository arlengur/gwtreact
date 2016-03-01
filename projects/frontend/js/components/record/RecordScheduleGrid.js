import React from 'react';
import Checkbox from '../common/Checkbox';
import Input from '../common/TextInputTypeahead';
import * as Actions from '../../actions/RecordScheduleActions';
import _ from 'lodash';
import Immutable from 'immutable';
import i18n from '../../constants/i18nConstants';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/RecordScheduleStore';
import TreeCommon from '../../components/crud/CrudTreesCommon';
const ls = AppUserSettingsStore.localizeStringCFL;

var _searchTimer = null;

const TaskTreeHead = React.createClass({
    render: function () {
        var tasks = this.props.tasks;
        return <div className="head-bg flex-none" style={{margin: '0 5px'}}>
            <div className="container-fluid override-padding-0 pull-left tree-head-width" style={{position: 'relative'}}>
                <div className="probe-head-text overflow-ellipsis pull-left">
                    <Checkbox id="taskTreeHeadId" className="pull-left" style={{lineHeight: '33px', margin: '0 5px'}}
                              checked={tasks.length > 0 && _.every(tasks, (t)=>this.props.selectedTasks.contains(t.entityKey))}
                              onChange={() => Actions.toggleAllTasks(_.chain(tasks).pluck('entityKey').value())}/>
                    <span style={{fontSize: '12px', color: '#6A97D5', lineHeight: '33px', paddingLeft: '5px'}}>
                        {ls(i18n.TASK)}
                    </span>
                </div>
                <div className="vertical-line" style={{borderColor: '#424242', left: '22px'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '44px'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '22px'}}></div>
            </div>
        </div>
    }
});

const RoundRobin = React.createClass({
    render: function () {
        if (this.props.roundRobin == 'enabled') {
            return <div className="rr-grid pull-right"></div>
        } else {
            return <div className="empty-grid pull-right"></div>
        }
    }
});

const ScheduleState = React.createClass({
    render: function () {
        var task = this.props.task;
        switch (task.scheduleState) {
            case 'SCHEDULED':
                return <div className="calendar-grid pull-right clickable"
                            title={ls(i18n.EDIT)}
                            onClick={() => Actions.editSchedule(task.agentKey, task.entityKey)}></div>;
            case 'CYCLIC':
                return <div className="cyclic-grid pull-right"></div>;
            case 'READY_TO_RUN':
                return <div className="passive-grid pull-right"></div>;
            default:
                return <div className="empty-grid pull-right"></div>
        }
    }
});

const TaskNode = React.createClass({
    render: function () {
        var task = this.props.task;
        return <li className="config-tree-li">
            <div className="container-fluid override-padding-0 overflow-ellipsis" id="tree">
                <Checkbox id={'task-' + task.entityKey} className="pull-left" style={{lineHeight: '23px', margin: '0 5px'}}
                          checked={this.props.selectedTasks.contains(task.entityKey)}
                          onChange={() => Actions.taskSelect(task.entityKey)}/>
                <RoundRobin roundRobin={task.roundRobin}/>
                <ScheduleState task={task}/>
                <span className="probe-tree-line-text">{TreeCommon.getHighlightedName(task.name + ' - ' + task.moduleName + ' (' + task.agentName + ')', this.props.filter)}</span>
            </div>
        </li>
    }
});

const TaskTree = React.createClass({
    render: function () {
        return <div className="probe-tree-background record-tree flex-11a" style={{position: 'relative', overflowY: 'auto'}}>
            <ul className="probe-tree-background override-margin-0 override-padding-0 tree-col-width" style={{position: 'relative'}}>
                {_.map(this.props.tasks, (task, id) => {
                    return <TaskNode key={id} task={task} selectedTasks={this.props.selectedTasks} filter={this.props.filter}/>
                })}
                <div className="vertical-line" style={{borderColor: '#424242', left: '22px'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '22px'}}></div>
                <div className="vertical-line" style={{borderColor: '#424242', right: '44px'}}></div>
            </ul>
            <div className="vertical-line" style={{borderColor: '#424242', left: '22px'}}></div>
            <div className="vertical-line" style={{borderColor: '#424242', right: '22px'}}></div>
            <div className="vertical-line" style={{borderColor: '#424242', right: '44px'}}></div>
        </div>
    }
});

const ProbeNode = React.createClass({
    render: function () {
        var probe = this.props.probe;
        return <li className="probe-tree-li">
            <div className="container-fluid override-padding-0">
                <div className="pull-left overflow-ellipsis" style={{width: '100%'}}>
                    <Checkbox id={'agent-' + probe.entityKey} className="pull-left" style={{lineHeight: '23px', margin: '0 5px'}}
                              checked={this.props.selectedProbes.contains(probe.entityKey)}
                              onChange={() => Actions.probeSelect(probe.entityKey)}/>
                    <div className="pull-left probe-simple"></div>
                    <span className="small-text probe-tree-line-text">{TreeCommon.getHighlightedName(probe.name, this.props.filter)}</span>
                </div>
            </div>
        </li>
    }
});

const ProbeTree = React.createClass({
    render: function () {
        return <div className="probe-tree-background record-tree flex-11a" style={{position: 'relative', overflowY: 'auto'}}>
            <ul className="probe-tree-background override-margin-0 override-padding-0" style={{position: 'relative'}}>
                {_.map(this.props.probes, (probe, id) => {
                    return <ProbeNode key={id} probe={probe} selectedProbes={this.props.selectedProbes} filter={this.props.filter}/>
                })}
            </ul>
        </div>
    }
});

var getDisplayedProbes = (probes, filt) => {
    return _.filter(probes, (p) => TreeCommon.matches(p.name, filt));
};
var getDisplayedTasks = (tasks, filt) => {
    return _.filter(tasks, (task) => TreeCommon.matches(task.name + ' - ' + task.moduleName + ' (' + task.agentName + ')', filt));
};

const RecordScheduleGrid = React.createClass({
    componentDidUpdate: function () {
        this.resizeHeadDelayed();
    },
    componentDidMount: function () {
        window.addEventListener('resize', this.resizeHeadDelayed);
    },
    componentWillUnmount: function () {
        window.removeEventListener('resize', this.resizeHeadDelayed);
    },
    resizeHeadDelayed: function () {
        setTimeout(this.resizeHead, 50);
    },
    resizeHead: function () {
        var tree = React.findDOMNode(this.refs.treeWidth);
        var treeElement = tree.getElementsByClassName('tree-col-width')[0];
        var head = React.findDOMNode(this.refs.headWidth);
        var headElement = head.getElementsByClassName('tree-head-width')[0];
        if (treeElement != null && headElement != null) {
            headElement.style.width = treeElement.getBoundingClientRect().width + 'px';
        }
    },
    updateSchedule: function (selectedProbes, tasks, selectedTasks, type) {
        var taskAgentMap = Immutable.Map(_.chain(tasks)
            .filter((task)=> {return selectedTasks.contains(task.entityKey)})
            .map(task=> [task.entityKey, task.agentKey])
            .value());
        Actions.updateSchedule(selectedProbes, JSON.stringify({type: type, taskAgentMap: taskAgentMap}));
    },
    updateProbeFilter: function (newFilter) {
        // Here we delay updating the tree if the expanded tree size is too big (> 100 elements)
        // We hope that the user is just in the middle of typing a more precise filter, and on next search field
        // update we will get a shorter list, which we will then display immediately
        if (_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if (newFilter == '') {
            Actions.updateProbeFilter(newFilter);
        } else {
            var probesVisible = getDisplayedProbes(this.props.store.get('agents'), newFilter);
            var nodesNum = probesVisible.length;
            if (nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    Actions.updateProbeFilter(newFilter);
                }, 500);
            } else {
                Actions.updateProbeFilter(newFilter);
            }
        }
    },
    updateTaskFilter: function (newFilter) {
        // Here we delay updating the tree if the expanded tree size is too big (> 100 elements)
        // We hope that the user is just in the middle of typing a more precise filter, and on next search field
        // update we will get a shorter list, which we will then display immediately
        if (_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if (newFilter == '') {
            Actions.updateTaskFilter(newFilter);
        } else {
            var probesVisible = getDisplayedTasks(this.props.store.get('tasks'), newFilter);
            var nodesNum = probesVisible.length;
            if (nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    Actions.updateTaskFilter(newFilter);
                }, 500);
            } else {
                Actions.updateTaskFilter(newFilter);
            }
        }
    },
    render: function () {
        var store = this.props.store;
        var displayedTasks = store.get('tasks');
        var taskFilter = store.get('taskFilter');
        if (taskFilter.length > 0) {
            displayedTasks = getDisplayedTasks(displayedTasks, taskFilter);
        }

        var selectedTasks = store.get('selectedTasks');
        var anyTasksSelected = !selectedTasks.isEmpty();
        var taskActionStyle = {cursor: anyTasksSelected ? 'pointer' : 'not-allowed'};

        var displayedProbes = store.get('agents');
        var probeFilter = store.get('probeFilter');
        if (probeFilter.length > 0) {
            displayedProbes = getDisplayedProbes(displayedProbes, probeFilter);
        }
        var multiTimeZone = store.get('multiTimeZone');

        return (<div className="flex flex-11a record-panel">
            <div className="col-sm-4 override-padding-0 flex flex-col record-black-right">
                <div className="flex-none" style={{margin: '5px 5px 0'}}>
                    <span className="small-text pull-left margin-left-5">
                        {ls(i18n.PROBES)}
                    </span>
                </div>
                <div className="record-tree-button-panel">
                    <Checkbox id="probeTreeHeadId"
                              className="pull-left"
                              style={{margin: '6px 0 0 1px'}}
                              checked={_.every(displayedProbes, (p)=>store.get('selectedProbes').contains(p.entityKey))}
                              onChange={() => Actions.toggleAllProbes(_.chain(displayedProbes).pluck('entityKey').value())}/>
                    <Input className="pull-right probe-tree-probe-search placeholder-dissappear"
                           style={{width: '100px'}}
                           placeholder={ls(i18n.SEARCH_TEXT)}
                           onChange={(v) => this.updateProbeFilter(v)}/>
                </div>
                <ProbeTree probes={displayedProbes} selectedProbes={store.get('selectedProbes')} filter={probeFilter}/>
            </div>
            <div className="col-sm-8 override-padding-0 flex flex-11a flex-col" style={{overflowY: 'auto'}}>
                <div className="flex-none" style={{margin: '5px 5px 0'}}>
                    <span className="small-text pull-left margin-left-5">
                        {ls(i18n.RECORDING_TASKS)}
                    </span>
                </div>
                <div className="record-tree-button-panel">
                    <div className="pull-left margin-0-5 calendar-btn"
                         style={taskActionStyle}
                         title={ls(i18n.CALENDAR)}
                         onClick={() => {if (anyTasksSelected) {Actions.createSchedule(multiTimeZone ? 'Probe' : store.get('localTimeZone'))}}}
                    ></div>
                    <div className="pull-left margin-0-5 cyclic-btn"
                         style={taskActionStyle}
                         title={ls(i18n.CYCLIC)}
                         onClick={() => {if (anyTasksSelected) {this.updateSchedule(store.get('selectedProbes'), displayedTasks, selectedTasks, 'CYCLIC')}}}
                    ></div>
                    <div className="pull-left margin-0-5 passive-btn"
                         style={taskActionStyle}
                         title={ls(i18n.PASSIVE)}
                         onClick={() => {if (anyTasksSelected) {this.updateSchedule(store.get('selectedProbes'), displayedTasks, selectedTasks, 'READY_TO_RUN')}}}
                    ></div>
                    <Input className="pull-right probe-tree-probe-search placeholder-dissappear"
                           style={{width: '100px'}}
                           placeholder={ls(i18n.SEARCH_TEXT)}
                           onChange={(v) => this.updateTaskFilter(v)}/>
                </div>
                <TaskTreeHead tasks={displayedTasks} selectedTasks={selectedTasks} ref="headWidth"/>
                <TaskTree tasks={displayedTasks} selectedTasks={selectedTasks} filter={taskFilter} ref="treeWidth"/>
            </div>
        </div>);
    }
});

export default RecordScheduleGrid;