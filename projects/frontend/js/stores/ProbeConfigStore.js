import {addons} from 'react/addons';
const {update} = addons;
import {EventEmitter} from 'events';
import assign from 'object-assign';
import Immutable from 'immutable';
import _ from 'lodash';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import Misc from '../util/Misc';

var probeConfigActions = AppConstants.PROBE_CONFIG;
var CHANGE_EVENT = 'change';

var _data = {};
var _selectedProbes = Immutable.Set();
var _selectedTasks = Immutable.Set();
var _configActive = false;
var _swUpdateModal = false;
var _swList = {};
var _filter = '';
var _expanded = Immutable.Set();
var _columnSizes = [30, 25, 9, 9, 9, 9, 9];
var _headerSizes = [30, 25, 9, 9, 9, 9, 9];
var _resizedColumn = -1;

// TODO: make pure (it's trivial)
var updateProbeState = (probeKeys, _state) => {
    _data = _.map(_data, function(probe) {
        if(probeKeys.indexOf(probe.component.key) != -1) {
            return update(probe, {$merge: {state: _state, lastResultTime: null, registrationTime: null}});
        } else {
            return probe;
        }
    });
};

var ProbeConfigStore = assign({}, EventEmitter.prototype, {
    getData:           () => _data,
    getSelectedProbes: () => _selectedProbes,
    getSelectedTasks:  () => _selectedTasks,
    getConfigActive:   () => _configActive,
    getSwUpdateModal:  () => _swUpdateModal,
    getSwList:         () => _swList,
    getFilter:         () => _filter,
    getExpanded:       () => _expanded,
    getColumnSizes:    () => _columnSizes,
    getHeaderSizes:    () => _headerSizes,
    getResizedColumn:  () => _resizedColumn,

    // Can't use es6 lambda because of 'this' semantics
    emitChange: function () {this.emit(CHANGE_EVENT)},
    addChangeListener: function (callback) {this.on(CHANGE_EVENT, callback)},
    removeChangeListener: function (callback) {this.removeListener(CHANGE_EVENT, callback)}
});

AppDispatcher.register(function (action) {
    switch (action.actionType) {
        case probeConfigActions.TREE.PROBE_SELECT:
            _selectedProbes = Misc.toggle(_selectedProbes, action.probeKey);
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.TASK_SELECT:
            _selectedTasks = Misc.toggle(_selectedTasks, action.taskKey);
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.TOGGLE_ALL:
            var registeredKeys = _.chain(_data)
                    .filter((d)=>d.state == 'SUCCESS')
                    .pluck('component.key')
                    .value();
            if(_.every(registeredKeys, (key) => _selectedProbes.contains(key))) {
                _selectedProbes = Immutable.Set();
            } else {
                _selectedProbes = Immutable.Set(registeredKeys);
            }
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.TOGGLE_EXPANDED:
            _expanded = Misc.toggle(_expanded, action.probeKey);
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.UPDATE_FILTER:
            _filter = action.filter;
            _expanded = action.expanded;
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.RESIZE_COLUMNS:
            _headerSizes = action.sizes;
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.START_RESIZE:
            _resizedColumn = action.col;
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.TREE.STOP_RESIZE:
            _columnSizes = _headerSizes;
            _resizedColumn = -1;
            ProbeConfigStore.emitChange();
            break;

        case probeConfigActions.RESPONSE.SW_RESTART:
            _selectedProbes = Immutable.Set();
            updateProbeState(action.keys, 'RESTART_SW');
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.HW_RESTART:
            _selectedProbes = Immutable.Set();
            updateProbeState(action.keys, 'RESTART_HW');
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.CONFIG_ROLLBACK:
            _selectedProbes = Immutable.Set();
            updateProbeState(action.keys, 'CONFIG_ROLLBACK');
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.CONFIG_UPDATE:
            _configActive = false;
            _selectedProbes = Immutable.Set();
            updateProbeState(action.keys, 'UPDATE_CONFIG');
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.SW_UPDATE:
            _swUpdateModal = false;
            _selectedProbes = Immutable.Set();
            updateProbeState(action.keys, 'UPDATE_SOFTWARE');
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.SW_LIST:
            _swList = action.swList;
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.RESPONSE.PROBE_STATS:
            _data = action.data;
            _selectedProbes = Immutable.Set();
            _selectedTasks = Immutable.Set();
            ProbeConfigStore.emitChange();
            break;

        case probeConfigActions.MODAL.CONFIG_EDITOR:
            _configActive = !_configActive;
            ProbeConfigStore.emitChange();
            break;
        case probeConfigActions.MODAL.UPDATE_SW:
            _swUpdateModal = action.visible;
            ProbeConfigStore.emitChange();
            break;
        default:
            // no op
    }
});

export default ProbeConfigStore;
