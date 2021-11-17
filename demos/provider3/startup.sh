#!/bin/bash

source /root/.bashrc

export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=`cd $(dirname $0)/..; pwd`

if [ ! -d "${BASE_DIR}/logs" ]; then
  mkdir ${BASE_DIR}/logs
fi

# start
echo "$JAVA ${JAVA_OPT}" > ${BASE_DIR}/logs/start.out 2>&1 &
nohup $JAVA ${JAVA_OPT} -jar ebnas-provider1-1.1.3.jar >> ${BASE_DIR}/logs/start.out 2>&1
tail -f ${BASE_DIR}/logs/start.out