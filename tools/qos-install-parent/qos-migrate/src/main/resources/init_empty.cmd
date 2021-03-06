@ echo off
setlocal enabledelayedexpansion

rem
rem  Creates and initializes the metadata table in the schema
rem  Example: 
rem     init_empty
rem   Also you can override flyway properties
rem     init_empty -url=jdbc:hsqldb:hsql://localhost:9001/qosdb -user=qos_ipad -password=qos_ipad
rem

set _REALPATH=%~dp0
set FLYWAY_HOME=%_REALPATH%\flyway

%FLYWAY_HOME%\flyway.cmd %* -initVersion=0 init