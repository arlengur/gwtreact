@ echo off
setlocal enabledelayedexpansion

set _REALPATH=%~dp0
set REQUESTER_HOME=%_REALPATH%

if [%1] == [-help] (
	echo.
	echo Usage:
	echo requester ^<parameters^>
	echo Example:
	echo requester opposite.host=qligent_server_host current.host=current_agent_host
	echo.
	goto :end
)

:handle_params
if NOT [%1] == [] (
	set D_PARAMS=%D_PARAMS% -D%1=%2
	shift
	shift
	goto :handle_params
)

set CLASSPATH=%REQUESTER_HOME%;%REQUESTER_HOME%/lib/*

java %D_PARAMS% -cp "%CLASSPATH%" com.tecomgroup.qos.broker.federation.Bootstrap

:end