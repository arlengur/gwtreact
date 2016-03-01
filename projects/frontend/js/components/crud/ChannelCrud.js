import React from 'react';
import Row from 'react-bootstrap/Row';
import AppActions from '../../actions/AppActions';
import CrudActions from '../../actions/ChannelCrudActions';
import AppConstants from '../../constants/AppConstants';
import i18nConstants from '../../constants/i18nConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/ChannelCrudStore';
import ChannelStatusStore from '../../stores/ChannelStatusStore';
import Header from './ChannelCrudHeader';
import AvailableProbesTree from './AvailableProbesTree';
import SelectedProbesTree from './SelectedProbesTree';
import ErrorDialog from '../../components/common/ErrorDialog';
import LoadingProgressBar from '../../components/common/LoadingProgressBar';

var ChannelCrud = React.createClass({
    getInitialState: function () {
        return {
            active: Store.isActive(),
            probes: Store.getProbes(),
            selected: Store.getSelected(),
            probeToTasks: Store.getProbeToTasks(),
            name: Store.getName(),
            logo: Store.getLogo(),
            interval: Store.getInterval(),
            loading: Store.isLoading()
        }
    },

    componentDidMount: function () {
        Store.addChangeListener(this._onChange);
        CrudActions.loadProbeData();
    },

    componentWillUnmount: function () {
        Store.removeChangeListener(this._onChange);
    },

    _onChange: function () {
        this.setState({
            active: Store.isActive(),
            probes: Store.getProbes(),
            selected: Store.getSelected(),
            probeToTasks: Store.getProbeToTasks(),
            name: Store.getName(),
            logo: Store.getLogo(),
            interval: Store.getInterval(),
            loading: Store.isLoading()
        });
    },

    render: function () {
        if (this.state.active) {
            var okToCreate = this.state.name.length > 0 && this.state.selected.size > 0;
            return <div className="modal show">
                <div className="grey-page">
                </div>
                <div className="modal-dialog bigmodal">
                    <div className="modal-content">
                        <div className="modal-header qligent-top-navbar override-padding-5 override-border-0">
                            <img src="img/crud/add-channel.png" className="pull-left" style={{paddingTop: '4px'}}/>
                            <img src="img/modal_close.png" className="pull-right"
                                style={{paddingTop: '6px', paddingRight: '6px'}}
                                onClick={function(){CrudActions.toggleActivateChannel(false);}}/>
                            <h4 className="modal-title">{Store.getActionType() == AppConstants.CRUD_CREATE_SET ?
                                AppUserStore.localizeString(i18nConstants.CREATE_NEW_SET): AppUserStore.localizeString(i18nConstants.UPDATE_SET)}</h4>
                        </div>
                        <div className="modal-body">
                            <div className="container-fluid">
                                <Row>
                                    <Header probes={this.state.probes}
                                        selected={this.state.selected}
                                        name={this.state.name}
                                        logo={this.state.logo}/>
                                </Row>
                                <Row>
                                    <div className="flex crud-tree-panels-wrapper">
                                        <AvailableProbesTree probes={this.state.probes} selected={this.state.selected}
                                            probeToTasks={this.state.probeToTasks}/>
                                        <SelectedProbesTree probes={this.state.probes} selected={this.state.selected}/>
                                    </div>
                                </Row>
                            </div>
                        </div>
                        <div className="modal-footer" style={{paddingTop: '0 !important'}}>
                            <div style={{textAlign: 'center'}}>
                                <button type="button" className={"btn btn-primary"+(okToCreate?"":" disabled")}
                                    onClick={Store.getActionType() == AppConstants.CRUD_CREATE_SET ?
                                        CrudActions.createChannel : CrudActions.updateChannel
                                    }>{Store.getActionType() == AppConstants.CRUD_CREATE_SET ?
                                    AppUserStore.localizeString(i18nConstants.CREATE):
                                    AppUserStore.localizeString(i18nConstants.UPDATE)}</button>
                                <button type="button" className="btn btn-primary"
                                    onClick={function(){CrudActions.toggleActivateChannel(false)}}>
                                    {AppUserStore.localizeString(i18nConstants.CANCEL)}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <ErrorDialog />
                <LoadingProgressBar dataLoaded={!this.state.loading}/>
            </div>
        } else {
            return <div className="modal hide"></div>
        }
    }
});

export default ChannelCrud;