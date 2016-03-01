
create or replace view MAgentProfile 
(
    ID,
    CREATEDBY,
    CREATIONDATETIME,
    MODIFICATIONDATETIME,
    MODIFIEDBY,
    VERSION,
    DESCRIPTION,
    NAME
) 
as
select 
    ID,
    'unknown',
    CREATION_DATE,
    MOD_TS,
    'unknown',
    MOD_VERS,
    DESCRIPTION,
    NAME
from QOS_PRO.Agent_Profile
;