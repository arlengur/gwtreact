import _ from 'lodash';
import Images from '../util/Images';
import i18nConstants from '../constants/i18nConstants';

var Locale = Object({
    capitaliseFirstLetter: function (string) {
        return string.charAt(0).toUpperCase() + string.slice(1);
    },
    localizeDateTime: function (timestamp, locale) {
        return timestamp != null ? new Date(timestamp).toLocaleString(locale) : null;
    },
    getActivityTitle: function (name, checked, i18n) {
        var property = '';
        if(checked) {
            if(name === 'ACTIVE'){
                property =  i18nConstants.HIDE_ACTIVE_ALARMS;
            } else {
                property =  i18nConstants.HIDE_CLEARED_ALARMS;
            }
        } else {
            if(name === 'ACTIVE') {
                property =  i18nConstants.SHOW_ACTIVE_ALARMS;
            } else {
                property =  i18nConstants.SHOW_CLEARED_ALARMS;
            }
        }
        return i18n.get(property);
    },

    getStateTitle: function(status, i18n) {
        var status = status == null ? "" : status;
        switch (status) {
            case "SUCCESS": return i18n.get(i18nConstants.REGISTERED);
            case "PARTIALLY": return i18n.get(i18nConstants.UNREGISTERED);
            //case "FAILED": return i18n.get(i18nConstants.FAILED);
            case "IN_PROGRESS": return i18n.get(i18nConstants.REGISTRATION);
            case "RESTART_SW": return i18n.get(i18nConstants.RESTARTING);
            case "CONFIG_ROLLBACK": return i18n.get(i18nConstants.CONFIG_ROLLING_BACK);
            case "UPDATE_CONFIG": return i18n.get(i18nConstants.CONFIG_UPDATING);
            case "UPDATE_SOFTWARE": return i18n.get(i18nConstants.SOFTWARE_UPDATING);
            default: return i18n.get(i18nConstants.UNREGISTERED);
        }
    },
    getGroupTitle: function(grp, i18n) {
        switch(grp) {
            case "RF_IP":       return i18n.get(i18nConstants.PHYSICAL_LINK);
            case "TS":          return i18n.get(i18nConstants.TRANSPORT_STREAM);
            case "VIDEO":       return i18n.get(i18nConstants.VIDEO);
            case "AUDIO":       return i18n.get(i18nConstants.AUDIO);
            case "EPG_CC_DATA": return i18n.get(i18nConstants.AUXILIARY_DATA);
        }
        return '';
    },
    zeroPad: function pad(num, size) {
        var s = "000000000" + num;
        return s.substr(s.length-size);
    },
    localizeDuration: function (millisec, detailed, i18n) {
        var days = Math.floor(millisec / (1000 * 60 * 60 * 24));
        var hours = Math.floor(millisec / (1000 * 60 * 60)) % 24;
        var minutes = Math.floor(millisec / (1000 * 60)) % 60;

        var duration = [];

        if( days > 0 ) duration.push(days + " " +  i18n.get(i18nConstants.DAYS));

        if( detailed ) {
            if (hours > 0 || minutes > 0 ) {
                duration.push(this.zeroPad(hours,2) + ":" + this.zeroPad(minutes, 2));
            }
        } else {
            if(hours > 0) {
                duration.push(hours + " " +  i18n.get(i18nConstants.HOURS));
            }
            if(minutes > 0) {
                duration.push(minutes + " " +  i18n.get(i18nConstants.MINUTES));
            }
        }
        return duration.join(" ");
    }
});

export default Locale;