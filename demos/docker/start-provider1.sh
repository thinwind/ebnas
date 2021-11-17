#!/bin/zsh

docker container stop ebnas-provider-1
docker container rm ebnas-provider-1

# Mac 获取ip
# LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`

#WSL2 获取ip
LOCAL_IP=`ipconfig.exe | grep -v Gateway | grep 192.168 | awk '{print $14}' | sed $'s/\r//'`

docker run -e JAVA_OPT="-Debnas.server-addr=${LOCAL_IP}:8848 -Debnas.local-ip=${LOCAL_IP}" --name ebnas-provider-1 -p 8089:8089 -d ebnas/provider:p3