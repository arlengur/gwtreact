DROP TABLE MAgentTask
;

create table MAgentTask 
(
    ID,
    AGENT_KEY,	
    CREATEDBY,
    CREATIONDATETIME,
    MODIFICATIONDATETIME,
    MODIFIEDBY,
    VERSION,
    MODULE_ID,
    RESULTCONFIGURATION_ID,
    MONITORINGINTERVAL,
    ISDONE
)
as
select 
    ID,
    TO_CHAR(ID),
    CREATION_USER,
    CREATION_DATE,
    MOD_TS,
    MOD_USER,
    VERSION,
    ACTION_ID,
    1,
    PERIOD,
    case when DELETED = '0' then 0 else 1 end
from QOS_PRO.Task task
;
UPDATE MAgentTask set RESULTCONFIGURATION_ID=NULL
;
ALTER TABLE MAgentTask ADD CONSTRAINT pk_AgentTask PRIMARY KEY ("ID")
;
ALTER TABLE MAgentTask ADD CONSTRAINT fk_AgentTaskResultConfig FOREIGN KEY ("RESULTCONFIGURATION_ID") REFERENCES "MRESULTCONFIGURATION" ("ID")
;