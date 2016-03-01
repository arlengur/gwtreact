CREATE OR REPLACE FUNCTION updateAlertPerceivedSeverity() RETURNS VOID AS
$BODY$
BEGIN
	IF ((select data_type from information_schema.columns where table_name = 'malert' and column_name='perceivedseverity') <> 'integer') THEN
		UPDATE malert SET perceivedseverity = '0' WHERE perceivedseverity = 'CRITICAL';
		UPDATE malert SET perceivedseverity = '1' WHERE perceivedseverity = 'MAJOR';
		UPDATE malert SET perceivedseverity = '2' WHERE perceivedseverity = 'WARNING';
		UPDATE malert SET perceivedseverity = '3' WHERE perceivedseverity = 'MINOR';
		UPDATE malert SET perceivedseverity = '4' WHERE perceivedseverity = 'NOTICE';
		UPDATE malert SET perceivedseverity = '5' WHERE perceivedseverity = 'INDETERMINATE';

		ALTER TABLE malert ALTER COLUMN perceivedseverity TYPE integer USING (perceivedseverity::integer);
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT updateAlertPerceivedSeverity();