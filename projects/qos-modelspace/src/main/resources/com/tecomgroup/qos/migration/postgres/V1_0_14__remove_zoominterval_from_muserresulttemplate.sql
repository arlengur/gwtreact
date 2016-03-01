CREATE OR REPLACE FUNCTION removeUserResultTemplateZoomInterval() RETURNS VOID AS
$BODY$
BEGIN
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_clienttimezone') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_clienttimezone;
	END IF;
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_enddatetime') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_enddatetime;
	END IF;
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_startdatetime') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_startdatetime;
	END IF;
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_timezone') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_timezone;
	END IF;
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_timezonetype') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_timezonetype;
	END IF;
	
	IF EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_type') THEN
		ALTER TABLE muserresulttemplate DROP COLUMN zoominterval_type;
	END IF;
	
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT removeUserResultTemplateZoomInterval();