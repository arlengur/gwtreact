CREATE OR REPLACE FUNCTION addZoomInterval() RETURNS VOID AS
$BODY$
BEGIN

	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_clienttimezone') THEN
		alter table muserresulttemplate add zoominterval_clienttimezone varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_enddatetime') THEN
		alter table muserresulttemplate add zoominterval_enddatetime timestamp;
	END IF;

	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_startdatetime') THEN
		alter table muserresulttemplate add zoominterval_startdatetime timestamp;
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_timezone') THEN
		alter table muserresulttemplate add zoominterval_timezone varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_timezonetype') THEN
		alter table muserresulttemplate add zoominterval_timezonetype varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'muserresulttemplate' and column_name='zoominterval_type') THEN
		alter table muserresulttemplate add zoominterval_type varchar(255);
	END IF;

    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addZoomInterval();