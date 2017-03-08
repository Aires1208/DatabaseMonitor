#!/bin/sh

DIRNAME=`dirname $0`
RUNHOME=`cd $DIRNAME/;pwd`
echo @RUNHOME@ $RUNHOME

if [ -f "$RUNHOME/setenv.sh" ];then
    . "$RUNHOME/setenv.sh"
else
    echo "can not found $RUNHOME/setenv.sh"
fi

echo ==========ENV INFO==============
echo @RUNHOME@ $RUNHOME
echo @Main_Class@ $Main_Class
echo @APP_INFO@ $APP_INFO
echo @Main_JAR@ $Main_JAR
echo ================================

echo start $APP_INFO ...

JAVA="$JAVA_HOME/bin/java"
JAVA_OPTS="-Xms50m -Xmx128m"
JAVA_OPTS="$JAVA_OPTS -Djava.security.egd=file:/dev/./urandom"
port=8777
#JAVA_OPTS="$JAVA_OPTS -Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,address=$port,server=y,suspend=n"
CLASS_PATH="$RUNHOME/:$RUNHOME/$Main_JAR"


echo ===============RUN INFO====================
echo @JAVA_HOME@ $JAVA_HOME
echo @JAVA@ $JAVA
echo @JAVA_OPTS@ $JAVA_OPTS
echo @CLASS_PATH@ $CLASS_PATH
echo ===========================================

echo "=====run smartsight dbmonitor agent====="
if [ -f "$RUNHOME/agent/run.sh" ];then
   echo "run smartsight agent"
   $RUNHOME/agent/run.sh
fi
echo "========================================="


"$JAVA" $JAVA_OPTS -classpath "$CLASS_PATH" -jar $RUNHOME/$Main_JAR