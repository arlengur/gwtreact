CREATE OR REPLACE FUNCTION addSnmpId() RETURNS VOID AS
$BODY$
BEGIN
	IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'snmp_id_seq' ) THEN
		create sequence snmp_id_seq;
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'magent' and column_name='snmp_id') THEN
		alter table MAgent add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MAgent add constraint MAgent_snmp_id_unique unique (snmp_id);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'magentmodule' and column_name='snmp_id') THEN
		alter table MAgentModule add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MAgentModule add constraint MAgentModule_snmp_id_unique unique (snmp_id); 
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mmediaagentmodule' and column_name='snmp_id') THEN
		alter table MMediaAgentModule add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MMediaAgentModule add constraint MMediaAgentModule_snmp_id_unique unique (snmp_id); 
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'magenttask' and column_name='snmp_id') THEN
		alter table MAgentTask add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MAgentTask add constraint MAgentTask_snmp_id_unique unique (snmp_id);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicy' and column_name='snmp_id') THEN
		alter table MPolicy add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MPolicy add constraint MPolicy_snmp_id_unique unique (snmp_id);
	END IF;
	
	IF NOT EXISTS (select * from information_schema.columns where table_name = 'mpolicytemplate' and column_name='snmp_id') THEN
		alter table MPolicyTemplate add snmp_id integer not null default nextval('snmp_id_seq');
		alter table MPolicyTemplate add constraint MPolicyTemplate_snmp_id_unique unique (snmp_id); 
	END IF;
	
    RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addSnmpId();