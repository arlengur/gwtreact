package com.tecomgroup.qos.gwt.client.view.desktop.widget;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.util.Margins;
import com.tecomgroup.qos.gwt.client.style.AppearanceFactoryProvider;
import com.tecomgroup.qos.gwt.client.style.CommonResources.CommonStyle;
import com.tecomgroup.qos.gwt.client.utils.DateUtils;

/**
 * @author smyshlyaev.s
 */
public class DateTimeWidget extends Widget implements HasValue<Date> {

	public static enum Mode {
		DATE, TIME,
	}

	private static final Map<String, DateTimeFormat> LOCALE_DATE_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("dd.MM.yy"));
			put("en", DateTimeFormat.getFormat("M/d/yy"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_UTC_DATE_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("dd.MM.yy Z"));
			put("en", DateTimeFormat.getFormat("M/d/yy Z"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("HH:mm"));
			put("en", DateTimeFormat.getFormat("h:mm a"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_UTC_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("HH:mm Z"));
			put("en", DateTimeFormat.getFormat("h:mm a Z"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_DATE_TIME_WITH_SEC_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("dd.MM.yy HH:mm:ss"));
			put("en", DateTimeFormat.getFormat("M/d/yy h:mm:ss a"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_DATE_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("dd.MM.yy HH:mm"));
			put("en", DateTimeFormat.getFormat("M/d/yy h:mm a"));
		}
	};

	private static final Map<String, DateTimeFormat> LOCALE_UTC_DATE_TIME_FORMATS = new HashMap<String, DateTimeFormat>() {
		{
			put("ru", DateTimeFormat.getFormat("dd.MM.yy HH:mm Z"));
			put("en", DateTimeFormat.getFormat("M/d/yy h:mm a Z"));
		}
	};

	private static final String DEFAULT_FORMAT_LOCALE = "ru";

	private static final Map<String, DateTimeWidget> instances = new TreeMap<String, DateTimeWidget>();

	public static void changeDateTimeCallback(final String id,
			final String value) {
		final DateTimeWidget source = instances.get(id);
		final Date date = parseValue(value, source.mode);
		ValueChangeEvent.fire(source, date);
	}
	private static native void disable(final String id) /*-{
		$wnd.qosDatePickerModule.disable(id);
	}-*/;
	private static native void enable(final String id) /*-{
		$wnd.qosDatePickerModule.enable(id);
	}-*/;
	private static String encodeValue(final Date value, final Mode mode) {
		switch (mode) {
			case DATE :
				return getCurrentLocaleDateFormat().format(value, DateUtils.getCurrentTimeZone());
			case TIME :
				return getCurrentLocaleTimeFormat().format(value, DateUtils.getCurrentTimeZone());
			default :
				return "";
		}
	}

	public static DateTimeFormat getCurrentLocaleDateFormat() {
		DateTimeFormat format = LOCALE_DATE_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_DATE_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleUTCDateFormat() {
		DateTimeFormat format = LOCALE_UTC_DATE_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_UTC_DATE_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleTimeFormat() {
		DateTimeFormat format = LOCALE_TIME_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_TIME_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleUTCTimeFormat() {
		DateTimeFormat format = LOCALE_UTC_TIME_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_UTC_TIME_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleDateTimeWithSecondsFormat() {
		DateTimeFormat format = LOCALE_DATE_TIME_WITH_SEC_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_DATE_TIME_WITH_SEC_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleDateTimeFormat() {
		DateTimeFormat format = LOCALE_DATE_TIME_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_DATE_TIME_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static DateTimeFormat getCurrentLocaleUTCDateTimeFormat() {
		DateTimeFormat format = LOCALE_UTC_DATE_TIME_FORMATS.get(LocaleInfo
				.getCurrentLocale().getLocaleName());
		if (format == null) {
			format = LOCALE_UTC_DATE_TIME_FORMATS.get(DEFAULT_FORMAT_LOCALE);
		}
		return format;
	}

	public static String formatDateTimeWithSeconds(Date date, int timeZoneOffset) {
		return getCurrentLocaleDateTimeWithSecondsFormat().format(date, TimeZone.createTimeZone(timeZoneOffset));
	}

	public static String formatDateTime(Date date, int timeZoneOffset) {
		return getCurrentLocaleDateTimeFormat().format(date, TimeZone.createTimeZone(timeZoneOffset));
	}

	private static native String getValue(final String id) /*-{
		return $wnd.qosDatePickerModule.getValue(id);
	}-*/;

	private static native void initDateField(final String id,
			final String language) /*-{
		$wnd.qosDatePickerModule.initDateField(id, language);
	}-*/;

	private static native void initTimeField(final String id,
			final String language) /*-{
		$wnd.qosDatePickerModule.initTimeField(id, language);
	}-*/;

	private static Date parseValue(final String value, final Mode mode) {
		Date date = null;

		try {
			switch (mode) {
				case DATE : {
					date = getCurrentLocaleUTCDateFormat().parseStrict(addTimezoneOffset(value));
					break;
				}
				case TIME : {
					date = getCurrentLocaleUTCTimeFormat().parseStrict(addTimezoneOffset(value));
					break;
				}
			}
		} catch (final IllegalArgumentException e) {
			// do nothing, return null
		}
		return date;
	}

	public static Date parseFullDate(final String value, final int offset) {
		String offsetZ = DateTimeFormat.getFormat("Z").format(new Date(), TimeZone.createTimeZone(-offset));
		return getCurrentLocaleUTCDateTimeFormat().parse(addTimezoneOffset(value, offsetZ));
	}

	private static String addTimezoneOffset(String value, String offset) {
		return new StringBuilder(value).append(" ").append(offset).toString();
	}

	private static String addTimezoneOffset(String value) {
		String offsetZ = DateTimeFormat.getFormat("Z").format(new Date(), TimeZone.createTimeZone(-DateUtils.getCurrentTimeZoneOffset()));
		return addTimezoneOffset(value, offsetZ);
	}

	private static native String setValue(final String id, final String value) /*-{
		return $wnd.qosDatePickerModule.setValue(id, value);
	}-*/;

    private static native boolean elementExists(final String id) /*-{
        return $wnd.qosDatePickerModule.exists(id);
    }-*/;

	private final Mode mode;

	private final InlineHTML html;

	private final String elementId;

	private boolean enabled;
	private final String language;

    // when html element is not yet added to DOM, we work with in memory value
    private Date value;

	public DateTimeWidget(final Mode mode) {
		this.mode = mode;
		language = LocaleInfo.getCurrentLocale().getLocaleName();
		elementId = Document.get().createUniqueId();
		html = new InlineHTML("<input id=\"" + elementId + "\" type=\"text\">");
		final CommonStyle css = AppearanceFactoryProvider.instance()
				.resources().css();
		html.addStyleName(css.dateTimeWidget());
		setElement(html.getElement());
		instances.put(elementId, this);
	}

	@Override
	public HandlerRegistration addValueChangeHandler(
			final ValueChangeHandler<Date> handler) {
		return this.addHandler(handler, ValueChangeEvent.getType());
	}

	/**
	 * @return String representation of a date or time, containing in text
	 *         field.
	 */
	public String getRawValue() {
		return getValue(elementId);
	}

	@Override
	public Date getValue() {
		if (elementExists(elementId)) {
			final Date parsedValue = parseValue(getValue(elementId), mode);
			return parsedValue != null ? parsedValue : value;
		}
		else {
			return value;
		}
	}

	public String getStringValue(Date date) {
		String val = null;
			try {
				switch (mode) {
					case DATE : {
						val = getCurrentLocaleDateFormat().format(date, DateUtils.getCurrentTimeZone());
						break;
					}
					case TIME : {
						val = getCurrentLocaleTimeFormat().format(date, DateUtils.getCurrentTimeZone());
						break;
					}
				}
			} catch (final IllegalArgumentException e) {
				// do nothing, return null
			}
		return val;
	}

	@Override
	public void onLoad() {
		super.onLoad();
		switch (mode) {
			case DATE :
				initDateField(elementId, language);
				break;
			case TIME :
				initTimeField(elementId, language);
				break;
		}
		if (!enabled) {
			disable(elementId);
		}
	}

	public void setEnabled(final boolean enabled) {
		this.enabled = enabled;
		if (enabled) {
			enable(elementId);
		} else {
			disable(elementId);
		}
	}

	public void setMargins(final Margins margins) {
		getElement().getStyle().setMarginTop(margins.getTop(), Unit.PX);
		getElement().getStyle().setMarginRight(margins.getRight(), Unit.PX);
		getElement().getStyle().setMarginBottom(margins.getLeft(), Unit.PX);
		getElement().getStyle().setMarginLeft(margins.getBottom(), Unit.PX);
	}

	@Override
	public void setValue(final Date value) {
		setValue(elementId, encodeValue(value, mode));
        this.value = value;
	}

	@Override
	public void setValue(final Date value, final boolean fireEvents) {
		if (value != null && !value.equals(getValue())) {
			setValue(value);
			if (fireEvents) {
				ValueChangeEvent.fire(this, value);
			}
		}
	}

	public void setWidth(final int width) {
		setWidth(width + "px");
	}
}
