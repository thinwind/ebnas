package io.github.thinwind.clusterhouse.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.thinwind.clusterhouse.dto.NodeDto;
import io.github.thinwind.clusterhouse.entity.Cluster;
import io.github.thinwind.clusterhouse.entity.ClusterNode;
import io.github.thinwind.clusterhouse.repo.ClusterNodeRepo;
import io.github.thinwind.clusterhouse.repo.ClusterRepo;

/* 
 * Copyright 2021 Shang Yehua <niceshang@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
@Transactional(rollbackFor = Exception.class)
public class OpenApiServiceImpl implements OpenApiService{
    
    @Autowired
    ClusterRepo clusterRepo;

    @Autowired
    ClusterNodeRepo clusterNodeRepo;
    
    @Override
    public void refresh(NodeDto node) {
        Cluster cluster = clusterRepo.findByName(node.getClusterName());
        if(cluster == null){
            cluster = new Cluster(node.getClusterName());
        }
        ClusterNode clusterNode = cluster.getNode(node.getIp(),node.getPort());
        if (clusterNode == null) {
            clusterNode = new ClusterNode();
            clusterNode.setIp(node.getIp());
            clusterNode.setPort(node.getPort()); 
            cluster.addNode(clusterNode);
        }else{
            clusterNode.setModifiedAt(new Date());
        }
        clusterRepo.saveAndFlush(cluster);
    }

    @Override
    public void maintain() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public List<Cluster> allHealthyClusters() {
        // TODO Auto-generated method stub
        return null;
    }
    
}
