package io.github.thinwind.clusterhouse.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import io.github.thinwind.clusterhouse.dto.NodeDto;
import io.github.thinwind.clusterhouse.entity.Cluster;
import io.github.thinwind.clusterhouse.entity.ClusterNode;
import io.github.thinwind.clusterhouse.entity.NodeStatus;
import io.github.thinwind.clusterhouse.repo.ClusterNodeRepo;
import io.github.thinwind.clusterhouse.repo.ClusterRepo;

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
/**
 *
 * TODO OpenApiServiceImpl说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-07  13:17
 *
 */
@Service
public class OpenApiServiceImpl implements OpenApiService {

    @Autowired
    ClusterRepo clusterRepo;

    @Autowired
    ClusterNodeRepo clusterNodeRepo;
    
    @Autowired
    NotifyService notifyService;

    @Value("${ebnas.cluster.timeout-for-unknown}")
    private int timeoutForUnknown;

    @Value("${ebnas.cluster.timeout-for-shutdown}")
    private int timeoutForShutdown;

    @Override
    public void refresh(NodeDto node) {
        boolean changed = false;
        Cluster cluster = clusterRepo.findByName(node.getClusterName());
        if (cluster == null) {
            cluster = new Cluster(node.getClusterName());
            changed = true;
        }
        ClusterNode clusterNode = cluster.getNode(node.getIp(), node.getPort());
        if (clusterNode == null) {
            clusterNode = new ClusterNode();
            clusterNode.setIp(node.getIp());
            clusterNode.setPort(node.getPort());
            cluster.addNode(clusterNode);
            changed = true;
        } else {
            if(clusterNode.getStatus() != NodeStatus.HEALTHY){
                clusterNode.setStatus(NodeStatus.HEALTHY);
                changed = true;
            }
            clusterNode.setModifiedAt(new Date());
        }
        clusterRepo.saveAndFlush(cluster);
        if(changed){
            notifyService.releaseLock();
        }
    }

    @Override
    public void maintain() {
        List<Cluster> clusters = clusterRepo.findAll();
        long current = System.currentTimeMillis();
        boolean changed = false;
        for (Cluster cluster : clusters) {
            if (cluster.getNodes().isEmpty()) {
                continue;
            }
            for (ClusterNode node : cluster.getNodes()) {
                if (node.getStatus() == NodeStatus.HEALTHY) {
                    if (current - node.getModifiedAt().getTime() >= timeoutForUnknown) {
                        node.setStatus(NodeStatus.UNKNOWN);
                        changed = true;
                    }
                } else if (node.getStatus() == NodeStatus.UNKNOWN) {
                    if (current - node.getModifiedAt().getTime() >= timeoutForShutdown) {
                        node.setStatus(NodeStatus.SHUTDOWN);
                        changed = true;
                    }
                }
            }
            clusterRepo.save(cluster);
        }
        
        if(changed){
            notifyService.releaseLock();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cluster> allHealthyClusters() {
        List<Cluster> clusters = clusterRepo.findAll();
        List<Cluster> result = new ArrayList<>();
        for (Cluster cluster : clusters) {
            Cluster shadow = null;
            try {
                shadow = cluster.clone();
            } catch (CloneNotSupportedException e) {
                // this should never happen
                e.printStackTrace();
            }
            for (ClusterNode node : cluster.getNodes()) {
                if (node.getStatus() == NodeStatus.SHUTDOWN) {
                    shadow.getNodes().remove(node);
                }else{
                    //避免序列号过程循环嵌套
                    node.setCluster(null);
                }
            }
            if (!shadow.getNodes().isEmpty()) {
                result.add(shadow);
            }
        }
        return result;
    }

}
