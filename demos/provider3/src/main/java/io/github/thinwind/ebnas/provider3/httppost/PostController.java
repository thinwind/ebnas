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
package io.github.thinwind.ebnas.provider3.httppost;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
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
public class PostController {

    @PostMapping("/post-mock")
    public Object postMock() throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put("recommend", "光大科技《黑客马拉松》定制版年金险");
        result.put("guarantee", "今日订购，终身锁定8%复利收益");
        result.put("p.s.", "满意请打100分哦");
        return result;
    }
}
