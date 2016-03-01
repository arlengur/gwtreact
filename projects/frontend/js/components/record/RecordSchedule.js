import React from 'react';
import NavMenu from '../../components/common/NavMenu';
import RecordGrid from './RecordScheduleGrid';
import CrudRecordSchedule from './CrudRecordSchedule';
import ErrorDialog from '../../components/common/ErrorDialog';
import * as Actions from '../../actions/RecordScheduleActions';
import {default as Store} from '../../stores/RecordScheduleStore';
import NotificationSystem from 'react-notification-system';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
import i18n from '../../constants/i18nConstants';
const ls = AppUserSettingsStore.localizeStringCFL;

const  RecordSchedule = React.createClass({
    _notificationSystem: null,

    componentDidMount: function() {
        this._notificationSystem = this.refs.notificationSystem;
        Actions.loadProbes();
        Actions.loadTimezones();
        Store.addChangeListener(this._onChange);
    },
    componentWillUnmount: function() {
        Store.removeChangeListener(this._onChange);
    },
    getInitialState: function() {
        return {
            isWarningShown: false,
            store: Store.getState()
        };
    },
    _onChange: function() {
        this.setState({store: Store.getState()});
        let state = Store.getState();
        if (state.get('multiTimeZone') && !this.state.isWarningShown) {
            this._notificationSystem.addNotification({
                message: ls(i18n.PROBES_FROM_DIFFERENT_TIMEZONE_SELECTED),
                level: 'warning'
            });
            this.setState({
                isWarningShown: true
            });
        } else if (!state.get('multiTimeZone') && this.state.isWarningShown) {
            this.setState({
                isWarningShown: false
            });
        }
    },

    render: function() {
        var store = this.state.store;
        return <div className="probe-view container-fluid">
            <NavMenu className="row" title={ls(i18n.RECORD_SCHEDULE)}/>
            <RecordGrid store={store}/>
            {store.get('crudActive') ?
                <CrudRecordSchedule store={store}/>
                : ""}
            <ErrorDialog/>
            <NotificationSystem ref="notificationSystem"/>
        </div>
    }
});

export default RecordSchedule;