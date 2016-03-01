import AppConstants from '../constants/AppConstants';
import {action} from '../dispatcher/AppDispatcher';
import Misc from '../util/Misc';
import Store from '../stores/RecordScheduleStore';
import AppUserStore from '../stores/AppUserSettingsStore';
import i18n from '../constants/i18nConstants';
import Immutable from 'immutable';

export function loadProbes () {
    var url = "rest/recording-scheduler/agent/list";
    $.ajax({
        url: url,
        dataType: 'json',
        method: 'GET',
        success: (agents) => action({
            actionType: AppConstants.RECORD_SCHEDULE.AGENTS_LOADED,
            agents: agents
        }),
        error: function (xhr, status, err) {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function probeSelect (probeKey) {
    action({actionType: AppConstants.RECORD_SCHEDULE.PROBE_SELECT, probeKey: probeKey});
    loadProbesTasks(Store.getState().get('selectedProbes'));
}

export function toggleAllProbes (probesKeys) {
    action({actionType: AppConstants.RECORD_SCHEDULE.TOGGLE_ALL_PROBES, probesKeys: probesKeys});
    loadProbesTasks(Store.getState().get('selectedProbes'));
}

export function taskSelect (taskKey) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.TASK_SELECT,
        taskKey: taskKey
    });
}

export function toggleAllTasks (tasksKeys) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.TOGGLE_ALL_TASKS,
        tasksKeys: tasksKeys
    });
}

export function loadProbesTasks (agentsKeys) {
    if (agentsKeys != null && !agentsKeys.isEmpty()) {
        $.ajax({
            url: 'rest/recording-scheduler/task/list',
            type: 'POST',
            data: JSON.stringify({agents: agentsKeys}),
            dataType: 'json',
            contentType: 'application/json',
            success: (tasks) => action({
                actionType: AppConstants.RECORD_SCHEDULE.TASKS_LOADED,
                tasks: tasks
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    } else {
        action({
            actionType: AppConstants.RECORD_SCHEDULE.TASKS_LOADED,
            tasks: ''
        })
    }
}

export function createSchedule (timeZoneId) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.TOGGLE_CREATE,
        timeZoneId: timeZoneId
    });
}

export function editSchedule (agentKey, taskKey) {
    $.ajax({
        url: 'rest/recording-scheduler/' + agentKey + '/' + taskKey,
        type: 'GET',
        dataType: 'json',
        success: (data) => action({
            actionType: AppConstants.RECORD_SCHEDULE.TOGGLE_EDIT,
            timeZoneId: data.timeZone,
            taskKey: data.taskKey,
            eventList: data.eventList
        }),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function closeSchedule () {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.TOGGLE_CLOSE
    });
}

export function updateProbeFilter (probeFilter) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.UPDATE_PROBE_FILTER,
        probeFilter: probeFilter
    });
}

export function updateTaskFilter (taskFilter) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.UPDATE_TASK_FILTER,
        taskFilter: taskFilter
    });
}

export function loadTimezones () {
    $.ajax({
        url: 'rest/recording-scheduler/timezone/list',
        type: 'GET',
        dataType: 'json',
        success: (timezones) => action({
            actionType: AppConstants.RECORD_SCHEDULE.TIMEZONES_LOADED,
            timezones: timezones
        }),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function addEventLine (line) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.ADD_EVENT_LINE,
        line: line
    });
}

export function removeEventLine (begin) {
    action({
        actionType: AppConstants.RECORD_SCHEDULE.REMOVE_EVENT_LINE,
        begin: begin
    });
}

export function showError (errorText) {
    action({
        actionType: AppConstants.ERROR_MODAL_SHOW,
        title: AppUserStore.localizeStringCFL(i18n.ERROR),
        content: AppUserStore.localizeStringCFL(errorText)
    });
}

export function createRecordScheduler (selectedProbes, batch) {
    $.ajax({
        url: 'rest/recording-scheduler/create/batch',
        type: 'POST',
        data: batch,
        dataType: 'json',
        contentType: 'application/json',
        success: () => loadProbesTasks(selectedProbes),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function updateRecordScheduler (selectedProbes, data) {
    $.ajax({
        url: 'rest/recording-scheduler/update',
        type: 'PUT',
        data: data,
        dataType: 'json',
        contentType: 'application/json',
        success: () => loadProbesTasks(selectedProbes),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function updateSchedule (selectedProbes, batch) {
    $.ajax({
        url: 'rest/recording-scheduler/change-type/batch',
        type: 'PUT',
        data: batch,
        dataType: 'json',
        contentType: 'application/json',
        success: () => {
            action({
                actionType: AppConstants.RECORD_SCHEDULE.UPDATE_SCHEDULE
            });
            loadProbesTasks(selectedProbes)
        },
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}