import React from 'react';
import Images from '../../util/Images';
import AppUserStore from '../../stores/AppUserSettingsStore';

var Clock = React.createClass({
    render: function() {
        var _time = AppUserStore.localizeTimeDuration(this.props.time, false);

        return (
            <div className="view-icon pull-right">
                <img src={Images.clockImage()}/>
                <span className="clock-text">  {_time}</span>
            </div>
        );
    }
});

export default Clock;
