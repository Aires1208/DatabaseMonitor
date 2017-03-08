#!/bin/sh

kill_service()
{
	for pid in $(lsof -i :$1 | grep LISTEN | grep -v grep | awk '{print $2}');do
		echo "kill $2 process.$pid"
		kill -9 $pid
		sleep 0.5s
	done

	for pid in $(ps -ef | grep $2 | grep -v grep | awk '{print $2}');do
		echo "kill $2 process.$pid"
		kill -9 $pid
		sleep 0.5s
	done
}

kill_service 8081 smartsight-dbmonitor-collector
