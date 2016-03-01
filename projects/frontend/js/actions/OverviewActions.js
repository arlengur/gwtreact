import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import ChannelStatusStore from '../stores/ChannelStatusStore';
import Misc from '../util/Misc';

// Ensures that calling loadChannelData is idempotent
var timerActive = false;
var timerId = 0;

var OverviewActions = {
    changeChannelView: function(v) {
        AppDispatcher.dispatch({
            actionType: AppConstants.OVERVIEW_CHANGE_VIEW,
            view: v
        });
    },
    loadChannelData: function() {
        var url = "rest/channel/state/channels?includeshistory=true&includesconfiguration=true";
        $.ajax({
            url: url,
            dataType: 'json',
            success: function(data) {
                ChannelStatusStore.loadData(data);
                if(!timerActive) {
                    timerId = setTimeout(function() {
                        timerActive = false;
                        this.loadChannelData();
                    }.bind(this), 10000);
                    timerActive = true;
                }
            }.bind(this),
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
                if(!timerActive) {
                    timerId = setTimeout(function () {
                        timerActive = false;
                        this.loadChannelData();
                    }.bind(this), 10000);
                    timerActive = true;
                }
            }.bind(this)
        });
    },
    addChannelOptimistic: function(ch) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_OPTIMISTIC_ADD,
            channel: ch
        });
    },
    setGridSize: function(width, height) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_PAGE_SIZE,
            width: width,
            height: height
        });
    },
    setPage: function(page) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_SET_PAGE,
            page: page
        });
    },
    toggleSeverity: function(sev) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_TOGGLE_SEVERITY,
            severity: sev
        });
    },
    selectSorting: function(order) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_SET_ORDER,
            order: order
        });
    },
    setFavourite: function(id, isFavourite) {
        AppDispatcher.dispatch({
            actionType: AppConstants.CHANNELS_SET_FAVOURITE,
            id: id,
            favourite: isFavourite
        });
        var url = "rest/channel/config/favourite/" + id;
        $.ajax({
            url: url,
            contentType: 'application/json',
            method: 'PUT',
            headers: {"isFavourite": isFavourite},
            success: function() {
                //don't wait for response
            },
            error: function(xhr, status, err) {
                console.error(url, status, err.toString());
                Misc.refreshIfForbidden(xhr);
            }.bind(this)
        });
    }
};

export default OverviewActions;
