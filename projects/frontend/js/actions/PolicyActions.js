import AppConstants from '../constants/AppConstants';
import AppDispatcher from '../dispatcher/AppDispatcher';
import Misc from '../util/Misc';

export function toggleCreate() {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.TOGGLE_CREATE
    })
}
export function addNotification() {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.ADD_NOTIFICATION
    })
}
export function removeNotification() {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.REMOVE_NOTIFICATION
    })
}

export function setConditions(conditions) {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.SET_CONDITIONS,
        conditions: conditions
    })
}

export function loadModules() {
    AppDispatcher.dispatch({actionType: AppConstants.POLICY.DIALOG.LOAD_MODULES});
    var url = "rest/group-policy/module/list/all";
    $.ajax({
        url: url,
        dataType: 'json',
        method: 'GET',
        success: function(data) {
            AppDispatcher.dispatch({
                actionType: AppConstants.POLICY.DIALOG.MODULES_LOADED,
                data: data
            });
        },
        error: function(xhr, status, err) {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }.bind(this)
    });
}

export function selectModule(module) {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.SELECT_MODULE,
        module: module
    });
    if(module != " ") {
        var url = "rest/group-policy/parameter/list";
        $.ajax({
            url: url,
            dataType: 'json',
            method: 'GET',
            data: {moduleName: module},
            success: function(data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.POLICY.DIALOG.PARAMS_LOADED,
                    data: data
                });
            },
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
}

export function selectParam(param, module) {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.SELECT_PARAM,
        param: param
    });
    if(param != " ") {
        var url = "rest/group-policy/task/list";
        $.ajax({
            url: url,
            dataType: 'json',
            method: 'GET',
            data: {
                moduleName: module,
                parameterName: param
            },
            success: function (data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.POLICY.DIALOG.TASKS_LOADED,
                    data: data
                });
            },
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
}

export function selectTasks(tasks, param, module) {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.SELECT_TASKS,
        tasks: tasks
    });
    if(tasks.size > 0) {
        var url = "rest/group-policy/agent/list";
        $.ajax({
            url: url,
            dataType: 'json',
            method: 'GET',
            data: {
                moduleName: module,
                parameterName: param,
                taskNames: tasks.toJS()
            },
            success: function(data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.POLICY.DIALOG.PROBES_LOADED,
                    data: data
                });
            },
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
}

export function selectProbes(probes) {
    AppDispatcher.dispatch({
        actionType: AppConstants.POLICY.DIALOG.SELECT_PROBES,
        probes: probes
    });
}