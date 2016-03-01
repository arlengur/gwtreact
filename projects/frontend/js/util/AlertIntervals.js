import {addons} from 'react/addons';
const {update} = addons;
import _ from 'lodash';
import buckets from 'buckets-js';
import {severityToNum} from './Alerts';

var AlertIntervals = Object.freeze({
    // Given a set of alert reports with possibly overlapping intervals
    // and start/end of the timeline, returns a set of non-overlapping intervals
    // that together cover the entire timeline. The severity of each resulting
    // interval will correspond to the maximum severity of alert report from the input
    // which is active during this interval.
    // The use case for this function is to minimize the number of DOM elements in timelines
    // (vs drawing each interval as a separate rectangle) while maintaining the exact same
    // appearance.
    // Example input:
    //   reports: [ {
    //      "startDateTime": -2,
    //      "endDateTime": 1,
    //      "severity": "WARNING"
    //   }, {
    //      "startDateTime": 3,
    //      "endDateTime": 9,
    //      "severity": "CRITICAL"
    //   }, {
    //      "startDateTime": 2,
    //      "endDateTime": null,
    //      "severity": "WARNING"
    //   }]
    //   start: 0
    //   end: 10
    // Example output:
    //   [ {
    //      "startDateTime": 0,
    //      "endDateTime": 1,
    //      "severity": "WARNING",
    //      "closed": true
    //   }, {
    //      "startDateTime": 1,
    //      "endDateTime": 2,
    //      "severity": "NONE",
    //      "closed": false
    //   }, {
    //      "startDateTime": 2,
    //      "endDateTime": 3,
    //      "severity": "WARNING",
    //      "closed": false
    //   }, {
    //      "startDateTime": 3,
    //      "endDateTime": 9,
    //      "severity": "CRITICAL",
    //      "closed": true
    //   }, {
    //      "startDateTime": 9,
    //      "endDateTime": 10,
    //      "severity": "WARNING",
    //      "closed": false
    //   }]
    merge: function(reports, start, end) {
        var preproc = _.chain(reports)
            .map(function(report) {
                if(report.endDateTime == null ) {
                    return update(report, {$merge: {endDateTime: end, closed: false}});
                } else {
                    return update(report, {$merge: {closed: true}});
                }
            })
            .map(function(report) {
                if(report.startDateTime < start) {
                    return update(report, {$merge: {startDateTime: start}})
                } else {
                    return report
                }
            })
            .map(function(report) {
                if(report.endDateTime > end) {
                    return update(report, {$merge: {endDateTime: end}})
                } else {
                    return report
                }
            }).value();
        preproc.push({
            startDateTime: start,
            endDateTime: end,
            severity: 'NONE',
            closed: false
        });
        var heap = buckets.Heap(function(r1, r2) {
            if(r1.startDateTime< r2.startDateTime) {
                return -1;
            } else if(r1.startDateTime== r2.startDateTime) {
                return 0;
            } else {
                return 1;
            }
        });
        _.each(preproc, function(report) {
            heap.add(report);
        });
        var result = [];
        while(heap.size() > 0) {
            var current = heap.removeRoot();
            while(heap.size() > 0 && heap.peek().startDateTime < current.endDateTime) {
                var earliest = heap.removeRoot();
                if(current.severity != earliest.severity) {
                    if(severityToNum(current.severity) < severityToNum(earliest.severity)) {
                        if(current.endDateTime <= earliest.endDateTime) {
                            heap.add(earliest);
                            current.endDateTime = earliest.startDateTime;
                        } else {
                            heap.add(earliest);
                            heap.add(update(current, {$merge: {startDateTime: earliest.endDateTime}}));
                            current.endDateTime = earliest.startDateTime;
                        }
                    } else {
                        if(current.endDateTime < earliest.endDateTime) {
                            heap.add(update(earliest, {$merge: {startDateTime: current.endDateTime}}));
                        } else {
                            // do nothing, effectively just deleting 'earliest' from heap
                        }
                    }
                } else if (current.closed != earliest.closed) {
                    if(current.closed == true && earliest.closed == false) {
                        current.endDateTime = earliest.startDateTime;
                        heap.add(earliest);
                    } else {
                        // do nothing, effectively just deleting 'earliest' from heap
                    }
                } else {
                    current.endDateTime = _.max([current.endDateTime, earliest.endDateTime]);
                }
            }
            result.push(current);
        }
        return result;
    }
});

export default AlertIntervals;
