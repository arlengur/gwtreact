import {EventEmitter} from 'events';
// TODO: remove object-assign, use either ES6 Object.assign, or _.assign
import assign from 'object-assign';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';

const CHANGE_EVENT = 'change';

// Create dialog state
export const DIALOG_STATE = {
    MODULE_LOADING: 1,
    MODULE_LOADED: 2,
    PARAM_LOADING: 3,
    PARAM_LOADED: 4,
    TASKS_LOADING: 5,
    TASKS_LOADED: 6,
    PROBES_LOADING: 7,
    PROBES_LOADED: 8,
    PROBES_SELECTED: 9
};

// TODO: templates and alerts types should be loaded on first load,
// but how do we show that they are loading if we got to the end too quickly?

var _state = Immutable.Map({
    createActive: false,
    createState: DIALOG_STATE.MODULE_LOADING,
    modules: [],
    params: [],
    tasks: {},
    probes: [],
    selectedModule: " ",
    selectedParam: " ",
    selectedTasks: Immutable.Set(),
    selectedProbes: Immutable.Set(),
    conditions: Immutable.Set(),
    notifications: 0
});


var PolicyStore = assign({}, EventEmitter.prototype, {
    getState: () => _state,

    // Can't use es6 lambda because of 'this' semantics
    emitChange: function () {this.emit(CHANGE_EVENT)},
    addChangeListener: function (callback) {this.on(CHANGE_EVENT, callback)},
    removeChangeListener: function (callback) {this.removeListener(CHANGE_EVENT, callback)}
});

AppDispatcher.register(function(action) {
    switch(action.actionType) {
        case AppConstants.POLICY.DIALOG.TOGGLE_CREATE:
            _state = _state.update('createActive', (v)=>!v);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.ADD_NOTIFICATION:
            _state = _state.update('notifications',(n)=>n+1);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.REMOVE_NOTIFICATION:
            _state = _state.update('notifications',(n)=>n-1);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.LOAD_MODULES:
            _state = _state.set('createState', DIALOG_STATE.MODULE_LOADING);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.MODULES_LOADED:
            _state = _state
                .set('createState', DIALOG_STATE.MODULE_LOADED)
                .set('modules', action.data);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.SELECT_MODULE: {
            let newState = action.module == " " ?
                DIALOG_STATE.MODULE_LOADED:
                DIALOG_STATE.PARAM_LOADING;
            _state = _state
                .set('createState', newState)
                .set('selectedModule', action.module)
                .set('params', [])
                .set('selectedParam', ' ')
                .set('tasks', [])
                .set('selectedTasks', Immutable.Set())
                .set('probes', [])
                .set('selectedProbes', Immutable.Set());
            PolicyStore.emitChange();
            break;
        }
        case AppConstants.POLICY.DIALOG.PARAMS_LOADED:
            _state = _state
                .set('createState', DIALOG_STATE.PARAM_LOADED)
                .set('params', action.data);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.SELECT_PARAM: {
            let newState = action.param== " " ?
                DIALOG_STATE.PARAM_LOADED :
                DIALOG_STATE.TASKS_LOADING;
            _state = _state
                .set('createState', newState)
                .set('selectedParam', action.param)
                .set('tasks', [])
                .set('selectedTasks', Immutable.Set())
                .set('probes', [])
                .set('selectedProbes', Immutable.Set());
            PolicyStore.emitChange();
            break;
        }
        case AppConstants.POLICY.DIALOG.TASKS_LOADED:
            _state = _state
                .set('createState', DIALOG_STATE.TASKS_LOADED)
                .set('tasks', action.data);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.SELECT_TASKS: {
            let newState = action.tasks.size == 0 ?
                DIALOG_STATE.TASKS_LOADED :
                DIALOG_STATE.PROBES_LOADING;
            _state = _state
                .set('createState', newState)
                .set('selectedTasks', action.tasks)
                .set('probes', [])
                .set('selectedProbes', Immutable.Set());
            PolicyStore.emitChange();
            break;
        }
        case AppConstants.POLICY.DIALOG.PROBES_LOADED:
            _state = _state
                .set('createState', DIALOG_STATE.PROBES_LOADED)
                .set('probes', action.data);
            PolicyStore.emitChange();
            break;
        case AppConstants.POLICY.DIALOG.SELECT_PROBES: {
            let newState = action.probes.size==0 ?
                DIALOG_STATE.PROBES_LOADED :
                DIALOG_STATE.PROBES_SELECTED;
            _state = _state
                .set('selectedProbes', action.probes)
                .set('createState', newState);
            PolicyStore.emitChange();
            break;
        }
        case AppConstants.POLICY.DIALOG.SET_CONDITIONS:
            _state = _state.set('conditions', action.conditions);
            PolicyStore.emitChange();
            break;
    }
});

export default PolicyStore;
