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

import java.util.ArrayList;
import java.util.List;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.listener.EventListener;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;
import com.alibaba.nacos.api.selector.AbstractSelector;
import com.alibaba.nacos.client.utils.LogUtils;

/**
 *
 * TODO MultiClusterNamingService说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-02  14:02
 *
 */
public class MultiClusterNamingService implements NamingService {

    private NacosNamingService localNamingService;

    private NacosNamingService[] otherDatahouses;

    public MultiClusterNamingService(String localHouse, String... others) {
        localNamingService = new NacosNamingService(localHouse);
        if (others.length > 0) {
            otherDatahouses = new NacosNamingService[others.length];
            for (int i = 0; i < others.length; i++) {
                otherDatahouses[i] = new NacosNamingService(others[i]);
            }
        } else {
            otherDatahouses = new NacosNamingService[0];
        }
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port) throws NacosException {
        localNamingService.registerInstance(serviceName, ip, port);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, String ip, int port)
            throws NacosException {
        localNamingService.registerInstance(serviceName, groupName, ip, port);
    }

    @Override
    public void registerInstance(String serviceName, String ip, int port, String clusterName)
            throws NacosException {
        localNamingService.registerInstance(serviceName, ip, port, clusterName);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, String ip, int port,
            String clusterName) throws NacosException {
        localNamingService.registerInstance(serviceName, groupName, ip, port, clusterName);
    }

    @Override
    public void registerInstance(String serviceName, Instance instance) throws NacosException {
        localNamingService.registerInstance(serviceName, instance);
    }

    @Override
    public void registerInstance(String serviceName, String groupName, Instance instance)
            throws NacosException {
        localNamingService.registerInstance(serviceName, groupName, instance);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port) throws NacosException {
        localNamingService.deregisterInstance(serviceName, ip, port);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, String ip, int port)
            throws NacosException {
        localNamingService.deregisterInstance(serviceName, groupName, ip, port);
    }

    @Override
    public void deregisterInstance(String serviceName, String ip, int port, String clusterName)
            throws NacosException {
        localNamingService.deregisterInstance(serviceName, ip, port, clusterName);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, String ip, int port,
            String clusterName) throws NacosException {
        localNamingService.deregisterInstance(serviceName, groupName, ip, port, clusterName);
    }

    @Override
    public void deregisterInstance(String serviceName, Instance instance) throws NacosException {
        localNamingService.deregisterInstance(serviceName, instance);
    }

    @Override
    public void deregisterInstance(String serviceName, String groupName, Instance instance)
            throws NacosException {
        localNamingService.deregisterInstance(serviceName, groupName, instance);
    }

    private List<Instance> mergeInstances(InstanceListSupplier... suppliers) throws NacosException {
        List<Instance> result = new ArrayList<>();
        Exception first = null;
        for (InstanceListSupplier supplier : suppliers) {
            List<Instance> instances;
            try {
                instances = supplier.get();
                if (instances != null && !instances.isEmpty()) {
                    result.addAll(instances);
                }
            } catch (Exception e) {
                if (first == null) {
                    first = e;
                }
                LogUtils.NAMING_LOGGER.error("获取服务错误", e);
            }
        }
        if (result.isEmpty() && first != null) {
            if (first instanceof NacosException) {
                throw (NacosException) first;
            }
            NacosException exception = new NacosException(NacosException.SERVER_ERROR,first.toString());
            exception.setCauseThrowable(first);
            throw exception;
        }
        return result;
    }

    @Override
    public List<Instance> getAllInstances(String serviceName) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].getAllInstances(serviceName);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, groupName);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].getAllInstances(serviceName, groupName);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, boolean subscribe)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].getAllInstances(serviceName, subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName, boolean subscribe)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, groupName, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] =
                    () -> otherDatahouses[j].getAllInstances(serviceName, groupName, subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, clusters);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].getAllInstances(serviceName, clusters);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName,
            List<String> clusters) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, groupName, clusters);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] =
                    () -> otherDatahouses[j].getAllInstances(serviceName, groupName, clusters);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, List<String> clusters,
            boolean subscribe) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, clusters, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] =
                    () -> otherDatahouses[j].getAllInstances(serviceName, clusters, subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> getAllInstances(String serviceName, String groupName,
            List<String> clusters, boolean subscribe) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.getAllInstances(serviceName, groupName, clusters,
                subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].getAllInstances(serviceName, groupName, clusters,
                    subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, boolean healthy)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, healthy);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, healthy);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName, boolean healthy)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, groupName, healthy);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, groupName, healthy);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, boolean healthy, boolean subscribe)
            throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, healthy, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, healthy, subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName, boolean healthy,
            boolean subscribe) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, groupName, healthy,
                subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, groupName, healthy,
                    subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, List<String> clusters,
            boolean healthy) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, clusters, healthy);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, clusters, healthy);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName,
            List<String> clusters, boolean healthy) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] =
                () -> localNamingService.selectInstances(serviceName, groupName, clusters, healthy);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, groupName, clusters,
                    healthy);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, List<String> clusters,
            boolean healthy, boolean subscribe) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] =
                () -> localNamingService.selectInstances(serviceName, clusters, healthy, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, clusters, healthy,
                    subscribe);
        }
        return mergeInstances(services);
    }

    @Override
    public List<Instance> selectInstances(String serviceName, String groupName,
            List<String> clusters, boolean healthy, boolean subscribe) throws NacosException {
        InstanceListSupplier[] services = new InstanceListSupplier[otherDatahouses.length + 1];
        services[0] = () -> localNamingService.selectInstances(serviceName, groupName, clusters,
                healthy, subscribe);
        for (int i = 1; i < services.length; i++) {
            int j = i - 1;
            services[i] = () -> otherDatahouses[j].selectInstances(serviceName, groupName, clusters,
                    healthy, subscribe);
        }
        return mergeInstances(services);
    }

    private Instance selectOne(InstanceSupplier... suppliers) throws NacosException {
        Exception first = null;
        for (InstanceSupplier instanceSupplier : suppliers) {
            try {
                Instance instance = instanceSupplier.get();
                if (instance != null) {
                    return instance;
                }
            } catch (Exception e) {
                if (first == null) {
                    first = e;
                }
                LogUtils.NAMING_LOGGER.error("Select Instance Error", first);
            }
        }
        if (first != null) {
            if (first instanceof NacosException) {
                throw (NacosException) first;
            }
            NacosException exception =
                    new NacosException(NacosException.SERVER_ERROR, first.toString());
            exception.setCauseThrowable(first);
            throw exception;
        }
        return null;
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName) throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName)
            throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName, groupName);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] =
                    () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, groupName);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, boolean subscribe)
            throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName, subscribe);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] =
                    () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, subscribe);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            boolean subscribe) throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName, groupName,
                subscribe);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, groupName,
                    subscribe);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, List<String> clusters)
            throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName, clusters);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, clusters);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            List<String> clusters) throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] =
                () -> localNamingService.selectOneHealthyInstance(serviceName, groupName, clusters);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, groupName,
                    clusters);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, List<String> clusters,
            boolean subscribe) throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] =
                () -> localNamingService.selectOneHealthyInstance(serviceName, clusters, subscribe);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, clusters,
                    subscribe);
        }
        return selectOne(suppliers);
    }

    @Override
    public Instance selectOneHealthyInstance(String serviceName, String groupName,
            List<String> clusters, boolean subscribe) throws NacosException {
        InstanceSupplier[] suppliers = new InstanceSupplier[otherDatahouses.length + 1];
        suppliers[0] = () -> localNamingService.selectOneHealthyInstance(serviceName, groupName,
                clusters, subscribe);
        for (int i = 1; i < suppliers.length; i++) {
            int j = i - 1;
            suppliers[i] = () -> otherDatahouses[j].selectOneHealthyInstance(serviceName, groupName,
                    clusters, subscribe);
        }
        return selectOne(suppliers);
    }

    @Override
    public void subscribe(String serviceName, EventListener listener) throws NacosException {
        localNamingService.subscribe(serviceName, listener);
    }

    @Override
    public void subscribe(String serviceName, String groupName, EventListener listener)
            throws NacosException {
        localNamingService.subscribe(serviceName, groupName, listener);
    }

    @Override
    public void subscribe(String serviceName, List<String> clusters, EventListener listener)
            throws NacosException {
        localNamingService.subscribe(serviceName, clusters, listener);
    }

    @Override
    public void subscribe(String serviceName, String groupName, List<String> clusters,
            EventListener listener) throws NacosException {
        localNamingService.subscribe(serviceName, groupName, listener);
    }

    @Override
    public void unsubscribe(String serviceName, EventListener listener) throws NacosException {
        localNamingService.unsubscribe(serviceName, listener);
    }

    @Override
    public void unsubscribe(String serviceName, String groupName, EventListener listener)
            throws NacosException {
        localNamingService.unsubscribe(serviceName, groupName, listener);
    }

    @Override
    public void unsubscribe(String serviceName, List<String> clusters, EventListener listener)
            throws NacosException {
        localNamingService.unsubscribe(serviceName, clusters, listener);
    }

    @Override
    public void unsubscribe(String serviceName, String groupName, List<String> clusters,
            EventListener listener) throws NacosException {
        localNamingService.unsubscribe(serviceName, groupName, clusters, listener);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize) throws NacosException {
        return localNamingService.getServicesOfServer(pageNo, pageSize);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, String groupName)
            throws NacosException {
        return localNamingService.getServicesOfServer(pageNo, pageSize, groupName);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, AbstractSelector selector)
            throws NacosException {
        return localNamingService.getServicesOfServer(pageNo, pageSize, selector);
    }

    @Override
    public ListView<String> getServicesOfServer(int pageNo, int pageSize, String groupName,
            AbstractSelector selector) throws NacosException {
        return localNamingService.getServicesOfServer(pageNo, pageSize, groupName, selector);
    }

    @Override
    public List<ServiceInfo> getSubscribeServices() throws NacosException {
        return localNamingService.getSubscribeServices();
    }

    @Override
    public String getServerStatus() {
        return localNamingService.getServerStatus();
    }

    static interface InstanceListSupplier {
        List<Instance> get() throws NacosException;
    }

    static interface InstanceSupplier {
        Instance get() throws NacosException;
    }
}
