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
package com.alibaba.nacos.client.naming.net;

import java.util.Map;

/**
 *
 * TODO HttpResult说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-08  15:18
 *
 */
public class HttpResult {
    final public int code;
    final public String content;
    final private Map<String, String> respHeaders;

    public HttpResult(int code, String content, Map<String, String> respHeaders) {
        this.code = code;
        this.content = content;
        this.respHeaders = respHeaders;
    }

    public String getHeader(String name) {
        return respHeaders.get(name);
    }
}
