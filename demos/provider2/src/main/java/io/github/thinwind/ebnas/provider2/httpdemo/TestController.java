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
package io.github.thinwind.ebnas.provider2.httpdemo;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
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

    @GetMapping("/get-mock")
    public Object getMock() throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put("recommend", "光大科技《黑客马拉松》定制版贷款");
        result.put("discount", "今天办理享受利息8折");
        result.put("promote", "您要了解永明保险吗？");
        return result;
    }
}
