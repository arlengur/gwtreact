
--Cutstom MGisPosition selection
create or replace view MMediaAgent 
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
    LIVEBROADCAST_ID,
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
    NULL,
    SELECTED_PROFILE_ID
from QOS_PRO.AGENT
;