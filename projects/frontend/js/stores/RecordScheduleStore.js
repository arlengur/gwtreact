import {EventEmitter} from 'events';
// TODO: remove object-assign, use either ES6 Object.assign, or _.assign
import assign from 'object-assign';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import Misc from '../util/Misc';
import _ from 'lodash';
import i18n from '../constants/i18nConstants';
import AppUserSettingsStore from '../stores/AppUserSettingsStore';
const CHANGE_EVENT = 'change';
const tr = AppUserSettingsStore.localizeString;


// TODO: templates and alerts types should be loaded on first load,
// but how do we show that they are loading if we got to the end too quickly?

var _state = Immutable.Map({
    crudActive: false,
    editActive: false,
    multiTimeZone: false,
    isWarningShown: false,
    timeZoneId: '',
    agents: [],
    probeFilter: '',
    taskFilter: '',
    tasks: [],
    timezones: [],
    localTimeZone: '',
    eventList: [],
    selectedTasks: Immutable.Set(),
    selectedProbes: Immutable.Set()
});


var RecordScheduleStore = assign({}, EventEmitter.prototype, {
    getState: () => _state,
    setLocalTimeZone: (timezones) => {
        //TODO Hardcore fix, cause moment.tz.guess() doesn't always work for russian locales
        //TODO We do not need local timezone itself, only offset used to convert the TimeSchedule to UTC
        var offset = new Date().toString().match(/([-\+][0-9]+)\s/)[1];
        var localTimezoneIndex = _.findIndex(timezones, function(o) { return o.offset == offset });
        localTimezoneIndex = localTimezoneIndex == -1 ? 0 : localTimezoneIndex;
        var localTimeZoneId = timezones[localTimezoneIndex].id;
        _state = _state.set('localTimeZone', localTimeZoneId);
    },

    // Can't use es6 lambda because of 'this' semantics
    emitChange: function () {this.emit(CHANGE_EVENT)},
    addChangeListener: function (callback) {this.on(CHANGE_EVENT, callback)},
    removeChangeListener: function (callback) {this.removeListener(CHANGE_EVENT, callback)}
});

AppDispatcher.register(function (action) {
    switch (action.actionType) {
        case AppConstants.RECORD_SCHEDULE.UPDATE_PROBE_FILTER:
            _state = _state
                .set('probeFilter', action.probeFilter);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.UPDATE_TASK_FILTER:
            _state = _state
                .set('taskFilter', action.taskFilter);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TOGGLE_CREATE:
            _state = _state
                .set('crudActive', true)
                .set('timeZoneId', action.timeZoneId);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TOGGLE_EDIT:
            _state = _state
                .set('crudActive', true)
                .set('editActive', true)
                .set('timeZoneId', action.timeZoneId)
                .set('eventList', action.eventList)
                .set('selectedTasks', Immutable.Set([action.taskKey]));
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TOGGLE_CLOSE:
            _state = _state
                .set('crudActive', false)
                .set('editActive', false)
                .set('timeZoneId', '')
                .set('eventList', [])
                .set('selectedTasks', Immutable.Set());
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.UPDATE_SCHEDULE:
            _state = _state
                .set('selectedTasks', Immutable.Set());
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TIMEZONES_LOADED:
            var timezones = action.timezones;
            RecordScheduleStore.setLocalTimeZone(timezones);
            timezones.splice(0, 0,
                {"id": _state.get('localTimeZone'), "description": tr(i18n.LOCAL_TIMEZONE)},
                {"id": "Probe", "description": tr(i18n.PROBE_TIMEZONE)});
            _state = _state.set('timezones', timezones);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.AGENTS_LOADED:
            _state = _state.set('agents', action.agents);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TASKS_LOADED:
            _state = _state.set('tasks', action.tasks);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.ADD_EVENT_LINE:
            _state = _state.update('eventList', (v)=>v.concat(action.line));
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.REMOVE_EVENT_LINE:
            _state = _state.update('eventList', (vals)=>_.filter(vals, (v)=>v.begin !== action.begin));
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.PROBE_SELECT:
            _state = _state.set('selectedProbes', Misc.toggle(_state.get('selectedProbes'), action.probeKey));
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TOGGLE_ALL_PROBES:
            if (_.every(action.probesKeys, (key) => _state.get('selectedProbes').contains(key))) {
                _state = _state.set('selectedProbes', Immutable.Set());
            } else {
                _state = _state.set('selectedProbes', Immutable.Set(action.probesKeys));
            }
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TASK_SELECT:
            let selected = Misc.toggle(_state.get('selectedTasks'), action.taskKey);
            let multiTZ =_.chain(_state.get('tasks')).filter((task)=> {return selected.contains(task.entityKey)}).pluck('timeZone').uniq().value().length > 1;
            _state = _state
                .set('selectedTasks', selected)
                .set('multiTimeZone', multiTZ);
            RecordScheduleStore.emitChange();
            break;
        case AppConstants.RECORD_SCHEDULE.TOGGLE_ALL_TASKS:
            if (_.every(action.tasksKeys, (key) => _state.get('selectedTasks').contains(key))) {
                _state = _state
                    .set('selectedTasks', Immutable.Set())
                    .set('multiTimeZone', false);
            } else {
                let selected = Immutable.Set(action.tasksKeys);
                let multiTZ =_.chain(_state.get('tasks')).filter((task)=> {return selected.contains(task.entityKey)}).pluck('timeZone').uniq().value().length > 1;
                _state = _state
                    .set('selectedTasks', selected)
                    .set('multiTimeZone', multiTZ);
            }
            RecordScheduleStore.emitChange();
            break;
    }
});

export default RecordScheduleStore;
