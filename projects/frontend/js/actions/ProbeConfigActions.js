import _ from 'lodash';
import AppConstants from '../constants/AppConstants';
import AppDispatcher from '../dispatcher/AppDispatcher';
import Store from '../stores/ProbeConfigStore';
import AppUserStore from '../stores/AppUserSettingsStore';
import Misc from '../util/Misc';
import i18n from '../constants/i18nConstants';

var actions = AppConstants.PROBE_CONFIG;

function keysToUrl (keys) {
    return _.map(keys, function (key) {return 'keys=' + key}).join('&');
}

var ProbeConfigActions = {
    loadProbeData: () => {
        var url = 'rest/probe-config/stats';
        $.ajax({
            url: url,
            dataType: 'json',
            success: (data) => AppDispatcher.dispatch({
                actionType: actions.RESPONSE.PROBE_STATS,
                data: data
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    downloadProbeConfFile: (key) => {
        var url = 'rest/probe-config/download_config?key=' + key;
        $.ajax({
            url: url,
            type: 'GET',
            dataType: 'xml',
            success: (response, status, xhr) => {
                var a = document.createElement("a");
                document.body.appendChild(a);
                a.style.display = "none";
                var blob = new Blob([xhr.responseText], {type: "application/xml"});
                var url = window.URL.createObjectURL(blob);
                a.href = url;
                a.download = key + ".xml";
                a.click();
                window.URL.revokeObjectURL(url);
                document.body.removeChild(a);
            },
            error: (xhr, status, err) => {
                if (xhr.status === 404) {
                    AppDispatcher.dispatch({
                        actionType: AppConstants.ERROR_MODAL_SHOW,
                        title: AppUserStore.localizeStringCFL(i18n.ERROR),
                        content: AppUserStore.localizeStringCFL(i18n.PROBE_CONFIGURATION_NOT_FOUND)
                    });
                } else if (xhr.status === 503) {
                    AppDispatcher.dispatch({
                        actionType: AppConstants.ERROR_MODAL_SHOW,
                        title: AppUserStore.localizeStringCFL(i18n.ERROR),
                        content: AppUserStore.localizeStringCFL(i18n.PROBE_CONFIGURATION_UNAVAILABLE)
                    });
                } else {
                    console.error(url, status, err.toString());
                    Misc.refreshIfForbidden(xhr);
                }
            }
        });
    },
    toggleConfigEditor: () => AppDispatcher.dispatch({
        actionType: actions.MODAL.CONFIG_EDITOR
    }),
    updateProbeConfig: (keys, file) => {
        var url = 'rest/probe-config/update_config?' + keysToUrl(keys);
        var fd = new FormData();
        fd.append("file", file);
        $.ajax({
            url: url,
            type: 'POST',
            data: fd,
            dataType: 'json',
            enctype: 'multipart/form-data',
            processData: false,
            contentType: false,
            success: (probeKeys) => AppDispatcher.dispatch({
                actionType: actions.RESPONSE.CONFIG_UPDATE,
                keys: probeKeys
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    probeSwRestart: (keys) => {
        var url = 'rest/probe-config/restart-sw?' + keysToUrl(keys);
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            success: (probeKeys) => AppDispatcher.dispatch({
                    actionType: actions.RESPONSE.SW_RESTART,
                    keys: probeKeys
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    probeHwRestart: (keys) => {
        var url = 'rest/probe-config/restart-hw?' + keysToUrl(keys);
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            success: (probeKeys) => AppDispatcher.dispatch({
                actionType: actions.RESPONSE.HW_RESTART,
                keys: probeKeys
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    rollbackProbe: (keys) => {
        var url = 'rest/probe-config/rollback?' + keysToUrl(keys);
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            success: (probeKeys) => AppDispatcher.dispatch({
                actionType: actions.RESPONSE.CONFIG_ROLLBACK,
                keys: probeKeys
            }),
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    swUpdate: (swPath, keys) => {
        var url = 'rest/probe-config/update-sw?sw-fileName=' + swPath + '&' + keysToUrl(keys);
        $.ajax({
            url: url,
            type: 'POST',
            dataType: 'json',
            success: (probeKeys) => {
                AppDispatcher.dispatch({
                    actionType: actions.RESPONSE.SW_UPDATE,
                    keys: probeKeys
                });
                ProbeConfigActions.loadProbeData();
            },
            error: (xhr, status, err) => {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }
        });
    },
    probeSelect: (_probeKey) => AppDispatcher.dispatch({
        actionType: actions.TREE.PROBE_SELECT,
        probeKey: _probeKey
    }),
    toggleAll: () => AppDispatcher.dispatch({
        actionType: actions.TREE.TOGGLE_ALL
    }),
    taskSelect: (_taskKey) => AppDispatcher.dispatch({
        actionType: actions.TREE.TASK_SELECT,
        taskKey: _taskKey
    }),
    swUpdateModal: (visible) => {
        if(visible) {
            var url = 'rest/probe-config/probe-sw-list';
            $.ajax({
                url: url,
                type: 'GET',
                dataType: 'json',
                success: (data) => AppDispatcher.dispatch({
                    actionType: actions.RESPONSE.SW_LIST,
                    swList: data
                }),
                error: (xhr, status, err) => {
                    console.error(url, status, err.toString());
                    Misc.refreshIfForbidden(xhr);
                }
            });
        }
        AppDispatcher.dispatch({
            actionType: actions.MODAL.UPDATE_SW,
            visible: visible
        })
    },
    toggleExpanded: (probeKey) => AppDispatcher.dispatch({
        actionType: actions.TREE.TOGGLE_EXPANDED,
        probeKey: probeKey
    }),
    updateFilter: (filter, expanded) => AppDispatcher.dispatch({
        actionType: actions.TREE.UPDATE_FILTER,
        filter: filter,
        expanded: expanded
    }),
    resizeColumns: (sizes) => AppDispatcher.dispatch({
        actionType: actions.TREE.RESIZE_COLUMNS,
        sizes: sizes
    }),
    startResize: (col) => AppDispatcher.dispatch({
        actionType: actions.TREE.START_RESIZE,
        col: col
    }),
    stopResize: () => AppDispatcher.dispatch({
        actionType: actions.TREE.STOP_RESIZE
    })
};

export default ProbeConfigActions;