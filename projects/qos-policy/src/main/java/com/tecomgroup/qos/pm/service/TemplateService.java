package com.tecomgroup.qos.pm.service;

import com.tecomgroup.qos.domain.UserSettings;

/**
 * @author smyshlyaev.s
 */
public interface TemplateService {
    String getTemplate(UserSettings.NotificationLanguage language, TemplateType type);

    UserSettings.NotificationLanguage getDefaultNotificationLanguage();

    public enum TemplateType {MAIL_SUBJECT, MAIL_BODY, SMS_BODY}
}
