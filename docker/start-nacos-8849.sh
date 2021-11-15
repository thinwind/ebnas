#!/bin/zsh

docker container stop ebnas-naming-8849
docker container rm ebnas-naming-8849

LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`
docker run -e JAVA_OPT="-Debnas.cluster-name=cluster2 -Debnas.clusterhouse=${LOCAL_IP}:7031 -Dserver.port=8849" -e HOST_IP=${LOCAL_IP} --name ebnas-naming-8849 -p 8849:8849 -d ebnas/naming-with-hb -m standalone 