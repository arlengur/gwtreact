
create or replace view MAgentTask 
(
    ID,
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
