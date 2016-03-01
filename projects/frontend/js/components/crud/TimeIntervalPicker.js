import React from 'react';
import {addons} from 'react/addons';
const {update} = addons;
import Row from 'react-bootstrap/Row';
import _ from 'lodash';
import Radio from '../common/Radio';
import Select from '../../components/common/Select';
import i18n from '../../constants/i18nConstants';
import Misc from '../../util/Misc';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const tr = AppUserSettingsStore.localizeString;

var TimeIntervalPicker = React.createClass({
    getMilliseconds: function(state) {
        return 1000*60*(
            parseInt(state.hours)*60 +
            parseInt(state.minutes));
    },
    updateState: function(toMerge)  {
        var newState = update(this.state, {$merge: toMerge});
        this.props.onChange(this.getMilliseconds(newState));
        this.setState(newState);
    },
    getInitialState: function() {
        var interval = parseInt(this.props.interval);
        if(interval == 1000*60*60*24) {
            return {option: '24h', hours: 24, minutes: 0}
        } else if(interval == 1000*60*60) {
            return {option: '1h', hours: 1, minutes: 0}
        } else if(interval == 1000*60*15) {
            return {option: '15min', hours: 0, minutes: 15}
        } else {
            return {
                option: 'custom',
                hours: Math.floor(interval / (1000*60*60)),
                minutes: Math.floor((interval % (1000*60*60)) / (1000*60))
            }
        }
    },
    render: function() {
        return <div>
            <Row style={{marginTop: '5px'}}>
                <Radio id="15min" className="pull-left" style={{margin: '2px 2px 0 0'}}
                    checked={this.state.option == '15min'}
                    onChange={() => this.updateState({option: '15min',hours: 0,minutes: 15})}
                />
                <label htmlFor="15min" className="pull-left small-text" style={{marginRight: '10px'}}>{tr(i18n._15_MIN)}</label>
                <Radio id="1h" className="pull-left" style={{margin: '2px 2px 0 0'}}
                    checked={this.state.option == '1h'}
                    onChange={() => this.updateState({option: '1h',hours: 1,minutes: 0})}
                />
                <label htmlFor="1h" className="small-text pull-left" style={{marginRight: '10px'}}>{tr(i18n._1_HOUR)}</label>
                <Radio id="24h" className="pull-left" style={{margin: '2px 2px 0 0'}}
                    checked={this.state.option == '24h'}
                    onChange={() => this.updateState({option: '24h',hours: 24,minutes: 0})}
                />
                <label htmlFor="24h" className="small-text pull-left">{tr(i18n._24_HOUR)}</label>
            </Row>
            <Row style={{marginTop: '8px'}}>
                <Radio id="custom" className="pull-left" style={{margin: '4px 4px 0 0'}}
                    checked={this.state.option == 'custom'}
                    onChange={() => this.updateState({option: 'custom'})}
                />
                <label htmlFor="custom" className="pull-left small-text" style={{marginTop: '2px'}}>{tr(i18n.CUSTOM)+":"}</label>
                <Select className="pull-left" width={45} style={{marginTop: '-4px', marginLeft: '5px'}}
                        value={this.state.hours}
                        id="hours" options={_.range(25)}
                        onChange={(v)=> this.updateState({option: 'custom', hours: v})}/>
                <label htmlFor="hours" className="pull-left small-text" style={{marginLeft: '5px'}}>{tr(i18n.HOURS)+","}</label>
                <Select className="pull-left" width={45} style={{marginTop: '-4px', marginLeft: '5px'}}
                        value={this.state.minutes}
                        id="mins" options={_.range(0, 60 ,5)}
                        onChange={(v)=>this.updateState({option: 'custom', minutes: v})}/>
                <label htmlFor="mins" className="pull-left small-text" style={{marginLeft: '5px'}}>{tr(i18n.MINUTES)+"."}</label>
            </Row>
        </div>
    }
});

export default TimeIntervalPicker;