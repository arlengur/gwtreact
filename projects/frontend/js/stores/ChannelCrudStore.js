import {EventEmitter} from 'events';
import assign from 'object-assign';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import OverviewActions from '../actions/OverviewActions';
import ChannelStatusStore from './ChannelStatusStore';
import Immutable from 'immutable';
import Images from '../util/Images';
import _ from 'lodash';

var CHANGE_EVENT = 'change';

var _active = false;
var _loading = false;
var _selectedTasks = Immutable.Set();
var _probes = [];
var _probeToTasks = Immutable.Map();
var _name = '';
var _id;
var _interval;
var _logo = Images.getDefaultChannelLogo();
var _actionType = AppConstants.CRUD_CREATE_SET;

var ChannelCrudStore = assign({}, EventEmitter.prototype, {

    toggleActive: function(active) {
        _active= active;
        this.emitChange();
    },
    toggleCreate: function() {
        _active= true;
        _actionType = AppConstants.CRUD_CREATE_SET;
        _id = ChannelStatusStore.getMaxId() + 1;
        _selectedTasks = Immutable.Set();
        _name = '';
        _interval = 24*60*60*1000;
        this.emitChange();
    },
    toggleEdit: function(config) {
        _active= true;
        _actionType = AppConstants.CRUD_UPDATE_SET;
        _id = config.id;
        _name = config.name;
        _logo = config.logo;
        _interval = config.interval;
        _selectedTasks = Immutable.Set(
            _.flatten(
            _.map(config.probes, function(p) {
                return _.pluck(p.tasks, 'id');})));

        this.emitChange();
    },
    loadData: function(probes) {
        _probes= probes;
        _probeToTasks = Immutable.Map(
            _.map(probes, function(p) {
                return [p.id, _.pluck(p.tasks, 'id')];}));
        this.emitChange();
    },
    selectTasks: function(ids) {
        _selectedTasks = _selectedTasks.union(ids);
        this.emitChange();
    },
    unselectTasks: function(ids) {
        _selectedTasks = _selectedTasks.subtract(ids);
        this.emitChange();
    },
    setName: function(name) {
        _name = name;
        this.emitChange();
    },
    setLogo: function(logo) {
        _logo = logo;
        this.emitChange();
    },
    setInterval: function(int) {
        _interval = int;
        this.emitChange();
    },
    setLoading: function(loading) {
        _loading = loading;
        this.emitChange();
    },
    getName: function() {return _name},
    getLogo: function() {return _logo},
    isActive: function() {return _active},
    isLoading: function() {return _loading},
    getProbes: function() {return _probes},
    getInterval: function() {return _interval},
    getSelected: function() {return _selectedTasks},
    getProbeToTasks: function() {return _probeToTasks},
    getActionType: function() {return _actionType},
    getId: function() {return _id},
    getDefaultStream: function() {
        var streamTask = _.chain(_probes)
            .pluck('tasks')
            .flatten()
            .filter(function(t) {
                return _selectedTasks.contains(t.id)
                    && typeof t.relatedRecordingTaskId != 'undefined'
                    && t.relatedRecordingTaskId != null
            })
            .first()
            .value();
        if(typeof streamTask != 'undefined') {
            return {
                id: streamTask.id,
                url: streamTask.streams.RTMP
            };
        } else {
            return {
                id: -1,
                url: ''
            };
        }
    },
    getCurrentTaskConfig: function() {
        var streamTaskId = this.getDefaultStream().id;
        return _.map(
            _.filter(_probes, function(p) {
                return _.some(p.tasks, function(t){
                    return _selectedTasks.contains(t.id);
                }.bind(this))
            }.bind(this)),
            function(p) {
                return {
                    id: p.id,
                    tasks: _.map(
                        _.filter(p.tasks, function(t) {
                            return _selectedTasks.contains(t.id);
                        }.bind(this)),
                        function(t) {
                            return {
                                id: t.id,
                                defaultStream: t.id == streamTaskId
                            }
                        }.bind(this)
                    )
                }
            }.bind(this));
    },
    getCurrentChannel: function() {
        return {
            channelId: _id,
            logo: _logo,
            channelName: _name,
            defaultStream: this.getDefaultStream().url,
            configuration: {streams: {RTMP: this.getDefaultStream().url}},
            parameterStates: [],
            interval: _interval,
            probes: this.getCurrentTaskConfig()
        };
    },
    uploadImage: function (file) {
        var url = "rest/channel/config/img/upload";
        var fd = new FormData();
        fd.append("file", file);

        $.ajax({
                url: url,
                type: 'POST',
                data: fd,
                enctype: 'multipart/form-data',
                processData: false,
                contentType: false,

            success: function(data) {
                    console.info(data["path"]);
                    this.setLogo(data["path"]);
                }.bind(this),
            error: function(xhr, status, err) {
                    console.error(url, status, err.toString());
                    OverviewActions.refreshIfForbidden(xhr);
                }.bind(this)
            });
    },

    emitChange: function() {this.emit(CHANGE_EVENT);},
    addChangeListener: function(callback) {this.on(CHANGE_EVENT, callback);},
    removeChangeListener: function(callback) {this.removeListener(CHANGE_EVENT, callback);}
});

AppDispatcher.register(function(action) {
    switch(action.actionType) {
        case AppConstants.TOGGLE_CREATE_CHANNEL:
            ChannelCrudStore.toggleCreate();
            break;
        case AppConstants.TOGGLE_EDIT_CHANNEL:
            ChannelCrudStore.toggleEdit(action.config);
            break;
        case AppConstants.TOGGLE_ACTIVATE_CHANNEL:
            ChannelCrudStore.toggleActive(action.active);
            break;
        case AppConstants.CHANNEL_CRUD_LOAD_PROBES:
            ChannelCrudStore.loadData(action.probes);
            break;
        case AppConstants.CHANNEL_CRUD_SELECT_TASKS:
            ChannelCrudStore.selectTasks(action.ids);
            break;
        case AppConstants.CHANNEL_CRUD_UNSELECT_TASKS:
            ChannelCrudStore.unselectTasks(action.ids);
            break;
        case AppConstants.CHANNEL_CRUD_SET_INTERVAL:
            ChannelCrudStore.setInterval(action.int);
            break;
        case AppConstants.CHANNEL_CRUD_SET_NAME:
            ChannelCrudStore.setName(action.name);
            break;
        case AppConstants.CHANNEL_CRUD_SET_LOGO:
            ChannelCrudStore.setLogo(action.logo);
            break;
        case AppConstants.CHANNEL_CRUD_NEW_IMAGE:
            ChannelCrudStore.uploadImage(action.file);
            break;

        default:
        // no op
    }
});

export default ChannelCrudStore;
