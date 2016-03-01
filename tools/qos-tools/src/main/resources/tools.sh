#!/bin/sh
#
# Copyright (c) 2013 Tecom LLC 
# All rights reserved
#
# Исключительное право (c) 2013 принадлежит ООО Теком 
# Все права защищены
#

TOOL_NAME=$1

if [  -z "$TOOL_NAME" ] || [ "$TOOL_NAME" = '-help' ] ; then
	TOOL_NAME=
fi

if [ -z "$TOOL_NAME" ] ; then 
	echo Usage:
	echo "tools <ToolName> <parameters>"
	echo Example:
	echo "tools SendAlert amqp.host=localhost"
	exit 0;
fi

while [[ ! -z $1 ]]
do
	D_PARAMS=$D_PARAMS" "-D$1
	shift
done

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
ABSOLUTE_PATH=`dirname $ABSOLUTE_PATH`
TOOLS_HOME=$ABSOLUTE_PATH
CLASSPATH=$TOOLS_HOME:$TOOLS_HOME/lib/*

java $D_PARAMS -cp "$CLASSPATH" com.tecomgroup.qos.tools.Tools $TOOL_NAME
