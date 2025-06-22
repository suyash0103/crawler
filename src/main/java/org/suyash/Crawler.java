package org.suyash;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Crawler {

    private static final Logger logger = LoggerFactory.getLogger(Crawler.class);

    private final ConcurrentMap<String, DomainAccessTracker> domainAccessMap = new ConcurrentHashMap<>();
    private String previousDomain = "";
    public int maxPagesToCrawl = 0;

    private final CrawlState crawlState;

    public Crawler(CrawlState state, int maxPagesToCrawl) {
        this.crawlState = state;
        this.maxPagesToCrawl = maxPagesToCrawl;
    }

    public synchronized int beginCrawl() {
        try {
            long startTime = System.currentTimeMillis();
            if (CollectionUtils.isEmpty(crawlState.toVisit)) {
                logger.info("URL Queue is empty. Stopping...");
                return 0;
            }

            while (!crawlState.toVisit.isEmpty()) {
                String url = crawlState.toVisit.poll();
                if (url == null || crawlState.visited.contains(url)) {
                    continue;
                }

                crawlState.visited.add(url);
                fetchAndExtractLinks(url);
                if (crawlState.pagesCrawled.get() >= maxPagesToCrawl) {
                    logger.info("Page crawling limit reached. Exiting...");
                    break;
                }
                int crawledSoFar = crawlState.pagesCrawled.incrementAndGet();
                logger.info("Crawled page #{}: {}", crawledSoFar, url);

                if (crawledSoFar % Constants.CRAWL_STATE_SAVE_THRESHOLD == 0) {
                    saveState();
                }
            }

            long endTime = System.currentTimeMillis();
            crawlState.totalTimeInSeconds = (endTime - startTime) / 1000;
            if (crawlState.pagesCrawled.get() > 0) {
                saveDataToFile();
            }
        } catch (Exception e) {
            logger.error("Exception occurred during crawling", e);
        }

        return 0;
    }

    private synchronized void fetchAndExtractLinks(String originURL) {
        try {
            Document doc = Jsoup.connect(originURL).userAgent("SimpleCrawler").get();
            String normalizedOriginURL = URLHelper.normalizeUrl(originURL);
            URI originUri = new URL(normalizedOriginURL).toURI();
            String originHost = originUri.getHost();
            String originTopDomain = URLHelper.getTopDomain(originURL);

            if (!StringUtils.equalsIgnoreCase(originTopDomain, previousDomain)) {
                previousDomain = originTopDomain;
                resetPreviousDomainTracker();
            }

            if (!shouldCrawl(originTopDomain)) {
                crawlState.toVisit.add(originTopDomain);
                crawlState.visited.remove(originURL);
                return;
            }

            Elements links = doc.select("a[href]");
            Set<String> visitedDomainsForCurrentURL = new HashSet<>();
            Set<String> visitedHostsForCurrentURL = new HashSet<>();

            for (Element link : links) {
                String destinationUrl = link.absUrl("href");
                String normalizedDestinationUrl = URLHelper.normalizeUrl(destinationUrl);

                if (URLHelper.isValidUrl(normalizedDestinationUrl)) {
                    if (!crawlState.visited.contains(normalizedDestinationUrl)) {
                        crawlState.toVisit.add(normalizedDestinationUrl);
                    }

                    handleInLinkForTopDomain(normalizedDestinationUrl, originHost, visitedDomainsForCurrentURL);

                    handleInLinkForHost(normalizedDestinationUrl, originHost, visitedHostsForCurrentURL);

                    handleOutLinkForOriginTopDomain(originTopDomain, normalizedDestinationUrl);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to fetch {}: {}", originURL, e.getMessage());
        }
    }

    private synchronized void handleInLinkForTopDomain(String normalizedDestinationUrl, String originHost, Set<String> visitedDomainsForCurrentURL) {
        String destinationTopDomain = URLHelper.getTopDomain(normalizedDestinationUrl);
        if (!visitedDomainsForCurrentURL.contains(destinationTopDomain)) {
            visitedDomainsForCurrentURL.add(destinationTopDomain);
            crawlState.inlinksForTopDomainsFromURLS.computeIfAbsent(destinationTopDomain, k -> new AtomicInteger()).incrementAndGet();
        }

        ConcurrentHashMap<String, AtomicInteger> innerMap =
                crawlState.inlinksForTopDomainsFromHosts.computeIfAbsent(destinationTopDomain, k -> new ConcurrentHashMap<>());
        innerMap.computeIfAbsent(originHost, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private synchronized void handleInLinkForHost(String normalizedDestinationUrl, String originHost,
                                     Set<String> visitedHostsForCurrentURL) throws MalformedURLException, URISyntaxException {
        URI destinationUri = new URL(normalizedDestinationUrl).toURI();
        String destinationHost = destinationUri.getHost();
        if (!visitedHostsForCurrentURL.contains(destinationHost)) {
            visitedHostsForCurrentURL.add(destinationHost);
            crawlState.inlinksForHostsFromURLs.computeIfAbsent(destinationHost, k -> new AtomicInteger()).incrementAndGet();
        }

        ConcurrentHashMap<String, AtomicInteger> innerMap =
                crawlState.inlinksForHostsFromHosts.computeIfAbsent(destinationHost, k -> new ConcurrentHashMap<>());
        innerMap.computeIfAbsent(originHost, k -> new AtomicInteger(0)).incrementAndGet();
    }

    private synchronized void handleOutLinkForOriginTopDomain(String originDomain, String normalizedDestinationUrl) {
        String destinationTopDomain = URLHelper.getTopDomain(normalizedDestinationUrl);
        Set<String> outlinks = crawlState.outlinksForDomain.computeIfAbsent(originDomain, k -> ConcurrentHashMap.newKeySet());
        outlinks.add(destinationTopDomain);
    }

    private synchronized void saveState() {
        try {
            PersistState.save(this.crawlState);
            logger.info("Saved crawl state");
        } catch (Exception e) {
            logger.error("Failed to save crawl state: {}", e.getMessage());
        }
    }

    public synchronized boolean shouldCrawl(String domain) {
        DomainAccessTracker tracker =
                domainAccessMap.computeIfAbsent(domain, k -> new DomainAccessTracker());
        return tracker.canCrawl();
    }

    public synchronized void resetPreviousDomainTracker() {
        DomainAccessTracker tracker =
                domainAccessMap.computeIfAbsent(previousDomain, k -> new DomainAccessTracker());
        tracker.reset();
    }

    private synchronized void saveDataToFile() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(new File(Constants.DATA_STORAGE_FILE), crawlState);
        } catch (Exception e) {
            logger.error("Failed to save crawl state: {}", e.getMessage());
        }
    }

}

/* Limitation:

*/