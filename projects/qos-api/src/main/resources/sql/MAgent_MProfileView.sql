

create or replace view MAgent_MProfile 
(
    MAGENT_ID,
    PROFILES_ID
) 
as
select 
    AGENT_ID,
    ID
from QOS_PRO.Agent_Profile
;