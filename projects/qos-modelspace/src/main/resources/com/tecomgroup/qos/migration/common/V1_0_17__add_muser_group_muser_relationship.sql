CREATE TABLE IF NOT EXISTS MUserGroup_MUser (
	musergroup_id bigint NOT NULL,
	users_id bigint NOT NULL,
	FOREIGN KEY (musergroup_id)
    	REFERENCES musergroup (id) MATCH SIMPLE
    	ON UPDATE NO ACTION ON DELETE NO ACTION,
	FOREIGN KEY (users_id)
    	REFERENCES muser (id) MATCH SIMPLE
    	ON UPDATE NO ACTION ON DELETE NO ACTION
)