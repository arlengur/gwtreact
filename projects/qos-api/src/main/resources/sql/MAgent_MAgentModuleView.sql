create or replace view MAgent_MAgentModule
(
    MAGENT_ID,
    MODULES_ID
)
as
select
    AGENT_ID,
    ACTION_ID
from QOS_PRO.Task task
;