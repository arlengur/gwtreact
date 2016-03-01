DROP TYPE RESULT_TABLE
;

DROP TYPE RESULT_RECORD
;

CREATE OR REPLACE TYPE RESULT_RECORD AS OBJECT   (
    ID          NUMBER(10,0),
    NICAMLEVEL  NUMBER(5,2),
    PERIOD      NUMBER(10,0),
    RECORDTIME  DATE,
    SIGNALLEVEL NUMBER(15,5),
    SIGNALNOISE NUMBER(15,5),
    STATUS_ID   NUMBER(10,0),
    VIDEOAUDIO  NUMBER(15,5)
  )
  ;

CREATE OR REPLACE TYPE RESULT_TABLE AS TABLE OF RESULT_RECORD
;

DROP FUNCTION GET_AGGREGATED_RESULTS
;

DROP FUNCTION GET_MAX_SIGNALLEVEL_RESULTS
;

DROP FUNCTION GET_MIN_SIGNALLEVEL_RESULTS
;

DROP FUNCTION GET_MAX_SIGNALNOISE_RESULTS
;

DROP FUNCTION GET_MIN_SIGNALNOISE_RESULTS
;

DROP FUNCTION GET_MAX_VIDEOAUDIO_RESULTS
;

DROP FUNCTION GET_MIN_VIDEOAUDIO_RESULTS
;

CREATE OR REPLACE FUNCTION get_max_signallevel_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE SIGNALLEVEL=(SELECT MAX(SIGNALLEVEL) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_max_signallevel_results;

show errors
;

CREATE OR REPLACE FUNCTION get_min_signallevel_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE SIGNALLEVEL=(SELECT MIN(SIGNALLEVEL) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_min_signallevel_results;

show errors
;

CREATE OR REPLACE FUNCTION get_max_signalnoise_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE SIGNALNOISE=(SELECT MAX(SIGNALNOISE) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_max_signalnoise_results;

show errors
;

CREATE OR REPLACE FUNCTION get_min_signalnoise_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE SIGNALNOISE=(SELECT MIN(SIGNALNOISE) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_min_signalnoise_results;

show errors
;

CREATE OR REPLACE FUNCTION get_max_videoaudio_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE VIDEOAUDIO=(SELECT MAX(VIDEOAUDIO) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_max_videoaudio_results;

show errors
;

CREATE OR REPLACE FUNCTION get_min_videoaudio_results( 
    all_results in RESULT_TABLE,
    portion in NUMBER,
    portion_index in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results   RESULT_TABLE := RESULT_TABLE();
  
BEGIN
  
  SELECT res_record BULK COLLECT INTO aggregated_results FROM 
            (SELECT RESULT_RECORD(ID, NICAMLEVEL, PERIOD, RECORDTIME, SIGNALLEVEL, SIGNALNOISE, STATUS_ID, videoaudio) AS res_record FROM 
            (SELECT result.*, rownum as rn FROM TABLE(all_results) result ORDER BY recordTime) 
            WHERE VIDEOAUDIO=(SELECT MIN(VIDEOAUDIO) FROM (SELECT result.*, rownum AS rn FROM TABLE(all_results) result ORDER BY recordTime) WHERE rn BETWEEN (portion_index-1)*portion AND portion_index*portion) 
            AND rn BETWEEN (portion_index-1)*portion AND portion_index*portion AND rownum < 2);
  
  RETURN aggregated_results;
END get_min_videoaudio_results;

show errors
;

CREATE OR REPLACE FUNCTION get_aggregated_results( 
    agent_name in VARCHAR2,
    action_name in VARCHAR2,
    start_date_time in DATE,
    end_date_time in DATE,
    aggregated_function in VARCHAR2, 
    aggregated_field in VARCHAR2,
    point_count in NUMBER
) 
RETURN RESULT_TABLE AS
  aggregated_results       RESULT_TABLE := RESULT_TABLE();
  all_results              RESULT_TABLE;
  cur_aggregated_results   RESULT_TABLE;
  portion                  NUMBER;
  portion_index            NUMBER;
  
BEGIN

  SELECT res_record BULK COLLECT INTO all_results FROM 
  (SELECT result.*, RESULT_RECORD(result.ID, result.NICAM_LEVEL, result.PERIOD, result.RECORD_TIME, result.SIGNAL_LEVEL, result.SIG_NOISE, (select stat.id from MStatus stat where stat.value=status and rownum=1), result.VID_AU) AS res_record FROM 
  QOS_RES.tv_it09a_log result where task_id in 
  (select id from QOS_PRO.task where agent_id = (select id from QOS_PRO.agent where name=agent_name) 
  and action_id = (select id from QOS_PRO.action where name=action_name)) 
  and record_Time between start_date_time and end_date_time order by result.record_time);

  SELECT ROUND((COUNT(*)/point_count)) INTO portion FROM TABLE(all_results);

  FOR ind in (select n from (select rownum n from dual connect by level <= point_count) where n >= 1)
  LOOP
  portion_index := ind.n;

  CASE
    WHEN lower(aggregated_function) = 'max' and lower(aggregated_field)='signallevel' 
         THEN cur_aggregated_results := get_max_signallevel_results(all_results, portion, portion_index);
    WHEN lower(aggregated_function) = 'min' and lower(aggregated_field)='signallevel' 
         THEN cur_aggregated_results := get_min_signallevel_results(all_results, portion, portion_index);
    WHEN lower(aggregated_function) = 'max' and lower(aggregated_field)='signalnoise' 
         THEN cur_aggregated_results := get_max_signalnoise_results(all_results, portion, portion_index);
    WHEN lower(aggregated_function) = 'min' and lower(aggregated_field)='signalnoise' 
         THEN cur_aggregated_results := get_min_signalnoise_results(all_results, portion, portion_index);
    WHEN lower(aggregated_function) = 'max' and lower(aggregated_field)='videoaudio' 
         THEN cur_aggregated_results := get_max_videoaudio_results(all_results, portion, portion_index);
    WHEN lower(aggregated_function) = 'min' and lower(aggregated_field)='videoaudio' 
         THEN cur_aggregated_results := get_min_videoaudio_results(all_results, portion, portion_index);
    ELSE cur_aggregated_results := RESULT_TABLE();
  END CASE;

  aggregated_results := aggregated_results MULTISET UNION cur_aggregated_results;
  END LOOP;

  RETURN aggregated_results;
END get_aggregated_results;

show errors
;