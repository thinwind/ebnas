FROM mrshang/jre8

# put nacos to /home/nacos
ADD distribution/target/nacos-server-1.1.3.tar.gz /home/

ENV BASE_DIR="/home/nacos"

WORKDIR $BASE_DIR

# set startup log dir
RUN mkdir -p logs \
	&& cd logs \
	&& touch start.out \
	&& ln -sf /dev/stdout start.out \
	&& ln -sf /dev/stderr start.out
    
RUN chmod a+x bin/startup-docker-jre8.sh 

VOLUME $BASE_DIR/logs

EXPOSE 8848

ENTRYPOINT ["bin/startup-docker-jre8.sh"]