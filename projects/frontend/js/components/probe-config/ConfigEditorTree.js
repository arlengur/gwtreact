import React from 'react';
import _ from 'lodash';
import Actions from '../../actions/ProbeConfigActions';

var ConfigEditorTree = React.createClass({
    render: function () {
        return <div className="config-tree flex-11a">
            <ul className="probe-tree-background override-margin-0 override-padding-0">
                {_.map(this.props.probes, function (probe, ix) {
                    return <ProbeNode
                        key={ix}
                        probe={probe}
                        selectedProbes={this.props.selectedProbes}
                    />
                }.bind(this))}
            </ul>
        </div>
    }
});

var ProbeNode = React.createClass({
    render: function () {
        var probe = this.props.probe;
        return <li className="config-tree-li">
            <div className="container-fluid override-padding-0 tree-col-width">
                <div className="small-text config-tree-line-text-dn pull-left col-xs-9 overflow-ellipsis">
                    <input type="checkbox"
                        disabled="true"
                        className="pull-left config-chbx"
                        style={{marginLeft: '5px', marginRight: '5px'}}
                        checked={this.props.selectedProbes.contains(probe.id)}
                        onChange={(event)=>{
                            event.stopPropagation();
                            this.props.nodeClick(probe.id);
                        }}
                    />
                    <span className="pull-left config-line"/>
                    <div className="pull-left probe-simple margin-right-2"/>
                    {probe.component.displayName}
                </div>
                <div className="config-tree-line pull-left col-xs-3">
                    <img src="img/probeConfig/download.png"
                        className="config-center-block"
                        onClick={(event)=>{
                            event.stopPropagation();
                            Actions.downloadProbeConfFile(probe.component.key);
                        }}
                    />
                </div>
            </div>
        </li>
    }
});

export default ConfigEditorTree;