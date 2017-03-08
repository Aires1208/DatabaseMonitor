#!/bin/sh

kill_service()
{
	LOOP_NUM=2
	for loop in $(seq $LOOP_NUM);do
		port_status=$(lsof -i :$1 | grep LISTEN | grep -v grep | wc -l)
		if [ ! $port_status -eq 0 ];then
			for pid in $(lsof -i :$1 | grep LISTEN | grep -v grep | awk '{print $2}');do
				echo "kill $2 process.$pid"
				kill -9 $pid
				sleep 0.5s
			done
		fi
	done
}

kill_service 8081 dbmonitor-collector
