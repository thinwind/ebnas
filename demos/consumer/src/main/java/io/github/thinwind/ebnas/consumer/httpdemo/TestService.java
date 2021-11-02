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
package io.github.thinwind.ebnas.consumer.httpdemo;

import java.util.concurrent.ExecutionException;
import java.net.URI;
import java.net.URISyntaxException;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * TODO TestService说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-02  11:24
 *
 */
@Service
public class TestService {

    @Autowired
    NamingService namingService;

    public String getString(String url) throws URISyntaxException, NacosException {
        URI uri = new URI(url);
        String serviceName = uri.getHost();

        Instance provider = namingService.selectOneHealthyInstance(serviceName);
        String hostName = provider.getIp();
        int port = provider.getPort();
        String targetUri = uri.getRawPath();
        return AsyncHttpUtil.get(hostName, port, targetUri);
    }
    
    public String postString(String url) throws URISyntaxException, NacosException, InterruptedException, ExecutionException {
        URI uri = new URI(url);
        String serviceName = uri.getHost();

        Instance provider = namingService.selectOneHealthyInstance(serviceName);
        String hostName = provider.getIp();
        int port = provider.getPort();
        String targetUri = uri.getRawPath();
        return AsyncHttpUtil.post(hostName, port, targetUri);
    }
}
