import React from 'react';
import _ from 'lodash';
import AppDispatcher from '../../dispatcher/AppDispatcher';
import AppConstants from '../../constants/AppConstants';
import Images from '../../util/Images';
import AppUserStore from '../../stores/AppUserSettingsStore';

var ActivityActions = {
    toggleActivity: function (_activity) {
        AppDispatcher.dispatch({
            actionType: AppConstants.ACTIVITY_FILTER_TOGGLE,
            activity: _activity
        })
    }
};

var ActivityFilter = React.createClass({
    render: function () {
        var services = _.map(['ACTIVE', 'NONACTIVE'], function (item, index) {
            return <FilterCell key={item} name={item}
                checked={!this.props.disabled.contains(item)}/>;
        }.bind(this));

        return (
            <div className="activity_filter">
            {services}
            </div>
        );
    }
});

var FilterCell = React.createClass({
    render: function () {
        var styleN = {backgroundImage: Images.activityFilterBg(this.props.name, this.props.checked)};
        var title = AppUserStore.getActivityTitle(this.props.name, this.props.checked);
        return (
            <div style={styleN} className={'filter_icon'}
                title={title}
                onClick={function() {
                    ActivityActions.toggleActivity(this.props.name)
                }.bind(this)}></div>
        );
    }
});

export default ActivityFilter;