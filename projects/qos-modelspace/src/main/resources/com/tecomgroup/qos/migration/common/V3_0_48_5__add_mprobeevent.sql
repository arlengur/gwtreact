 CREATE TABLE IF NOT EXISTS mprobe_event (
  id BIGINT NOT NULL,
  key varchar(255),
  event_type VARCHAR(255),
  status VARCHAR(50),
  agent_key VARCHAR(255),
  user_login VARCHAR (255),
  timestamp TIMESTAMP,
  created_timestamp TIMESTAMP,
  CONSTRAINT mprobe_event_pk PRIMARY KEY (id)
);

CREATE INDEX mprobe_event_user_type_idx ON mprobe_event (user_login, event_type);
