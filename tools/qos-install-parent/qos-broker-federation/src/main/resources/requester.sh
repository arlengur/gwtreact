if [ "$1" = '-help' ] ; then
	echo ""
	echo "Usage:"
	echo "requester.sh <parameters>"
	echo "Example:"
	echo "requester.sh opposite.host=qligent_server_host current.host=current_agent_host"
	echo ""
	exit 1
fi

ABSOLUTE_PATH=$(cd `dirname "${BASH_SOURCE[0]}"` && pwd)/`basename "${BASH_SOURCE[0]}"`
ABSOLUTE_PATH=`dirname $ABSOLUTE_PATH`
REQUESTER_HOME=$ABSOLUTE_PATH

while [[ ! -z $1 ]]
do
	D_PARAMS=$D_PARAMS" "-D$1
	shift
done

CLASSPATH=$REQUESTER_HOME:$REQUESTER_HOME/lib/*

java $D_PARAMS -cp "$CLASSPATH" com.tecomgroup.qos.broker.federation.Bootstrap