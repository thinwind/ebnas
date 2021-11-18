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
package com.alibaba.nacos.client.naming;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.api.selector.AbstractSelector;
import com.alibaba.nacos.client.naming.clusterhouse.ClusterBeatReactor;
import com.alibaba.nacos.client.naming.clusterhouse.ClusterHttpUtil;
import com.alibaba.nacos.client.naming.clusterhouse.Pair;

/**
 *
 * NamingService的包装类，将调用过程托管给内部的NamingService实例
 * 
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-08  13:57
 *
 */
public class NamingServiceWrapper implements NamingService {

    private MultiClusterNamingService namingService;

    private Set<String> localNamingAddrSet = new HashSet<>();

    private String localClusterName;

    private String clusterHouseAddresses;

    private boolean refreshLocalNamingAddr = false;

    private Set<String> remoteNamingAddrSet = new HashSet<>();;

    private final static String CLUSTER_LIST_URI = "/openapi/clusters?delay=0";

    ClusterBeatReactor beatReactor;

    public NamingServiceWrapper(String localClusterName, String localNamingAddrs,
            String clusterHouseAddresses) {
        this.localClusterName = localClusterName;
        this.clusterHouseAddresses = clusterHouseAddresses;
        refreshLocalNamingAddr = (localNamingAddrs == null || localNamingAddrs.trim().isEmpty());
        if (!refreshLocalNamingAddr) {
            for (String addr : localNamingAddrs.split(",")) {
                localNamingAddrSet.add(addr);
            }
        }

        refreshNamingAddrs();

        if (refreshLocalNamingAddr) {
            localNamingAddrs = localNamingAddrSet.stream().collect(Collectors.joining(","));
        }

        namingService = new MultiClusterNamingService(localNamingAddrs,
                remoteNamingAddrSet.toArray(new String[remoteNamingAddrSet.size()]));

        // heartbeat
        beatReactor = new ClusterBeatReactor(clusterHouseAddresses, localClusterName, this);
    }

    private void refreshNamingAddrs() {
        if (clusterHouseAddresses == null || clusterHouseAddresses.isEmpty()) {
            return;
        }

        String clusterHouses[] = clusterHouseAddresses.split(",");
        for (String clusterHouse : clusterHouses) {
            String url = "http://" + clusterHouse + CLUSTER_LIST_URI;
            Pair<Set<String>, Set<String>> pair =
                    ClusterHttpUtil.getClusterhouses(localClusterName, url);
            if (refreshLocalNamingAddr) {
                localNamingAddrSet.addAll(pair.left);
            }
            remoteNamingAddrSet.addAll(pair.right);
        }
    }

    public NamingServiceWrapper(Set<String> localNamingAddrSet, Set<String> remoteNamingAddrSet){
        if(localNamingAddrSet != null && !localNamingAddrSet.isEmpty()){
            String localNamingAddrs = localNamingAddrSet.stream().collect(Collectors.joining(","));
            namingService = new MultiClusterNamingService(localNamingAddrs,
                remoteNamingAddrSet.toArray(new String[remoteNamingAddrSet.size()]));
        }
    }
    
    public NamingServiceWrapper(String localNamingAddrs, Set<String> remoteNamingAddrSet) {
        namingService = new MultiClusterNamingService(localNamingAddrs,
                remoteNamingAddrSet.toArray(new String[remoteNamingAddrSet.size()]));
    }

    public synchronized void refresh(Set<String> localNamingAddr, Set<String> remoteNamingAddrSet) {
        boolean changed = false;
        if (refreshLocalNamingAddr) {
            if (!this.localNamingAddrSet.containsAll(localNamingAddr)) {
                this.localNamingAddrSet.addAll(localNamingAddr);
                changed=true;
            }
        }

        if (!this.remoteNamingAddrSet.containsAll(remoteNamingAddrSet)) {
            this.remoteNamingAddrSet.addAll(remoteNamingAddrSet);
            changed = true;
        }
        //判断是否有变动
        if (changed) {
            if (namingService != null) {
                namingService.shutdown();
            }
            String localNamingAddrs= localNamingAddrSet.stream().collect(Collectors.joining(","));
            namingService = new MultiClusterNamingService(localNamingAddrs,
                    remoteNamingAddrSet.toArray(new String[remoteNamingAddrSet.size()]));
        }
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port) throws NacosException {
        this.namingService.registerInstance(serviceName, ip, port);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, String ip, int port)
            throws NacosException {
        namingService.registerInstance(serviceName, groupName, ip, port);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, String clusterName)
            throws NacosException {
        namingService.deregisterInstance(serviceName, ip, port, clusterName);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, String ip, int port,
            String clusterName) throws NacosException {
        namingService.registerInstance(serviceName, groupName, ip, port, clusterName);
    }

    @Override
    public void registerInstance(String serviceName, Instance instance) throws NacosException {
        namingService.registerInstance(serviceName, instance);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, Instance instance)
            throws NacosException {
        namingService.registerInstance(serviceName, groupName, instance);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) throws NacosException {
        namingService.deregisterInstance(serviceName, ip, port);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, String ip, int port)
            throws NacosException {
        namingService.deregisterInstance(serviceName, groupName, ip, port);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port, String clusterName)
            throws NacosException {
        namingService.deregisterInstance(serviceName, ip, port, clusterName);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, String ip, int port,
            String clusterName) throws NacosException {
        namingService.deregisterInstance(serviceName, groupName, ip, port, clusterName);
    }

    @Override
    public void deregisterInstance(String serviceName, Instance instance) throws NacosException {
        namingService.deregisterInstance(serviceName, instance);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, Instance instance)
            throws NacosException {
        namingService.deregisterInstance(serviceName, groupName, instance);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName) throws NacosException {
        return namingService.getAllInstances(serviceName);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName)
            throws NacosException {
        return namingService.getAllInstances(serviceName, groupName);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, boolean subscribe)
            throws NacosException {
        return namingService.getAllInstances(serviceName, subscribe);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName, boolean subscribe)
            throws NacosException {
        return namingService.getAllInstances(serviceName, groupName, subscribe);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters)
            throws NacosException {
        return namingService.getAllInstances(serviceName, clusters);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName,
            List<String> clusters) throws NacosException {
        return namingService.getAllInstances(serviceName, groupName, clusters);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters,
            boolean subscribe) throws NacosException {
        return namingService.getAllInstances(serviceName, clusters, subscribe);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName,
            List<String> clusters, boolean subscribe) throws NacosException {
        return namingService.getAllInstances(serviceName, groupName, clusters, subscribe);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, boolean healthy)
            throws NacosException {
        return namingService.selectInstances(serviceName, healthy);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName, boolean healthy)
            throws NacosException {
        return namingService.selectInstances(serviceName, groupName, healthy);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, boolean healthy, boolean subscribe)
            throws NacosException {
        return namingService.selectInstances(serviceName, healthy, subscribe);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName, boolean healthy,
            boolean subscribe) throws NacosException {
        return namingService.selectInstances(serviceName, groupName, healthy, subscribe);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, List<String> clusters,
            boolean healthy) throws NacosException {
        return namingService.selectInstances(serviceName, clusters, healthy);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName,
            List<String> clusters, boolean healthy) throws NacosException {
        return namingService.selectInstances(serviceName, groupName, clusters, healthy, healthy);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, List<String> clusters,
            boolean healthy, boolean subscribe) throws NacosException {
        return namingService.selectInstances(serviceName, clusters, healthy, subscribe);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName,
            List<String> clusters, boolean healthy, boolean subscribe) throws NacosException {
        return namingService.selectInstances(serviceName, groupName, clusters, healthy, subscribe);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName)
            throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, groupName);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, boolean subscribe)
            throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, subscribe);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            boolean subscribe) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, groupName, subscribe);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, List<String> clusters)
            throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, clusters);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            List<String> clusters) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, groupName, clusters);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, List<String> clusters,
            boolean subscribe) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, clusters, subscribe);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            List<String> clusters, boolean subscribe) throws NacosException {
        return namingService.selectOneHealthyInstance(serviceName, groupName, clusters, subscribe);
    }

    @Override
    public void subscribe(String serviceName, EventListener listener) throws NacosException {
        namingService.subscribe(serviceName, listener);
    }

    @Override
    public void subscribe(String serviceName, String groupName, EventListener listener)
            throws NacosException {
        namingService.subscribe(serviceName, groupName, listener);
    }

    @Override
    public void subscribe(String serviceName, List<String> clusters, EventListener listener)
            throws NacosException {
        namingService.subscribe(serviceName, clusters, listener);
    }

    @Override
    public void subscribe(String serviceName, String groupName, List<String> clusters,
            EventListener listener) throws NacosException {
        namingService.subscribe(serviceName, groupName, clusters, listener);
    }

    @Override
    public void unsubscribe(String serviceName, EventListener listener) throws NacosException {
        namingService.unsubscribe(serviceName, listener);
    }

    @Override
    public void unsubscribe(String serviceName, String groupName, EventListener listener)
            throws NacosException {
        namingService.unsubscribe(serviceName, groupName, listener);
    }

    @Override
    public void unsubscribe(String serviceName, List<String> clusters, EventListener listener)
            throws NacosException {
        namingService.unsubscribe(serviceName, clusters, listener);
    }

    @Override
    public void unsubscribe(String serviceName, String groupName, List<String> clusters,
            EventListener listener) throws NacosException {
        namingService.unsubscribe(serviceName, groupName, clusters, listener);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize) throws NacosException {
        return namingService.getServicesOfServer(pageNo, pageSize);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, String groupName)
            throws NacosException {
        return namingService.getServicesOfServer(pageNo, pageSize, groupName);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, AbstractSelector selector)
            throws NacosException {
        return namingService.getServicesOfServer(pageNo, pageSize, selector);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, String groupName,
            AbstractSelector selector) throws NacosException {
        return namingService.getServicesOfServer(pageNo, pageSize, groupName, selector);
    }

    @Override
    public List<ServiceInfo> getSubscribeServices() throws NacosException {
        return namingService.getSubscribeServices();
    }

    @Override
    public String getServerStatus() {
        return namingService.getServerStatus();
    }

    public boolean isRefreshLocalNamingAddr() {
        return refreshLocalNamingAddr;
    }

    public void setRefreshLocalNamingAddr(boolean refreshLocalNamingAddr) {
        this.refreshLocalNamingAddr = refreshLocalNamingAddr;
    }

    public String getLocalClusterName() {
        return localClusterName;
    }

    public void setLocalClusterName(String localClusterName) {
        this.localClusterName = localClusterName;
    }

}
