import {groupToNum, severityToNum} from './Alerts';

var Images = Object.freeze({
    goReports: "img/detailed/GoReports.png",
    goAnalysis: "img/detailed/GoAnalysis.png",
    goBroadcast: "img/detailed/GoBroadcast.png",
    goRecorded: "img/detailed/GoToRecorded.png",
    severitySuffix: function (s) {
        var severityNum = severityToNum(s);
        if (severityNum > 4) {
            return 'red.png';
        } else if (severityNum > 0) {
            return 'yellow.png';
        } else {
            return 'green.png';
        }
    },
    audio: function (s) {
        return 'img/sound_' + this.severitySuffix(s)
    },
    video: function (s) {
        return 'img/video_' + this.severitySuffix(s)
    },
    other: function (s) {
        return 'img/dots_' + this.severitySuffix(s)
    },
    changeMode1: function (a) {
        return a ? "img/changemode1_active.png" : "img/changemode1.png"
    },
    changeMode2: function (a) {
        return a ? "img/changemode2_active.png" : "img/changemode2.png"
    },
    changeMode3: function (a) {
        return a ? "img/changemode3_active.png" : "img/changemode3.png"
    },
    timelinePrefixes: {
        "RF": "img/lines/Lines_RF_",
        "TS": "img/lines/Lines_TS_",
        "VIDEO": "img/lines/Lines_Video_",
        "AUDIO": "img/lines/Lines_Audio_",
        "DATA": "img/lines/Lines_Data_",
        "IP": "img/lines/Lines_IP_",
        "EPG_CC_DATA": "img/lines/Lines_EPG_CC_DATA_",
        "RF_IP": "img/lines/Lines_RF_IP_"
    },
    timelineIcon: function (t, s) {
        return this.timelinePrefixes[t] + this.severitySuffix(s);
    },
    moveArrow: function (c) {
        if (c) {
            return "img/tree_move_arrow_active.png";
        } else {
            return "img/tree_move_arrow_inactive.png";
        }
    },
    groupImgPrefix: function (s) {
        switch (groupToNum(s)) {
            case 0: return "_rf.png";
            case 1: return "_ip.png";
            case 2: return "_ts.png";
            case 3: return "_video.png";
            case 4: return "_audio.png";
            case 5: return "_epg.png";
            case 6: return "_cc.png";
            default:return "_data.png";
        }
    },
    groupLargeIcon: function (s) {
        return "img/crud/set" + this.groupImgPrefix(s);
    },
    groupPref: function (group) {
        switch (group.toUpperCase()) {
            case "RF": return "RF";
            case "IP": return "IP";
            case "TS": return "TS";
            case "AUDIO": return "Audio";
            case "VIDEO": return "Video";
            case "CC":   return "CC";
            case "EPG":  return "EPG";
            case "DATA": return "Data";
            default: return "";
        }
    },
    groupFilterSuffix: function (sev) {
        if (typeof sev != 'string') {
            return '_B';
        }
        var severityNum = severityToNum(sev);
        if (severityNum > 4) {
            return '_R';
        } else if (severityNum > 0) {
            return '_Y';
        } else {
            return '_G';
        }
    },
    groupFilterImg: function (group, sev) {
        var extSuffix = (groupToNum(group) < 5) ? '_Big.png' : '.png';
        return 'img/detailed/filter/' +
            this.groupPref(group) +
            this.groupFilterSuffix(sev) + extSuffix;
    },
    groupFilterBg: function (group, sev1, sev2, sev3) {
        switch (group.toUpperCase()) {
            case "RF_IP":
                return 'url(' + this.groupFilterImg("RF", sev1) +
                    '), url(' + this.groupFilterImg("IP", sev2) + ')';
            case "EPG_CC_DATA":
                return 'url(' + this.groupFilterImg("EPG", sev1) +
                    '), url(' + this.groupFilterImg("CC", sev2) +
                    '), url(' + this.groupFilterImg("DATA", sev3) + ')';
            default:
                return 'url(' + this.groupFilterImg(group, sev1) + ')';
        }
    },
    activityFilterBg: function (activity, enabled) {
        var actName = activity == 'ACTIVE' ? 'Active_' : 'NonActive_';
        var suffix = enabled ? 'On.png)' : 'Off.png)';
        return 'url(img/detailed/filter/Filter_Activity_' + actName + suffix;
    },
    alertTabSeveritySuf: function (severity) {
        var severityNum = severityToNum(severity);
        if (severityNum > 4) {
            return 'R';
        } else if (severityNum > 0) {
            return 'Y';
        } else {
            return '';
        }
    },
    alertTabBg: function (group, severity) {
        return "url(img/detailed/filter/" + this.groupPref(group) + "_"
            + this.alertTabSeveritySuf(severity) + "_Big.png)";
    },
    liveRecordedVideo: function (live) {
        if (live) {
            return "img/live.png";
        } else {
            return "img/recorded.png";
        }
    },
    getShowAllTasksImage: function (enabled) {
        if (enabled) {
            return "img/detailed/show_all_tasks_enabled.png";
        } else {
            return "img/detailed/show_all_tasks_disabled.png";
        }
    },
    clockImage: function () {
        return "img/clock.png";
    },
    treeLine: function(last) {
        return last ? "img/tree_line_end.png" : "img/tree_line_middle.png"
    },
    alertTreeIconPrefix: function(group) {
        switch (group.toUpperCase()) {
            case "RF": return "RF";
            case "IP": return "IP";
            case "TS": return "TS";
            case "AUDIO": return "Sound";
            case "VIDEO": return "Video";
            case "CC":   return "CC";
            case "EPG":  return "EPG";
            case "DATA": return "Data";
            default: return "";
        }
    },
    alertTreeIconSuffix: function(severity) {
        var severityNum = severityToNum(severity);
        if (severityNum > 4) {
            return 'R';
        } else if (severityNum > 0) {
            return 'Y';
        }
    },
    alertTreeIcon: function(group, severity) {
        return 'img/alert/Tree_' + this.alertTreeIconPrefix(group) + '_' +
                this.alertTreeIconSuffix(severity) + '.png';
    },
    detailsProbeExpand: function(expanded) {
        return expanded ? "img/tree_minus.png" : "img/tree_plus.png";
    },
    navMenuImage: function(expanded){
        var open = expanded ? "_opened" : "";
        return "url(img/nav/MainBtn" + open + ".png)";
    },
    getDefaultChannelLogo: function () {
        return "img/channels/default.png";
    },
    remoteProbeStatus: function(status, isProbe) {
        var nodeType = isProbe ? "probe" : "tsk";
        var status = status == null ? "" : status;
        switch (status.toUpperCase()) {
            case "SUCCESS": return "img/probeConfig/" + nodeType + "_sign_good.png";
            case "PARTIALLY": return "img/probeConfig/" + nodeType + "_sign_warning.png";
            //case "FAILED": return "img/probeConfig/" + nodeType + "_sign_alarm.png";
            case "IN_PROGRESS": return "img/probeConfig/" + nodeType + "_sign_registration.png";
            case "RESTART_SW": return "img/probeConfig/probe_sign_restarting.png";
            case "RESTART_HW": return "img/probeConfig/probe_sign_restarting.png";
            case "CONFIG_ROLLBACK": return "img/probeConfig/probe_sign_rollingBack.png";
            case "UPDATE_CONFIG": return "img/probeConfig/probe_sign_updatingConfig.png";
            case "UPDATE_SOFTWARE": return "img/probeConfig/probe_sign_updatingSW.png";
            default: return "img/probeConfig/" + nodeType + "_sign_warning.png";
        }
    },
    taskGroup: function(group) {
        return "tsk-"+group.toLowerCase();
    },
    favouriteIcon: function(active, hover) {
        if(active && hover) {
            return "img/pinned_set_over.png";
        } else if(active && !hover) {
            return "img/pinned_set.png";
        } else if(!active && hover) {
            return "img/pin_set_over.png";
        } else if(!active && !hover) {
            return "img/pin_set.png";
        }
    },
    severityFilter: function(sev) {
        switch(sev) {
            case 'CRITICAL': return 'channel-filter-crit';
            case 'WARNING': return 'channel-filter-warn';
            case 'NONE': return 'channel-filter-none';
            default: throw "Argument should be one of ['CRITICAL','WARNING','NONE']"
        }
    }
});
export default Images;
