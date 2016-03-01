CREATE TABLE IF NOT EXISTS mrecording_schedule_event_list (
  id BIGINT NOT NULL,
  eventlist_id BIGINT NOT NULL,
  start_date VARCHAR(30) NOT NULL,
  end_date VARCHAR(30) NOT NULL,
  comment VARCHAR (255),
  CONSTRAINT mschedule_event_list_pk PRIMARY KEY (id)
);

CREATE INDEX mschedule_event_list_parent_id ON mrecording_schedule_event_list (eventlist_id);