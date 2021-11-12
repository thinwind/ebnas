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
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import org.apache.hc.client5.http.async.methods.AbstractBinResponseConsumer;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleRequestProducer;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpException;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.nio.AsyncResponseConsumer;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.ByteArrayBuffer;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;

/**
 *
 * TODO AsyncPostUtil说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-06-24  15:24
 *
 */
public class AsyncHttpUtil {
    public static final CloseableHttpAsyncClient httpClient;

    public static final int RESPONSE_INIT_CAP = 128;

    static final Charset UTF8 = Charset.forName("UTF-8");

    static {
        RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(null)
                .setConnectTimeout(90000, TimeUnit.MICROSECONDS)
                .setDefaultKeepAlive(10, TimeUnit.SECONDS).build();
        final IOReactorConfig ioReactorConfig =
                IOReactorConfig.custom().setSoTimeout(Timeout.ofSeconds(10)).build();
        PoolingAsyncClientConnectionManager cm = new PoolingAsyncClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(200);
        httpClient = HttpAsyncClients.custom().disableConnectionState().disableCookieManagement()
                .disableAuthCaching().disableRedirectHandling()
                .setDefaultRequestConfig(requestConfig).setIOReactorConfig(ioReactorConfig)
                .setRetryStrategy(
                        new DefaultHttpRequestRetryStrategy(3, TimeValue.ofMilliseconds(500)))
                .setConnectionManager(cm).build();
        httpClient.start();
    }

    public static String post(String hostName, int port, String uri)
            throws InterruptedException, ExecutionException {
        return new String(executeHttp(HttpPost.METHOD_NAME, hostName, port, uri), UTF8);
    }

    public static String get(String hostName, int port, String uri) {
        return new String(executeHttp(HttpGet.METHOD_NAME, hostName, port, uri), UTF8);
    }

    static byte[] executeHttp(String method, String hostName, int port, String uri) {
        HttpHost host = new HttpHost(hostName, port);
        SimpleHttpRequest simpleHttpRequest = new SimpleHttpRequest(method, host, uri);
        Future<byte[]> future = httpClient.execute(SimpleRequestProducer.create(simpleHttpRequest),
                responseConsumer(), null);
        try {
            return future.get();
        } catch (Exception e) {
            e.printStackTrace();
            return e.toString().getBytes(UTF8);
        }
    }

    static AsyncResponseConsumer<byte[]> responseConsumer() {
        return new AbstractBinResponseConsumer<byte[]>() {
            private final ByteArrayBuffer buffer = new ByteArrayBuffer(RESPONSE_INIT_CAP);

            @Override
            public void releaseResources() {
                buffer.clear();
            }

            @Override
            protected void start(HttpResponse response, ContentType contentType)
                    throws HttpException, IOException {
                //just do nothing
            }

            @Override
            protected byte[] buildResult() {
                return buffer.toByteArray();
            }

            @Override
            protected int capacityIncrement() {
                return Integer.MAX_VALUE;
            }

            @Override
            protected void data(ByteBuffer src, boolean endOfStream) throws IOException {
                if (src == null) {
                    return;
                }
                if (src.hasArray()) {
                    buffer.append(src.array(), src.arrayOffset() + src.position(), src.remaining());
                } else {
                    while (src.hasRemaining()) {
                        buffer.append(src.get());
                    }
                }
            }

        };
    }

}

