import {EventEmitter} from 'events';
import assign from 'object-assign';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';

var CHANGE_EVENT = 'change';

var _active = false;
var _title = "";
var _content = "";

var ErrorDialogStore = assign({}, EventEmitter.prototype, {
    getState: function() {
        return Immutable.Map({
            active: _active,
            title: _title,
            content: _content
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
        case AppConstants.ERROR_MODAL_SHOW:
            _active = true;
            _title = action.title;
            _content = action.content;
            ErrorDialogStore.emitChange();
            break;
        case AppConstants.ERROR_MODAL_HIDE:
            _active = false;
            ErrorDialogStore.emitChange();
            break;
    }
});

export default ErrorDialogStore;
