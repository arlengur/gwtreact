import _ from 'lodash';
import React from 'react';
import * as Actions from '../../actions/PolicyActions';
import {severityTo6Colors2, ALL_SEVERITIES} from '../../util/Alerts';
import Select from '../common/Select';
import {DIALOG_STATE as STATE} from '../../stores/PolicyStore';
import Misc from '../../util/Misc';
import {severityToNum} from '../../util/Alerts';

const Overlay = React.createClass({
    render: function() {
        if(this.props.disabled == true) {
            return <div className="grey-out"/>
        } else if (this.props.loading == true) {
            //TODO: i18n
            return <div className="searching"/>
        } else {
            // empty as per http://facebook.github.io/react/docs/component-specs.html#render
            return null;
        }
    }
});

const Notification = React.createClass({
    render: function()  {
        return <div style={{margin: '0 0 5px', background: '#424242', height: 33, borderRadius: '5px !important', border: '1px solid #626262'}} className="row override-padding-5">
            <Select width="30%" className="pull-left" options={["SMS", "Email"]}/>
            <div style={{marginLeft: 5}} className="cross-icon pull-right"
                onClick={Actions.removeNotification}/>
            <Select width="50%" className="pull-right" options={["User", "Admin"]}/>
        </div>
    }
});

const Notifications = React.createClass({
    render: function() {
        let store = this.props.store,
            state = store.get('createState'),
            notifications = store.get('notifications');
        return <div style={{flex: '1 0 250px'}} className="flex flex-col override-padding-5 relative">
            <Overlay disabled={state != STATE.PROBES_SELECTED}/>
            <div style={{height: 20, margin: '-3px -3px 0', background: '#393939'}} className="row flex-none">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Notifications</span>
            </div>
            <div style={{margin: '5px 0 0', height: 33}} className="row flex-none override-padding-5">
                <label style={{marginRight: 5}} className="small-text pull-left text23" htmlFor="alarm-type">Alarm type:</label>
                <Select width={150} className="pull-left" options={["RTPStatistics", "MpegTsStatistics"]}/>
            </div>
            <div style={{margin: '5px 0 0', background: '#5f5f5f', height: 33}} className="row flex-none override-padding-5">
                <div className="create-btn pull-left" onClick={Actions.addNotification}/>
                <Select width={100} className="pull-right" options={["Template", "Template 2"]}/>
                <label style={{marginRight: 5}} className="small-text pull-right text23" htmlFor="notification-template">Template:</label>
            </div>
            <div style={{background: '#222222', padding: 5, overflowY: 'auto'}} className="flex-11a">
                {_.times(notifications, () => <Notification/>)}
            </div>
        </div>
    }
});

const Condition = React.createClass({
    render: function() {
        let severity = this.props.severity;
        return <div style={{
                        margin: '0 0 5px',
                        background: '#424242',
                        height: 100,
                        borderRadius: '5px !important',
                        border: '1px solid #626262'
                    }}
                    className="row">
            <div style={{
                    height: '100%',
                    width: '100%',
                    padding: '5px 5px 5px 10px',
                    borderTop: '5px solid '+ severityTo6Colors2(severity),
                    borderRadius: '5px !important'
                 }}
                 className="pull-left">
                <div className="row override-margin-0">
                    <span className="small-text pull-left">{Misc.firstUppercase(severity)}</span>
                </div>
                <div style={{paddingTop: 5}} className="row override-margin-0">
                    <div style={{marginTop: 5}} className="pull-left raise-icon" />
                    <span style={{width: '30%'}} className="pull-left small-text text23 margin-left-5 overflow-ellipsis">Raise if parameter</span>
                    <Select width={40} className="pull-left margin-left-5" options={[">", ">=", "<", "<="]}/>
                    <input type="text" style={{width: 30}} className="pull-left search-field margin-left-5" />
                    <Select width={50} className="pull-right margin-left-5" options={["sec", "min"]}/>
                    <input type="text" style={{width: 30}} className="pull-right search-field margin-left-5" />
                    <span className="pull-right small-text text23 margin-left-5">within</span>
                </div>
                <div style={{paddingTop: 10}} className="row override-margin-0">
                    <div style={{marginTop: 5}} className="pull-left cease-icon" />
                    <span style={{width: '30%'}} className="pull-left small-text text23 margin-left-5 overflow-ellipsis">Cease if parameter</span>
                    <Select width={40} className="pull-left margin-left-5" options={[">", ">=", "<", "<="]}/>
                    <input type="text" style={{width: 30}} className="pull-left search-field margin-left-5" />
                    <Select width={50} className="pull-right margin-left-5" options={["sec", "min"]}/>
                    <input type="text" style={{width: 30}} className="pull-right search-field margin-left-5" />
                    <span className="pull-right small-text text23 margin-left-5">within</span>
                </div>
            </div>
        </div>

    }
});

const ConditionToggle = React.createClass({
    propTypes: {severity: React.PropTypes.string.isRequired},
    render: function() {
        let severity = this.props.severity,
            store = this.props.store,
            conditions = store.get('conditions'),
            active = conditions.has(severity);
        return <div style={{marginRight: 5, background: severityTo6Colors2(severity), width: 23, height: 23}}
                    className="pull-left override-padding-5">
            <input type="checkbox" style={{display: 'block', margin: '0 0 5px !important'}}
                   value={active}
                   onChange={()=>Actions.setConditions(Misc.toggle(conditions, severity))}/>
        </div>
    }
});

const Conditions = React.createClass({
    render: function() {
        let store = this.props.store,
            state = store.get('createState'),
            conditions = store.get('conditions');
        return <div style={{flex: '1 0 250px'}} className="flex flex-col override-padding-5 black-bottom relative">
            <Overlay disabled={state != STATE.PROBES_SELECTED}/>
            <div style={{height: 20, margin: '-3px -3px 0', background: '#393939'}} className="row flex-none">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Conditions</span>
            </div>
            <div style={{margin: '5px 0 0', background: '#5f5f5f', height: 33}} className="row flex-none override-padding-5">
                {_.map(ALL_SEVERITIES, (s)=><ConditionToggle key={s} severity={s} store={store}/>)}
                <Select width={100} className="pull-right" options={["Template", "Template 2"]}/>
                <label style={{marginRight: 5}} className="small-text pull-right text23" htmlFor="condition-template">Template:</label>
            </div>
            <div style={{background: '#222222', padding: 5, overflowY: 'auto'}} className="flex-11a">
                {_.chain(conditions.toJS())
                    .sortBy((cond)=>-severityToNum(cond))
                    .map((cond) => <Condition key={cond} severity={cond}/>)
                    .value()}
            </div>
        </div>
    }
});

const Module = React.createClass({
    render: function() {
        const store = this.props.store;
        return <div className="flex-none black-bottom relative">
            <Overlay loading={store.get('createState') == STATE.MODULE_LOADING}/>
            <div style={{height: 20, margin: '2px 2px 0', background: '#393939'}} className="row">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Module</span>
            </div>
            <div style={{margin: '10px 5px 5px'}} className="row">
                <Select width="100%" className="pull-left"
                        options={[" "].concat(store.get('modules'))}
                        value={store.get('selectedModule')}
                        onChange={(module) => Actions.selectModule(module)}/>
            </div>
        </div>
    }
});

const Parameter = React.createClass({
    render: function() {
        let store = this.props.store,
            module = store.get('selectedModule');
        return <div className="flex-none black-bottom relative">
            <Overlay loading={store.get('createState') == STATE.PARAM_LOADING}
                     disabled={store.get('createState') < STATE.PARAM_LOADING}/>
            <div style={{height: 20, margin: '2px 2px 0', background: '#393939'}} className="row">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Parameter</span>
            </div>
            <div style={{margin: '10px 5px 5px'}} className="row">
                <Select width="100%" className="pull-left"
                        options={[" "].concat(store.get('params'))}
                        value={store.get('selectedParam')}
                        onChange={(param) => Actions.selectParam(param, module)}/>
            </div>
        </div>
    }
});

const Task = React.createClass({
    propTypes: {
        name: React.PropTypes.string.isRequired,
        store: React.PropTypes.object.isRequired
    },
    render: function() {
        let store = this.props.store,
            name = this.props.name,
            tasks = store.get('selectedTasks'),
            selected = tasks.contains(name);
        return <li className="config-tree-li">
            <div className="container-fluid override-padding-0 overflow-ellipsis">
                <input type="checkbox" className="pull-left chbx"
                       value={selected}
                       onChange={()=>Actions.selectTasks(Misc.toggle(tasks, name))}/>
                <div className="pull-left tsk-data"/>
                <span className="probe-tree-line-text">{name}</span>
            </div>
        </li>
    }
});

const ChannelsTasks = React.createClass({
    render: function() {
        const store = this.props.store;
        return <div className="flex flex-col flex-11a relative">
            <Overlay loading={store.get('createState') == STATE.TASKS_LOADING}
                     disabled={store.get('createState') < STATE.TASKS_LOADING}/>
            <div style={{height: 20, margin: '2px 2px 0', background: '#393939'}} className="row">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Channel or Task</span>
            </div>
            <div className="config-tree-button-panel flex-none">
                <input type="checkbox" className="pull-left" />
                <input type="text" placeholder="Search" style={{width: 100}} className="pull-right search-field placeholder-dissappear" />
            </div>
            <div className="probe-tree-background config-tree flex-11a">
                <ul className="probe-tree-background override-margin-0 override-padding-0">
                    {_.map(
                        store.get('tasks'),
                        (v, k) =><Task key={k} name={k} store={this.props.store} />)}
                </ul>
            </div>
        </div>
    }
});

const Probe = React.createClass({
    propTypes: {
        name: React.PropTypes.string.isRequired,
        store: React.PropTypes.object.isRequired
    },
    render: function() {
        let store = this.props.store,
            name = this.props.name,
            probes = store.get('selectedProbes'),
            selected = probes.contains(name);
        return <li className="config-tree-li">
            <div className="container-fluid override-padding-0 overflow-ellipsis">
                <input type="checkbox" className="pull-left chbx"
                       value={selected}
                       onChange={()=>Actions.selectProbes(Misc.toggle(probes, name))}/>
                <div className="pull-left probe-simple"/>
                <span className="probe-tree-line-text">{name}</span>
            </div>
        </li>
    }
});

const Probes = React.createClass({
    render: function() {
        const store = this.props.store;
        return <div className="col-sm-3 override-padding-0 flex flex-col black-right relative">
            <Overlay loading={store.get('createState') == STATE.PROBES_LOADING}
                     disabled={store.get('createState') < STATE.PROBES_LOADING}/>
            <div style={{height: 20, margin: '2px 2px 0', background: '#393939'}} className="row">
                <span style={{margin: '2px 5px 0', color: '#b5b5b5'}} className="small-text pull-left">Probes</span>
            </div>
            <div className="config-tree-button-panel flex-none">
                <input type="checkbox" className="pull-left" />
                <input type="text" placeholder="Search" style={{width: 100}} className="pull-right search-field placeholder-dissappear" />
            </div>
            <div className="probe-tree-background config-tree flex-11a">
                <ul className="probe-tree-background override-margin-0 override-padding-0">
                    {_.map(store.get('probes'),
                        (p) => <Probe key={p.entityKey} name={p.entityKey} store={this.props.store}/>)}
                </ul>
            </div>
        </div>
    }
});

const PolicyCreate = React.createClass({
    componentDidMount: function() {
        Actions.loadModules();
    },
    render: function() {
        return (
            //TODO modal component
            <div className="modal flex">
                <div className="grey-page" />
                <div className="modal-dialog bigmodal policy-create-dialog flex flex-col">
                    <div className="modal-head flex-none">
                        <img style={{paddingTop: 6, paddingRight: 6}} className="pull-right" src="../img/modal_close.png"
                             onClick={Actions.toggleCreate}/>
                        <h4 className="modal-title">Create Policy</h4>
                    </div>
                    <div style={{overflow: 'hidden'}} className="flex flex-11a config-panel">
                        <div className="col-sm-3 override-padding-0 flex flex-col black-right">
                            <Module store={this.props.store}/>
                            <Parameter store={this.props.store}/>
                            <ChannelsTasks store={this.props.store}/>
                        </div>
                        <Probes store={this.props.store}/>
                        <div style={{overflowY: 'auto'}} className="col-sm-6 override-padding-0 flex flex-col">
                            <div style={{height: 35, padding: 5}} className="flex black-bottom">
                                <label htmlFor="policy-name" className="flex-none pull-left small-text text23">
                                    Policy Name:
                                </label>
                                <input type="text" style={{marginLeft: 10}} className="flex-11a search-field" id="policy-name" />
                            </div>
                            <Conditions store={this.props.store}/>
                            <Notifications store={this.props.store}/>
                        </div>
                    </div>
                    <div className="footer flex-none">
                        <button className="btn btn-primary config-btn pull-left" type="button">Done</button>
                        <button style={{marginLeft: 5}} className="btn btn-primary config-btn pull-right" type="button">
                            Cancel
                        </button>
                        <button className="btn btn-primary config-btn pull-right" type="button">Clear</button>
                    </div>
                </div>
            </div>
        );
    }
});

export default PolicyCreate;