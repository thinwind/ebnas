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
package io.github.thinwind.clusterhouse.config;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.client.naming.NamingServiceWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import io.github.thinwind.clusterhouse.entity.Cluster;
import io.github.thinwind.clusterhouse.entity.ClusterNode;
import io.github.thinwind.clusterhouse.entity.NodeStatus;
import io.github.thinwind.clusterhouse.proxy.ProxyFilter;
import io.github.thinwind.clusterhouse.proxy.ProxyServlet;
import io.github.thinwind.clusterhouse.repo.ClusterRepo;

/**
 *
 * TODO ProxyConfig说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-15  14:35
 *
 */
@Configuration
public class ProxyConfig {

    @Bean
    public ServletRegistrationBean<ProxyServlet> servletRegistrationBean() {
        ServletRegistrationBean<ProxyServlet> servletRegistrationBean =
                new ServletRegistrationBean<>(new ProxyServlet(), "/proxy/*");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, "true");
        return servletRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<ProxyFilter> providerRegisterFilter(ObjectMapper objectMapper,
            NamingService namingService) {
        FilterRegistrationBean<ProxyFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ProxyFilter(objectMapper, namingService));
        filterRegistrationBean.setName("EbnasHttpProxyFilter");
        filterRegistrationBean.setUrlPatterns(Collections.singletonList("/proxy/*"));
        filterRegistrationBean.setOrder(Ordered.LOWEST_PRECEDENCE);

        return filterRegistrationBean;
    }

    @Bean
    public NamingServiceWrapper namingService(ClusterRepo clusterRepo) {
        List<Cluster> clusters = clusterRepo.findAll();
        NamingServiceWrapper namingServiceWrapper =
                new NamingServiceWrapper(Collections.emptySet(), Collections.emptySet());
        namingServiceWrapper.setRefreshLocalNamingAddr(true);
        if (clusters.isEmpty()) {
            return namingServiceWrapper;
        }
        String localClusterName = clusters.get(0).getName();
        namingServiceWrapper.setLocalClusterName(localClusterName);
        Set<String> localClusters = new HashSet<>();
        Set<String> remoteClusters = new HashSet<>();
        for (Cluster cluster : clusters) {
            for (ClusterNode node : cluster.getNodes()) {
                if (node.getStatus() != NodeStatus.SHUTDOWN) {
                    if (localClusterName.equals(cluster.getName())) {
                        localClusters.add(node.getIp() + ":" + node.getPort());
                    } else {
                        remoteClusters.add(node.getIp() + ":" + node.getPort());
                    }
                }
            }
        }
        namingServiceWrapper.refresh(localClusters, remoteClusters);
        return namingServiceWrapper;
    }
}
