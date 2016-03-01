import React from 'react';
import Immutable from 'immutable';
import _ from 'lodash';
import AppDispatcher from '../../dispatcher/AppDispatcher';
import AppConstants from '../../constants/AppConstants';
import Images from '../../util/Images';
import AppUserStore from '../../stores/AppUserSettingsStore';

var GroupActions = {
    toggleGroup: function(group) {
        AppDispatcher.dispatch({
            actionType: AppConstants.GROUP_FILTER_TOGGLE,
            group: group
        })
    }
};

var GroupFilter = React.createClass({
    render: function () {
        var filters = [];

        var rfIpUnavailable = this.props.available.intersect(['RF', 'IP']).size == 0;
        filters.push(
            <FilterCell key="RF_IP" grp="RF_IP"
                class='filter_rf_ip_icon'
                url={Images.groupFilterBg("RF_IP",
                    this.props.maxSeverities.get("RF"),
                    this.props.maxSeverities.get("IP"))}
                checked={!rfIpUnavailable && !this.props.disabled.contains("RF_IP")}
                disabled={rfIpUnavailable}
            />);

        filters = filters.concat(_.map(['TS', 'VIDEO', 'AUDIO'],
            function(group) {
                var unavailable = !this.props.available.contains(group);
                return <FilterCell key={group} grp={group}
                    class='filter_icon indent'
                    url={Images.groupFilterBg(group, this.props.maxSeverities.get(group))}
                    checked={!unavailable && !this.props.disabled.contains(group)}
                    disabled={unavailable}/>
            }.bind(this)
        ));

        var dataUnavailable = this.props.available.intersect(['EPG', 'CC', 'DATA']).size == 0;
        filters.push(
            <FilterCell key="EPG_CC_DATA" grp="EPG_CC_DATA"
                class='filter_epg_data_cc_icon indent'
                url={Images.groupFilterBg("EPG_CC_DATA",
                    this.props.maxSeverities.get("EPG"),
                    this.props.maxSeverities.get("CC"),
                    this.props.maxSeverities.get("DATA"))}
                checked={!dataUnavailable && !this.props.disabled.contains("EPG_CC_DATA")}
                disabled={dataUnavailable}/>);

        return <div className="group_filter">
            {filters}
        </div>
    }
});

var FilterCell = React.createClass({
    render: function () {
        var bgImg = {backgroundImage: this.props.url};
        var groupTitle = AppUserStore.getGroupTitle(this.props.grp);
        return (
            <div style={bgImg} className={this.props.class} title={groupTitle}>
                <input type="checkbox" checked={this.props.checked}
                    onChange={function() {
                        GroupActions.toggleGroup(this.props.grp)
                    }.bind(this)}
                    disabled={this.props.disabled ? "disabled" : ""}/>
            </div>
        );
    }
});

export default GroupFilter;