#!/bin/zsh

docker container stop ebnas-8849
docker container rm ebnas-8849

LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`
docker run -e JAVA_OPT="-Debnas.cluster-name=cluster2 -Debnas.clusterhouse=${LOCAL_IP}:7031 -Dserver.port=8849" -e HOST_IP=${LOCAL_IP} --name ebnas-8849 -p 8849:8849 -d ebnas:hb3 -m standalone 