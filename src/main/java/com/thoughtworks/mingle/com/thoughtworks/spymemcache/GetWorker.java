package com.thoughtworks.mingle.com.thoughtworks.spymemcache;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang.math.RandomUtils;


import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GetWorker extends Worker implements Runnable {

    private final Set<String> existingKeys;
    private final ConcurrentHashMap<Long, AtomicLong> timings;

    public GetWorker(Set<String> existingKeys, ConcurrentHashMap<Long, AtomicLong> timings) {
        this.existingKeys = existingKeys;
        this.timings = timings;
    }

    @Override
    public void run() {
        long start = -1L;
        long duration = -1L;

        MemcachedClient client = getNewClient();

        while(true) {
            int keyCount = 0;
            for(String existingKey : existingKeys) {
                start = System.currentTimeMillis();
                String value = client.get(existingKey).toString();
                duration = System.currentTimeMillis() - start;
                keyCount++;
                timings.putIfAbsent(duration, new AtomicLong(0L));
                int retryCounter = 0;
                while(!timings.replace(duration, timings.get(duration), new AtomicLong(timings.get(duration).incrementAndGet()))) {
                    if (retryCounter++ > 100) {
                        System.err.println(String.format("[%s] !!! Concurrency alert: gave up recording a timing because it took too many retries.", Thread.currentThread().getName()));
                        break;
                    }
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }

            }
            System.out.println(String.format("[%s] Checked %d keys. Starting over...", Thread.currentThread().getName(), keyCount));
            try {
                Thread.sleep(25 + RandomUtils.nextInt(25));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }
}
