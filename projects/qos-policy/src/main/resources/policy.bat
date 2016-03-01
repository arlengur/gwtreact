@ echo off
setlocal enabledelayedexpansion

if [%1] == [-help] (
	echo.
	echo Usage:
	echo ^<parameters^>		runs policy manager with provided params
	echo Example:
	echo policy amqp.host=localhost
	echo.
	echo -version		prints policy manager version
	echo -help			prints this help
	echo.
	goto :end
)

if [%1] == [-version] (
	set RUN_PARAMS=%1
	shift
)

:handle_params
if NOT [%1] == [] (
	set D_PARAMS=%D_PARAMS% -D%1=%2
	shift
	shift
	goto :handle_params
)

set _REALPATH=%~dp0
set PM_HOME=%_REALPATH%
set CLASSPATH=%PM_HOME%;%PM_HOME%/lib/*

java -Dpm.home=%PM_HOME% %D_PARAMS% -cp "%CLASSPATH%" com.tecomgroup.qos.pm.PolicyManager %RUN_PARAMS%

:end
