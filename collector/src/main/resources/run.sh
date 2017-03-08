#!/bin/bash

source /etc/profile
if [ ! -z $CATALINA_HOME ];then
	unset -v CATALINA_HOME
fi

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`

if [ -z "$JAVA_HOME" ];then
    echo -e "---the parameter \"JAVA_HOME\" is not setted.---"
    exit 1
elif [ ! -d $JAVA_HOME ];then
    echo -e "---the parameter \"JAVA_HOME\" is not setted correctly.---"
    exit 1
fi

export Kafka_IP=127.0.0.1
export Kafka_Port=9092
export Kafka_ZK=2181
export HBase_IP=127.0.0.1
export HBase_Port=2181

JAVA="$JAVA_HOME/bin/java"
JAVA_OPTS="-Djava.security.egd=file:/dev/./urandom"

APP_PACKAGE=`ls | grep *.war | grep -v original | grep -v grep`
cd $RUNHOME
$JAVA $JAVA_OPTS -jar $APP_PACKAGE &
