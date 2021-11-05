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
package io.github.thinwind.clusterhouse.domain;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;

/**
 *
 * TODO Cluster说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-03  19:02
 *
 */
public class Cluster {

    @Getter
    private final String name;

    @Getter
    private Set<ClusterNode> nodes = new HashSet<>();

    private static final ConcurrentHashMap<String, Cluster> cache = new ConcurrentHashMap<>();

    private Cluster(String name) {
        this.name = name;
    }

    public static Cluster getInstance(String name) {
        Cluster cluster = cache.get(name);
        if (cluster == null) {
            cluster = new Cluster(name);
            cache.putIfAbsent(name, cluster);
            cluster = cache.get(name);
        }
        return cluster;
    }

    public static Map<String, Cluster> snapshot() {
        return new HashMap<>(cache);
    }

    public static void clear(String name) {
        cache.remove(name);
    }

    public void destroy() {
        clear(this.name);
    }

    public void addNode(ClusterNode node) {
        this.nodes.add(node);
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
}
