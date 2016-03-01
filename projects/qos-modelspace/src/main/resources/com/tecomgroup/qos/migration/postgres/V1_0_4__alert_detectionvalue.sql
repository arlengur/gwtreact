CREATE OR REPLACE FUNCTION addAlertDetectionValue() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'malert' and column_name='detectionvalue') THEN
		alter table MAlert add detectionvalue double precision;
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addAlertDetectionValue();