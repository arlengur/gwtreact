import React from 'react';
import AppActions from '../../actions/AppActions';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/ErrorDialogStore';
import i18n from '../../constants/i18nConstants';

var ErrorDialog = React.createClass({
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
                <div className="modal-dialog bigmodal error-page flex flex-col flex-11a">
                    <div className="modal-head flex-none">
                        <img src="img/modal_close.png"
                             className="pull-right"
                             style={{paddingTop: '6px', paddingRight: '6px', cursor: 'pointer'}}
                             onClick={AppActions.closeErrorModal}/>
                        <h4 className="modal-title">{this.state.title}</h4>
                    </div>
                    <div className="flex flex-11a flex-col error-panel">
                        <div className="flex-none error-text">
                            {this.state.content + '.'}
                        </div>
                    </div>
                    <div className="error-footer flex-none">
                        <button type="button"
                                className="btn btn-primary pull-right"
                                onClick={AppActions.closeErrorModal}>
                            {AppUserStore.localizeString(i18n.CLOSE)}
                        </button>
                    </div>
                </div>
            </div>
        } else {
            return <div className="modal hide"/>;
        }
    }
});

export default ErrorDialog;
