#!/bin/zsh

docker container stop ebnas-8848
docker container rm ebnas-8848
# LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`
docker run --name ebnas-8848 -p 8848:8848 -d ebnas:a4 -m standalone