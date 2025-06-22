package org.suyash;

class CrawlWorker implements Runnable {
    private final Crawler crawler;

    public CrawlWorker(CrawlState state, int maxPagesToCrawl) {
        this.crawler = new Crawler(state, maxPagesToCrawl);
    }

    @Override
    public void run() {
        while (true) {
            int ret = crawler.beginCrawl();
            if (ret == 0) {
                return;
            }
        }
    }
}

