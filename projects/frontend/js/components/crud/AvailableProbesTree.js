import React from 'react';
import Col from 'react-bootstrap/Col';
import Immutable from 'immutable';
import _ from 'lodash';
import CrudActions from '../../actions/ChannelCrudActions';
import Input from '../../components/common/TextInputTypeahead';
import i18n from '../../constants/i18nConstants';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const localize = AppUserSettingsStore.localizeString;
import Store from '../../stores/ChannelCrudStore';
import {groupToNum} from '../../util/Alerts';
import Images from '../../util/Images';
import Common from './CrudTreesCommon';
import Misc from '../../util/Misc';

var _searchTimer = null;

var AvailableProbesTree = React.createClass({
    isProbeSelected: function(id) {
        return _.every(this.props.probeToTasks.get(id), (t) => this.props.selected.contains(t))
    },
    updateFilter: function(newFilter) {
        // Here we delay updating the tree if the expanded tree size is too big (> 100 elements)
        // We hope that the user is just in the middle of typing a more precise filter, and on next search field
        // update we will get a shorter list, which we will then display immediately
        if(_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if(newFilter =='') {
            this.setState({
                filter: newFilter,
                expanded: Immutable.Set()
            });
        } else {
            // All probes that have matching tasks should be visible and expanded,
            // probes that have matching name but no matching tasks should be visible, but collapsed
            var newExpanded = Immutable.Set(
                _.chain(this.props.probes)
                    .filter((p) => _.some(p.tasks, (t)=> Common.matches(t.name, newFilter)))
                    .pluck('id')
                    .value());
            var probesVisible = Common.getDisplayedProbes(this.props.probes, newFilter);
            var taskNum = _.chain(probesVisible)
                .map((p) => {
                    var matchingTasks = _.filter(p.tasks, (t) => Common.matches(t.name, newFilter));
                    if(Common.matches(p.name, newFilter)) {
                        return (matchingTasks.length > 0) ? p.tasks.length : 0;
                    } else {
                        return matchingTasks.length
                    }
                })
                .sum()
                .value();
            var nodesNum = probesVisible.length + taskNum;
            if(nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    this.setState({
                        filter: newFilter,
                        expanded: newExpanded
                    });
                }, 500);
            } else {
                this.setState({
                    filter: newFilter,
                    expanded: newExpanded
                });
            }
        }
    },
    getProbeRow: function(p) {
        var isExpanded = this.state.expanded.contains(p.id);
        var taskRows = [];
        if(isExpanded) {
            var displayedTasks = Common.getDisplayedTasks(p, this.state.filter);
            var size = displayedTasks.length;
            taskRows = _.chain(displayedTasks)
                .sortBy('name')
                .sortBy((t) => groupToNum(t.parameterGroup))
                .map((t, ix) => this.getTaskRow(t, ix == size - 1))
                .value();
        }
        return <li className="crud-tree-probe" key={p.id}>
            <div className="container-fluid override-padding-0-5">
                <img src={isExpanded ? "img/tree_minus_no_line.png" :"img/tree_plus.png"}
                    className="pull-left crud-tree-image-left"
                    onClick={() => this.setState({expanded: Misc.toggle(this.state.expanded, p.id)})}/>
                <img src="img/tree_probe_23_no_line.png" className="pull-left crud-tree-image-left"/>
                <img src={Images.moveArrow(!this.isProbeSelected(p.id))} className="pull-right crud-tree-image-right"
                    onClick={() => CrudActions.selectTasks(
                        _.pluck(Common.getDisplayedTasks(p, this.state.filter),'id'))}/>
                <div className="small-text overflow-ellipsis crud-tree-text">
                    {Common.getHighlightedName(p.name, this.state.filter)}
                </div>
            </div>
            <ul className="override-margin-0 override-padding-0">
                {taskRows}
            </ul>
        </li>
    },
    getTaskRow: function(t, last) {
        return <li className="crud-tree-task" key={t.id}>
            <div className="container-fluid override-padding-0-5">
                <img src={Images.treeLine(last)} className="pull-left" />
                <div className={"pull-left " + Images.taskGroup(t.parameterGroup)}/>
                <img src={Images.moveArrow(!this.props.selected.contains(t.id))}
                     className="pull-right crud-tree-image-right"
                     onClick={()=>CrudActions.selectTasks([t.id])}/>
                <div className="small-text overflow-ellipsis crud-tree-text">
                    {Common.getHighlightedName(t.name, this.state.filter)}
                </div>
            </div>
        </li>
    },
    getInitialState: function() {
        return {
            expanded: Immutable.Set(),
            filter: ''
        }
    },
    componentWillUnmount: function() {
        clearTimeout(_searchTimer);
    },
    render: function() {
        return <Col xs={6} className="override-padding-0 crud-left-tree-panel">
            <span className="small-text" style={{marginBottom: '3'}}>{localize(i18n.TASKS_AVAILABLE)}</span>
            <div className="crud-tree-search-panel">
                <Input className="small-text crud-tree-probe-search placeholder-disappear"
                       placeholder={localize(i18n.SEARCH_TEXT)}
                       onChange={(v)=>this.updateFilter(v)}
                       value={this.state.filter}/>
                <img src="img/tree_move_arrow_inactive.png" className="pull-right crud-tree-image-top"
                    onClick={() => {
                        CrudActions.selectTasks(
                            _.flatten(_.map(
                                Common.getDisplayedProbes(this.props.probes, this.state.filter),
                                function(p) {
                                    return _.pluck(Common.getDisplayedTasks(p, this.state.filter), 'id')
                                }.bind(this))))}}/>
            </div>
            <div className="crud-tree">
                <ul className="crud-tree-filler override-padding-0 override-margin-0">
                    {_.map(Common.getDisplayedProbes(this.props.probes, this.state.filter), this.getProbeRow)}
                </ul>
            </div>
        </Col>
    }
});

export default AvailableProbesTree;