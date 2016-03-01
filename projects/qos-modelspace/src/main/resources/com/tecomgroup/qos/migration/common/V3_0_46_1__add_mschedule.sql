 CREATE TABLE IF NOT EXISTS mrecording_schedule (
  id BIGINT NOT NULL,
  name varchar(255),
  task_key VARCHAR(255),
  agent_key VARCHAR(255),
  time_zone VARCHAR(50),
  CONSTRAINT mschedule_pk PRIMARY KEY (id),
  CONSTRAINT mschedule_agent_task_key UNIQUE (agent_key, task_key)
);