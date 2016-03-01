DROP TYPE MONITORING_TABLE
;

DROP TYPE MONITORING_RECORD
;

CREATE OR REPLACE TYPE MONITORING_RECORD AS OBJECT   (
    ID          	NUMBER(10,0),
	AGENTNAME		VARCHAR2(255),
	DEVICENAME		VARCHAR2(255),
	MODULENAME		VARCHAR2(255),
	STARTDATETIME	DATE,
	ENDDATETIME		DATE,
	STATUS_ID		NUMBER(10,0)
  )
  ;

CREATE OR REPLACE TYPE MONITORING_TABLE AS TABLE OF MONITORING_RECORD
;

create or replace
FUNCTION get_monitorings( 
    agent_name in VARCHAR2,
    action_name in VARCHAR2,
    status_name in VARCHAR2,
    start_date_time in DATE,
    end_date_time in DATE,
    start_index in NUMBER,
    result_size in NUMBER
) 
RETURN MONITORING_TABLE AS
	monitorings       MONITORING_TABLE;
  
BEGIN
	
SELECT monitoring_rec BULK COLLECT INTO monitorings FROM
(SELECT monitoring.monitoring_rec, rownum rnum FROM
(SELECT monitoring.*, MONITORING_RECORD(monitoring.ID, monitoring.AGENTNAME, monitoring.DEVICENAME, monitoring.MODULENAME, monitoring.STARTDATETIME, monitoring.ENDDATETIME, monitoring.STATUS_ID) AS monitoring_rec FROM
(select get_id as ID, agent_name as AGENTNAME, device_instance_name as DEVICENAME, action_name as MODULENAME, start_time as STARTDATETIME, end_time as ENDDATETIME, (select stat.id from MStatus stat where stat.value=status and rownum=1) as STATUS_ID 
from (
select a.*, s.name as status_name from  (  select bk.name as 
agent_name, t.agent_id, mt2.task_id as task_id, mt2.start_time, mt2.end_time,      
(mt2.end_time - mt2.start_time)*24*60*60 as duration, t.action_id, 
a.name as action_name, mt2.prev_status, mt2.status, mt2.next_status,   
      case when nvl(mt2.status, '000') = '000' then 0 else 2 end as 
status_id,         t.device_instance_id, d.name as 
device_instance_name    from    (        select task_id, record_time as 
start_time,          prev_status, status, next_status,          
lead(record_time) over (partition by task_id order by record_time) as 
end_time       from       ( select record_time, task_id,               
lag(status,1,-200) over (partition by task_id order by record_time) as 
prev_status,             lead(status,1, 200) over (partition by task_id 
order by record_time) as next_status,               status           
from         (               select record_time,task_id,status_id, 
status from QOS_RES.TV_IT09A_LOG)           where status is not null  and            
  record_time between start_date_time and end_date_time )       where 
(status<>prev_status or next_status = 200 or prev_status = -200)    ) 
mt2,        QOS_PRO.task t,  QOS_PRO.agent bk, QOS_PRO.action a,   
QOS_PRO.device_instance d    where         bk.id = t.agent_id and bk.name=agent_name and        
a.id = t.action_id and a.name=action_name and        t.id = mt2.task_id and        d.id = 
t.device_instance_id and  next_status <> 200   order by t.id, 
mt2.start_time ) a, QOS_RES.log_status s WHERE a.status_id = s.id and s.name=status_name ORDER BY a.device_instance_id, a.action_id, a.start_time desc
) ORDER BY start_time) monitoring) monitoring where rownum <= (start_index+result_size) ) monitoring where rnum  >= start_index;

	RETURN monitorings;
END get_monitorings;

show errors
;