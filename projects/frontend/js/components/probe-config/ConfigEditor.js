import React from 'react';
import _ from 'lodash';
import Immutable from 'immutable';
import Dropzone from 'react-dropzone';
import Misc from '../../util/Misc';
import Actions from '../../actions/ProbeConfigActions';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/ProbeConfigStore';
import ProbesAndTasks from '../../components/probe-config/ProbesAndTasks';
import ConfirmText from './ProbeActionConfirmText';
import i18nConstants from '../../constants/i18nConstants';
import Tree from './ConfigEditorTree';
import XmlHighlight from './XmlHighlight';

var ConfigEditor = React.createClass({
    getInitialState: function () {
        return {
            configSelectedProbes: Immutable.Set(),
            name: '',
            edit: false,
            fileView: '',
            headColWidth: 0
        }
    },
    componentDidMount: function () {
        window.addEventListener('resize', this.resizeHead);
    },
    componentWillUnmount: function () {
        window.removeEventListener('resize', this.resizeHead);
    },
    resizeHead: function () {
        setTimeout(function () {
            var tree = React.findDOMNode(this.refs.tree);
            var width = React.findDOMNode(this.refs.headColWidth);
            if(tree !== null) {
                width.style.width = tree.getElementsByClassName('tree-col-width')[0].getBoundingClientRect().width + 'px';
            }
        }.bind(this), 100);
    },
    onDrop: function (res) {
        if(res[0].type.indexOf('text/xml') > -1) {
            this.setState({
                name: res[0].name,
                file: res[0]
            });
        }
    },
    onEdit: function () {
        var reader = new FileReader();
        reader.onload = function(){
            this.setState({
                edit: !this.state.edit,
                fileView: reader.result
            });
        }.bind(this);
        reader.readAsText(this.state.file);
    },
    onDone: function () {
        AppActions.showConfirmModal(
            AppUserStore.localizeStringCFL('UPDATE_CONFIG'),
            <ConfirmText
                action={i18nConstants.UPDATE_CONFIG}
                probeNames={this.props.probeNames}
                fileName={this.state.name}/>,
            AppConstants.PROBE_CONFIG.RESPONSE.CONFIG_UPDATE,
            () => Actions.updateProbeConfig(this.props.selected.toJS(), this.state.file));
    },
    nodeClick: function (probeId) {
        this.setState({
            configSelectedProbes: Misc.toggle(this.state.configSelectedProbes, probeId)
        });
    },
    render: function () {
        if (!this.state.edit) {
            var displayedProbes = _.chain(this.props.probes)
                .filter((p)=>this.props.selected.contains(p.component.key))
                .sortBy((p)=>p.component.displayName.toUpperCase())
                .value();
            return <div className="modal flex show">
                <div className="modal-dialog bigmodal config-page flex flex-col">
                    <div className="config-header flex-none">
                        <img src="img/modal_close.png" className="pull-right clickable modal-close"
                            onClick={() => Actions.toggleConfigEditor()}/>
                        <h4 className="modal-title">{AppUserStore.localizeString(i18nConstants.CONFIG_EDITOR)}</h4>
                    </div>
                    <div className="flex flex-11a config-panel">
                        <div className="col-xs-6 override-padding-0 flex flex-col flex-11a" style={{borderRight: '3px solid #000000'}}>
                            <div className="config-tree-button-panel flex-none">
                                <img src="img/probeConfig/delete_0.png"
                                    title={AppUserStore.localizeString(i18nConstants.DELETE_PROBE)}
                                    style={{paddingRight: '10px'}}/>
                                <img src="img/probeConfig/CompareConfig_0.png"
                                    title={AppUserStore.localizeString(i18nConstants.COMPARE_CONFIGURATION)}
                                    style={{paddingRight: '10px'}}/>
                            </div>
                            <div className="config-tree-head flex-none">
                                <div className="container-fluid override-padding-0 pull-left" ref="headColWidth">
                                    <div className="config-head-text-dn overflow-ellipsis pull-left col-xs-9">
                                        <input disabled="true"
                                            type="checkbox"
                                            className="pull-left config-chbx-head"
                                        />
                                        <span className="pull-left config-line-head"/>
                                        {AppUserStore.localizeString(i18nConstants.DISPLAY_NAME)}
                                    </div>
                                    <div className="config-head-text overflow-ellipsis pull-left col-xs-3">
                                        {AppUserStore.localizeString(i18nConstants.DOWNLOAD)}
                                    </div>
                                </div>
                            </div>
                            <Tree ref="tree"
                                probes={displayedProbes}
                                selectedProbes={this.state.configSelectedProbes}
                            />
                        </div>
                        <div className="col-xs-6 override-padding-0 flex flex-col">
                            <div className="assign-panel flex-none">
                                <input type="radio" className="pull-left" checked="true" readOnly="true" style={{marginRight: '10px'}}/>
                                <label className="label-text">{AppUserStore.localizeString(i18nConstants.ASSIGN_FROM_FILE) + ':'}</label>
                                <br/>
                                <div className="container-fluid override-padding-0">
                                    <input className="config-assign-file col-xs-9" readOnly="true" type="text" value={this.state.name}/>
                                    <Dropzone accept='application/xml' onDrop={this.onDrop} style={{cursor: 'pointer'}}>
                                        <button type="button" className="btn btn-primary config-btn col-xs-3">
                                            {AppUserStore.localizeString(i18nConstants.BROWSE)}
                                        </button>
                                    </Dropzone>
                                </div>
                                <div className="assign-file-btn">
                                    <button type="button"
                                        disabled={this.state.name === ''}
                                        className="btn btn-primary config-btn pull-right"
                                        onClick={this.onEdit}>
                                        {AppUserStore.localizeString(i18nConstants.EDIT)}
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="footer flex-none">
                        <button type="button" className="btn btn-primary config-btn"
                            disabled={this.state.name === ''}
                            onClick={this.onDone}>
                            {AppUserStore.localizeString(i18nConstants.DONE)}
                        </button>
                        <button type="button" className="btn btn-primary config-btn pull-right"
                            onClick={()=>{Actions.toggleConfigEditor()}}>
                            {AppUserStore.localizeString(i18nConstants.CANCEL)}
                        </button>
                    </div>
                </div>
            </div>
        } else {
            return <div className="modal flex show">
                <div className="modal-dialog bigmodal config-page flex flex-col flex-11a">
                    <div className="config-header flex-none">
                        <img src="img/modal_close.png" className="pull-right"
                            style={{paddingTop: '6px', paddingRight: '6px', cursor: 'pointer'}}
                            onClick={this.onEdit}
                        />
                        <h4 className="modal-title">{this.state.name}</h4>
                    </div>
                    <div className="flex flex-11a config-edit-panel">
                        <XmlHighlight text={this.state.fileView}/>
                    </div>
                    <div className="footer-view flex-none">
                        <button type="button" className="btn btn-primary config-btn" onClick={this.onEdit}>
                            {AppUserStore.localizeString(i18nConstants.DONE)}
                        </button>
                        <button type="button" className="btn btn-primary config-btn pull-right" onClick={this.onEdit}>
                            {AppUserStore.localizeString(i18nConstants.CANCEL)}
                        </button>
                    </div>
                </div>
            </div>
        }
    }
});

export default ConfigEditor;