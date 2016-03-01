import React from 'react';
import Col from 'react-bootstrap/Col';
import Immutable from 'immutable';
import _ from 'lodash';
import CrudActions from '../../actions/ChannelCrudActions';
import Input from '../../components/common/TextInputTypeahead';
import i18n from '../../constants/i18nConstants';
import Store from '../../stores/ChannelCrudStore';
import {groupToNum} from '../../util/Alerts';
import Images from '../../util/Images';
import Common from './CrudTreesCommon';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const localize = AppUserSettingsStore.localizeString;

var SelectedProbesTree = React.createClass({
    getDisplayedProbes: function() {
        return _.filter(
            Common.getDisplayedProbes(this.props.probes, this.state.filter),
            function(p) {
                return _.some(p.tasks, function(t) {
                    return this.props.selected.contains(t.id);
                }.bind(this))
            }.bind(this));
    },

    getDisplayedTasks: function(p) {
        return _.filter(
            Common.getDisplayedTasks(p, this.state.filter),
            function(t) {
                return this.props.selected.contains(t.id);
            }.bind(this)
        );
    },

    updateFilter: function(newFilter) {
        if(newFilter == '') {
            this.setState({
                filter: newFilter,
                collapsed: Immutable.Set()
            });
        } else {
            this.setState({
                filter: newFilter
            });
        }
    },

    getProbeRow: function(p) {
        var isExpanded = !this.state.collapsed.contains(p.id);
        var taskRows = [];
        if(isExpanded) {
            var displayedTasks = this.getDisplayedTasks(p, this.state.filter);
            var size = displayedTasks.length;
            taskRows = _.chain(displayedTasks)
                .sortBy('name')
                .sortBy(function(t){return groupToNum(t.parameterGroup)})
                .map(function(t, ix){return this.getTaskRow(t, ix==size-1)}.bind(this))
                .value();
        }
        return <li className="crud-tree-probe" key={p.id}>
            <div key={p.id} className="container-fluid override-padding-0-5">
                <img src={isExpanded ? "img/tree_minus_no_line.png" : "img/tree_plus.png"}
                     className="pull-left crud-tree-image-left"
                     onClick={function() {
                         this.setState({
                             collapsed: isExpanded ? this.state.collapsed.add(p.id) : this.state.collapsed.remove(p.id)
                         });
                     }.bind(this)}/>
                <img src={isExpanded ? "img/tree_probe_23_line.png" : "img/tree_probe_23_no_line.png"}
                     className="pull-left crud-tree-image-left"/>
                <img src="img/tree_cross_dark.png" className="pull-right crud-tree-image-right"
                     onClick={function() {CrudActions.unselectProbes([p.id])}.bind(this)}/>
                <span className="small-text overflow-ellipsis crud-tree-text">{Common.getHighlightedName(p.name, this.state.filter)}</span>
            </div>
            <ul className="override-margin-0 override-padding-0">
                {taskRows}
            </ul>
        </li>
    },

    getTaskRow: function(t, last) {
        return <li className="crud-tree-task" key={t.id}>
            <div className="container-fluid override-padding-0-5">
                <img src={Images.treeLine(last)} className="pull-left"/>
                <div className={"pull-left crud-tree-image-left " + Images.taskGroup(t.parameterGroup)}/>
                <img src="img/tree_cross_dark.png" className="pull-right crud-tree-image-right"
                    onClick={function() {CrudActions.unselectTask(t.id)}.bind(this)}/>
                <span className="small-text overflow-ellipsis crud-tree-text">{Common.getHighlightedName(t.name, this.state.filter)}</span>
            </div>
        </li>
    },

    getInitialState: function() {
        return {
            collapsed: Immutable.Set(),
            filter: ''
        }
    },

    render: function() {
        return <Col xs={6} className="override-padding-0 crud-right-tree-panel">
            <span className="small-text" style={{marginBottom: '3'}}>{localize(i18n.TASKS_SELECTED)}</span>
            <div className="crud-tree-search-panel">
                <Input className="small-text crud-tree-probe-search placeholder-disappear"
                    placeholder={localize(i18n.SEARCH_TEXT)}
                    value={this.state.filter}
                    onChange={(v) => this.updateFilter(v)}/>
                <img src="img/tree_cross_light.png" className="pull-right crud-tree-image-top"
                    onClick={()=> {
                        CrudActions.unselectProbes(
                            _.pluck(this.getDisplayedProbes(), 'id'));
                    }}/>
            </div>
            <div className="crud-tree">
                <div className="crud-tree-filler">
                    {_.map(this.getDisplayedProbes(this.props.probes, this.state.filter), this.getProbeRow)}
                </div>
            </div>
        </Col>
    }
});

export default SelectedProbesTree;