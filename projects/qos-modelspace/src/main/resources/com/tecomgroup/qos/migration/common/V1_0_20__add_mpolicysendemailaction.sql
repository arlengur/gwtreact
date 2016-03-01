CREATE TABLE IF NOT EXISTS MPolicySendEmailAction (
	id bigint not null,
	name varchar(255),
	subject varchar(255) not null,
	body varchar(10240) not null,
	PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS MPolicySendEm_MContactInformat (
 	mpolicysendemailaction_id bigint NOT NULL,
 	contacts_id bigint NOT NULL,
 	FOREIGN KEY (mpolicysendemailaction_id)
    	REFERENCES MPolicySendEmailAction (id) MATCH SIMPLE
     	ON UPDATE NO ACTION ON DELETE NO ACTION,
    	UNIQUE (mpolicysendemailaction_id, contacts_id)
);