CREATE TABLE IF NOT EXISTS mprobe_event_properties (
  id BIGINT NOT NULL,
  propertylist_id BIGINT NOT NULL,
  key VARCHAR(255) NOT NULL,
  value VARCHAR(255) NOT NULL,
  CONSTRAINT mprobe_event_properties_pk PRIMARY KEY (id)
);

CREATE INDEX mprobe_event_properties_parent_id ON mprobe_event_properties (propertylist_id);