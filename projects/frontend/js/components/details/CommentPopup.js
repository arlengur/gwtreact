import React from 'react';
import _ from 'lodash';
import Store from '../../stores/ChannelDetailsStore';
import DetailsActions from '../../actions/DetailsActions';
import AppUserStore from '../../stores/AppUserSettingsStore';
import i18nConstants from '../../constants/i18nConstants';
import Misc from '../../util/Misc';

var matchUpdateType = function(updateType){
    switch (updateType) {
        case "ACK": return "MSG_ACKNOWLEDGED";
        case "UNACK": return "MSG_UNACKNOWLEDGED";
        case "OPERATOR_CLEARED": return "OPERATOR_CLEARED";
        case "COMMENT": return "COMMENTED";
        default: return "COMMENTED";
    }
};
var matchOptimisticUpdateType = function(updateType){
    switch (updateType) {
        case "ACKNOWLEDGE": return "ACK";
        case "UNACKNOWLEDGE": return "UNACK";
        case "CLEAR": return "OPERATOR_CLEARED";
        case "COMMENT": return "COMMENT";
        default: return "COMMENT";
    }
};
var CommentPopup = React.createClass({
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
    _onTextInput: function (event) {
        this.setState({
            value: event.target.value
        })
    },
    onAddComment: function(){
        var comment = this.state.value == undefined ? '' : this.state.value.trim();
        if (comment == '') {return;}
        var comments = this.state.comments;
        comments.push({
            "comment": comment,
            "datetime": new Date(),
            "updateType": matchOptimisticUpdateType(this.state.title),
            "userName": AppUserStore.getUsername()
        });
        this.setState({
            value: '',
            comments: comments
        });
        switch (this.state.title) {
            case i18nConstants.ACKNOWLEDGE:
                DetailsActions.acknowledgeAlert(true, comment, this.state.taskId, this.state.reportId);
                break;
            case i18nConstants.UNACKNOWLEDGE:
                DetailsActions.acknowledgeAlert(false, comment, this.state.taskId, this.state.reportId);
                break;
            case i18nConstants.CLEAR:
                DetailsActions.clearAlert(comment, this.state.taskId, this.state.reportId);
                break;
            case i18nConstants.COMMENT:
                DetailsActions.commentAlert(comment, this.state.taskId, this.state.reportId);
        }
    },
    render: function () {
        var comments = _.chain(this.state.comments)
            .map(function (comm) {
                return Misc.formatDate(comm.datetime) + ' '
                    + comm.userName
                    + ' ('
                    + AppUserStore.localizeString(matchUpdateType(comm.updateType))
                    + '):\n'
                    + comm.comment;
            })
            .sortBy('datetime')
            .reverse()
            .value();
        if (this.state.active) {
            return <div className="modal show">
                <div className="grey-page"></div>
                <div className="modal-dialog bigmodal comm-window">
                    <div className="modal-content">
                        <div className="modal-header qligent-top-navbar override-padding-5 override-border-0">
                            <h4 className="modal-title">{AppUserStore.localizeString(this.state.title)}</h4>
                        </div>
                        <div className="modal-body">
                            <label style={{color: '#ffffff', fontWeight: 'normal'}}>
                                {AppUserStore.localizeString(i18nConstants.HISTORY) + ':'}
                            </label>
                            <textarea
                                readOnly="true"
                                disabled="true"
                                className="hljs comm-area"
                                value={comments.join('\n')}
                                rows="6"/>
                            <label style={{color: '#ffffff', fontWeight: 'normal', paddingTop: '10px'}}>
                                {AppUserStore.localizeString(i18nConstants.COMMENT) + ':'}
                            </label>
                            <textarea
                                className="hljs comm-area"
                                rows="6"
                                onChange={this._onTextInput}
                                value={this.state.value}/>
                        </div>
                        <div className="modal-footer" style={{paddingTop: '0 !important'}}>
                            <div style={{textAlign: 'right'}}>
                                <button type="button" className="btn btn-primary"
                                    onClick={this.onAddComment}>
                                    {AppUserStore.localizeStringCFL(i18nConstants.ADD_COMMENT)}
                                </button>
                                <button type="button" className="btn btn-primary"
                                    onClick={this.state.onClose}>
                                    {AppUserStore.localizeStringCFL(i18nConstants.CLOSE)}
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        } else {
            return <div className="modal hide"></div>
        }
    }
});

export default CommentPopup;