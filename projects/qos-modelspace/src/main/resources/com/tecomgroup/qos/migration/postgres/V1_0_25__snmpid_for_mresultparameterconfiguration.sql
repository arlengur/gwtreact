CREATE OR REPLACE FUNCTION addSnmpIdForMResultParameterConfiguration() RETURNS VOID AS
$BODY$
BEGIN
  IF NOT EXISTS (SELECT 0 FROM pg_class where relname = 'snmp_id_seq' ) THEN
    create sequence snmp_id_seq;
  END IF;

  IF NOT EXISTS (select * from information_schema.columns where table_name = 'mresultparameterconfiguration' and column_name='snmp_id') THEN
    alter table MResultParameterConfiguration add snmp_id integer not null default nextval('snmp_id_seq');
    alter table MResultParameterConfiguration add constraint MResultParameterConfiguration_snmp_id_unique unique (snmp_id);
  END IF;

  RETURN;
END;
$BODY$
LANGUAGE plpgsql;

SELECT addSnmpIdForMresultParameterConfiguration();