import React from 'react';
import {Router, Route, IndexRoute} from 'react-router';
import Overview  from 'components/overview/Overview';
import DetailedView from 'components/details/DetailedView';
import ProbesAndTasks from 'components/probe-config/ProbesAndTasks';
import NotFound from 'components/common/NotFound';
import AppUserSettingsStore from 'stores/AppUserSettingsStore';
import Policies from 'components/policy/Policies';
import RecordSchedule from 'components/record/RecordSchedule';

var App = React.createClass({
    // TODO: unify top-level markup and make common route that has NavMenu, dialogs, etc
    render: function() {
        return this.props.children;
    }
});

AppUserSettingsStore.loadUserData(() => {
    AppUserSettingsStore.loadI18n(() => {
        AppUserSettingsStore.loadNavigationData(() => {
            var routes = [];
            if (AppUserSettingsStore.needToDisplay('CHANNEL_VIEW')) {
                routes = routes.concat([
                <Route path="overview" component={Overview} key="overview"/>,
                <Route path="details/:channelId" component={DetailedView} key="details"/>])
            }
            if (AppUserSettingsStore.needToDisplay('POLICIES')) {
                routes.push(<Route path="policy" component={Policies} key="policy"/>);
            }
            if (AppUserSettingsStore.needToDisplay('RECORDING_SCHEDULE')) {
                routes.push(<Route path="record" component={RecordSchedule} key="record"/>);
            }
            if (AppUserSettingsStore.needToDisplay('PROBE_CONFIG')) {
                routes.push(<Route path="probes" component={ProbesAndTasks} key="probes"/>);
            }
            routes = routes.concat([
                <Route path="*" component={NotFound} key="notFound"/>,
                <IndexRoute component ={Overview} key="index"
                    onEnter={(location, replaceWith)=>replaceWith(null, '/overview')}/>
            ]);
            React.render(
                <Router>
                    <Route path="/" component={App}>
                        {routes}
                    </Route>
                </Router>,
                document.getElementById('react-root'));
        });
    });
});


