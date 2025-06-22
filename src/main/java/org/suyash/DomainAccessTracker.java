package org.suyash;

public class DomainAccessTracker {
    private long crawlStartMillis = 0;
    private long lastAccessMillis = 0;
    private boolean inWindow = false;

    public synchronized boolean canCrawl() {
        long now = System.currentTimeMillis();

        if (!inWindow) {
            if (lastAccessMillis == 0 || now - lastAccessMillis >= Constants.WAIT_TIME_THRESHOLD) {
                crawlStartMillis = now;
                inWindow = true;
                lastAccessMillis = now;
                return true;
            } else {
                return false;
            }
        } else {
            // In active crawl window
            if (now - crawlStartMillis <= Constants.POLITENESS_THRESHOLD) {
                lastAccessMillis = now;
                return true;
            } else {
                // 30s window expired
                inWindow = false;
                lastAccessMillis = now;
                return false;
            }
        }
    }

    public synchronized void reset() {
        crawlStartMillis = 0;
        lastAccessMillis = 0;
        inWindow = false;
    }
}

