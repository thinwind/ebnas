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

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * TODO Cluster说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-03  19:02
 *
 */
public class Cluster implements Cloneable{
    
    private Integer id;

    private String name;

    private Set<ClusterNode> nodes = new HashSet<>();

    private Date createdAt;
    private Date modifiedAt;
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Set<ClusterNode> getNodes() {
        return nodes;
    }
    public void setNodes(Set<ClusterNode> nodes) {
        this.nodes = nodes;
    }
    public Date getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public Date getModifiedAt() {
        return modifiedAt;
    }
    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }
    
    
}
