package com.tecomgroup.qos.pm.service;

import static com.tecomgroup.qos.domain.UserSettings.NotificationLanguage.EN;
import static com.tecomgroup.qos.domain.UserSettings.NotificationLanguage.RU;
import static com.tecomgroup.qos.domain.UserSettings.NotificationLanguage.RU_TRANSLIT;
import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.MAIL_BODY;
import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.MAIL_SUBJECT;
import static com.tecomgroup.qos.pm.service.TemplateService.TemplateType.SMS_BODY;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

import com.tecomgroup.qos.domain.UserSettings.NotificationLanguage;
import com.tecomgroup.qos.util.Utils;

/**
 * @author smyshlyaev.s
 */
public class DefaultTemplateService
		implements
			InitializingBean,
			TemplateService {

	private final static Logger LOGGER = Logger
			.getLogger(DefaultTemplateService.class);

	@Value("#{systemProperties['pm.home']}")
	private final String pmHome = null;

	@Value("${pm.default.notification.language}")
	private NotificationLanguage defaultLang;

	@Value("${pm.templates.dir}")
	private String templatesDir;

	@Value("${pm.generate.templates}")
	private Boolean generateTemplates;

	@Value("${mail.template.subject.ru.file}")
	private String emailSubjectRuFile;
	@Value("${mail.template.subject.ru.default}")
	private String emailSubjectRuDefault;

	@Value("${mail.template.body.ru.file}")
	private String emailBodyRuFile;
	@Value("${mail.template.body.ru.default}")
	private String emailBodyRuDefault;

	@Value("${mail.template.subject.ru_translit.file}")
	private String emailSubjectRuTranslitFile;
	@Value("${mail.template.subject.ru_translit.default}")
	private String emailSubjectRuTranslitDefault;

	@Value("${mail.template.body.ru_translit.file}")
	private String emailBodyRuTranslitFile;
	@Value("${mail.template.body.ru_translit.default}")
	private String emailBodyRuTranslitDefault;

	@Value("${mail.template.subject.en.file}")
	private String emailSubjectEnFile;
	@Value("${mail.template.subject.en.default}")
	private String emailSubjectEnDefault;

	@Value("${mail.template.body.en.file}")
	private String emailBodyEnFile;
	@Value("${mail.template.body.en.default}")
	private String emailBodyEnDefault;

	@Value("${sms.template.body.ru.file}")
	private String smsBodyRuFile;
	@Value("${sms.template.body.ru.default}")
	private String smsBodyRuDefault;

	@Value("${sms.template.body.en.file}")
	private String smsBodyEnFile;
	@Value("${sms.template.body.en.default}")
	private String smsBodyEnDefault;

	@Value("${sms.template.body.ru_translit.file}")
	private String smsBodyRuTranslitFile;
	@Value("${sms.template.body.ru_translit.default}")
	private String smsBodyRuTranslitDefault;

	private final String[][] templates = new String[NotificationLanguage
			.values().length][TemplateType.values().length];

	@Override
	public void afterPropertiesSet() throws Exception {
		createDirIfNotExists(Utils.getAbsoluteFile(pmHome, new File(
				templatesDir)));
		templates[RU.ordinal()][MAIL_SUBJECT.ordinal()] = readFromFileIfExists(
				templatesDir, emailSubjectRuFile, emailSubjectRuDefault);
		templates[RU.ordinal()][MAIL_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, emailBodyRuFile, emailBodyRuDefault);
		templates[RU.ordinal()][SMS_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, smsBodyRuFile, smsBodyRuDefault);
		templates[EN.ordinal()][MAIL_SUBJECT.ordinal()] = readFromFileIfExists(
				templatesDir, emailSubjectEnFile, emailSubjectEnDefault);
		templates[EN.ordinal()][MAIL_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, emailBodyEnFile, emailBodyEnDefault);
		templates[EN.ordinal()][SMS_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, smsBodyEnFile, smsBodyEnDefault);
		templates[RU_TRANSLIT.ordinal()][MAIL_SUBJECT.ordinal()] = readFromFileIfExists(
				templatesDir, emailSubjectRuTranslitFile,
				emailSubjectRuTranslitDefault);
		templates[RU_TRANSLIT.ordinal()][MAIL_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, emailBodyRuTranslitFile,
				emailBodyRuTranslitDefault);
		templates[RU_TRANSLIT.ordinal()][SMS_BODY.ordinal()] = readFromFileIfExists(
				templatesDir, smsBodyRuTranslitFile, smsBodyRuTranslitDefault);
	}

	private void createDirIfNotExists(final File dir) {
		if (!dir.exists()) {
			try {
				FileUtils.forceMkdir(dir);
			} catch (final IOException e) {
				LOGGER.error("Failed to create directory: " + dir);
			}
		}
	}

	@Override
	public NotificationLanguage getDefaultNotificationLanguage() {
		return defaultLang;
	}

	@Override
	public String getTemplate(final NotificationLanguage language,
			final TemplateType type) {
		return templates[language.ordinal()][type.ordinal()];
	}

	private String readFromFileIfExists(final String dir, final String path,
			final String defaultValue) {
		String result = defaultValue;
		final File templateFile = new File(dir + "/" + path);
		final File absoluteFile = Utils.getAbsoluteFile(pmHome, templateFile);
		final boolean fileExists = absoluteFile.exists();
		if (generateTemplates || !fileExists) {
			if (!fileExists) {
				try {
					FileUtils.touch(absoluteFile);
					LOGGER.info("Created new file for message template: "
							+ absoluteFile.getAbsolutePath());
				} catch (final IOException e) {
					LOGGER.error("Failed to create file " + path, e);
				}
			}
			if (absoluteFile.exists()) {
				try {
					FileUtils.write(absoluteFile, defaultValue);
					LOGGER.info("Default message template saved to: "
							+ absoluteFile.getAbsolutePath());
				} catch (final IOException e) {
					LOGGER.error("Error writing to file " + path, e);
					absoluteFile.delete();
					LOGGER.info("Deleted corrupted file " + path);
				}
			}
		} else {
			try {
				result = FileUtils.readFileToString(absoluteFile);
				LOGGER.info("Reading message template from: "
						+ absoluteFile.getAbsolutePath());
			} catch (final IOException e) {
				LOGGER.error("Error reading from file " + path, e);
			}
		}
		return result;
	}
}
