
create or replace view MAgent 
(
    ID, 
    CREATEDBY, 
    CREATIONDATETIME, 
    MODIFICATIONDATETIME, 
    MODIFIEDBY, 
    VERSION, 
    AGENTTYPE, 
    DESCRIPTION,
    NAME,
    DISPLAYNAME,
    TIMEZONE,
    DIVISION_ID,
    GISPOSITION_ID,
    SELECTEDPROFILE_ID
) 
as 
select 
    ID, 
    CREATION_USER, 
    CREATION_DATE,
    MOD_TS, 
    MOD_USER, 
    MOD_VERS, 
   'INTERNAL' AS AGENT_TYPE, 
    DESCRIPTION, 
    NAME, 
    NAME,
    'Europe/Moscow',
    DIVISION_ID, 
    NULL,
    SELECTED_PROFILE_ID
from QOS_PRO.AGENT
;
    