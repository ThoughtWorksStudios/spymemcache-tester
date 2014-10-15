package com.thoughtworks.mingle.com.thoughtworks.spymemcache;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import java.util.Set;

public class SetWorker extends Worker implements Runnable {

    public static final int VALUE_SIZE = 2048;
    public static final int KEY_SIZE = 32;

    private final Set<String> keys;

    public SetWorker(Set<String> keys) {
        this.keys = keys;
    }

    @Override
    public void run() {
        MemcachedClient client = getNewClient();
        long start = -1;
        long duration = -1;

        while(true) {
            String key = RandomStringUtils.randomAlphabetic(KEY_SIZE);
            start = System.currentTimeMillis();
            client.set(key, 60000, RandomStringUtils.randomAlphabetic(VALUE_SIZE));
            duration = System.currentTimeMillis() - start;
            if (duration > 50L) {
                System.out.println(String.format("[%s] Setting value for %s took %dms", Thread.currentThread().getName(), key, duration));
            }
            keys.add(key);
            System.out.println(String.format("[%s] There are now %d keys.", Thread.currentThread().getName(), keys.size()));
            try {
                Thread.sleep(10 + RandomUtils.nextInt(10));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
