import React from 'react';
import Row from 'react-bootstrap/Row';
import _ from 'lodash';
import CrudActions from '../../actions/ChannelCrudActions';
import Input from '../../components/common/TextInputTypeahead';
import i18nConstants from '../../constants/i18nConstants';
import AppConstants from '../../constants/AppConstants';
import AppUserStore from '../../stores/AppUserSettingsStore';
import Store from '../../stores/ChannelCrudStore';
import Images from '../../util/Images';
import IconDropZone from './IconDropZone';
import TimeIntervalPicker from './TimeIntervalPicker';

var NameSection = React.createClass({
    render: function() {
        return <div className="crud-header-section">
            <div className="override-padding-0">
                <IconDropZone logo={this.props.logo}/>
                <div className="override-padding-0 crud-channel-title-section">
                    <span className="small-text vertical-top">{AppUserStore.localizeString(i18nConstants.TITLE)}</span>
                    <Input className="small-text crud-edit-channel-name"
                            placeholder={AppUserStore.localizeString(i18nConstants.ENTER_CHANNEL_NAME)}
                            value={this.props.name}
                            onChange={function(v){
                                CrudActions.setChannelName(v);
                            }.bind(this)}/>
                </div>
            </div>
        </div>
    }
});

var GroupSection = React.createClass({
    render: function() {
        return <div className="crud-header-section">
            <div className="container-fluid">
                <Row>
                    <span className="small-text vertical-top">{AppUserStore.localizeString(i18nConstants.TIME_INTERVAL)}</span>
                </Row>
                <TimeIntervalPicker onChange={function(m){
                    CrudActions.setChannelInterval(m);}}
                    interval={Store.getInterval()}/>
            </div>
        </div>
    }
});

var ParametersSection = React.createClass({
    render: function() {
        var parameterIcons = _.chain(this.props.probes)
            .pluck('tasks')
            .flatten()
            .filter(function(t) {return this.props.selected.contains(t.id)}.bind(this))
            .pluck('parameterGroup')
            .uniq()
            .sortBy(Images.groupToNum)
            .map(function(g) {
                return <img key={g} className="crud-set-image" src={Images.groupLargeIcon(g)}/>
            })
            .value();

        return <div className="crud-header-last-section">
            <div className="container-fluid">
                <Row>
                    <span className="small-text vertical-top">{AppUserStore.localizeString(i18nConstants.ANALYSIS_LEVEL)}</span>
                </Row>
                <Row>
                    {parameterIcons}
                </Row>
            </div>
        </div>
    }
});

var ChannelCrudLeftPanel = React.createClass({
    render: function() {
        return <div className="crud-header">
            <NameSection name={this.props.name} logo={this.props.logo}/>
            <GroupSection/>
            <ParametersSection probes={this.props.probes} selected={this.props.selected}/>
        </div>
    }
});

export default ChannelCrudLeftPanel;