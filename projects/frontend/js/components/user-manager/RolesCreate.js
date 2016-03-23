import React from 'react';
import _ from 'lodash';
import Immutable from 'immutable';
import ConfirmDialog from '../../components/common/ConfirmDialog';
import AppActions from '../../actions/AppActions';
import AppConstants from '../../constants/AppConstants';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import i18n from '../../constants/i18nConstants';
import RolesStore from '../../stores/RolesStore';
import * as Actions from '../../actions/RolesActions';
import Radio from '../common/Radio';
import Input from '../common/TextInputTypeahead';
import Select from '../common/Select';
import Checkbox from '../common/Checkbox';
import TreeCommon from '../../components/crud/CrudTreesCommon';
import Images from '../../util/Images';

const ls = AppUserSettingsStore.localizeString;

var _searchTimer = null;

var getDisplayedPages = (pages, selectedPages, filt) => {
    return _.filter(pages, (p) => TreeCommon.matches(p.name, filt));
};

const RolesSelect = React.createClass({
    render: function () {
        var roles = Immutable.List(this.props.roles).toArray();
        roles = _.sortBy(roles, (r)=>r.name.toUpperCase());
        roles.splice(0, 0, {id: "", name: ""});

        return (
            <div className="row flex-none" style={{margin: '5px 8px 0px'}}>
                <div className="col-xs-6" style={{padding: '0'}}>
                    <label htmlFor="copy-permission" className="small-text pull-left text23 overflow-ellipsis"
                           style={{margin: 0}}>{ls(i18n.USER_MANAGER_ROLE_COPY_PERMISSIONS_FROM)}:</label>
                </div>
                <div className="col-xs-6" style={{padding: '0'}}>
                    <Select width="100%" className="pull-left"
                            options={_.map((roles), (role) => {return [role.id, role.name]})}
                            onChange={(v) => Actions.selectPages(v)}/>
                </div>
            </div>
        );
    }
});

const PagesItem = React.createClass({
    render: function () {
        var item = this.props.page;
        return (
            <li className="crud-tree-probe">
                <div className="container-fluid override-padding-0-5 text23">
                    <Checkbox id={item.id} className="pull-left" style={{margin: '6px 1px 0 2px'}}
                              checked={this.props.selectedPages.contains(item.id)}
                              onChange={() => Actions.selectPage(item.id)}/>
                    <div className={"pull-left " + Images.roleClassName(item.id)}/>
                    <div className="small-text overflow-ellipsis crud-tree-text">{TreeCommon.getHighlightedName(item.name, this.props.filter)}</div>
                </div>
            </li>
        );
    }
});

const PagesList = React.createClass({
    render: function () {
        return (
            <div className="probe-tree-background config-tree flex-11a">
                <ul className="crud-tree-filler override-padding-0 override-margin-0 um">
                    {_.map(this.props.pages, (page) => {
                        return <PagesItem page={page} selectedPages={this.props.selectedPages} filter={this.props.filter}/>
                    })}
                </ul>
            </div>
        );
    }
});


const RolesCreate = React.createClass({
    getInitialState: function () {
        return {
            roleName: this.props.store.get('roleName') != null ? this.props.store.get('roleName') : ''
        }
    },
    updatePagesFilter: function (newFilter) {

        if (_searchTimer != null) {
            clearTimeout(_searchTimer);
            _searchTimer = null;
        }
        if (newFilter == '') {
            Actions.updatePagesFilter(newFilter);
        } else {
            var pagesVisible = getDisplayedPages(this.props.store.get('pages'), this.props.store.get('selectedPages'), newFilter);
            var nodesNum = pagesVisible.length;
            if (nodesNum > 100) {
                _searchTimer = setTimeout(() => {
                    Actions.updatePagesFilter(newFilter);
                }, 500);
            } else {
                Actions.updatePagesFilter(newFilter);
            }
        }
    },
    updateRoleName: function (roleName) {
        if (roleName != null) {
            this.setState({
                roleName: roleName
            });
        }
    },
    createNewRole: function () {
        Actions.createNewRole(JSON.stringify({
            name: this.state.roleName,
            subjects: this.props.store.get('selectedPages'),
            comment: ''
        }));
        Actions.closeRole();
    },
    render: function () {
        var store = this.props.store;
        var title = ls(i18n.USER_MANAGER_CREATE_ROLE);

        var displayedPages = _.sortBy(this.props.store.get('pages'), (r)=>r.name.toUpperCase());
        var pagesFilter = this.props.store.get('pagesFilter');
        var selectedPages = this.props.store.get('selectedPages');

        if (pagesFilter.length > 0) {
            displayedPages = getDisplayedPages(displayedPages, selectedPages, pagesFilter);
        }

        return <div className="modal flex" style={{background: 'rgba(0,0,0,.75)'}}>
            <div className="modal-dialog bigmodal create-new-role flex flex-col">
                <div className="modal-head flex-none">
                    <img src="../img/modal_close.png" className="pull-right cursor-pointer"
                         style={{padding: '2px', margin: '7px 4px 0 0'}}
                         onClick={Actions.closeRole}/>
                    <h4 className="modal-title">{title}</h4>
                </div>
                <div className="flex flex-11a config-panel um" style={{overflow: 'hidden'}}>
                    <div className="col-sm-12 override-padding-0 flex flex-col">
                        <div className="flex-none black-bottom relative">
                            <div className="row" style={{height: '20px', margin: '2px 2px 0', background: '#393939'}}>
                                <span className="small-text pull-left" style={{margin: '2px 5px 0', color: '#b5b5b5'}}>{ls(i18n.USER_MANAGER_ROLE_MAIN_INFO)}</span>
                            </div>
                            <div className="row" style={{margin: '5px 8px 0px'}}>
                                <div className="col-xs-6" style={{padding: '0'}}>
                                    <span className="pull-left small-text text23 overflow-ellipsis">{ls(i18n.USER_MANAGER_ROLE_NAME)}:</span>
                                </div>
                                <div className="col-xs-6" style={{padding: '0'}}>
                                    <Input className="pull-right search-field"
                                           style={{width: '100%'}}
                                           value={this.state.roleName}
                                           onChange={(v) => this.updateRoleName(v)}/>
                                </div>
                            </div>
                            <div className="row" style={{margin: '0px 0px 10px'}}>
                                <RolesSelect roles={store.get('roles')}/>
                            </div>
                        </div>
                        <div className="flex flex-col flex-11a relative">
                            <div className="row" style={{height: '20px', margin: '2px 2px 0', background: '#393939'}}>
                                <span className="small-text pull-left" style={{margin: '2px 5px 0', color: '#b5b5b5'}}>{ls(i18n.USER_MANAGER_ROLE_PERMISSIONS)}</span>
                            </div>
                            <div className="config-tree-button-panel flex-none">
                                <Checkbox id='pagesTreeHeadId'
                                          className="pull-left"
                                          style={{margin: '6px 3px 0 3px'}}
                                          checked={_.every(displayedPages, (p)=>selectedPages.contains(p.id))}
                                          onChange={() => Actions.toggleAllPages(_.chain(displayedPages).pluck('id').value())}/>
                                <Input className="pull-right search-field placeholder-dissappear"
                                       style={{width: '100px'}}
                                       placeholder={ls(i18n.SEARCH_TEXT)}
                                       onChange={(v) => this.updatePagesFilter(v)}/>
                            </div>
                            <PagesList selectedPages={selectedPages} pages={displayedPages} filter={pagesFilter}/>
                        </div>
                    </div>
                </div>
                <div className="footer flex-none">
                    <button type="button" className="btn btn-primary config-btn pull-left"
                            disabled={this.state.roleName == null || this.state.roleName == ''}
                            onClick={this.createNewRole}>
                        {ls(i18n.OK)}
                    </button>
                    <button type="button" className="btn btn-primary config-btn pull-right" style={{marginLeft: '5px'}}
                            onClick={Actions.closeRole}>
                        {ls(i18n.CANCEL)}
                    </button>
                </div>
            </div>
            <ConfirmDialog/>
        </div>
    }
});

export default RolesCreate;
