import React from 'react';
import _ from 'lodash';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import * as Actions from '../../actions/RolesActions';
import Checkbox from '../common/Checkbox';
import Input from '../common/TextInputTypeahead';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import i18n from '../../constants/i18nConstants';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const Ls = AppUserSettingsStore.localizeStringCFL;

var _searchTimer = null;

var getDisplayedRoles = (roles, selectedRoles, filt) => {
    return _.filter(roles, (p) => TreeCommon.matches(p.name, filt) || TreeCommon.matches(p.comment, filt) || selectedRoles.contains(p.id));
};

var DeleteRolesConfirmText = React.createClass({
    render: function () {
        return <div className="flex flex-11a flex-col confirm-panel">
            <div className="flex-none confirm-text">
                {Ls(i18n.ROLE_DELETE_WARN) + ' (' + this.props.roles.length + '):'}
            </div>
            <div className="flex flex-11a flex-col confirm-probes-text">
                {_.chain(this.props.roles)
                    .sortBy((r)=>r.name.toUpperCase())
                    .map((role, id) => [<span className="flex-none" key={id}>{role.name}</span>])
                    .flatten()
                    .value()}
            </div>
        </div>
    }
});

const RolesItem = React.createClass({
    render: function () {

        var item = this.props.role;

        return (
            <li className="config-tree-li">
                <div className="container-fluid override-padding-0 overflow-ellipsis" id="tree">
                    <div className="pull-left overflow-ellipsis" style={{width: '15%'}}>
                        <Checkbox id={item.id} className="pull-left" style={{lineHeight: '23px', margin: '0 5px'}}
                            checked={this.props.selectedRoles.contains(item.id)}
                            onChange={() => Actions.selectRole(item.id)}/>

                        <span className="probe-tree-line-text role-name"
                              onClick={()=>Actions.editRole(item)}>
                            {TreeCommon.getHighlightedName(item.name, this.props.filter)}
                        </span>
                    </div>
                    <div className="pull-left overflow-ellipsis" style={{width: '15%'}}>
                        <span className="probe-tree-line-text">{item.number_of_users}</span>
                    </div>
                </div>
            </li>
        );
    }
});

const RolesList = React.createClass({
    render: function () {
        return (
            <ul className="probe-tree-background override-margin-0 override-padding-0 um row-center" style={{position: 'relative'}}>
                   {_.map(this.props.roles, (role, id) => {
                       return <RolesItem key={id} role={role} selectedRoles={this.props.selectedRoles} filter={this.props.filter}/>
                   })}
            </ul>
        );
    }
});

const RolesGrid = React.createClass({
    updateRolesFilter: function (newFilter) {
        // Here we delay updating the tree if the expanded tree size is too big (> 100 elements)
        // We hope that the user is just in the middle of typing a more precise filter, and on next search field
        // update we will get a shorter list, which we will then display immediately
        if (_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if (newFilter == '') {
            Actions.updateRolesFilter(newFilter);
        } else {
            var rolesVisible = getDisplayedRoles(this.props.store.get('roles'), this.props.store.get('selectedRoles'), newFilter);
            var nodesNum = rolesVisible.length;
            if (nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    Actions.updateRolesFilter(newFilter);
                }, 500);
            } else {
                Actions.updateRolesFilter(newFilter);
            }
        }
    },

    render: function () {
        var displayedRoles = _.sortBy(this.props.store.get('roles'), (r)=>r.name.toUpperCase());
        var rolesFilter = this.props.store.get('rolesFilter');
        var selectedRoles = this.props.store.get('selectedRoles');

        if (rolesFilter.length > 0) {
            displayedRoles = getDisplayedRoles(displayedRoles, selectedRoles, rolesFilter);
        }
        var anyRolesSelected = !selectedRoles.isEmpty();
        var rolesActionStyle={cursor: anyRolesSelected ? 'pointer' : 'not-allowed'};

        return (
            <div className="flex flex-11a flex-col">
                <div className="row um-tabs">
                    <ul className="nav nav-tabs">
                        <li className="active"><a href="#">{Ls(i18n.USER_MANAGER_ROLES)}</a></li>
                    </ul>
                </div>
                <div className="row probe-tree-toolbar" style={{marginTop: '0'}}>
                    <div className="probe-probes-find">

                        <div onClick={Actions.createRole} className="pull-left create-btn cursor-pointer" style={{margin: '2px 0 0 8px'}} title="Create new role"></div>

                        <span className="pull-left probe-tree-toolbar-line"></span>

                        <div className="pull-left margin-0-5 delete-btn"
                             style={rolesActionStyle}
                             title={Ls(i18n.DELETE_ROLE)}
                             onClick={()=>{if(anyRolesSelected){
                                        AppActions.showConfirmModal(
                                            Ls(i18n.DELETE_ROLE),
                                            <DeleteRolesConfirmText roles={_.filter(displayedRoles, (role) => selectedRoles.contains(role.id))}/>,
                                            AppConstants.USER_MANAGER_ROLES.ROLES_LOADED,
                                            () => Actions.deleteRoles(_.chain(displayedRoles).filter((role) => selectedRoles.contains(role.id)).pluck('name').value())
                                        )}
                        }}/>

                        <Input className="pull-right probe-tree-probe-search placeholder-dissappear"
                               style={{width: '100px'}}
                               placeholder={Ls(i18n.SEARCH_TEXT)}
                               onChange={(v) => this.updateRolesFilter(v)}/>

                    </div>
                    <div className="head-bg flex-none">
                        <div className="container-fluid override-padding-0 pull-left" id="head" style={{position: 'relative', width: '100%'}}>
                            {/* 15%, 10%, 75% */}
                            <div className="probe-head-text overflow-ellipsis pull-left" style={{position: 'relative', width: '15%'}}>
                                <span className="pull-right header-drag"></span>

                                <Checkbox id='rolesTreeHeadId'
                                          className="pull-left"
                                          style={{lineHeight: '33px', margin: '0 5px'}}
                                          checked={_.every(displayedRoles, (p)=>selectedRoles.contains(p.id))}
                                          onChange={() => Actions.toggleAllRoles(_.chain(displayedRoles).pluck('id').value())}/>
                                {Ls(i18n.USER_MANAGER_ROLE)}
                            </div>
                            <div className="probe-head-text overflow-ellipsis pull-left" style={{position: 'relative', width: '15%'}}>
                                <span className="pull-right header-drag"></span>
                                <span className="pull-left header-drag"></span>
                                {Ls(i18n.USER_MANAGER_ROLE_NUMBER_OF_USERS)}
                            </div>
                            <div className="vertical-line" style={{borderColor: '#424242', left: '22px'}}></div>
                            <div className="vertical-line" style={{borderColor: '#424242', left: '15%'}}></div>
                            <div className="vertical-line" style={{borderColor: '#424242', left: '30%'}}></div>
                        </div>
                    </div>
                    <div className="probe-tree-background record-tree flex-11a" style={{position: 'relative', overflowY: 'auto', margin: '0'}}>

                        <RolesList roles={displayedRoles} selectedRoles={selectedRoles} filter={rolesFilter}/>

                        <div className="vertical-line" style={{borderColor: '#424242', left: '22px'}}></div>
                        <div className="vertical-line" style={{borderColor: '#424242', left: '15%'}}></div>
                        <div className="vertical-line" style={{borderColor: '#424242', left: '30%'}}></div>
                    </div>
                </div>
                <ConfirmDialog/>
            </div>
        );
    }

});

export default RolesGrid;