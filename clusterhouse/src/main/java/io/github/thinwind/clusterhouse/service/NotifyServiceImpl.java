/* 
 * Copyright 2021 Shang Yehua <niceshang@outlook.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.thinwind.clusterhouse.service;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import org.springframework.stereotype.Service;

/**
 *
 * TODO NotifyServiceImpl说明
 *
 * @author Shang Yehua <niceshang@outlook.com>
 * @since 2021-11-11  15:59
 *
 */
@Service
public class NotifyServiceImpl implements NotifyService {

    private List<CountDownLatch> locks = new Vector<>();
    
    @Override
    public CountDownLatch getLock() {
        CountDownLatch lock = new CountDownLatch(1);
        locks.add(lock);
        return lock;
    }

    @Override
    public void releaseLock() {
        List<CountDownLatch> target = locks;
        locks = new Vector<>();
        for (CountDownLatch lock : target){
            lock.countDown();
        }
    }
    
}
