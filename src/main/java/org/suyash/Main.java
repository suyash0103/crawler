package org.suyash;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {

        if (args.length == 0) {
            logger.info("No URLs in input. Stopping...");
            return;
        }

        Set<String> urls = new HashSet<>();
        boolean resume = false;
        int maxPagesToCrawl = 0;

        for (String arg : args) {
            if (arg.startsWith("--resume=")) {
                resume = Boolean.parseBoolean(arg.split("=")[1]);
                continue;
            }

            if (arg.startsWith("--maxpages=")) {
                maxPagesToCrawl = Integer.parseInt(arg.split("=")[1]);
                continue;
            }

            if (arg.startsWith("--urls=")) {
                String allURLs = arg.split("=")[1];
                String[] urlArray = allURLs.split(",");
                if (urlArray.length == 0) {
                    logger.info("No URL input provided");
                    return;
                }

                if (urlArray.length > Constants.MAX_INPUT_ALLOWED) {
                    logger.error("Number of input URLs should be less than {}", Constants.MAX_INPUT_ALLOWED);
                    return;
                }

                for (String url : urlArray) {
                    if (URLHelper.isValidUrl(url)) {
                        urls.add(url);
                    }
                }
            }
        }

        CrawlState state = new CrawlState();
        try {
            if (resume && PersistState.stateExists()) {
                state = PersistState.load();
                logger.info("Crawl state loaded. Resuming crawling...");
            } else {
                state = new CrawlState();
                state.visited = ConcurrentHashMap.newKeySet();
                state.toVisit = new ConcurrentLinkedQueue<>();
                state.inlinksForTopDomainsFromURLS = new ConcurrentHashMap<>();
                state.inlinksForHostsFromURLs = new ConcurrentHashMap<>();
                state.inlinksForHostsFromHosts = new ConcurrentHashMap<>();
                state.inlinksForTopDomainsFromHosts = new ConcurrentHashMap<>();
                state.outlinksForDomain = new ConcurrentHashMap<>();
                state.pagesCrawled = new AtomicInteger(0);
            }
            state.toVisit.addAll(urls);
        } catch (Exception e) {
            logger.error("Error while verifying state: ", e);
        }

        int numThreads = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        List<Future<?>> futures = new ArrayList<>();

        try {
            for (int i = 0; i < numThreads; i++) {
                futures.add(executor.submit(new CrawlWorker(state, maxPagesToCrawl)));
            }
        } catch (Exception e) {
            logger.error("Exception during crawl ", e);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                logger.error("Exception during getting future", e);
            }
        }

        executor.shutdown();
    }
}