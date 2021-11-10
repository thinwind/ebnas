/*
 * Copyright 2021 Shang Yehua <niceshang@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.alibaba.nacos.client.naming.clusterhouse;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.net.HttpURLConnection;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.client.naming.NamingServiceWrapper;
import com.alibaba.nacos.client.naming.net.HttpResult;

/**
 *
 * TODO ClusterBeatReactor说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-08  15:10
 *
 */
public class ClusterBeatReactor {

    private final String clusterHouseAddresses;

    private final String localClusterName;

    private NamingServiceWrapper namingServiceWrapper;

    private final static String CLUSTER_LIST_URI = "/openapi/clusters";

    private final static int DELAY = 500;

    private ScheduledExecutorService executorService;

    public ClusterBeatReactor(String clusterHouseAddresses, String localClusterName,
            NamingServiceWrapper namingServiceWrapper) {
        this.clusterHouseAddresses = clusterHouseAddresses;
        this.namingServiceWrapper = namingServiceWrapper;
        this.localClusterName = localClusterName;
        String clusterHouses[] = clusterHouseAddresses.split(",");
        executorService =
                new ScheduledThreadPoolExecutor(clusterHouses.length, new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread thread = new Thread(r);
                        thread.setDaemon(true);
                        thread.setName("com.github.thinwind.ebnas.cluster.beat");
                        return thread;
                    }
                });
        for (String addr : clusterHouses) {
            executorService.schedule(new ClusterBeatTask("http://" + addr + CLUSTER_LIST_URI),
                    DELAY, TimeUnit.MILLISECONDS);
        }
    }

    public String getClusterHouseAddresses() {
        return this.clusterHouseAddresses;
    }

    class ClusterBeatTask implements Runnable {

        private final String url;

        public ClusterBeatTask(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            HttpResult result = ClusterHttpUtil.httpGet(url);
            if (HttpURLConnection.HTTP_OK == result.code) {
                List<Cluster> clusters = JSON.parseArray(result.content, Cluster.class);
                String localClusterAddr = null;
                Set<String> remoteNamingAddrSet = new HashSet<>();
                for (Cluster cluster : clusters) {
                    if (localClusterName.equals(cluster.getName())) {
                        localClusterAddr = mergeAddr(cluster);
                    } else {
                        remoteNamingAddrSet.add(mergeAddr(cluster));
                    }
                }
                namingServiceWrapper.refresh(localClusterAddr, remoteNamingAddrSet);
            }
            executorService.schedule(new ClusterBeatTask(url), DELAY, TimeUnit.MILLISECONDS);
        }

    }

    private String mergeAddr(Cluster cluster) {
        StringBuilder builder = new StringBuilder();
        for (ClusterNode node : cluster.getNodes()) {
            builder.append(node.getIp()).append(":").append(node.getPort());
            builder.append(",");
        }
        builder.setLength(builder.length() - 1);
        return builder.toString();
    }
}
