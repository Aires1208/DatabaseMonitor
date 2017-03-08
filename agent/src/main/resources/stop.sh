#!/bin/sh

kill_service()
{
	port_status=$(lsof -i :$1 | grep LISTEN | grep -v grep | wc -l)
	if [ ! $port_status -eq 0 ];then
		for pid in $(lsof -i :$1 | grep LISTEN | grep -v grep | awk '{print $2}');do
			echo "kill $2 process.$pid"
			kill -9 $pid
			sleep 0.5s
		done
	fi
}

kill_service 38205 dbmonitor-agent
