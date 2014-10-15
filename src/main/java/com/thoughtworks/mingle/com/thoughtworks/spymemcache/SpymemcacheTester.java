package com.thoughtworks.mingle.com.thoughtworks.spymemcache;

import net.spy.memcached.MemcachedClient;
import org.apache.commons.lang.RandomStringUtils;


import javax.print.DocFlavor;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.atomic.AtomicLong;

public class SpymemcacheTester {

    private static final int WRITER_COUNT = 2;
    private static final int READER_COUNT = 5;

    /**
     * Set the env variable MEMCACHE_ENDPOINTS to a comma-separated list of your endpoints.
     * Run it!
     * Ctrl+C to see the summary timings
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        final Set<String> keys = new ConcurrentSkipListSet<String>();
        final ConcurrentHashMap<Long, AtomicLong> timings = new ConcurrentHashMap<Long, AtomicLong>();

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("");
                System.out.println("");
                System.out.println("");
                System.out.println(Thread.currentThread().getName());
                System.out.println("=============================");
                System.out.println("GET timings");
                System.out.println("=============================");

                List<Long> allTimes = new ArrayList<Long>(timings.keySet());
                Collections.sort(allTimes);

                for(Long time : allTimes) {
                    System.out.println(String.format("[%dms] x %d", time, timings.get(time).get()));
                }

            }
        }));

        for(int i=0; i<WRITER_COUNT; i++) {
            new Thread(new SetWorker(keys), "MemcacheSetWorker#" + i).start();
        }
        
        for(int i=0; i<READER_COUNT; i++) {
            new Thread(new GetWorker(keys, timings), "MemcacheGetWorker#" + i).start();
        }

        while(true) {
            Thread.sleep(60000);
        }

    }



}
