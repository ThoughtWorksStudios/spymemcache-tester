package com.thoughtworks.mingle.com.thoughtworks.spymemcache;

import net.spy.memcached.MemcachedClient;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.List;

public class Worker {

    protected MemcachedClient getNewClient() {
        final long start = System.currentTimeMillis();
        MemcachedClient c = null;
        try {
            c = new MemcachedClient(getEndpointAddresses());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        final long duration = System.currentTimeMillis() - start;
        System.out.println(String.format("[%s] opening memcache connection took %dms", Thread.currentThread().getName(), duration));
        return c;
    }

    private List<InetSocketAddress> getEndpointAddresses() {
        String commaSeparatedEndpoints = System.getenv("MEMCACHE_ENDPOINTS");
        String[] endpoints = commaSeparatedEndpoints.split(",");
        List<InetSocketAddress> endpointAddresses = new LinkedList<InetSocketAddress>();

        for (String endpoint : endpoints) {
            endpointAddresses.add(new InetSocketAddress(endpoint, 11211));
        }
        return endpointAddresses;
    }

}
