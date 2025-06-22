# crawler

This project crawls over the web, given a few input URLs.

## Implementation Details
1. Number of input URLs allowed are <= 9
2. Runs in a multi threaded way. Threads are predefined in code, non-configurable. Threads are currently harcoded as 3
3. Stores following data in a JSON format:
   - Links of visited URLs
   - Total time (seconds) taken to crawl
   - In-link count of domains from unique URLs across the web. If URL is https://www.maps.google.com, domain in this aspect is google.com
   - In-link count of hosts from unique URLs across the web. If URL is https://www.maps.google.com, host in this aspect is www.maps.google.com
   - In-link count of domains from unique hosts across the web
   - In-link count of hosts from unique hosts across the web
   - Out-links going from a domain to other unique domains
4. The state of current progress is saved. If code stops abruptly, it can be resumed from previous saved state.
5. Arguments to the code
   - --urls=https://www.facebook.com/,https://www.google.com,https://www.nytimes.com/,https://www.wsj.com/ : Comma separated input urls
   - --maxpages=200 : Max pages to crawl. No contraints of min or max provided in code
   - --resume=false : Resume from previous saved state. Value can be true or false
6. Tries to not abuse domains. This crawler honors politeness while hitting domains. A domain is hit for maximum of 60 seconds continuously. If hit for 60 seconds, code waits for 10 seconds to hit the domain again. In the meanwhile, it hits other URLs.
7. Tries to detect malicious and invalid URLs and skips them.

## How to Run
1. If Intellij is available, use the existing Intellij Configurations.
2. If you want to run this from command line, use these commands from the top directory crawler:
   - mvn clean install
   - mvn clean package
   - mvn exec:java -Dexec.mainClass="org.suyash.Main" -Dexec.args="--urls=https://www.facebook.com/,https://www.google.com,https://www.nytimes.com/,https://www.wsj.com/ --resume=false --maxpages=200"


## Limitations
1. We won't know the inCount of base URLs.
2. We have limitation on url like google.com and google.org separately.
3. If program runs successfully and state is saved, and if we again start program to resume from saved state, it will not run as max pages are already crawled as per state data
