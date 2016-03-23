import {EventEmitter} from 'events';
import assign from 'object-assign';
import Immutable from 'immutable';
import _ from 'lodash';
import OverviewActions from '../actions/OverviewActions';
import Misc from '../util/Misc';
import Locale from '../util/Locale';

var _username;
var _authorities;
var _locale;
var _i18n = Immutable.Map();
var _pages_to_show;

var AppUserSettingsStore = assign({}, EventEmitter.prototype, {

    loadData: function (data) {
        _username = data.username;
        _authorities = data.authorities;
        _locale = Misc.getUrlParamDefault('locale','en');
    },
    needToDisplay: function(link) {
       return _.indexOf(_pages_to_show, link) != -1;
    },
    getLocale: function() {
        return _locale;
    },
    getUsername: function() {
        return _username;
    },
    isAdmin: function() {
        return _.any(_authorities, (a) => a.authority == 'ROLE_ADMIN');
    },
    loadUserData: function(callback) {
        var url = 'rest/channel/config/user';
        $.ajax({
            url: url,
            dataType: 'json',
            success: function (data) {
                AppUserSettingsStore.loadData(data);
                callback();
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
                OverviewActions.refreshIfForbidden(xhr);
            }.bind(this)
        });
    },
    loadNavigationData: function(callback) {
        var url = 'rest/channel/config/pages';
        $.ajax({
            url: url,
            dataType: 'json',
            success: function (data) {
                _pages_to_show = data;
                callback();
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
            }.bind(this)
        });
    },
    loadI18n: function(callback) {
        var url = 'i18n/properties.' + _locale;
        $.ajax({
            url: url,
            dataType: 'json',
            contentType: "application/x-www-form-urlencoded;charset=ISO-8859-15",
            success: function (data) {
                _i18n = Immutable.Map(data);
                callback();
            }.bind(this),
            error: function (xhr, status, err) {
                console.error(url, status, err.toString());
            }.bind(this)
        });
    },

    // i18n processing
    localizeString: function(property) {
        return _i18n.get(property);
    },
    localizeStringCFL: function(property) {
        return Locale.capitaliseFirstLetter(_i18n.get(property));
    },
    localizeDateTime: function (timestamp) {
        return Locale.localizeDateTime(timestamp, _locale);
    },
    localizeTimeDuration: function(timestamp, mode) {
        return Locale.localizeDuration(timestamp, mode, _i18n);
    },
    getActivityTitle: function (name, checked) {
        return Locale.getActivityTitle(name, checked, _i18n);
    },
    getGroupTitle: function(grp) {
        return Locale.getGroupTitle(grp, _i18n);
    },
    getStateTitle: function(state) {
        return Locale.getStateTitle(state, _i18n);
    }
});


export default AppUserSettingsStore;
