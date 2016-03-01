CREATE OR REPLACE FUNCTION addNotificationLanguageUserSetting() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muser' and column_name='settings_notificationlanguage') THEN
		alter table MUser add settings_notificationlanguage varchar(255);
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addNotificationLanguageUserSetting();