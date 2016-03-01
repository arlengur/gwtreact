    --Common tables 
    
    CREATE TABLE IF NOT EXISTS MDivision (
        id int8 not null,
        name varchar(255) not null unique,
        ordernumber int4,
        parent_id int8,
        primary key (id),
        foreign key (parent_id) references MDivision
    );
    
    CREATE TABLE IF NOT EXISTS MProfile (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        description varchar(1024),
        displayname varchar(255),
        primary key (id)
    );
    
    --Property related tables
    
    CREATE TABLE IF NOT EXISTS MProperty (
        id int8 not null,
        displayname varchar(255),
        name varchar(255) not null,
        required boolean not null,
        PROPERTY_TYPE varchar(255) not null,
        value varchar(255),
        primary key (id)
    );
    
    
    --Agent related tables

    CREATE TABLE IF NOT EXISTS MAgent (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        agenttype varchar(255),
        description varchar(1024),
        disabled boolean not null,
        gisposition_latitude float8,
        gisposition_longitude float8,
        gisposition_title varchar(255),
        netaddress varchar(255),
        platform varchar(255),
        timezone varchar(255),
        division_id int8,
        selectedprofile_id int8,
        primary key (id),
        foreign key (division_id) references MDivision,
        foreign key (selectedprofile_id) references MProfile
    );
    
    CREATE TABLE IF NOT EXISTS MAgent_MProfile (
        MAgent_id int8 not null,
        profiles_id int8 not null,
        primary key (MAgent_id, profiles_id),
        unique (profiles_id),
        foreign key (MAgent_id) references MAgent,
        foreign key (profiles_id) references MProfile
    );
    
    CREATE TABLE IF NOT EXISTS MAgent_MProperty (
        MAgent_id int8 not null,
        properties_id int8 not null,
        primary key (MAgent_id, properties_id),
        unique (properties_id),
        foreign key (MAgent_id) references MAgent,
		foreign key (properties_id) references MProperty
    );
    
    --Result configuration related tables
    
    CREATE TABLE IF NOT EXISTS MResultConfigurationSharedData (
        id int8 not null,
        samplingrate int8,
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MResultConfigurationTemplate (
        id int8 not null,
        samplingrate int8,
        parameterdisplaynameformat varchar(255),
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MResultConfiguration (
        id int8 not null,
        samplingrate int8,
        templateresultconfiguration_id int8,
        primary key (id),
		foreign key (templateresultconfiguration_id) references MResultConfigurationTemplate
    );
    
    CREATE TABLE IF NOT EXISTS MResultParameterLocation (
        id int8 not null,
        filelocation varchar(255),
        filename varchar(255),
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MParameterThreshold (
        id int8 not null,
        criticallevel float8,
        type varchar(255) not null,
        warninglevel float8,
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MResultParameterConfiguration (
        id int8 not null,
        aggregationtype varchar(255) not null,
        description varchar(1024),
        disabled boolean not null,
        displayformat varchar(255),
        displayname varchar(255),
        name varchar(255) not null,
        PARAMETER_TYPE varchar(255) not null,
        units varchar(255),
        location_id int8,
        threshold_id int8,
        primary key (id),
		foreign key (location_id) references MResultParameterLocation,
		foreign key (threshold_id) references MParameterThreshold
    );
    
    CREATE TABLE IF NOT EXISTS MResultConfigur_MResultParamet (
        MResultConfigurationSharedD_id int8 not null,
        parameterconfigurations_id int8 not null,
        unique (parameterconfigurations_id),
		foreign key (parameterconfigurations_id) references MResultParameterConfiguration
    );

    CREATE TABLE IF NOT EXISTS MResultConfiguratio_MProperty (
        MResultConfigurationTempl_id int8 not null,
        propertyconfigurations_id int8 not null,
        unique (propertyconfigurations_id),
		foreign key (MResultConfigurationTempl_id) references MResultConfigurationTemplate,
		foreign key (propertyconfigurations_id) references MProperty
    );

    CREATE TABLE IF NOT EXISTS MResultParameterConf_MProperty (
        MResultParameterConfigurat_id int8 not null,
        properties_id int8 not null,
		foreign key (MResultParameterConfigurat_id) references MResultParameterConfiguration,
		foreign key (properties_id) references MProperty
    );
    
    --Agent module related tables

    CREATE TABLE IF NOT EXISTS MAgentModule (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        templateresultconfiguration_id int8,
        primary key (id),
		foreign key (parent_id) references MAgent,
		foreign key (templateresultconfiguration_id) references MResultConfigurationTemplate
    );
   
    --Agent task related tables

    CREATE TABLE IF NOT EXISTS MAgentTask (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        disabled boolean not null,
        done boolean not null,
        resultconfiguration_id int8,
        primary key (id),
		foreign key (resultconfiguration_id) references MResultConfiguration
    );

    CREATE TABLE IF NOT EXISTS MAgentTask_MProperty (
        MAgentTask_id int8 not null,
        properties_id int8 not null,
        unique (properties_id),
        foreign key (MAgentTask_id) references MAgentTask,
		foreign key (properties_id) references MProperty
    );
    
     CREATE TABLE IF NOT EXISTS MChartSeries (
        id int8 not null,
        chartname varchar(255),
        parameter_id int8,
        task_id int8,
        primary key (id),
		foreign key (parameter_id) references MResultParameterConfiguration,
		foreign key (task_id) references MAgentTask
    );
    
    --Alert related tables
  
    CREATE TABLE IF NOT EXISTS MAlertType (
        id int8 not null,
        description varchar(1024),
        displayname varchar(255) not null,
        displaytemplate varchar(255),
        name varchar(255) not null unique,
        probablecause varchar(255) not null,
        primary key (id)
    );

    CREATE TABLE IF NOT EXISTS MAlert (
        id int8 not null,
        context varchar(255),
        extradata varchar(255),
        perceivedseverity varchar(255) not null,
        settings varchar(255),
        specificreason varchar(255) not null,
        alerttype_id int8,
        acknowledged boolean not null,
        acknowledgmentdatetime timestamp,
        ALERT_COUNT int8 not null,
        cleareddatetime timestamp,
        countsincelastack int8 not null,
        creationdatetime timestamp not null,
        disabled boolean not null,
        lastupdatedatetime timestamp,
        lastupdatetype varchar(255) not null,
        status varchar(255) not null,
        originator_id int8,
        source_id int8,
        primary key (id),
        unique (alerttype_id, source_id, originator_id, settings),
		foreign key (alerttype_id) references MAlertType
    );

    CREATE TABLE IF NOT EXISTS MAlertReport (
        id int8 not null,
        enddatetime timestamp,
        perceivedseverity varchar(255) not null,
        startdatetime timestamp not null,
        alert_id int8,
        primary key (id),
		foreign key (alert_id) references MAlert
    );

    CREATE TABLE IF NOT EXISTS MAlertUpdate (
        id int8 not null,
        comment varchar(1024),
        datetime timestamp not null,
        field varchar(255),
        newvalue varchar(255),
        oldvalue varchar(255),
        updatetype varchar(255) not null,
        USER_NAME varchar(255) not null,
        alert_id int8,
        primary key (id),
        foreign key (alert_id) references MAlert
    );
   
   --User related tables
    
   CREATE TABLE IF NOT EXISTS MUser (
        id int8 not null,
        email varchar(255),
        firstname varchar(255),
        lastname varchar(255),
        login varchar(255) unique,
        password varchar(255),
        phone varchar(255),
        position varchar(255),
        secondname varchar(255),
        division_id int8,
        primary key (id),
		foreign key (division_id) references MDivision
    );
    
    CREATE TABLE IF NOT EXISTS MUser_roles (
        MUser_id int8 not null,
        roles varchar(255),
        foreign key (MUser_id) references MUser 
    );
    
    CREATE TABLE IF NOT EXISTS MDashboard (
        id int8 not null,
        columnnumber int4 not null,
        rownumber int4 not null,
        serializedwidgets varchar(10240),
        username varchar(255) unique,
        primary key (id)
    );
    
 	--Schedule related tables
    
     CREATE TABLE IF NOT EXISTS MSchedule (
        id int8 not null,
        duration int8 not null,
        enddatetime timestamp,
        interval int8 not null,
        startdatetime timestamp not null,
        primary key (id)
    );
    
    --Policy related tables
    
    CREATE TABLE IF NOT EXISTS MContinuousFallCondition (
        id int8 not null,
        criticallevel_ceaseduration int8,
        criticallevel_ceaselevel varchar(255),
        criticallevel_raiseduration int8,
        criticallevel_raiselevel varchar(255),
        parameteridentifier_name varchar(255) not null,
        thresholdtype varchar(255) not null,
        warninglevel_ceaseduration int8,
        warninglevel_ceaselevel varchar(255),
        warninglevel_raiseduration int8,
        warninglevel_raiselevel varchar(255),
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MAccumulatedFallCondition (
        id int8 not null,
        criticallevel_ceaseduration int8,
        criticallevel_ceaselevel varchar(255),
        criticallevel_raiseduration int8,
        criticallevel_raiselevel varchar(255),
        parameteridentifier_name varchar(255) not null,
        thresholdtype varchar(255) not null,
        warninglevel_ceaseduration int8,
        warninglevel_ceaselevel varchar(255),
        warninglevel_raiseduration int8,
        warninglevel_raiselevel varchar(255),
        criticallevel_analysisinterval int8,
        primary key (id)
    );

    CREATE TABLE IF NOT EXISTS MContinuousFallCon_MProperty (
        MContinuousFallCondition_id int8 not null,
        parameteridentif_properties_id int8 not null,
		foreign key (parameteridentif_properties_id) references MProperty
    );

    CREATE TABLE IF NOT EXISTS MPolicy (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        disabled boolean not null,
        source_key varchar(255) not null,
        source_type varchar(255) not null,
        condition_id int8 not null,
        primary key (id),
        unique (condition_id)
    );
    
    CREATE TABLE IF NOT EXISTS MPolicyTemplate (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        disabled boolean not null,
        source_key varchar(255) not null,
        source_type varchar(255) not null,
        condition_id int8 not null,
        agentname varchar(255),
        primary key (id),
        unique (condition_id)
    );

    CREATE TABLE IF NOT EXISTS MPolicySendAlertAction (
        id int8 not null,
        name varchar(255),
        alerttype varchar(255) not null,
        primary key (id)
    );

    CREATE TABLE IF NOT EXISTS MPolicySharedD_MPolicySendAl (
        MPolicySharedData_id int8 not null,
        actions_id int8 not null,
		foreign key (actions_id) references MPolicySendAlertAction
    );
    
    --Media related tables
    
     CREATE TABLE IF NOT EXISTS MStream_MProperty (
        MStream_id int8 not null,
        properties_id int8 not null,
        unique (properties_id),
		foreign key (properties_id) references MProperty
    );
    
    --Live video related tables

    CREATE TABLE IF NOT EXISTS MLiveStream (
        id int8 not null,
        STREAM_KEY varchar(255) not null,
        templateurl varchar(255) not null,
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MLiveStreamTemplate (
        id int8 not null,
        name varchar(255),
        user_id int8,
        primary key (id),
		foreign key (user_id) references MUser
    );

    CREATE TABLE IF NOT EXISTS MLiveStreamWrapper (
        id int8 not null,
        streamkey varchar(255) not null,
        taskkey varchar(255) not null,
        primary key (id)
    );

    CREATE TABLE IF NOT EXISTS MLiveStreamTe_MLiveStreamWrap (
        MLiveStreamTemplate_id int8 not null,
        wrappers_id int8 not null,
        unique (wrappers_id),
        foreign key (MLiveStreamTemplate_id) references MLiveStreamTemplate,
		foreign key (wrappers_id) references MLiveStreamWrapper
    );
    
    --Recorded video related tables

    CREATE TABLE IF NOT EXISTS MRecordedStream (
        id int8 not null,
        STREAM_KEY varchar(255) not null,
        templatedownloadurl varchar(255) not null,
        templatestreamurl varchar(255) not null,
        primary key (id)
    );

    CREATE TABLE IF NOT EXISTS MRecordedStreamTemplate (
        id int8 not null,
        name varchar(255),
        user_id int8,
        primary key (id),
		foreign key (user_id) references MUser
    );

    CREATE TABLE IF NOT EXISTS MRecordedStreamWrapper (
        id int8 not null,
        streamkey varchar(255) not null,
        taskkey varchar(255) not null,
        enddatetime timestamp,
        intervaltype varchar(255) not null,
        startdatetime timestamp,
        videotimezone varchar(255),
        primary key (id)
    );
    
    CREATE TABLE IF NOT EXISTS MRecordedStrea_MRecordedStre (
        MRecordedStreamTemplate_id int8 not null,
        wrappers_id int8 not null,
        unique (wrappers_id),
		foreign key (MRecordedStreamTemplate_id) references MRecordedStreamTemplate,
		foreign key (wrappers_id) references MRecordedStreamWrapper
    );
    
    CREATE TABLE IF NOT EXISTS MVideoResult (
        id int8 not null,
        enddatetime timestamp not null,
        filename varchar(255) not null,
        source_key varchar(255) not null,
        source_type varchar(255) not null,
        startdatetime timestamp not null,
        primary key (id)
    );
    
    --Media agent module related tables

    CREATE TABLE IF NOT EXISTS MMediaAgentModule (
        id int8 not null,
        createdby varchar(255),
        creationdatetime timestamp,
        modificationdatetime timestamp,
        modifiedby varchar(255),
        version int8 not null,
        displayname varchar(255) not null,
        entity_key varchar(255) not null unique,
        parent_id int8,
        templateresultconfiguration_id int8,
        primary key (id),
        foreign key (parent_id) references MAgent,
		foreign key (templateresultconfiguration_id) references MResultConfigurationTemplate
    );

    CREATE TABLE IF NOT EXISTS MMediaAgentModule_MStream (
        MMediaAgentModule_id int8 not null,
        templatestreams_id int8 not null,
        unique (templatestreams_id),
		foreign key (MMediaAgentModule_id) references MMediaAgentModule
    );
    
    --User template related tables

    CREATE TABLE IF NOT EXISTS MUserAlertsTemplate (
        id int8 not null,
        name varchar(255),
        serializedcriterion varchar(10240),
        hiddencolumnsstring varchar(1024),
        order_propertyname varchar(255),
        order_type varchar(255),
        user_id int8,
        primary key (id),
		foreign key (user_id) references MUser
    );

    CREATE TABLE IF NOT EXISTS MUserReportsTemplate (
        id int8 not null,
        name varchar(255),
        serializedcriterion varchar(10240),
        hiddencolumnsstring varchar(1024),
        order_propertyname varchar(255),
        order_type varchar(255),
        timeinterval_clienttimezone varchar(255),
        timeinterval_enddatetime timestamp,
        timeinterval_startdatetime timestamp,
        timeinterval_timezone varchar(255),
        timeinterval_timezonetype varchar(255),
        timeinterval_type varchar(255),
        user_id int8,
        primary key (id),
		foreign key (user_id) references MUser
    );
   
    CREATE TABLE IF NOT EXISTS MUserReportsTempl_sourcekeys (
        MUserReportsTemplate_id int8 not null,
        sourcekeys varchar(255),
		foreign key (MUserReportsTemplate_id) references MUserReportsTemplate
    );

    CREATE TABLE IF NOT EXISTS MUserResultTemplate (
        id int8 not null,
        name varchar(255),
        timeinterval_clienttimezone varchar(255),
        timeinterval_enddatetime timestamp,
        timeinterval_startdatetime timestamp,
        timeinterval_timezone varchar(255),
        timeinterval_timezonetype varchar(255),
        timeinterval_type varchar(255),
        user_id int8,
        primary key (id),
		foreign key (user_id) references MUser
    );
    
    CREATE TABLE IF NOT EXISTS MUserResultTempl_MChartSeries (
        MUserResultTemplate_id int8 not null,
        series_id int8 not null,
        primary key (MUserResultTemplate_id, series_id),
		foreign key (MUserResultTemplate_id) references MUserResultTemplate,
		foreign key (series_id) references MChartSeries
    );
    
    --Service related tables 

    CREATE TABLE IF NOT EXISTS hibernate_sequences (
         sequence_name varchar(255),
         sequence_next_hi_value int4 
    );