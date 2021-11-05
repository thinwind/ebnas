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
package io.github.thinwind.clusterhouse.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.github.thinwind.clusterhouse.domain.Cluster;
import io.github.thinwind.clusterhouse.domain.ClusterNode;
import io.github.thinwind.clusterhouse.dto.NodeDto;

/**
 *
 * TODO PostController说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-06-24  16:54
 *
 */
@RestController
@RequestMapping("/openapi")
public class ClusterNodeApi {

    @PostMapping("/register/")
    public Object register(@RequestBody NodeDto node) {
        Cluster cluster = Cluster.getInstance(node.getClusterName());
        ClusterNode clusterNode = new ClusterNode();
        clusterNode.setIp(node.getIp());
        clusterNode.setPort(node.getPort());
        cluster.addNode(clusterNode);
        return "OK";
    }

    @GetMapping("/clusters")
    public Object snapshot() {
        return Cluster.snapshot();
    }
}
