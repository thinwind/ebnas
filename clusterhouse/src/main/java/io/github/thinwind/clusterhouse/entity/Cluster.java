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
package io.github.thinwind.clusterhouse.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import io.github.thinwind.clusterhouse.misc.Consts;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * TODO Cluster说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-03  19:02
 *
 */
@Setter
@Getter
@Entity
public class Cluster implements Cloneable{
    
    @Id
    @GeneratedValue(generator = Consts.ID_GENERATOR)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<ClusterNode> nodes = new HashSet<>();

    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @UpdateTimestamp
    private Date modifiedAt;
    
    public Cluster(){}
    
    public Cluster(String name) {
        this.name = name;
    }

    public void addNode(ClusterNode node) {
        this.nodes.add(node);
        node.setCluster(this);
    }

    public ClusterStatus status() {
        boolean health = false;
        for (ClusterNode node : this.nodes) {
            if (node.getStatus() == NodeStatus.HEALTHY) {
                health = true;
                break;
            }
        }
        if (health) {
            return ClusterStatus.HEALTHY;
        } else {
            return ClusterStatus.SHUTDOWN;
        }
    }

    public ClusterNode getNode(String ip, int port) {
        for (ClusterNode node:nodes) {
            if(ip.equals(node.getIp()) && port == node.getPort()) {
                return node;
            }
        }
        return null;
    }
    
    public Cluster clone() throws CloneNotSupportedException {
        Cluster shadow = (Cluster) super.clone();
        shadow.nodes = new HashSet(this.nodes);
        return shadow;
    }
}
