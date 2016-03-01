CREATE OR REPLACE FUNCTION addSyncInterval() RETURNS VOID AS
$BODY$
BEGIN

	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinter_clienttimezone') THEN
		alter table MRecordedStreamTemplate add column synctimeinter_clienttimezone varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinterval_enddatetime') THEN
		alter table MRecordedStreamTemplate add column synctimeinterval_enddatetime timestamp;
	END IF;

	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinterval_startdatetime') THEN
		alter table MRecordedStreamTemplate add column synctimeinterval_startdatetime timestamp;
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinterval_timezone') THEN
		alter table MRecordedStreamTemplate add column synctimeinterval_timezone varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinterval_timezonetype') THEN
		alter table MRecordedStreamTemplate add column synctimeinterval_timezonetype varchar(255);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mrecordedstreamtemplate' and column_name='synctimeinterval_type') THEN
		alter table MRecordedStreamTemplate add column synctimeinterval_type varchar(255);
	END IF;

    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addSyncInterval();