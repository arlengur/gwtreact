import React from 'react';
import _ from 'lodash';
import Actions from '../../actions/ProbeConfigActions';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import ConfirmText from './ProbeActionConfirmText';
import AppUserStore from '../../stores/AppUserSettingsStore';
import i18nConstants from '../../constants/i18nConstants';

var SwUpdateModal = React.createClass({
    getInitialState: function() {
        return {
            selectedVer: ''
        }
    },
    render: function() {
        return <div className="modal show">
            <div className="grey-page"></div>
            <div className="modal-dialog bigmodal sw-update-window">
                <div className="modal-content">
                    <div className="modal-header qligent-top-navbar override-padding-5 override-border-0">
                        <h4 className="modal-title">{AppUserStore.localizeStringCFL(i18nConstants.UPDATE_SOFTWARE)}</h4>
                    </div>
                    <div className="modal-body">
                        <label className="small-text">{AppUserStore.localizeString(i18nConstants.SELECT_SOFTWARE_VERSION) + ':'}</label>
                        <div className="sw-list-wrapper details-tree-background">
                            <ul className="override-padding-0 sw-list details-tree-background">
                                {_.map(this.props.versions, (ver) => {
                                    var className = "small-text sw-list-item" + (this.state.selectedVer == ver ? " selected" : "");
                                    return <li key={ver} className={className}
                                               onClick={()=>this.setState({selectedVer: ver})}>
                                        {ver}
                                    </li>
                                })}
                            </ul>
                        </div>
                    </div>
                    <div className="modal-footer" style={{paddingTop: 0}}>
                        <div style={{textAlign: 'right'}}>
                            <button type="button"
                                disabled={this.state.selectedVer === ''}
                                className="btn btn-primary"
                                onClick={() => AppActions.showConfirmModal(
                                    AppUserStore.localizeStringCFL(i18nConstants.UPDATE_SOFTWARE),
                                    <ConfirmText action={i18nConstants.UPDATE_SOFTWARE}
                                                 probeNames={this.props.probeNames}
                                                 version={this.state.selectedVer}/>,
                                    AppConstants.PROBE_CONFIG.RESPONSE.SW_UPDATE,
                                    () => Actions.swUpdate(this.state.selectedVer, this.props.selected.toJS()))}>
                                {AppUserStore.localizeString(i18nConstants.OK)}
                            </button>
                            <button type="button" className="btn btn-primary"
                                onClick={()=>{Actions.swUpdateModal(false)}}>
                                {AppUserStore.localizeString(i18nConstants.CANCEL)}
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    }
});

export default SwUpdateModal;