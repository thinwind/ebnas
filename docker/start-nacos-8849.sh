#!/bin/zsh

docker container rm ebnas-8849
LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`
docker run --name ebnas-8849 -p 8849:8848 -e HOST_IP=$LOCAL_IP -d ebnas:a4 -m standalone