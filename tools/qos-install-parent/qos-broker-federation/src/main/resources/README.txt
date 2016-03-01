OVERVIEW:
This tool is able to establish federation between local broker and Q'ligent server broker.
It is useful when setting up a local broker on controlblock's side.

PREREQUISITES:
1. RabbitMQ broker must be installed on the server side
2. RabbitMQ broker must be installed on the controlblock side (local machine)

USAGE:
1. Place this directory under the <rabbitmq_home>\sbin dir.
2. Go to it in console
3. In console run: "setup local.host remote.host [agent.key] [virtual.host]" (without quotes),
	where 
		"local.host" is local machine hostname and
		"remote.host" is aggregation server hostname.
	        "agent.key" (optional) - name of the federation upstream on server side
	        "virtual.host" (optional) - name of the virtual host on agent broker
	NOTE: do not specify current host as "localhost" except when you are using network tunneling.
	Please use host visible from Q'ligent server.
	Example:
		"setup rtrs-cbk-1 qligent-server cbk1 qilgent-server-vm"	

ADDITIONAL INFO:
For more information please refer this wiki page:
http://rnd.tecom.nnov.ru/projects/qos/wiki/%D0%9D%D0%B0%D1%81%D1%82%D1%80%D0%BE%D0%B9%D0%BA%D0%B0_%D0%BB%D0%BE%D0%BA%D0%B0%D0%BB%D1%8C%D0%BD%D0%BE%D0%B3%D0%BE_%D0%B1%D1%80%D0%BE%D0%BA%D0%B5%D1%80%D0%B0_%D0%BD%D0%B0_%D0%91%D0%9A