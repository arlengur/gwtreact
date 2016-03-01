import React from 'react';
import _ from 'lodash';
import Loading from './LoadingProgressBar';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/ConfirmDialogStore';
import i18nConstants from '../../constants/i18nConstants';

var ConfirmDialog = React.createClass({
    getInitialState: function () {
        return Store.getState().toJS();
    },
    componentDidMount: function () {
        Store.addChangeListener(this._onChange);
    },
    componentWillUnmount: function () {
        Store.removeChangeListener(this._onChange);
    },
    _onChange: function () {
        this.setState(Store.getState().toJS());
    },
    render: function () {
        if(this.state.active) {
            return <div className="modal flex show">
                <div className="modal-dialog bigmodal confirm-page flex flex-col flex-11a">
                    <div className="modal-head flex-none">
                        <img src="img/modal_close.png"
                             className="pull-right"
                             style={{paddingTop: '6px', paddingRight: '6px', cursor: 'pointer'}}
                             onClick={this.state.onReject}/>
                        <h4 className="modal-title">{this.state.title}</h4>
                    </div>
                    {this.state.content}
                    <div className="confirm-footer flex-none">
                        <button type="button"
                                className="btn btn-primary"
                                onClick={this.state.onConfirm}>
                            {AppUserStore.localizeString(i18nConstants.OK)}
                        </button>
                        <button type="button"
                                className="btn btn-primary pull-right"
                                onClick={this.state.onReject}>
                            {AppUserStore.localizeString(i18nConstants.CANCEL)}
                        </button>
                    </div>
                </div>
                {this.state.loading ? <Loading/> : ""}
            </div>
        } else {
            return <div className="modal hide"/>;
        }
    }
});

export default ConfirmDialog;
