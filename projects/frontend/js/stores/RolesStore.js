import {EventEmitter} from 'events';
// TODO: remove object-assign, use either ES6 Object.assign, or _.assign
import assign from 'object-assign';
import Immutable from 'immutable';
import AppDispatcher from '../dispatcher/AppDispatcher';
import AppConstants from '../constants/AppConstants';
import Misc from '../util/Misc';
import _ from 'lodash';

const CHANGE_EVENT = 'change';

// Create dialog state
export const DIALOG_STATE = {

};

var _state = Immutable.Map({
    createActive: false,
    roles: [],
    rolesFilter: '',
    roleName: '',
    selectedRoles: Immutable.Set(),
    modules: [],
    modalSelectedRole: " ",
    permissionsFilter: '',
    notifications: 0,
    treeExpanded: Immutable.Set(),
    pagesFilter: '',
    selectedPages: Immutable.Set(),
    pages: [
        {
            "id": "MAIN",
            "name" : "Dashboard"
        },
        {
            "id": "CHANNEL_VIEW",
            "name" : "Сhannel view"
        },
        {
            "id": "ALERTS",
            "name" : "Alarms"
        },
        {
            "id": "PROBE_CONFIG",
            "name" : "Probe config"
        },
        {
            "id": "RECORDING_SCHEDULE",
            "name" : "Recording schedule"
        },
        {
            "id": "LIVE_VIDEO",
            "name" : "Live video"
        },
        {
            "id": "RECORDED_VIDEO",
            "name" : "Recorded video"
        },
        {
            "id": "CHARTS",
            "name" : "Charts"
        },
        {
            "id": "REPORTS",
            "name" : "Reports"
        },
        {
            "id": "MAP",
            "name" : "Map"
        },
        {
            "id": "USER_MANAGER_ROLES",
            "name" : "Roles"
        },
        {
            "id": "POLICIES_ADVANCED",
            "name" : "Policies"
        }
    ]
});

var RolesStore = assign({}, EventEmitter.prototype, {
    getState: () => _state,

    // Can't use es6 lambda because of 'this' semantics
    emitChange: function () {this.emit(CHANGE_EVENT)},
    addChangeListener: function (callback) {this.on(CHANGE_EVENT, callback)},
    removeChangeListener: function (callback) {this.removeListener(CHANGE_EVENT, callback)}
});

AppDispatcher.register(function(action) {
    switch(action.actionType) {

        case AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_CREATE:
            _state = _state.set('createActive', true);
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_CLOSE:
            _state = _state
                .set('createActive', false)
                .set('roleName', '')
                .set('selectedPages', Immutable.Set());
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.ROLE_EDIT:
            _state = _state
                .set('createActive', true)
                .set('roleName', action.roleName)
                .set('selectedPages', Immutable.Set(action.roleSubjects));
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.ROLE_NAME_UPDATE:
            _state = _state.set('roleName', (v)=>!v);
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.ROLES_LOADED:
            _state = _state
                .set('roles', action.roles)
                .set('selectedRoles', Immutable.Set());
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.ROLE_CREATED:
            _state = _state.set('pagesFilter', action.pagesFilter);
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_EXPANDED:
            _state = _state.set('treeExpanded', action.key);
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.SELECTED_ROLE:
            _state = _state.set('selectedRoles', Misc.toggle(_state.get('selectedRoles'), action.roleId));
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.SELECTED_ALL_ROLES:
            if (_.every(action.rolesIds, (roleId) => _state.get('selectedRoles').contains(roleId))) {
                _state = _state.set('selectedRoles', Immutable.Set());
            } else {
                _state = _state.set('selectedRoles', Immutable.Set(action.rolesIds));
            }
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.UPDATE_ROLES_FILTER:
            _state = _state.set('rolesFilter', action.rolesFilter);
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_PAGE:
            _state = _state.set('selectedPages', Misc.toggle(_state.get('selectedPages'), action.pageId));
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_PAGES:
            var role = _.find(_state.get('roles'), (r) => r.id == action.pageId);
            if (role) {
                _state = _state.set('selectedPages', Immutable.Set(role.subjects));
            }
            else {
                _state = _state.set('selectedPages', Immutable.Set());
            }
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_ALL_PAGES:
            if (_.every(action.pagesIds, (pageId) => _state.get('selectedPages').contains(pageId))) {
                _state = _state.set('selectedPages', Immutable.Set());
            } else {
                _state = _state.set('selectedPages', Immutable.Set(action.pagesIds));
            }
            RolesStore.emitChange();
            break;

        case AppConstants.USER_MANAGER_ROLES.MODAL.UPDATE_PAGES_FILTER:
            _state = _state.set('pagesFilter', action.pagesFilter);
            RolesStore.emitChange();
            break;

    }
});

export default RolesStore;
