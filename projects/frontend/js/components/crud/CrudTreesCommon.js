import React from 'react';
import _ from 'lodash';

var CrudTreesCommon = Object.freeze({
    matches: function(str, f) {
        return (str == null) ? false : (str.toUpperCase().indexOf(f.toUpperCase()) > -1);
    },

    getDisplayedProbes: function(probes, filter) {
        return _.filter(probes, (p) =>
            this.matches(p.name, filter) ||
                _.some(p.tasks, (t) => this.matches(t.name, filter)));
    },

    getDisplayedTasks: function(p, filt) {
        if(this.matches(p.name, filt)) {
            return p.tasks;
        } else {
            return _.filter(p.tasks, (t) => this.matches(t.name, filt));
        }
    },

    getHighlightedName: function(name, f) {
        if(name == null) {return '';}
        var ix = name.toUpperCase().indexOf(f.toUpperCase());
        if(ix > -1) {
            var start = name.substr(0, ix);
            var highlighted = name.substr(ix, f.length);
            var end = name.substr(ix + f.length);
            return [
                <span key={0}>{start}</span>,
                <span key={1} className="crud-tree-matching-text">{highlighted}</span>,
                <span key={2}>{end}</span>];
        } else {
            return name;
        }
    }
});

export default CrudTreesCommon;