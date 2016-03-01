import _ from 'lodash';
import React from 'react';
import OverviewActions from '../../actions/OverviewActions';
import ChannelCrud from '../../components/crud/ChannelCrud';
import NavMenu from '../../components/common/NavMenu';
import LoadingProgressBar from '../../components/common/LoadingProgressBar';
import i18nConstants from '../../constants/i18nConstants';
import Store from '../../stores/ChannelStatusStore';
import AppUserStore from '../../stores/AppUserSettingsStore';
import {currentSeverity, severityToNum} from '../../util/Alerts';
import Locale from '../../util/Locale';
import Paging from '../../util/ChannelGridPaging';
import ChannelsGrid from './ChannelsGrid';
import ChannelsGridToolbar from './ChannelsGridToolbar';
import PagingFooter from './PagingFooter';

var Overview = React.createClass({
    getInitialState: function() {
        return {
            channelStatus: Store.getChannelsState(),
            view: Store.getView(),
            dataLoaded: Store.isDataLoaded(),
            currentPage: Store.getCurrentPage(),
            channelsPerPage: Store.getChannelsPerPage(),
            disabledSev: Store.getDisabledSev(),
            order: Store.getOrder()
        };
    },
    componentDidMount: function() {
        Store.addChangeListener(this._onChange);
        OverviewActions.loadChannelData();
    },
    componentWillUnmount: function() {
        Store.removeChangeListener(this._onChange);
    },
    _onChange: function() {
        this.setState({
            channelStatus: Store.getChannelsState(),
            view: Store.getView(),
            dataLoaded: Store.isDataLoaded(),
            currentPage: Store.getCurrentPage(),
            channelsPerPage: Store.getChannelsPerPage(),
            disabledSev: Store.getDisabledSev(),
            order: Store.getOrder()
        });
    },

    render: function() {
        var currentSeverities = _.object(
            _.map(this.state.channelStatus, (ch)=> {
                var reports = _.chain(ch.parameterStates)
                    .pluck('alertsHistory')
                    .flatten()
                    .value();
                return [ch.channelId, currentSeverity(reports, ch.endTime)];
            }));
        var channelsFiltered = _.filter(this.state.channelStatus, (ch) => {
            return !this.state.disabledSev.contains(currentSeverities[ch.channelId]);
        });
        var pagingProps = {
            channelNum:  channelsFiltered.length,
            currentPage: this.state.currentPage,
            perPage: this.state.channelsPerPage
        };
        var sortFn;
        if(this.state.order == 'Name') {
            sortFn = 'channelName';
        } else if(this.state.order == 'Severity'){
            sortFn = (ch) => -severityToNum(currentSeverities[ch.channelId]);
        } else {
            sortFn = 'channelId';
        }
        var channelsOnPage = _.chain(channelsFiltered)
            .sortBy(sortFn)
            .sortBy((c) => !c.configuration.isFavourite)
            .drop(this.state.channelsPerPage*(this.state.currentPage - 1))
            .take(this.state.channelsPerPage)
            .value();
        return <div className = "flex flex-col" style={{height: '100%'}}>
            <NavMenu title={AppUserStore.localizeString(i18nConstants.MULTIPLE_CHANNEL_VIEW_TITLE)}/>
            <ChannelsGridToolbar view={this.state.view} disabledSev={this.state.disabledSev} order={this.state.order}/>
            <ChannelsGrid view={this.state.view} channels={channelsOnPage}/>
            <PagingFooter {...pagingProps}/>
            <ChannelCrud/>
            <LoadingProgressBar dataLoaded={this.state.dataLoaded}/>
        </div>;
    }
});

export default Overview;
