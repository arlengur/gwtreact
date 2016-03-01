import React from 'react';
import AppDispatcher from '../../dispatcher/AppDispatcher';
import AppConstants from '../../constants/AppConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import {ALL_SEVERITIES, severityTo6Colors} from '../../util/Alerts';

var SeverityActions = {
    toggleSeverityFilter: function (name) {
        AppDispatcher.dispatch({
            actionType: AppConstants.SEVERITY_FILTER_TOGGLE,
            severity: name
        })
    }
};

var SeverityFilter = React.createClass({
    render: function () {
        return <div className="severity_filter">
            {ALL_SEVERITIES.map(function (severity) {
                return <FilterCell key={severity} name={severity}
                    checked={!this.props.disabled.contains(severity)}/>;
            }.bind(this))}
        </div>
    }
});

var FilterCell = React.createClass({
    render: function () {
        var bgColor = {background: severityTo6Colors(this.props.name)};
        return (
            <div className='severity_item'>
                <span style={bgColor} className='severity_icon'>
                    <input type="checkbox" checked={this.props.checked ? "checked" : ""}
                        onChange={function () {
                            SeverityActions.toggleSeverityFilter(this.props.name);
                        }.bind(this)}/>
                </span>
                <span className='severity_text'>{AppUserStore.localizeString(this.props.name)}</span>
            </div>
        );
    }
});

export default SeverityFilter;