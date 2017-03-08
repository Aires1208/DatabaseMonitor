#!/bin/sh

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/; pwd`

if [ -z "$JAVA_HOME" ];then
    echo -e "---the parameter \"JAVA_HOME\" is not setted.---"
    exit 1
elif [ ! -d $JAVA_HOME ];then
    echo -e "---the parameter \"JAVA_HOME\" is not setted correctly.---"
    exit 1
fi

APP_PACKAGE=`basename $RUNHOME/*.jar`

JAVA="$JAVA_HOME/bin/java"
JAVA_OPTS="-Xms50m -Xmx128m -Djava.security.egd=file:/dev/./urandom"

CLASS_PATH="$RUNHOME/:$RUNHOME/lib/*:$RUNHOME/${APP_PACKAGE}"
MAIN_CLASS=com.zte.ums.zenap.itm.agent.AgentApp
$JAVA $JAVA_OPTS -classpath $CLASS_PATH $MAIN_CLASS server $RUNHOME/itm-agent.yml &
