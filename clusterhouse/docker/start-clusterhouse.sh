#!/bin/zsh

docker container stop ebnas-clusterhouse
docker container rm ebnas-clusterhouse

# Mac 获取ip
LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`

#WSL2 获取ip
# LOCAL_IP=`ipconfig.exe | grep -v Gateway | grep 192.168 | awk '{print $14}' | sed $'s/\r//'`

docker run --name ebnas-clusterhouse -p 7031:7031 -d ebnas/clusterhouse:ch1