@ echo off
setlocal enabledelayedexpansion

rem  Repair the database


set _REALPATH=%~dp0
set FLYWAY_HOME=%_REALPATH%\flyway

%FLYWAY_HOME%\flyway.cmd %* repair