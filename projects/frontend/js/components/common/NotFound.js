import React from 'react';
import NavMenu from './NavMenu';
import i18n from '../../constants/i18nConstants';
import AppSettingsStore from '../../stores/AppUserSettingsStore';
const tr = AppSettingsStore.localizeString;

var NotFound = React.createClass({
    render: function() {
        return (
            <div style={{height: '100%'}} className="flex flex-col">
                <NavMenu/>
                <div className="flex-center">
                    <div className="container-fluid">
                        <div className="row">
                            <span style={{fontSize: '15em', fontWeight: 'bold', color: '#3c3c3c'}}>404</span>
                        </div>
                        <div className="row">
                            <span style={{fontSize: '1.5em', color: 'white'}}>{tr(i18n.NOT_FOUND)}</span>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
});

export default NotFound;
