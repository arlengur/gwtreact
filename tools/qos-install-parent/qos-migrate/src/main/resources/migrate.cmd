@ echo off
setlocal enabledelayedexpansion

rem  Do a migration

set _REALPATH=%~dp0
set FLYWAY_HOME=%_REALPATH%\flyway

%FLYWAY_HOME%\flyway.cmd %* migrate