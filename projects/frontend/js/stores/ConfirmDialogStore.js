import {EventEmitter} from 'events';
import assign from 'object-assign';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';

var CHANGE_EVENT = 'change';

var _active = false;
var _loading = false;
var _title = "";
var _content = "";
var _closeAction = "";
var _onConfirm = () => {AppDispatcher.dispatch({actionType: AppConstants.CONFIRM_MODAL_HIDE})};
var _onReject= () => {AppDispatcher.dispatch({actionType: AppConstants.CONFIRM_MODAL_HIDE})};

var ConfirmDialogStore = assign({}, EventEmitter.prototype, {
    getState: function() {
        return Immutable.Map({
            active: _active,
            loading: _loading,
            title: _title,
            content: _content,
            onConfirm: _onConfirm,
            onReject: _onReject
        });
    },
    emitChange: function () {
        this.emit(CHANGE_EVENT);
    },
    addChangeListener: function (callback) {
        this.on(CHANGE_EVENT, callback);
    },
    removeChangeListener: function (callback) {
        this.removeListener(CHANGE_EVENT, callback);
    }
});

AppDispatcher.register((action) => {
    switch(action.actionType) {
        case AppConstants.CONFIRM_MODAL_SHOW:
            _active = true;
            _title = action.title;
            _content = action.content;
            _closeAction = action.closeAction;
            if(typeof action.onConfirm == 'function') {
                _onConfirm = () => {
                    // This state flow implies async action, generalize for sync action if/when needed
                    _loading = true;
                    ConfirmDialogStore.emitChange();
                    action.onConfirm();
                }
            }
            if(typeof action.onReject == 'function') {
                _onReject = action.onReject;
            }
            ConfirmDialogStore.emitChange();
            break;
        case AppConstants.CONFIRM_MODAL_HIDE:
        case _closeAction:
            _active = false;
            _loading = false;
            ConfirmDialogStore.emitChange();
            break;
    }
});

export default ConfirmDialogStore;
