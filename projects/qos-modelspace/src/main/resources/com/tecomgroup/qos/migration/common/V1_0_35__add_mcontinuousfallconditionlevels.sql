    CREATE TABLE IF NOT EXISTS MPolicyConditionLevels (
        id bigint not null,
        criticallevel_ceaseduration bigint,
        criticallevel_ceaselevel varchar(255),
        criticallevel_raiseduration bigint,
        criticallevel_raiselevel varchar(255),
        thresholdtype varchar(255) not null,
        warninglevel_ceaseduration bigint,
        warninglevel_ceaselevel varchar(255),
        warninglevel_raiseduration bigint,
        warninglevel_raiselevel varchar(255),
        CONSTRAINT mpolicyconditionlevels_pkey PRIMARY KEY (id)
    );