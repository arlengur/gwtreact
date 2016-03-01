DROP FUNCTION GET_RESULTS
;

CREATE OR REPLACE FUNCTION get_results( 
    agent_name in VARCHAR2,
    action_name in VARCHAR2,
    start_date_time in DATE,
    end_date_time in DATE, 
    start_index in NUMBER,
    result_size in NUMBER
) 
RETURN RESULT_TABLE AS
	results       RESULT_TABLE;
  
BEGIN
   
 SELECT res_record BULK COLLECT INTO results FROM 
( select result.res_record, ROWNUM rnum from 
  (SELECT result.*, RESULT_RECORD(result.ID, result.NICAM_LEVEL, result.PERIOD, result.RECORD_TIME, result.SIGNAL_LEVEL, result.SIG_NOISE, (select stat.id from MStatus stat where stat.value=status and rownum=1), result.VID_AU) AS res_record FROM 
  QOS_RES.tv_it09a_log result where task_id in 
  (select id from QOS_PRO.task where agent_id = (select id from QOS_PRO.agent where name=agent_name) 
  and action_id = (select id from QOS_PRO.action where name=action_name)) 
  and record_Time between start_date_time and end_date_time order by result.record_time) result 
  where ROWNUM <= (start_index + result_size) )
where rnum  >= start_index;

	RETURN results;
END get_results;

show errors
;