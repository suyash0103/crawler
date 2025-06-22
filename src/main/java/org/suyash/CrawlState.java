package org.suyash;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CrawlState implements Serializable {

    public Set<String> visited;

    @JsonIgnore
    public Queue<String> toVisit;

    public AtomicInteger pagesCrawled;

    public long totalTimeInSeconds;

    public ConcurrentMap<String, AtomicInteger> inlinksForTopDomainsFromURLS;

    public ConcurrentMap<String, AtomicInteger> inlinksForHostsFromURLs;

    public ConcurrentMap<String, ConcurrentHashMap<String, AtomicInteger>> inlinksForTopDomainsFromHosts;

    public ConcurrentMap<String, ConcurrentHashMap<String, AtomicInteger>> inlinksForHostsFromHosts;

    public ConcurrentMap<String, Set<String>> outlinksForDomain;

}
