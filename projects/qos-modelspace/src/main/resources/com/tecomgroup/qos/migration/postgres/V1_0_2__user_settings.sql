CREATE OR REPLACE FUNCTION addAudibleAlertModeUserSetting() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muser' and column_name='settings_audiblealertmode') THEN
		alter table MUser add settings_audiblealertmode varchar(255); 
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addAudibleAlertModeUserSetting();