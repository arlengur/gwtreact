import _ from 'lodash';
import Immutable from 'immutable';
import {EventEmitter} from 'events';
import assign from 'object-assign';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import Misc from '../util/Misc';
import Paging from '../util/ChannelGridPaging';

var CHANGE_EVENT = 'change';

var _channelsState = [];
var _view = 3;
var _isDataLoaded = false;
// Used to calculate number of elements per page
var _gridWidth = 1024;
var _gridHeight = 768;
var _currentPage = 1;
var _disabledSev = Immutable.Set();
var _order = 'Created';

var ChannelStatusStore = assign({}, EventEmitter.prototype, {

    loadData: function (data) {
        _channelsState = data["channelsState"];
        _isDataLoaded = true;
        this.emitChange();
    },
    isDataLoaded: function () {
        return _isDataLoaded;
    },
    getMaxId: function() {
        return _.max(_.pluck(_channelsState, 'channelId'));
    },
    getChannelsState: function() {
        return _channelsState;
    },
    getView: function() {
        return _view;
    },
    getChannelsPerPage: function() {
        return Paging.channelsPerPage(_gridWidth, _gridHeight, _view);
    },
    getCurrentPage: function() {
        return _currentPage;
    },
    getDisabledSev: function() {
        return _disabledSev;
    },
    getOrder: function() {
        return _order;
    },
    emitChange: function() {
        this.emit(CHANGE_EVENT);
    },
    addChangeListener: function(callback) {
        this.on(CHANGE_EVENT, callback);
    },
    removeChangeListener: function(callback) {
        this.removeListener(CHANGE_EVENT, callback);
    }
});

AppDispatcher.register(function(action) {
    switch(action.actionType) {
        case AppConstants.OVERVIEW_CHANGE_VIEW:
            _view = action.view;
            _currentPage = 1;
            ChannelStatusStore.emitChange();
            break;
        case AppConstants.CHANNELS_OPTIMISTIC_ADD:
            _channelsState.push(action.channel);
            ChannelStatusStore.emitChange();
            break;
        case AppConstants.CHANNELS_PAGE_SIZE:
            _gridWidth = action.width;
            _gridHeight = action.height;
            ChannelStatusStore.emitChange();
            break;
        case AppConstants.CHANNELS_SET_PAGE:
            _currentPage = action.page;
            ChannelStatusStore.emitChange();
            break;
        case AppConstants.CHANNELS_TOGGLE_SEVERITY:
            var newDisabled = Misc.toggle(_disabledSev, action.severity);
            if(newDisabled.size != 3) {
                _disabledSev = newDisabled;
                _currentPage = 1;
                ChannelStatusStore.emitChange();
            }
            break;
        case AppConstants.CHANNELS_SET_ORDER:
            _order = action.order;
            ChannelStatusStore.emitChange();
            break;
        case AppConstants.CHANNELS_SET_FAVOURITE:
            // TODO: we have to deep copy here to avoid mutation in place,
            // should use Immutable vector for state instead
            var newChannelState = _.cloneDeep(_channelsState);
            var ch = _.find(newChannelState, (ch)=>ch.channelId == action.id);
            ch.configuration.isFavourite = action.favourite;
            _channelsState = newChannelState;
            ChannelStatusStore.emitChange();
            break;
        default:
            // no op
    }
});

export default ChannelStatusStore;
