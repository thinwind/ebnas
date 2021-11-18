#!/bin/zsh

docker container stop ebnas-naming-8848
docker container rm ebnas-naming-8848

# Mac 获取ip
LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`

#WSL2 获取ip
# LOCAL_IP=`ipconfig.exe | grep -v Gateway | grep 192.168 | awk '{print $14}' | sed $'s/\r//'`

docker run -e JAVA_OPT="-Debnas.cluster-name=cluster1 -Debnas.clusterhouse=${LOCAL_IP}:7031" -e "HOST_IP=${LOCAL_IP}" --name ebnas-naming-8848 -p 8848:8848 -d ebnas/nacos:f1 -m standalone 