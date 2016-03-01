import _ from 'lodash';
import OverviewActions from './OverviewActions';
import DetailsActions from './DetailsActions';
import AppConstants from '../constants/AppConstants';
import AppDispatcher from '../dispatcher/AppDispatcher';
import CrudStore from '../stores/ChannelCrudStore';
import Misc from '../util/Misc';
import AppUserStore from '../stores/AppUserSettingsStore';
import i18n from '../constants/i18nConstants';

var ChannelCrudActions = {
    toggleCreateChannel: function() {
        AppDispatcher.dispatch({
            actionType: AppConstants.TOGGLE_CREATE_CHANNEL
        });
    },
    toggleActivateChannel: function(t) {
        AppDispatcher.dispatch({
            actionType: AppConstants.TOGGLE_ACTIVATE_CHANNEL,
            active: t
        });
    },
    loadProbeData: function() {
        var url = "rest/channel/config/probes";
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.CHANNEL_CRUD_LOAD_PROBES,
                    probes: data
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    selectTasks: function(ids) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_SELECT_TASKS,
            ids: ids
        });
    },
    unselectTask: function(id) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_UNSELECT_TASKS,
            ids: [id]
        });
    },
    unselectProbes: function(ids) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_UNSELECT_TASKS,
            ids: _.flatten(_.map(ids,
                function(id) {
                    return CrudStore.getProbeToTasks().get(id);
                }))
        });
    },
    setChannelName: function(name) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_SET_NAME,
            name: name
        });
    },
    setChannelLogo: function(logo) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_SET_LOGO,
            logo: logo
        });
    },
    newImageSelected: function(file) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_NEW_IMAGE,
            file: file
        });
    },
    setChannelInterval: function(int) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNEL_CRUD_SET_INTERVAL,
            int: int
        });
    },
    createChannel: function() {
        CrudStore.setLoading(true);
        var request = {
            name: CrudStore.getName(),
            logo: CrudStore.getLogo(),
            isChannel: true,
            interval: CrudStore.getInterval(),
            probes: CrudStore.getCurrentTaskConfig()
        };
        var url = "rest/channel/config";
        $.ajax({
            url: url,
            data: JSON.stringify(request),
            contentType: 'application/json',
            method: 'POST',
            success: () => {
                CrudStore.setLoading(false);
                OverviewActions.addChannelOptimistic(CrudStore.getCurrentChannel());
                ChannelCrudActions.toggleCreateChannel();
                ChannelCrudActions.toggleActivateChannel(false);
            },
            error: (xhr, status, err) => {
                CrudStore.setLoading(false);
                if (xhr.status === 409) {
                    AppDispatcher.dispatch({
                        actionType: AppConstants.ERROR_MODAL_SHOW,
                        title: AppUserStore.localizeStringCFL(i18n.ERROR),
                        content: AppUserStore.localizeStringCFL(i18n.CHANNEL_NAME_SHOULD_BE_UNIQUE)
                    });
                } else {
                    console.error(url, status, err.toString());
                    Misc.refreshIfForbidden(xhr);
                }
            }
        });
    },
    getChannelConfig: function(id) {
        var url = "rest/channel/config/" + id;
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data) {
                AppDispatcher.dispatch({
                    actionType: AppConstants.TOGGLE_EDIT_CHANNEL,
                    config: data
                });
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    updateChannel: function() {
        var channelId = CrudStore.getId();

        var request = {
            id: channelId,
            name: CrudStore.getName(),
            logo: CrudStore.getLogo(),
            isChannel: true,
            interval: CrudStore.getInterval(),
            probes: CrudStore.getCurrentTaskConfig()
        };
        var url = "rest/channel/config";
        $.ajax({
            url: url,
            data: JSON.stringify(request),
            contentType: 'application/json',
            method: 'PUT',
            success: function() {
                DetailsActions.loadChannelData(channelId);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
        ChannelCrudActions.toggleActivateChannel(false);
    },
    removeChannel:function(id) {
        var url = "rest/channel/config/" + id;
        $.ajax({
            url: url,
            method: 'DELETE',
            dataType: 'json',
            success: function() {
                ChannelCrudActions.toggleActivateChannel(false);
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
};

export default ChannelCrudActions;