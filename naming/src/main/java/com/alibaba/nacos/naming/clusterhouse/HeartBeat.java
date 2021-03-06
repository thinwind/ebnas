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
package com.alibaba.nacos.naming.clusterhouse;

import java.util.HashMap;
import java.util.Map;
import java.nio.charset.Charset;
import javax.annotation.PostConstruct;
import com.alibaba.nacos.core.utils.InetUtils;
import com.alibaba.nacos.naming.misc.HttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *
 * TODO HeartBeat说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-13  19:30
 *
 */
@Component
public class HeartBeat {

    private final static Map<String, String> NAME_MAPPINGS = new HashMap<>();

    static {
        NAME_MAPPINGS.put("credit", "信用卡服务集群");
        NAME_MAPPINGS.put("ebank", "总行服务集群");
        NAME_MAPPINGS.put("yongming", "永明服务集群");
    }

    @Value("${ebnas.cluster-name}")
    String clusterName;

    @Value("${ebnas.clusterhouse}")
    String clusterhouseAddresses;

    String[] urls;

    byte[] data;

    @Value("${server.port}")
    int port;

    @PostConstruct
    public void init() {
        String[] chaddrs = clusterhouseAddresses.split(",");
        urls = new String[chaddrs.length];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = "http://" + chaddrs[i] + "/openapi/register/";
        }

        String title = NAME_MAPPINGS.get(clusterName);
        if(title == null){
            title = clusterName;
        }
        String template =
                "{\"clusterName\":\"{{clustername}}\",\"ip\":\"{{ip}}\",\"port\":{{port}}}";
        String ip = InetUtils.getSelfIp();
        String portStr = String.valueOf(port);
        template = template.replace("{{clustername}}", title);
        template = template.replace("{{ip}}", ip);
        template = template.replace("{{port}}", portStr);
        data = template.getBytes(Charset.forName("UTF-8"));
        System.out.println(template);
    }

    @Scheduled(initialDelay = 3000, fixedDelay = 10_000)
    public void heartbeat() {
        for (String url : urls) {
            try {
                HttpClient.asyncHttpPostLarge(url, null, data, null);
            } catch (Exception e) {
                // Just ignore
                e.printStackTrace();
            }
        }

    }
}
