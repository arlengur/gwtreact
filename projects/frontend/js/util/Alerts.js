import {addons} from 'react/addons';
const {update} = addons;
import _ from 'lodash';

export const ALL_GROUPS = ['RF', 'IP', 'TS', 'VIDEO', 'AUDIO', 'EPG', 'DATA', 'CC'];
export const ALL_SEVERITIES = ['CRITICAL', 'MAJOR', 'WARNING', 'MINOR', 'NOTICE', 'INDETERMINATE'];

export function groupToNum (s) {
    switch(s.toUpperCase()) {
        case "RF":    return 0;
        case "IP":    return 1;
        case "TS":    return 2;
        case "VIDEO": return 3;
        case "AUDIO": return 4;
        case "EPG":   return 5;
        case "CC":    return 6;
        case "DATA":
        default:
            return 7;
    }
}

export function severityToNum(s) {
    if(typeof s == 'string') {
        switch (s.toUpperCase()) {
            case 'CRITICAL': return 6;
            case 'MAJOR':    return 5;
            case 'WARNING':  return 4;
            case 'MINOR':    return 3;
            case 'NOTICE':   return 2;
            case 'INDETERMINATE': return 1;
            default: return 0;
        }
    } else {
        return 0;
    }
}

export function severityTo3Colors(s) {
    var severityNum = severityToNum(s);
    if(severityNum > 4) {
        return '#ff0000';
    } else if(severityNum > 0) {
        return '#ffaa00';
    } else {
        return '#00c200';
    }
}

export function severityTo6Colors(s) {
    if(typeof s == 'string') {
        switch (s.toUpperCase()) {
            case 'CRITICAL': return '#FF2700';
            case 'MAJOR':    return '#FF6100';
            case 'WARNING':  return '#FCC219';
            case 'MINOR':    return '#94601A';
            case 'NOTICE':   return '#969696';
            case 'INDETERMINATE':
            default:
                return '#5E5E5E';
        }
    } else {
        return '#5E5E5E';
    }
}

// TODO: unify severity colors between detailed veiw and policy
export function severityTo6Colors2(s) {
    if(typeof s == 'string') {
        switch (s.toUpperCase()) {
            case 'CRITICAL': return '#FF0000';
            case 'MAJOR':    return '#FF8000';
            case 'WARNING':  return '#FCC93F';
            case 'MINOR':    return '#FFFF3B';
            case 'NOTICE':   return '#E6E6E6';
            case 'INDETERMINATE':
            default:
                return '#A3A3A3';
        }
    } else {
        return '#A3A3A3';
    }
}

export function severityToLineY(s) {
    var severityNum = severityToNum(s);
    if(severityNum > 4) {
        return 1;
    } else if(severityNum > 0) {
        return 3;
    } else {
        return 4;
    }
}

export function currentSeverity(reports, endTime) {
    if(reports == null || typeof reports == 'undefined') {
        return "NONE";
    } else {
        var open = _.filter(reports,
            (report) => (report.endDateTime == null ||  report.endDateTime > endTime));
        if(_.isEmpty(open)) {return "NONE";}
        var maxSeverityReport = _.max(open, (report) => severityToNum(report.severity));
        return maxSeverityReport.severity;
    }
}

export function filterAlerts(alerts, disabledGroup, disabledSeverities, disabledActivities) {
    return _.chain(alerts)
        .filter((group) => !disabledGroup.contains(group.group))
        .map((group) => update(
            group,
            {
                alertsHistory:
                {$apply: (alerts) =>_.chain(alerts)
                        .filter((alert) => !disabledSeverities.contains(alert.severity))
                        .filter((alert) =>
                            (alert.endDateTime == null && !disabledActivities.contains('ACTIVE')) ||
                            (alert.endDateTime != null && !disabledActivities.contains('NONACTIVE')))
                        .value()
                }
            }))
        .value();
}

export function filterAlertsByInterval(alerts, interval) {
    return _.filter(alerts, function(alert) {
        var alertEnd = alert.endDateTime || Number.MAX_VALUE;
        // see http://c2.com/cgi/wiki?TestIfDateRangesOverlap
        return alertEnd >= interval.start && interval.end >= alert.startDateTime;
    });
}

//TODO: using groups as keys eliminates most problems with alert processing
export function group8toGroup5(groups) {
    var newGroups = [];
    var epgCcData = null;
    var rfIp = null;
    var tempGroup = [];

    ALL_GROUPS.forEach(function (group) {
        var temp = _.filter(groups, function (item) {
            return item.group == group
        });
        if (temp.length > 0) {
            tempGroup.push(temp);
        }
    });

    _.flatten(tempGroup).forEach(function (group) {
        var newGroup;
        if (group.group == "EPG" || group.group == "CC" || group.group == "DATA") {
            newGroup = epgCcData || {group: "EPG_CC_DATA", alertsHistory: []};
            newGroup.alertsHistory = newGroup.alertsHistory.concat(group.alertsHistory);
            epgCcData = newGroup;
        }
        else if (group.group == "RF" || group.group == "IP") {
            newGroup = rfIp || {group: "RF_IP", alertsHistory: []};
            newGroup.alertsHistory = newGroup.alertsHistory.concat(group.alertsHistory);
            rfIp = newGroup;
        }
        else {
            newGroups.push(group);
        }
    });

    if (epgCcData != null) {
        newGroups.push(epgCcData);
    }

    if (rfIp != null) {
        newGroups.unshift(rfIp);
    }

    return newGroups;
}
