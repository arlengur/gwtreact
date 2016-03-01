var qosDatePickerModule = (function() {
    function getLocaleSettings(locale) {
        var settings;
        switch(locale) {
            case "en":
                settings = {
                    time : { format: "g:i A", mask: "19:59 AM" },
                    date : { format: "n/j/y", mask: "19/39/99" },
                    dayOfWeekStart: 0
                };
                break;
            case "ru":
            default:
                settings = {
                    time : { format: "H:i", mask: "29:59" },
                    date : { format: "d.m.y", mask: "39.19.99" },
                    dayOfWeekStart: 1
                };
        }
        return settings;
    }

    return {
        initDateField: function(id, language) {
            var settings = getLocaleSettings(language);
            $('#' + id).datetimepicker({
                timepicker: false,
                datepicker: true,
                lang: language,
                mask: settings.date.mask,
                format: settings.date.format,
                dayOfWeekStart: settings.dayOfWeekStart,
                onChangeDateTime: function(dp, $input) {
                    window.changeDateTimeCallback(id, $input.val());
                }
            });
        },
        initTimeField: function(id, language) {
            var settings = getLocaleSettings(language);
            $('#' + id).datetimepicker({
                timepicker: true,
                datepicker: false,
                lang: language,
                mask: settings.time.mask,
                format: settings.time.format,
                step: 15,
                onChangeDateTime: function(dp, $input) {
                    window.changeDateTimeCallback(id, $input.val());
                }
            });
        },
        enable: function(id) {
            $('#' + id).removeAttr('disabled');
        },
        disable: function(id) {
            $('#' + id).attr('disabled', 'disabled');
        },
        getValue: function(id) {
            return $('#' + id).val();
        },
        setValue: function(id, value) {
            $('#' + id).val(value);
        },
        exists: function(id) {
            return $('#' + id).length > 0;
        }
    };
}());