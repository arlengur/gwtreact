import React from 'react';
import NavMenu from '../../components/common/NavMenu';
import RolesGrid from './RolesGrid';
import RolesCreate from './RolesCreate';
import * as Actions from '../../actions/RolesActions';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/RolesStore';
import i18n from '../../constants/i18nConstants';
import NotificationSystem from 'react-notification-system';
import ErrorDialog from '../../components/common/ErrorDialog';

const Ls = AppUserSettingsStore.localizeString;

var Roles = React.createClass({
    _notificationSystem: null,

    componentDidMount: function() {
        this._notificationSystem = this.refs.notificationSystem;
        Actions.loadRoles();
        Store.addChangeListener(this._onChange);
    },
    componentWillUnmount: function() {
        Store.removeChangeListener(this._onChange);
    },
    getInitialState: function() {
        return {
            store: Store.getState()
        };
    },
    _onChange: function() {
        this.setState({store: Store.getState()});
    },

    render: function() {

        var store = this.state.store;

        return (

            <div className="probe-view container-fluid">

                <NavMenu className="row" title={Ls(i18n.USER_MANAGER_ROLES)}/>

                <RolesGrid store={store}/>

                {store.get('createActive') ? <RolesCreate store={store}/> : ""}

                <ErrorDialog/>
                <NotificationSystem ref="notificationSystem"/>

            </div>
        );
    }
});

export default Roles;
