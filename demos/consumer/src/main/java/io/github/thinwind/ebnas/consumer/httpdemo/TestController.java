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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * TODO PostController说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-06-24  16:54
 *
 */
@RestController
public class TestController {

    @Autowired
    TestService testService;
    
    @Autowired
    ProxyService proxyService;

    @GetMapping("/get-provider2")
    public Object getProvider2() throws Exception {
        String url = "http://provider2/get-mock";
        return testService.getString(url);
    }
    
    
    @PostMapping("/post-provider1")
    public Object postProvider1() throws Exception {
        String url = "http://provider1/post-mock";
        return testService.postString(url);
    }

    @PostMapping("/post-proxy")
    public Object postProvider1Proxy() throws Exception {
        String url = "http://provider1:7031/proxy/post-mock";
        return proxyService.postString(url);
    }
    
    @GetMapping("/get-proxy")
    public Object getProvider2Proxy() throws Exception {
        String url = "http://provider2:7031/proxy/get-mock";
        return proxyService.getString(url);
    }
    
}
