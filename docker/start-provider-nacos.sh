#!/bin/zsh

# Mac 获取ip
export LOCAL_IP=`ifconfig | grep 192.168 | awk '{print $2}'`

docker-compose -f ./provider-nacos.yml -p provider-nacos up -d