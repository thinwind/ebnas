#!/bin/zsh

docker container stop ebnas-naming-8848
docker container rm ebnas-naming-8848
LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`
docker run -e JAVA_OPT="-Debnas.cluster-name=cluster1 -Debnas.clusterhouse=${LOCAL_IP}:7031" -e HOST_IP=${LOCAL_IP} --name ebnas-naming-8848 -p 8848:8848 -d ebnas/naming-with-hb -m standalone 