import React from 'react';
import {addons} from 'react/addons';
const {update} = addons;
import _ from 'lodash';
import Immutable from 'immutable';
import Store from '../../stores/ProbeConfigStore';
import Actions from '../../actions/ProbeConfigActions';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import i18n from '../../constants/i18nConstants';
import NavMenu from '../../components/common/NavMenu';
import Misc from '../../util/Misc';
import Input from '../../components/common/TextInputTypeahead';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import Tree from './ProbesAndTasksTree';
import ConfirmText from './ProbeActionConfirmText';
import ConfigEditor from './ConfigEditor';
import {groupToNum} from '../../util/Alerts';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import SwUpdateModal from './SwUpdateModal';
import ErrorDialog from '../../components/common/ErrorDialog';

var _searchTimer = null;
const MIN_COL_SIZE = 8;

var probeStatusOrder = (status) => {
    switch(status) {
        case 'SUCCESS':         return 0;
        case 'IN_PROGRESS':     return 1;
        case 'CONFIG_ROLLBACK': return 2;
        case 'UPDATE_CONFIG':   return 3;
        case 'RESTART_SW':      return 4;
        case 'RESTART_HW':      return 5;
        case 'UPDATE_SOFTWARE': return 6;
        default: return 99;
    }
};

var getDisplayedTasks = (p, filt) => {
    if (TreeCommon.matches(p.component.displayName, filt) ||
        TreeCommon.matches(p.agentVersion, filt) ||
        TreeCommon.matches(p.component.key, filt)) {
        return p.tasksStatistic;
    } else {
        return _.filter(p.tasksStatistic, (t) =>
            TreeCommon.matches(t.displayName, filt) ||
            TreeCommon.matches(t.key, filt));
    }
};

var getDisplayedProbes = (probes, filter) =>
    _.filter(probes, (p) => getDisplayedTasks(p, filter).length > 0);

var ProbeActionsToolbar = React.createClass({
    updateFilter: function(newFilter) {
        // Here we delay updating the tree if the expanded tree size is too big (> 100 elements)
        // We hope that the user is just in the middle of typing a more precise filter, and on next search field
        // update we will get a shorter list, which we will then display immediately
        if(_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if(newFilter =='') {
            Actions.updateFilter(newFilter, Immutable.Set());
        } else {
            // All probes that have matching tasks should be visible and expanded,
            // probes that have matching names but no matching tasks should be visible, but collapsed
            var newExpanded = Immutable.Set(
                _.chain(this.props.probes)
                    .filter((p) => _.some(p.tasksStatistic, (t)=> TreeCommon.matches(t.displayName, newFilter) || TreeCommon.matches(t.key, newFilter)))
                    .pluck('id')
                    .value());
            var probesVisible = getDisplayedProbes(this.props.probes, newFilter);
            var taskNum = _.chain(probesVisible)
                .map((p) => {
                    var matchingTasks = _.filter(p.tasksStatistic, (t) => TreeCommon.matches(t.displayName, newFilter) || TreeCommon.matches(t.key, newFilter));
                    if(TreeCommon.matches(p.component.displayName, newFilter) ||
                        TreeCommon.matches(p.agentVersion, newFilter) ||
                        TreeCommon.matches(p.component.key, newFilter)) {
                        return (matchingTasks.length > 0) ? p.tasksStatistic.length : 0;
                    } else {
                        return matchingTasks.length
                    }
                })
                .sum()
                .value();
            var nodesNum = probesVisible.length + taskNum;
            if(nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    Actions.updateFilter(newFilter, newExpanded);
                }, 500);
            } else {
                Actions.updateFilter(newFilter, newExpanded);
            }
        }
    },
    render: function() {
        var anyProbesSelected = !this.props.selectedProbes.isEmpty();
        var probeActionStyle={cursor: anyProbesSelected ? 'pointer' : 'not-allowed'};
        var selectedProbes = this.props.selectedProbes.toJS();
        return <div className="probe-probes-find">
            <div className="pull-left margin-0-5 tagging-btn not-allowed"
                 title={AppUserStore.localizeString(i18n.TAGGING)}/>
            <span className="pull-left probe-tree-toolbar-line"/>
            <div className="pull-left margin-0-5 rollback-conf-btn"
                 style={probeActionStyle}
                 title={AppUserStore.localizeStringCFL(i18n.CONFIG_ROLLBACK)}
                 onClick={()=>{if(anyProbesSelected){
                            AppActions.showConfirmModal(
                                AppUserStore.localizeStringCFL(i18n.CONFIG_ROLLBACK),
                                <ConfirmText action={i18n.CONFIG_ROLLBACK} probeNames={this.props.probeNames}/>,
                                AppConstants.PROBE_CONFIG.RESPONSE.CONFIG_ROLLBACK,
                                () => Actions.rollbackProbe(selectedProbes)
                            )}
                        }}/>
            <div className="pull-left margin-0-5 upd-cfg-btn"
                 style={probeActionStyle}
                 title={AppUserStore.localizeStringCFL(i18n.UPDATE_CONFIG)}
                 onClick={()=>{if(anyProbesSelected){Actions.toggleConfigEditor()}}}/>
            <span className="pull-left probe-tree-toolbar-line"/>
            <div className="pull-left margin-0-5 software-btn clickable"
                 style={probeActionStyle}
                 title={AppUserStore.localizeStringCFL(i18n.UPDATE_SOFTWARE)}
                 onClick={()=>{if(anyProbesSelected){Actions.swUpdateModal(true)}}}/>
            <span className="pull-left probe-tree-toolbar-line"/>
            <div className="pull-left margin-0-5 restart-btn"
                 style={probeActionStyle}
                 title={AppUserStore.localizeStringCFL(i18n.RESTART_SW)}
                 onClick={()=>{if(anyProbesSelected){
                            AppActions.showConfirmModal(
                                AppUserStore.localizeStringCFL(i18n.RESTART_SW),
                                <ConfirmText action={i18n.RESTART_SW} probeNames={this.props.probeNames}/>,
                                AppConstants.PROBE_CONFIG.RESPONSE.SW_RESTART,
                                () => Actions.probeSwRestart(selectedProbes)
                            )}
                        }}/>
            <div className="pull-left margin-0-5 restart-probe-btn"
                 style={probeActionStyle}
                 title={AppUserStore.localizeStringCFL(i18n.RESTART_HW)}
                 onClick={()=>{if(anyProbesSelected){
                            AppActions.showConfirmModal(
                                AppUserStore.localizeStringCFL(i18n.RESTART_HW),
                                <ConfirmText action={i18n.RESTART_HW} probeNames={this.props.probeNames}/>,
                                AppConstants.PROBE_CONFIG.RESPONSE.HW_RESTART,
                                () => Actions.probeHwRestart(selectedProbes)
                            )}
                        }}/>
            <div className="pull-left margin-0-5 delete-btn not-allowed"
                 title={AppUserStore.localizeString(i18n.DELETE_TASK)}/>
            <Input className="pull-right probe-tree-probe-search placeholder-dissappear"
                   placeholder={AppUserStore.localizeString(i18n.SEARCH_TEXT)}
                   value={this.props.filter}
                   onChange={(v) => this.updateFilter(v)}/>
            <span className="pull-right probe-tree-toolbar-line margin-right-4"/>
            <div className="pull-right clickable upd-btn"
                 style={{marginRight: '5px'}}
                 title={AppUserStore.localizeString(i18n.UPDATE)}
                 onClick={Actions.loadProbeData}/>
        </div>
    }
});

var TreeHeader = React.createClass({
    startDrag: function(col) {Actions.startResize(col)},
    stopDrag: function() {Actions.stopResize()},
    drag: function(e) {
        var sizes = this.props.headerSizes;
        var colNum = this.props.headerSizes.length;
        var wrapper = React.findDOMNode(this.refs.headerWrapper);
        var offset = (e.pageX - wrapper.offsetLeft)/ wrapper.getBoundingClientRect().width;
        var ix = this.props.resizedColumn;
        var diff = offset*100 - _.sum(sizes.slice(0,ix+1));
        // can we handle such offset?
        if(diff < 0) { //moving to the left
            if(offset*100 < (ix+1)*MIN_COL_SIZE) {
                return false;
            }
        } else { // moving to the right
            if(offset*100 > 100 - (colNum - (ix + 1))*MIN_COL_SIZE) {
                return false;
            }
        }
        // change column sizes
        var newSizes = sizes.slice(0); // clone array
        if(diff < 0) { // moving to the left
            newSizes[ix+1] = newSizes[ix+1] - diff;
            var diffRemainder = -diff;
            for(var i = ix; i >= 0; i--) {
                if(newSizes[i] - diffRemainder > MIN_COL_SIZE) {
                    newSizes[i] = newSizes[i] - diffRemainder;
                    Actions.resizeColumns(newSizes);
                    return true;
                } else {
                    diffRemainder = diffRemainder - (newSizes[i] - MIN_COL_SIZE);
                    newSizes[i] = MIN_COL_SIZE;
                }
            }
        } else { // diff > 0, moving to the right
            newSizes[ix] = newSizes[ix] + diff;
            var diffRemainder = diff;
            for(var i = ix+1; i < colNum; i++) {
                if(newSizes[i] - diffRemainder > MIN_COL_SIZE) {
                    newSizes[i] = newSizes[i] - diffRemainder;
                    Actions.resizeColumns(newSizes);
                    return true;
                } else {
                    diffRemainder = diffRemainder - (newSizes[i] - MIN_COL_SIZE);
                    newSizes[i] = MIN_COL_SIZE;
                }
            }
        }
    },
    componentWillReceiveProps: function(newProps) {
        if (this.props.resizedColumn == -1 &&
            newProps.resizedColumn != -1) {
            document.addEventListener('mousemove', this.drag);
            document.addEventListener('mouseup', this.stopDrag);
        } else if (this.props.resizedColumn != -1 &&
                   newProps.resizedColumn == -1) {
            document.removeEventListener('mousemove', this.drag);
            document.removeEventListener('mouseup', this.stopDrag);
        }
    },
    render: function() {
        var width = this.props.headerSizes;
        var registered = _.filter(this.props.probes, (d)=>d.state == 'SUCCESS');
        var lineColor = this.props.resizedColumn == -1 ? "#424242" : "white";
        return <div className="container-fluid override-padding-0 pull-left"
                    style={{position: "relative"}}
                    ref="headerWrapper">
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[0]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(0)}/>
                <input type="checkbox" className="pull-left chbx-head"
                       disabled={_.every(this.props.probes,(p)=> p.state !== 'SUCCESS')}
                       onChange={(e) => {
                                e.stopPropagation();
                                Actions.toggleAll();
                            }}
                       checked={(registered.length > 0) &&
                                 _.every(registered, (p) =>this.props.selected.contains(p.component.key))}/>
                {AppUserStore.localizeString(i18n.DISPLAY_NAME)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[1]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(1)}/>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(0)}/>
                {AppUserStore.localizeString(i18n.DESCRIPTION)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[2]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(2)}/>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(1)}/>
                {AppUserStore.localizeString(i18n.PROBE_SW_VERSION)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[3]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(3)}/>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(2)}/>
                {AppUserStore.localizeString(i18n.REG_TIME)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[4]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(4)}/>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(3)}/>
                {AppUserStore.localizeString(i18n.LAST_RESULT_TIME)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[5]+"%", position: "relative"}}>
                <span className="pull-right header-drag"
                      onMouseDown={() => this.startDrag(5)}/>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(4)}/>
                {AppUserStore.localizeString(i18n.KEY)}
            </div>
            <div className="probe-head-text overflow-ellipsis pull-left"
                 style={{width: width[6]+"%", position: "relative"}}>
                <span className="pull-left header-drag"
                      onMouseDown={() => this.startDrag(5)}/>
                {AppUserStore.localizeString(i18n.STATUS)}
            </div>
            <div className="vertical-line"
                 style={{borderColor: '#424242', left: '22px'}}/>
            {_.chain(_.range(1, this.props.headerSizes.length))
                .map((n) => _.sum(this.props.headerSizes.slice(0, n)))
                .map((perc) => <div key={perc} className="vertical-line"
                                    style={{borderColor: lineColor, left: perc+'%'}}/>)
                .value()}
        </div>
    }
});


var ProbesAndTasks = React.createClass({
    getInitialState: function () {
        return {
            probes: Store.getData(),
            filter: Store.getFilter(),
            expanded: Store.getExpanded(),
            selectedProbes: Store.getSelectedProbes(),
            selectedTasks: Store.getSelectedTasks(),
            swUpdateModal: Store.getSwUpdateModal(),
            configEditor: Store.getConfigActive(),
            swList: Store.getSwList(),
            columnSizes: Store.getColumnSizes(),
            headerSizes: Store.getHeaderSizes(),
            resizedColumn: Store.getResizedColumn()
        }
    },
    componentDidUpdate : function () {
        this.resizeHead();
    },
    componentDidMount: function () {
        Store.addChangeListener(this._onChange);
        window.addEventListener('resize', this.resizeHeadDelayed);
        Actions.loadProbeData();
    },
    componentWillUnmount: function () {
        Store.removeChangeListener(this._onChange);
        window.removeEventListener('resize', this.resizeHeadDelayed);
        clearTimeout(_searchTimer);
    },
    _onChange: function () {
        this.setState({
            probes: Store.getData(),
            filter: Store.getFilter(),
            expanded: Store.getExpanded(),
            selectedProbes: Store.getSelectedProbes(),
            selectedTasks: Store.getSelectedTasks(),
            swUpdateModal: Store.getSwUpdateModal(),
            configEditor: Store.getConfigActive(),
            swList: Store.getSwList(),
            columnSizes: Store.getColumnSizes(),
            headerSizes: Store.getHeaderSizes(),
            resizedColumn: Store.getResizedColumn()
        });
    },
    // TODO: consider a more intelligent way, see https://developer.mozilla.org/en-US/docs/Web/Events/resize
    resizeHeadDelayed: function() {
        setTimeout(this.resizeHead, 50);
    },
    resizeHead: function () {
        var tree = React.findDOMNode(this.refs.tree);
        var treeElement = tree.getElementsByClassName('tree-col-width')[0];
        var width = React.findDOMNode(this.refs.headColWidth);
        if(treeElement != null) {
            width.style.width = treeElement.getBoundingClientRect().width + 'px';
        }
    },
    render: function () {
        var selectedProbeNames = _.chain(this.state.probes)
            .filter((probe)=>this.state.selectedProbes.contains(probe.component.key))
            .map((probe)=>probe.component.displayName)
            .value();
        var displayedProbes = this.state.probes;
        if (this.state.filter.length > 0) {
            displayedProbes = getDisplayedProbes(displayedProbes, this.state.filter);
            displayedProbes = _.map(displayedProbes, (probe) =>
                update(probe, {
                    tasksStatistic: {
                        $apply: () => getDisplayedTasks(probe, this.state.filter)
                    }
                })
            );
        }
        displayedProbes = _.chain(displayedProbes)
            .sortByAll(
                (probe) => probeStatusOrder(probe.state),
                (probe) => probe.component.displayName.toUpperCase())
            .map((probe) =>
                update(probe, {
                    tasksStatistic: {
                        $apply: (tasks) => _.sortByAll(tasks,
                            (task) => groupToNum(task.group),
                            (task) => task.displayName.toUpperCase())
                    }
                })
            )
            .value();
        var pageTitle = AppUserStore.localizeString(i18n.PROBES_AND_TASKS);
        return <div className="probe-view container-fluid">
            <NavMenu className="row" title={pageTitle}/>
            <div className="row probe-tree-toolbar">
                <ProbeActionsToolbar
                    probes={this.state.probes}
                    filter={this.state.filter}
                    selectedProbes={this.state.selectedProbes}
                    probeNames={selectedProbeNames}/>
                <div className="head-bg flex-none">
                    <TreeHeader ref="headColWidth"
                                resizedColumn={this.state.resizedColumn}
                                headerSizes={this.state.headerSizes}
                                probes={this.state.probes}
                                selected={this.state.selectedProbes}/>
                </div>
                <Tree ref="tree"
                      resizedColumn={this.state.resizedColumn}
                      headerSizes={this.state.headerSizes}
                      columnSizes={this.state.columnSizes}
                      probes={displayedProbes}
                      filter={this.state.filter}
                      selectedProbes={this.state.selectedProbes}
                      selectedTasks={this.state.selectedTasks}
                      expanded={this.state.expanded}/>
            </div>
            {this.state.configEditor?
                <ConfigEditor probes={this.state.probes}
                              selected={this.state.selectedProbes}
                              probeNames={selectedProbeNames}/> : ""}
            {this.state.swUpdateModal?
                <SwUpdateModal selected={this.state.selectedProbes}
                               probeNames={selectedProbeNames}
                               versions={this.state.swList}/> : ""}
            <ConfirmDialog/>
            <ErrorDialog/>
        </div>
    }
});

export default ProbesAndTasks;