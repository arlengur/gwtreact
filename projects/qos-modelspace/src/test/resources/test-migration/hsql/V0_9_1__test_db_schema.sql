    CREATE TABLE IF NOT EXISTS MFakeTaskAttribute (
        id bigint not null,
        name varchar(255),
        value varchar(255),
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MFakeTask (
        id bigint not null,
        description varchar(1024),
        name varchar(255),
        source_key varchar(255) not null,
        source_type varchar(255) not null,
        superattribute_id bigint,
        primary key (id),
		foreign key (superattribute_id) references MFakeTaskAttribute
    );

    CREATE TABLE IF NOT EXISTS MFakeTask_MFakeTaskAttribute (
        MFakeTask_id bigint not null,
        attributes_id bigint not null,
        unique (attributes_id),
        foreign key (MFakeTask_id) references MFakeTask,
		foreign key (attributes_id) references MFakeTaskAttribute
    );
    
    CREATE TABLE IF NOT EXISTS MTestAgentModule (
        id bigint not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version bigint not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null,
        parent_id bigint,
        templateresultconfiguration_id bigint,
        primary key (id),
        unique (entity_key),
 		foreign key (templateresultconfiguration_id) references MResultConfigurationTemplate,
 	    foreign key (parent_id) references MAgent
    );
    