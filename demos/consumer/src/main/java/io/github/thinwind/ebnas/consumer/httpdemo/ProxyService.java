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

import java.io.IOException;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * TODO ProxyService说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-15  17:14
 *
 */
@Service
public class ProxyService {

    @Autowired
    CloseableHttpClient httpclient;

    @PreDestroy
    public void release() {
        try {
            httpclient.close();
        } catch (IOException e) {
            // Just ignore
            e.printStackTrace();
        }
    }

    public String getString(String url) throws Exception {
        final HttpGet httpget = new HttpGet(url);
        try (final CloseableHttpResponse response = httpclient.execute(httpget)) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        }
    }

    public String postString(String url)
            throws Exception {
        final HttpPost httppost = new HttpPost(url);
        try (final CloseableHttpResponse response = httpclient.execute(httppost)) {
            return EntityUtils.toString(response.getEntity(), "UTF-8");
        } catch (Exception e) {
            throw e;
        }
    }
}
