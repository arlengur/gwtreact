@echo off
net session > nul 2>&1 || ^
echo ### FAILED ### Permission denied. Script must be started with Administrator privileges && pause && exit

set CURRENT_HOST=%1
set OPPOSITE_HOST=%2
set FEDERATION_NAME=%3
set VIRTUAL_HOST=%4
set RABBITMQ_BIN_PATH=..
set RABBITMQ_USER=guest
set OPPOSITE_VHOST=%%%%2f

if "%CURRENT_HOST%"=="" goto :help
if "%OPPOSITE_HOST%"=="" goto :help

if "%VIRTUAL_HOST%"=="" (
		set VIRTUAL_HOST_ARG=
		set CURRENT_URL=%CURRENT_HOST%
	) else (
		echo Creating new virtual host %VIRTUAL_HOST%
		set VIRTUAL_HOST_ARG=-p %VIRTUAL_HOST%
		set CURRENT_URL=%CURRENT_HOST%/%VIRTUAL_HOST%		
		call %RABBITMQ_BIN_PATH%\rabbitmqctl add_vhost %VIRTUAL_HOST%
		call %RABBITMQ_BIN_PATH%\rabbitmqctl.bat set_permissions -p %VIRTUAL_HOST% %RABBITMQ_USER% .* .* .*
	)

call %RABBITMQ_BIN_PATH%\rabbitmq-plugins enable rabbitmq_federation
call %RABBITMQ_BIN_PATH%\rabbitmq-plugins enable rabbitmq_federation_management
net stop RabbitMQ
net start RabbitMQ
echo ======================================================
echo Please do not stop the script, RabbitMQ is loading...
echo ======================================================
timeout /T 10 /NOBREAK
pause

call %RABBITMQ_BIN_PATH%\rabbitmqctl %VIRTUAL_HOST_ARG% set_parameter federation-upstream my-upstream "{""uri"":""amqp://%OPPOSITE_HOST%/%OPPOSITE_VHOST%""}" 
call %RABBITMQ_BIN_PATH%\rabbitmqctl %VIRTUAL_HOST_ARG% set_policy federate-me ^^^^qos\.service "{""federation-upstream-set"":""all""}" --apply-to exchanges
call %RABBITMQ_BIN_PATH%\rabbitmqctl %VIRTUAL_HOST_ARG% set_policy federate-qos-replies ^^^^qos-cbk\. "{""federation-upstream-set"":""all""}" --apply-to queues
call %RABBITMQ_BIN_PATH%\rabbitmqctl %VIRTUAL_HOST_ARG% set_policy federate-pm-replies ^^^^pm-replyqueue- "{""federation-upstream-set"":""all""}" --apply-to queues
if "%FEDERATION_NAME%"=="" (
		call requester opposite.host=%OPPOSITE_HOST% current.host=%CURRENT_URL%
	) else (
		call requester opposite.host=%OPPOSITE_HOST% current.host=%CURRENT_URL% federation.name=%FEDERATION_NAME%
	)
goto :end

:help
echo Usage: setup ^<current.host^> ^<opposite.host^> ^[agent.key^] ^[virtual.host^]
echo: do not specify current host as "localhost" except when you are using network tunneling.
echo: Please use host visible from Q'ligent server.
echo Example: setup rtrs-cbk-1 qligent-server cbk1 qilgent-server-vm
goto :end

:end