CREATE OR REPLACE FUNCTION addAgentTaskPolicyDisableColumns() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'magent' and column_name='deleted') THEN
		alter table MAgent add deleted boolean; 
		update MAgent set deleted=false;
		alter table MAgent alter column deleted set not null; 
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'magenttask' and column_name='deleted') THEN
		alter table MAgentTask add deleted boolean; 
		update MAgentTask set deleted=false;
		alter table MAgentTask alter column deleted set not null; 
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicy' and column_name='deleted') THEN
		alter table MPolicy add deleted boolean; 
		update MPolicy set deleted=false;
		alter table MPolicy alter column deleted set not null; 
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicytemplate' and column_name='deleted') THEN
		alter table MPolicyTemplate add deleted boolean; 
		update MPolicyTemplate set deleted=false;
		alter table MPolicyTemplate alter column deleted set not null; 
	END IF;
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addAgentTaskPolicyDisableColumns();