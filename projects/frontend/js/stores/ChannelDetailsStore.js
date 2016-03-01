import {EventEmitter} from 'events';
import assign from 'object-assign';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import Misc from '../util/Misc';
import Immutable from 'immutable';
import moment from 'moment';
import _ from 'lodash';

var CHANGE_EVENT = 'change';
var DAY_MILLIS = 24*60*60*1000;
var currentMillis = moment().valueOf();

var _locale;
var _data = {groups: []};
var _probes = [];
var _taskToProbe = Immutable.Map();
var _taskToRecordingTask = Immutable.Map();
var _selectedTasks = Immutable.Set();
var _alertReports = [];
var _probeKey;
var _tasksIds;
var _selectionId;
var _disabledGroups = Immutable.Set();
var _disabledSeverities = Immutable.Set();
var _disabledActivities = Immutable.Set();
var _channelId;
var _channelName;
var _interval = {
    start: currentMillis - DAY_MILLIS,
    end: currentMillis
};
var _selectedInterval = _interval;
var _isDataLoaded = false;

var _active = false;
var _title = "";
var _taskId = undefined;
var _reportId = undefined;
var _comments = [];
var _onClose= () => {AppDispatcher.dispatch({actionType: AppConstants.COMMENT_POPUP_HIDE})};

var ChannelDetailsStore = assign({}, EventEmitter.prototype, {
        updateAlert: function  (data, reportId) {
            _alertReports.forEach(function (alertReport) {
                alertReport.alertsHistory.forEach(function (alertItem) {
                    if (alertItem.alertReportId == reportId) {
                        alertItem.alert.acknowledged = data.acknowledged === 'true';
                        alertItem.alert.cleared = parseInt(data.cleared, 10);
                        alertItem.alert.updated = parseInt(data.updated, 10);
                        alertItem.alert.lastUpdateType = data.lastupdatetype;
                        alertItem.alert.status = data.status;
                    }
                });
            });
            this.emitChange();
        },
        getState: function() {
            return Immutable.Map({
                active: _active,
                title: _title,
                taskId: _taskId,
                reportId: _reportId,
                comments: _comments,
                onClose: _onClose
            });
        },
        loadData: function (data) {
            _data = data.configuration;
            _probes = data.configuration.probes;
            _taskToProbe = Immutable.Map(
                _.chain(_probes)
                .map(function(p) {return _.map(p.tasks, function(t) {return [t.id, p.id]})})
                .flatten()
                .value());
            _taskToRecordingTask = Immutable.Map(
                _.chain(_probes)
                .pluck('tasks')
                .flatten()
                .map(function(t) {return [t.id, t.relatedRecordingTaskId]})
                .value());
            _alertReports = data.parameterStates;
            _disabledGroups = Immutable.Set();
            _channelId = data.channelId;
            _channelName = data.channelName;
            _interval = {start: data.startDate, end: data.endDate};
            _selectedInterval = _interval;
            _isDataLoaded = true;
            this.emitChange();
        },
        loadComments: function (data) {
            _comments = data;
            this.emitChange();
        },
        getTaskById: function (taskId) {
            for (var i = 0; i < _data.probes.length; i++) {
                for (var j = 0; j < _data.probes[i].tasks.length; j++) {
                    var task = _data.probes[i].tasks[j];
                    if (task.id == taskId) {
                        return task;
                    }
                }
            }
        },
        toggleSeverity: function (severity) {
            _disabledSeverities = Misc.toggle(_disabledSeverities, severity);
            ChannelDetailsStore.emitChange();
        },
        toggleGroup: function (group) {
            _disabledGroups = Misc.toggle(_disabledGroups, group);
            if(group == "RF_IP") {
                _disabledGroups = Misc.toggleAll(_disabledGroups, ["RF", "IP"]);
            }
            if(group == "EPG_CC_DATA") {
                _disabledGroups = Misc.toggleAll(_disabledGroups, ["EPG", "CC", "DATA"]);
            }
            ChannelDetailsStore.emitChange();
        },
        toggleActivity: function (activity) {
            _disabledActivities = Misc.toggle(_disabledActivities, activity);
            ChannelDetailsStore.emitChange();
        },
        isDataLoaded: function () {
            return _isDataLoaded;
        },
        getLocale: function() {
            return _locale;
        },
        getChannelId: function () {
            return _channelId;
        },
        getChannelName: function () {
            return _channelName;
        },
        getDisabledActivities: function () {
            return _disabledActivities;
        },
        getDisabledSeverities: function() {
            return _disabledSeverities;
        },
        getDisabledGroups: function() {
            return _disabledGroups;
        },
        getProbeKey: function () {
            return _probeKey;
        },
        getTasksIds: function () {
            return _tasksIds;
        },
        getSelectionId: function() {
            return _selectionId;
        },
        getTaskToProbe: function() {
            return _taskToProbe;
        },
        getTaskToRecordingTask: function() {
            return _taskToRecordingTask;
        },
        getAlertReports: function () {
            return _alertReports;
        },
        getDetails: function () {
            return _data;
        },
        getProbes: function () {
            return _probes;
        },
        getInterval: function () {
            return _interval;
        },
        getSelectedInterval: function () {
            return _selectedInterval;
        },
        getSelectedTasks: function() {
            return _selectedTasks;
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
    })
    ;

AppDispatcher.register(function (action) {
    switch (action.actionType) {
        case AppConstants.GROUP_FILTER_TOGGLE:
            ChannelDetailsStore.toggleGroup(action.group);
            break;
        case AppConstants.SEVERITY_FILTER_TOGGLE:
            ChannelDetailsStore.toggleSeverity(action.severity);
            break;
        case AppConstants.ACTIVITY_FILTER_TOGGLE:
            ChannelDetailsStore.toggleActivity(action.activity);
            break;
        case AppConstants.TREE_CLICK:
            _probeKey = action.probeKey;
            _tasksIds = action.tasksIds;
            _selectionId = action.selectionId;
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.SELECT_INTERVAL:
            _selectedInterval = action.interval;
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.DETAILED_TREE_UNSELECT_ALL:
            _selectedTasks = Immutable.Set();
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.DETAILED_TREE_SELECT_TASKS:
            _selectedTasks = _selectedTasks.union(action.ids);
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.DETAILED_TREE_UNSELECT_TASKS:
            _selectedTasks = _selectedTasks.subtract(action.ids);
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.CLEAR_DATA_LOAD_INDICATOR:
            _isDataLoaded = false;
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.ALERT_UPDATE:
            ChannelDetailsStore.updateAlert(action.data, action.reportId);
            break;
        case AppConstants.COMMENT_POPUP_OPEN:
            _active = true;
            _title = action.title;
            _taskId = action.taskId;
            _reportId = action.reportId;
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.COMMENT_POPUP_HIDE:
            _active = false;
            ChannelDetailsStore.emitChange();
            break;
        case AppConstants.COMMENT_POPUP_LOAD_COMMENTS:
            _comments = action.comments;
            ChannelDetailsStore.emitChange();
            break;

        default:
        // no op
    }
});

export default ChannelDetailsStore;
