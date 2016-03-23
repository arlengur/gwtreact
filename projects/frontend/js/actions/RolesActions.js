import AppConstants from '../constants/AppConstants';
import {action} from '../dispatcher/AppDispatcher';
import Misc from '../util/Misc';
import RolesStore from '../stores/RolesStore';
import AppUserSettingsStore from '../stores/AppUserSettingsStore';
import i18n from '../constants/i18nConstants';
import Immutable from 'immutable';


export function createRole() {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_CREATE
    })
}

export function closeRole() {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_CLOSE
    })
}

export function selectRole(roleId) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.SELECTED_ROLE,
        roleId: roleId
    });
}


export function selectPage(pageId) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_PAGE,
        pageId: pageId
    });
}

export function selectPages(pageId) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_PAGES,
        pageId: pageId
    });
}

export function loadRoles() {
    var url = "rest/rbac/roles/list";
    $.ajax({
        url: url,
        dataType: 'json',
        method: 'GET',
        success: (roles) => action({
            actionType: AppConstants.USER_MANAGER_ROLES.ROLES_LOADED,
            roles: roles
        }),
        error: function (xhr, status, err) {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}


export function createNewRole(data) {
    $.ajax({
        url: 'rest/rbac/roles/create',
        type: 'POST',
        data: data,
        dataType: 'json',
        contentType: 'application/json',
        success: () => loadRoles(),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}

export function editRole(role) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.ROLE_EDIT,
        roleName: role.name,
        roleSubjects: role.subjects
    });
}

export function updateRoleName (roleName) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.ROLE_NAME_UPDATE,
        roleName: roleName
    });
}

export function toggleExpanded(id) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.TOGGLE_EXPANDED,
        id: id
    });
}

export function toggleAllRoles(rolesIds) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.SELECTED_ALL_ROLES,
        rolesIds: rolesIds
    });
}

export function toggleAllPages(pagesIds) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.SELECTED_ALL_PAGES,
        pagesIds: pagesIds
    });
}

export function updateRolesFilter (rolesFilter) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.UPDATE_ROLES_FILTER,
        rolesFilter: rolesFilter
    });
}

export function updatePagesFilter (pagesFilter) {
    action({
        actionType: AppConstants.USER_MANAGER_ROLES.MODAL.UPDATE_PAGES_FILTER,
        pagesFilter: pagesFilter
    });
}

export function deleteRoles(names) {
    $.ajax({
        url: 'rest/rbac/roles/delete',
        type: 'DELETE',
        contentType: 'application/json',
        data: JSON.stringify({names}),
        success: () => loadRoles(),
        error: (xhr, status, err) => {
            console.error(url, status, err.toString());
            Misc.refreshIfForbidden(xhr);
        }
    });
}


