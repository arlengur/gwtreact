import _ from 'lodash';
import AppConstants from '../constants/AppConstants';
import AppDispatcher from '../dispatcher/AppDispatcher';
import DetailsStore from '../stores/ChannelDetailsStore';
import AppUserSettingsStore from '../stores/AppUserSettingsStore';
import Misc from '../util/Misc';

function paramsToUrl (params) {
    var stringParams = [];
    for (var key in params) {
        stringParams.push(key + "=" + params[key]);
    }
    return stringParams.join(";");
}

var DetailsActions = {
    loadChannelData: function(channelId) {
        var url = 'rest/channel/state/' + channelId + '/details';
        $.ajax({
            url: url,
            dataType: 'json',
            success: function (data) {
                DetailsStore.loadData(data);
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    goReports: function (taskIds, interval) {
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#reports;" +
            paramsToUrl({
                tasks: taskIds.join(","),
                startDate: Math.round(interval.start),
                endDate: Math.round(interval.end)
            }))
    },
    goLiveVideo: function (taskIds, interval) {
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#video;" +
            paramsToUrl({
                tasks: taskIds.join(","),
                startDate: Math.round(interval.start),
                endDate: Math.round(interval.end)
            }))
    },
    goRecordedVideo: function (taskIds, interval) {
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#recorded;" +
            paramsToUrl({
                tasks: taskIds.join(","),
                startDate: Math.round(interval.start),
                endDate: Math.round(interval.end)
            }))
    },
    goAnalysis: function (tasks, alerts, interval) {
        var urlParams = {
            tasks: encodeURIComponent(tasks.join(",")),
            startDate: Math.round(interval.start),
            endDate: Math.round(interval.end)
        };
        urlParams = _.reduce(tasks,
            function (urlParams, taskId) {
                var taskParamNames = _.map(alerts.get(taskId), function (alert) {
                    return alert.alert.parameterId
                });
                urlParams[taskId] = encodeURIComponent(taskParamNames.join(","));
                return urlParams;
            },
            urlParams);
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#charts;" + paramsToUrl(urlParams))
    },
    goTaskAnalysis: function (task, parameterId, interval) {
        var urlParams = {
            tasks: task,
            startDate: Math.round(interval.start),
            endDate: Math.round(interval.end)
        };
        urlParams[task] = parameterId;
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#charts;" + paramsToUrl(urlParams))
    },
    goSource: function (probeKey) {
        window.open('./?locale=' + AppUserSettingsStore.getLocale() + "#probeStatus;agentName=" + probeKey)
    },
    treeUnselectAll: function () {
        AppDispatcher.dispatch({
            actionType: AppConstants.DETAILED_TREE_UNSELECT_ALL
        });
    },
    selectTasks: function (ids) {
        AppDispatcher.dispatch({
            actionType: AppConstants.DETAILED_TREE_SELECT_TASKS,
            ids: ids
        });
    },
    unselectTasks: function (ids) {
        AppDispatcher.dispatch({
            actionType: AppConstants.DETAILED_TREE_UNSELECT_TASKS,
            ids: ids
        });
    },
    clearDataLoadIndicator: function () {
        AppDispatcher.dispatch({
            actionType: AppConstants.CLEAR_DATA_LOAD_INDICATOR
        });
    },
    acknowledgeAlert: function (ack, comment, taskId, reportId) {
        var urlPart = ack ? 'acknowledge' : 'unacknowledge';
	    var url = "rest/channel/action/" + urlPart + "/" + taskId;
        $.ajax({
            method: "PUT",
            url: url,
            data: comment,
            contentType: 'text/plain',
            success: function (data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.ALERT_UPDATE,
                    data: data,
                    reportId: reportId
                });
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    clearAlert: function (comment, taskId, reportId) {
        var url = "rest/channel/action/clear/" + taskId;
        $.ajax({
            method: "PUT",
            url: url,
            data: comment,
            contentType: 'text/plain',
            success: function (data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.ALERT_UPDATE,
                    data: data,
                    reportId: reportId
                });
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    commentAlert: function (comment, taskId, reportId) {
        var url = "rest/channel/action/comment/" + taskId;
        $.ajax({
            method: "PUT",
            url: url,
            data: comment,
            contentType: 'text/plain',
            success: function (data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.ALERT_UPDATE,
                    data: data,
                    reportId: reportId
                });
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    openCommentPopup: function (title, reportId, taskId) {
        DetailsActions.getComments(taskId);
        AppDispatcher.dispatch({
            actionType: AppConstants.COMMENT_POPUP_OPEN,
            title: title,
            taskId: taskId,
            reportId: reportId
        });
    },
    selectInterval: function (interval) {
        AppDispatcher.dispatch({
            actionType: AppConstants.SELECT_INTERVAL,
            interval: interval
        });
    },
    getComments: function (taskId) {
        var url = "rest/channel/action/comments/" + taskId;
        $.ajax({
            method: "GET",
            url: url,
            dataType: 'json',
            success: function (data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.COMMENT_POPUP_LOAD_COMMENTS,
                    comments: data
                });
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
};

export default DetailsActions;