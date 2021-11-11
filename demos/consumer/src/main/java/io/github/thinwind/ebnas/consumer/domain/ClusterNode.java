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
package io.github.thinwind.ebnas.consumer.domain;

import java.util.Date;
import java.util.Objects;
import io.undertow.server.handlers.proxy.mod_cluster.NodeStatus;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * 集群节点
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-03  18:56
 *
 */
@Setter
@Getter
public class ClusterNode {

    private Integer id;
    
    private Cluster cluster;
        
    private String ip;

    private int port;

    private String status;

    private Date createdAt;

    private Date modifiedAt;

    @Override
    public int hashCode() {
        return Objects.hash(ip, port);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ClusterNode other = (ClusterNode) obj;
        return Objects.equals(ip, other.ip) && port == other.port;
    }

}
