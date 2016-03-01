@ echo off
setlocal enabledelayedexpansion

set TOOL_NAME=%1
set D_PARAMS =

if [%1] == [-help] (
	set TOOL_NAME=
)

:handle_params
if NOT [%2] == [] (
	set D_PARAMS=%D_PARAMS% -D%2=%3
	shift
	shift
	goto :handle_params
)

java %D_PARAMS% -classpath .;lib/* com.tecomgroup.qos.tools.Tools %TOOL_NAME%

if [%TOOL_NAME%] == [] (
	goto :help
) else (
	goto :end
)

:help
echo.
echo Usage:
echo tools ^<ToolName^> ^<parameters^>
echo Example:
echo tools SendAlert amqp.host=localhost
echo.

:end
