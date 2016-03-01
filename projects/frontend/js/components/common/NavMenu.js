import React from 'react';
import {Link, History} from 'react-router';
import AppActions from '../../actions/AppActions';
import i18n from '../../constants/i18nConstants';
import Images from '../../util/Images';
import AppUserSettingsStore from '../../stores/AppUserSettingsStore';
const tr = AppUserSettingsStore.localizeString;

var NavMenu = React.createClass({
    mixins: [History],

    getInitialState: function () {
        return {
            expanded: false
        };
    },
    clickHandler: function () {
        this.setState({
            expanded: !this.state.expanded
        });
    },
    blurHandler: function () {
        if (this.state.expanded) {
            this.setState({
                expanded: false
            });
        }
    },
    getGwtLink: function (link) {
        var locale = AppUserSettingsStore.getLocale();
        if (locale == undefined) {
            locale = 'en';
        }
        return './?locale=' + locale + link + ';page=0';
    },
    render: function () {
        var mainBg = {backgroundImage: Images.navMenuImage(this.state.expanded)};
        var username=AppUserSettingsStore.getUsername();
        var linkStyle = {color: 'white'};
        var video_link;
        var recorded_link;

        if(AppUserSettingsStore.needToDisplay('MAIN') == true) {
            var links = [<li key="dashboard">
                <a href={this.getGwtLink('#dashboard')} className="navItem dashboard">{tr(i18n.NAV_DASHBOARD)}</a>
            </li>]
        }

        if(!this.history.isActive('/overview') && AppUserSettingsStore.needToDisplay('CHANNEL_VIEW')) {
            links.push(<li key="channelView"><Link to={'/overview'} className="navItem channelView">{tr(i18n.NAV_CHANNEL_VIEW)}</Link></li>);
        }

        if (AppUserSettingsStore.needToDisplay('PROBE_CONFIG')) {
            links.push(<li key="probesAndTasks"><a href={this.getGwtLink('#probesAndTasks')} className="navItem probesAndTasks">{tr(i18n.NAV_PROBES_AND_TASKS)}</a></li>);
        }

        if(!this.history.isActive('/probes' ) && AppUserSettingsStore.needToDisplay('PROBE_CONFIG')) {
            links.push(<li key="probeConfig"><Link to={'/probes'} className="navItem probeConfig">{tr(i18n.NAV_PROBE_CONFIG)}</Link></li>);
        }

        if (AppUserSettingsStore.needToDisplay('ALERTS')) {
            links.push(<li key="alerts">
                <a href={this.getGwtLink('#alerts')} className="navItem alarms">{tr(i18n.NAV_ALERTS)}</a>
            </li>);
        }

        if(!this.history.isActive('/record' ) && AppUserSettingsStore.needToDisplay('RECORDING_SCHEDULE')) {
            links.push(<li key="recordSchedule"><Link to={'/record'} className="navItem recordSchedule">{tr(i18n.NAV_RECORD_SCHEDULE)}</Link></li>);
        }

        if(AppUserSettingsStore.needToDisplay('LIVE_VIDEO') == true) {
            video_link = <li><a href={this.getGwtLink('#video')} className="navItem live">{tr(i18n.NAV_LIVE_VIDEO)}</a></li>;
        }

        if(AppUserSettingsStore.needToDisplay('RECORDED_VIDEO') == true) {
            recorded_link = <li><a href={this.getGwtLink('#recorded')} className="navItem recorded">{tr(i18n.NAV_RECORDED_VIDEO)}</a></li>;
        }

        if(AppUserSettingsStore.needToDisplay('LIVE_VIDEO') || AppUserSettingsStore.needToDisplay('RECORDED_VIDEO') ) {
            links.push(<li key="video" className="dropdown-submenu">
                <a className="navItem linkNoClick videoWall">{tr(i18n.NAV_VIDEO_WALL)}</a>
                <ul className="dropdown-menu">
                {video_link}
                {recorded_link}
                </ul>
            </li>);
        }

        if(AppUserSettingsStore.needToDisplay('CHARTS') == true) {
            links.push(<li key="charts"><a href={this.getGwtLink('#charts')} className="navItem analysis">{tr(i18n.NAV_ANALYSIS)}</a></li>);
        }

        if(AppUserSettingsStore.needToDisplay('REPORTS') == true) {
            links.push(<li key="reports"><a href={this.getGwtLink('#reports')} className="navItem reports">{tr(i18n.NAV_REPORTS)}</a></li>);
        }

        if(AppUserSettingsStore.needToDisplay('MAP') == true) {
            links.push(<li key="gis"><a href={this.getGwtLink('#gis')} className="navItem map">{tr(i18n.NAV_MAP)}</a></li>);
        }

        return (
            <nav className={"qligent-top-navbar flex-none " + this.props.className}>
                <div className="container-fluid override-padding-0">
                    <div className="dropdown pull-left">
                        <a href="#" className="dropdown-toggle" data-toggle="dropdown" onBlur={this.blurHandler} onClick={this.clickHandler} style={mainBg} role="button" aria-expanded="false"></a>
                        <ul className="dropdown-menu" role="menu">
                            {links}
                        </ul>
                    </div>
                    <div className="logout-button pull-right" style={{margin: '10px 25px auto 10px'}}
                        title={tr(i18n.LOGOUT)}
                        onClick={AppActions.logout}/>
                    <div className="pull-right username small-text">
                        <a href={this.getGwtLink('#user')}  style={linkStyle}>{username}</a>
                    </div>
                    <span className="dashboard-title overflow-ellipsis">
                     {this.props.title}
                    </span>
                </div>
            </nav>
        );
    }
});

export default NavMenu;