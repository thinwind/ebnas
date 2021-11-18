/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import static com.alibaba.nacos.client.utils.LogUtils.NAMING_LOGGER;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.common.Constants;
import com.alibaba.nacos.client.naming.net.HttpResult;
import com.alibaba.nacos.client.naming.utils.IoUtils;
import com.alibaba.nacos.client.utils.LogUtils;
import com.alibaba.nacos.client.utils.StringUtils;
import com.google.common.net.HttpHeaders;

/**
 *
 * http 请求工具
 * 
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-08  15:11
 *
 */
public class ClusterHttpUtil {

    public static final int TIME_OUT_MILLIS = 50_000;
    public static final int CON_TIME_OUT_MILLIS = 3000;

    private static final String POST = "POST";
    private static final String PUT = "PUT";
    private static final String GET = "GET";

    public static HttpResult httpGet(String url) {
        return httpGet(url, null, null);
    }

    public static HttpResult httpGet(String url, List<String> headers,
            Map<String, String> paramValues) {
        return request(url, headers, paramValues, "UTF-8", GET);
    }

    public static HttpResult httpGet(String url, List<String> headers,
            Map<String, String> paramValues, String encoding) {
        return request(url, headers, paramValues, encoding, GET);
    }

    public static HttpResult request(String url, List<String> headers,
            Map<String, String> paramValues, String encoding, String method) {
        HttpURLConnection conn = null;
        try {
            String encodedContent = encodingParams(paramValues, encoding);
            url += (StringUtils.isEmpty(encodedContent)) ? "" : ("?" + encodedContent);

            conn = (HttpURLConnection) new URL(url).openConnection();

            setHeaders(conn, headers, encoding);
            conn.setConnectTimeout(CON_TIME_OUT_MILLIS);
            conn.setReadTimeout(TIME_OUT_MILLIS);
            conn.setRequestMethod(method);
            conn.setDoOutput(true);
            conn.connect();
            if (POST.equals(method) || PUT.equals(method)) {
                byte[] b = encodedContent.getBytes();
                conn.setRequestProperty("Content-Length", String.valueOf(b.length));
                conn.getOutputStream().write(b, 0, b.length);
                conn.getOutputStream().flush();
                conn.getOutputStream().close();
            }
            NAMING_LOGGER.debug("Request from server: " + url);
            return getResult(conn);
        } catch (Exception e) {
            try {
                if (conn != null) {
                    NAMING_LOGGER.warn("failed to request " + conn.getURL() + " from "
                            + InetAddress.getByName(conn.getURL().getHost()).getHostAddress());
                }
            } catch (Exception e1) {
                NAMING_LOGGER.error("[NA] failed to request ", e1);
                //ignore
            }

            NAMING_LOGGER.error("[NA] failed to request ", e);

            return new HttpResult(500, e.toString(), Collections.<String, String>emptyMap());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static HttpResult getResult(HttpURLConnection conn) throws IOException {
        int respCode = conn.getResponseCode();

        InputStream inputStream;
        if (HttpURLConnection.HTTP_OK == respCode || HttpURLConnection.HTTP_NOT_MODIFIED == respCode
                || Constants.WRITE_REDIRECT_CODE == respCode) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Map<String, String> respHeaders =
                new HashMap<String, String>(conn.getHeaderFields().size());
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            respHeaders.put(entry.getKey(), entry.getValue().get(0));
        }

        String encodingGzip = "gzip";

        if (encodingGzip.equals(respHeaders.get(HttpHeaders.CONTENT_ENCODING))) {
            inputStream = new GZIPInputStream(inputStream);
        }

        return new HttpResult(respCode, IoUtils.toString(inputStream, getCharset(conn)),
                respHeaders);
    }

    private static String getCharset(HttpURLConnection conn) {
        String contentType = conn.getContentType();
        if (StringUtils.isEmpty(contentType)) {
            return "UTF-8";
        }

        String[] values = contentType.split(";");
        if (values.length == 0) {
            return "UTF-8";
        }

        String charset = "UTF-8";
        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        return charset;
    }

    private static void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext();) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }

        conn.addRequestProperty("Content-Type", "application/json;charset=" + encoding);
        conn.addRequestProperty("Accept-Charset", encoding);
    }

    private static String encodingParams(Map<String, String> params, String encoding)
            throws UnsupportedEncodingException {
        if (null == params || params.isEmpty()) {
            return "";
        }

        params.put("encoding", encoding);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (StringUtils.isEmpty(entry.getValue())) {
                continue;
            }

            sb.append(entry.getKey()).append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }

        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Pair<Set<String>, Set<String>> getClusterhouses(String localClusterName,
            String url) {
        Set<String> localClusters = new HashSet<>();
        Set<String> remoteClusters = new HashSet<>();;
        try {
            HttpResult result = httpGet(url);
            if (HttpURLConnection.HTTP_OK == result.code) {
                ClusterHttpResult chResult =
                        JSON.parseObject(result.content, ClusterHttpResult.class);
                if (chResult.isSuccess()) {
                    List<Cluster> clusters = chResult.getData();
                    for (Cluster cluster : clusters) {
                        String clusterName = cluster.getName();
                        if (localClusterName.equals(clusterName)) {
                            for (ClusterNode node : cluster.getNodes()) {
                                localClusters.add(node.getIp() + ":" + node.getPort());
                            }
                        } else {
                            for (ClusterNode node : cluster.getNodes()) {
                                remoteClusters.add(node.getIp() + ":" + node.getPort());
                            }
                        }
                    }
                } else {
                    LogUtils.NAMING_LOGGER.warn(
                            "Cluster House Result Error, Error code: {}, Error message:{}",
                            chResult.getErrorCode(), chResult.getErrorMessage());
                }
            } else {
                LogUtils.NAMING_LOGGER.warn(
                        "Reuqest to Cluster House Error, Error code: {}, Error message:{}",
                        result.code, result.content);
            }
        } catch (Exception e) {
            //Just ignore
            LogUtils.NAMING_LOGGER.warn("Reuqest to Cluster House Error", e);
        }

        return Pair.of(localClusters, remoteClusters);
    }

    public static void main(String[] args) {
        String url = "http://127.0.0.1:7031/openapi/clusters?delay=0";
        String localClusterName = "demo1";
        Pair<Set<String>, Set<String>> pair = getClusterhouses(localClusterName, url);
        
        System.out.println(pair.left.stream().collect(Collectors.joining(",")));
        System.out.println(pair.right.stream().collect(Collectors.joining(",")));
    }
}
