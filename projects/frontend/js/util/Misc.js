import _ from 'lodash';
import moment from 'moment';

var Misc= Object.freeze({
    getScrollbarWidth: function () {
        var inner = document.createElement('p');
        inner.style.width = "100%";
        inner.style.height = "200px";

        var outer = document.createElement('div');
        outer.style.position = "absolute";
        outer.style.top = "0px";
        outer.style.left = "0px";
        outer.style.visibility = "hidden";
        outer.style.width = "200px";
        outer.style.height = "150px";
        outer.style.overflow = "hidden";
        outer.appendChild(inner);

        document.body.appendChild(outer);
        var w1 = inner.offsetWidth;
        outer.style.overflow = 'scroll';
        var w2 = inner.offsetWidth;
        if (w1 == w2) w2 = outer.clientWidth;

        document.body.removeChild(outer);

        return w1 - w2;
    },
    getUrlParam: function(name) {
        return decodeURIComponent(
                (new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)')
                    .exec(location.search)||[,""])[1]
                    .replace(/\+/g, '%20'))
            || null
    },
    getUrlParamDefault: function(name, defaultValue) {
        var value = this.getUrlParam(name);

        if (value == null || value == undefined) {
            return defaultValue;
        }
        return value;
    },
    refreshIfForbidden: function(xhr) {
        if (xhr.status == 403) { //Forbidden, go to login
            //Use a reload,Spring Secure will redirect to Login
            location.reload(true);
        }
    },
    toggle: function(_set, item) {
        if(_set.contains(item)) {
            return _set.delete(item)
        } else {
            return _set.add(item)
        }
    },
    toggleMap: function(_map, key,item) {
        if(_map.has(key)) {
            return _map.delete(key);
        } else {
            return _map.set(key,item);
        }
    },
    toggleAll: function(set, items) {
        return _.reduce(items, this.toggle, set);
    },
    guid: function (){
        function _p8(s) {
            var p = (Math.random().toString(16)+"000000000").substr(2,8);
            return s ? "-" + p.substr(0,4) + "-" + p.substr(4,4) : p ;
        }
        return _p8() + _p8(true) + _p8(true) + _p8();
    },
    getDays: function(millsec) {
        return Math.floor(millsec / (1000*60*60*24));
    },
    getHours: function(millsec) {
        var noDaysMillsec = millsec - this.getDays(millsec) * (1000*60*60*24);
        return Math.floor(noDaysMillsec / (1000*60*60));
    },
    getMinutes: function(millsec) {
        var noHoursMillsec = millsec - this.getDays(millsec) * (1000*60*60*24) - this.getHours(millsec) * (1000*60*60);
        return Math.floor(noHoursMillsec / (1000*60));
    },
    formatDate: function (date) {
        return date != null ? moment(date).format('MM.DD.YYYY, HH:mm:ss') : null;
    },
    validateDigit: function(event) {
        if (event.charCode < 48 || event.charCode > 57) {
            event.stopPropagation();
            event.preventDefault();
        }
    },
    validateDate: function(event) {
        let ok = (event.charCode >= 48 && event.charCode <= 57) ||
            event.charCode == 47;
        if(!ok) {
            event.stopPropagation();
            event.preventDefault();
        }
    },
    validateTime: function(event) {
        let ok = (event.charCode >= 48 && event.charCode <= 57) ||
            event.charCode == 58;
        if(!ok) {
            event.stopPropagation();
            event.preventDefault();
        }
    },
    validateRealNumDigit: function(event) {
        let ok = (event.charCode >= 48 && event.charCode <= 57) ||
            event.charCode == 46 ||
            event.charCode == 45 ||
            event.charCode == 109 ||
            event.charCode == 189;
        if(!ok) {
            event.stopPropagation();
            event.preventDefault();
        }
    },
    validateInt: function(val, max, min) {
        if(typeof min == 'undefined') min = 0;
        if(typeof max == 'undefined') max = Infinity;
        var num = parseInt(val);
        return val== '' || (!isNaN(num) && num <= max && num >= min);
    },
    validateFloat: function(val, max, min) {
        if(typeof min == 'undefined') min = -Infinity;
        if(typeof max == 'undefined') max = Infinity;
        var num = parseFloat(val);
        return val== '' || (!isNaN(num) && num <= max && num >= min);
    },
    firstUppercase: function(string) {
        return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
    }
});

export default Misc;