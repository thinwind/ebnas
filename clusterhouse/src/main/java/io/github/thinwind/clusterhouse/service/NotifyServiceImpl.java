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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.tuple.Pair;
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

    // private List<CountDownLatch> locks = new Vector<>();
    
    private ConcurrentMap<Long, CountDownLatch> locks = new ConcurrentHashMap<>();
    
    private final AtomicLong CURSOR = new AtomicLong(0);
    
    @Override
    public Pair<Long, CountDownLatch> getLock() {
        Long current = CURSOR.getAndIncrement();
        CountDownLatch lock = new CountDownLatch(1);
        locks.put(current, lock);
        return Pair.of(current, lock);
    }

    @Override
    public void releaseLock() {
        ConcurrentMap<Long, CountDownLatch> target = locks;
        locks = new ConcurrentHashMap<>();
        for (CountDownLatch lock : target.values()) {
            lock.countDown();
        }
    }

    @Override
    public void releaseLock(Long id) {
        CountDownLatch lock = locks.remove(id);
        if (lock != null) {
            lock.countDown();
        }
    }
    
}
