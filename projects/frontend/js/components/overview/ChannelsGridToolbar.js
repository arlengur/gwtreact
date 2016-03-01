import _ from 'lodash';
import React from 'react';
import OverviewActions from '../../actions/OverviewActions';
import CrudActions from '../../actions/ChannelCrudActions';
import i18n from '../../constants/i18nConstants';
import Select from '../../components/common/Select';
import Images from '../../util/Images';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const tr = AppUserSettingsStore.localizeString;

var ChannelsGridToolbar = React.createClass({
    getSevFilterTooltip: function(active, sev) {
        var action = active ? i18n.DISABLE : i18n.ENABLE;
        var severity;
        switch(sev) {
            case "CRITICAL": severity = i18n.FILTER_CRITICAL; break;
            case "WARNING":  severity = i18n.FILTER_WARNING;  break;
            default:         severity = i18n.FILTER_NONE;     break;
        }
        return tr(action)+" "+tr(severity);
    },
    render: function() {
        return <div className="container-fluid view-selector-toolbar override-padding-0 flex-none">
            <div className="pull-left black-right">
                <img src="img/crud/Create_set.png"
                     title={tr(i18n.CREATE_WIDGET)}
                     className="create-set-icon clickable"
                     onClick={() => {
                         CrudActions.setChannelLogo(Images.getDefaultChannelLogo());
                         CrudActions.toggleCreateChannel(true)}}/>
            </div>
            <img src={Images.changeMode1(this.props.view==1)}
                 title={tr(i18n.STATUS_VIEW)}
                 className="view-icon pull-left clickable"
                 onClick={()=>OverviewActions.changeChannelView(1)}/>
            <img src={Images.changeMode2(this.props.view==2)}
                 title={tr(i18n.VIDEO_WALL)}
                 className="view-icon pull-left clickable"
                 onClick={()=>OverviewActions.changeChannelView(2)}/>
            <img src={Images.changeMode3(this.props.view==3)}
                 title={tr(i18n.DETAILED_VIEW)}
                 className="view-icon pull-left clickable"
                 onClick={()=>OverviewActions.changeChannelView(3)}/>
            <div className="pull-left black-left container-fluid override-padding-0-10">
                {_.map(['CRITICAL', 'WARNING', 'NONE'], (sev) => {
                    var enabled = !this.props.disabledSev.contains(sev);
                    var className = "pull-left clickable severity-filter-icon " + Images.severityFilter(sev)
                        + (enabled ? " clicked" : "");
                    return <div key={sev} className={className}
                                title={this.getSevFilterTooltip(enabled, sev)}
                                onClick={()=>OverviewActions.toggleSeverity(sev)}/>})}
            </div>
            <Select value={this.props.order} width={130} id="sort-by"
                    className="pull-right" style={{marginRight: 4, marginTop: 5}}
                    options={[['Created', tr(i18n.CREATION_DATE_ORDER)],
                              ['Name', tr(i18n.NAME_ORDER)],
                              ['Severity', tr(i18n.SEVERITY_ORDER)]]}
                    onChange={(val) => OverviewActions.selectSorting(val)}/>
            <label className="pull-right small-text" htmlFor="sort-by" style={{marginTop: 7, marginRight: 10}}>
                {tr(i18n.SORT_BY)}
            </label>
        </div>
    }
});

export default ChannelsGridToolbar;
