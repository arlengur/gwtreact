import React from 'react';
import _ from 'lodash';
import Actions from '../../actions/ProbeConfigActions';
import Misc from '../../util/Misc';
import Images from '../../util/Images';
import Locale from '../../util/Locale';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import AppUserStore from '../../stores/AppUserSettingsStore';
import i18nConstants from '../../constants/i18nConstants';

var ProbesAndTasksTree = React.createClass({
    getVerticalLines: function() {
        var checkboxLine = <div key="chkbx" className="vertical-line"
                                style={{borderColor: '#424242', left: '22px'}}/>;
        var columnLines =  _.chain(_.range(1, this.props.columnSizes.length))
            .map((n) => _.sum(this.props.columnSizes.slice(0, n)))
            .map((perc) => <div key={"col"+perc} className="vertical-line"
                                style={{borderColor: '#424242', left: perc+'%'}}/>)
            .value();
        var headerLines =  (this.props.resizedColumn != -1) ?
            _.chain(_.range(1, this.props.headerSizes.length))
                .map((n) => _.sum(this.props.headerSizes.slice(0, n)))
                .map((perc) => <div key={"head"+perc} className="vertical-line"
                                    style={{borderColor: 'white', left: perc+'%'}}/>)
                .value()
            : [];
        return [checkboxLine].concat(columnLines).concat(headerLines);
    },
    render: function () {
        var verticalLines = this.getVerticalLines();
        return <div className="probe-tree probe-tree-background">
            <ul className="probe-tree-background override-margin-0 override-padding-0"
                style={{position: 'relative'}}>
                {_.map(this.props.probes, function (probe, ix) {
                    return <ProbeNode
                        key={ix}
                        columnSizes={this.props.columnSizes}
                        probe={probe}
                        expanded={this.props.expanded.contains(probe.component.key)}
                        filter={this.props.filter}
                        selectedProbes={this.props.selectedProbes}
                        selectedTasks={this.props.selectedTasks}
                    />
                }.bind(this))}
                {verticalLines}
            </ul>
            {verticalLines}
        </div>
    }
});

var ProbeNode = React.createClass({
    render: function () {
        var probe = this.props.probe;
        var taskNum = probe.tasksStatistic.length;
        var width = this.props.columnSizes;
        return <li className="probe-tree-li">
            <div className="container-fluid override-padding-0 small-text tree-col-width">
                <div className="pull-left overflow-ellipsis"
                    style={{width: width[0]+'%', position: 'relative'}}>
                    <input type="checkbox"
                        className="pull-left chbx"
                        disabled={probe.state !== 'SUCCESS'}
                        style={{marginLeft: '5px', marginRight: '5px'}}
                        checked={this.props.selectedProbes.contains(probe.component.key)}
                        onChange={function (event) {
                            event.stopPropagation();
                            Actions.probeSelect(probe.component.key);
                        }}
                    />
                    <div className={'pull-left margin-left-7 ' + (this.props.expanded ? "probe-tree-minus" : "probe-tree-plus")}
                        onClick={function (event) {
                            event.stopPropagation();
                            Actions.toggleExpanded(probe.component.key);
                        }.bind(this)}/>
                    <div className="pull-left probe-simple margin-left-2"/>
                    <span className="small-text probe-tree-line-text">
                        {TreeCommon.getHighlightedName(probe.component.displayName, this.props.filter)}
                    </span>
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis"
                    style={{width: width[1]+'%', position: 'relative'}}>
                    {probe.component.description}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis"
                    style={{width: width[2]+'%', position: 'relative'}}>
                    {TreeCommon.getHighlightedName(probe.agentVersion, this.props.filter)}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis"
                    style={{width: width[3]+'%', position: 'relative'}}>
                    {Locale.localizeDateTime(probe.registrationTime,AppUserSettingsStore.getLocale())}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis"
                    style={{width: width[4]+'%', position: 'relative'}}>
                    {Locale.localizeDateTime(probe.lastResultTime,AppUserSettingsStore.getLocale())}
                </div>
                <div className="probe-tree-line-text pull-left overflow-ellipsis"
                    style={{width: width[5]+'%', position: 'relative'}}>
                    {TreeCommon.getHighlightedName(probe.component.key, this.props.filter)}
                </div>
                <div className="probe-tree-line-text pull-left"
                    style={{width: width[6]+'%', position: 'relative'}}>
                    <img src={Images.remoteProbeStatus(probe.state, true)}
                        className="status-img"
                        title={AppUserStore.getStateTitle(probe.state)}/>
                </div>
            </div>
            <ul className="override-margin-0 override-padding-0">
                {this.props.expanded ?
                    _.map(probe.tasksStatistic,
                        function (task, ix) {
                            return <TaskNode
                                key={ix}
                                columnSizes={this.props.columnSizes}
                                task={task}
                                filter={this.props.filter}
                                last={ix == taskNum - 1}
                                selectedTasks={this.props.selectedTasks}/>
                        }.bind(this))
                    : ""}
            </ul>
        </li>
    }
});

var TaskNode = React.createClass({
    render: function () {
        var task = this.props.task;
        var width = this.props.columnSizes;
        return <li className="probe-tree-li">
            <div className="container-fluid override-padding-0 small-text">
                <div className="pull-left overflow-ellipsis"
                    style={{width: width[0]+'%', position: 'relative'}}>
                    <input type="checkbox"
                        className="pull-left chbx"
                        style={{marginLeft: '5px', marginRight: '5px'}}
                        checked={this.props.selectedTasks.contains(task.key)}
                        disabled={true}
                        onChange={function (event) {
                            event.stopPropagation();
                            Actions.taskSelect(task.key);
                        }}
                    />
                    <div className={'pull-left ' + (this.props.last ? "probe-line-end" : "probe-line-middle")}
                         style={{marginLeft: '12px'}}/>
                    <div className={'pull-left ' + Images.taskGroup(task.group)}/>
                    <span className="small-text probe-tree-line-text">
                        {TreeCommon.getHighlightedName(task.displayName, this.props.filter)}
                    </span>
                </div>
                <div className="probe-tree-line-text pull-left" style={{width: width[1]+'%', position: 'relative'}}/>
                <div className="probe-tree-line-text pull-left" style={{width: width[2]+'%', position: 'relative'}}/>
                <div className="probe-tree-line-text pull-left" style={{width: width[3]+'%', position: 'relative'}}>
                    {Misc.formatDate(task.registrationTime)}
                </div>
                <div className="probe-tree-line-text pull-left" style={{width: width[4]+'%', position: 'relative'}}>
                    {Misc.formatDate(task.lastResultTime)}
                </div>
                <div className="probe-tree-line-text overflow-ellipsis pull-left" style={{width: width[5]+'%', position: 'relative'}}>
                    {TreeCommon.getHighlightedName(task.key, this.props.filter)}
                </div>
                <div className="probe-tree-line-text pull-left" style={{width: width[6]+'%', position: 'relative'}}/>
            </div>
        </li>
    }
});

export default ProbesAndTasksTree;