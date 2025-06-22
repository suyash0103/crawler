package org.suyash;

import java.util.List;

public class Constants {

    // TLDs of malicious URLs. False positives are possible. These TLDs have the most % of malicious URLs.
    // https://www.cybercrimeinfocenter.org/top-20-tlds-by-malicious-phishing-domains
    public static final List<String> MALICIOUS_TLD = List.of(
            "host",
            "live",
            "wang",
            "icu",
            "gq",
            "buzz",
            "tk");

    public static final String BASE64_REGEX = "^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?$";

    public static final int CRAWL_STATE_SAVE_THRESHOLD = 10;

    public static final int MAX_INPUT_ALLOWED = 9;

    public static final String STATE_FILE = "crawl_state.ser";

    public static final String DATA_STORAGE_FILE = "data.json";

    public static final long POLITENESS_THRESHOLD = 60 * 1000L; // 60 seconds

    public static final long WAIT_TIME_THRESHOLD = 10 * 1000L; // 10 seconds

    public static final int NUM_THREADS = 3;
}
