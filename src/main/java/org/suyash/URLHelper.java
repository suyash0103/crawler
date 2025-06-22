package org.suyash;

import com.google.common.net.InternetDomainName;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLHelper {

    private static final Logger logger = LoggerFactory.getLogger(URLHelper.class);

    public static boolean isValidUrl(String url) {
        try {
            if (StringUtils.isBlank(url)) {
                return false;
            }
            URI uri = new URL(url).toURI();
            if (uri.getScheme() == null || (!uri.getScheme().equalsIgnoreCase("http") &&
                    !uri.getScheme().equalsIgnoreCase("https"))) {
                return false;
            }

            return uri.getHost() != null && !isMaliciousUrl(url) && !isTLDMalicious(uri);
        } catch (Exception e) {
            logger.error("Invalid URL: {}, exception occurred {}", url, e.getMessage());
            return false;
        }
    }

    public static boolean isMaliciousUrl(String url) {
        try {
            URI uri = new URL(url).toURI();
            String host = uri.getHost();
            Pattern base64Pattern = Pattern.compile(Constants.BASE64_REGEX);
            Matcher matcher = base64Pattern.matcher(url);
            return host == null
                    || host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")
                    || host.length() > 253
                    || url.toLowerCase().contains("redirect")
                    || url.length() > 500
                    || matcher.find();
        } catch (Exception e) {
            logger.error("Exception occured while checking malicious url", e);
            return true;
        }
    }

    private static boolean isTLDMalicious(URI uri) {
        try {
            String host = uri.getHost();
            if (host == null) {
                return true;
            }

            String[] parts = host.toLowerCase().split("\\.");
            if (parts.length < 2) {
                return Constants.MALICIOUS_TLD.contains(host);
            }

            return Constants.MALICIOUS_TLD.contains(parts[parts.length - 1]);
        } catch (Exception e) {
            return true;
        }
    }

    public static String normalizeUrl(String url) {
        try {
            URI uri = new URI(url);
            URI normalizedUri = new URI(
                    uri.getScheme(),
                    uri.getAuthority(),
                    uri.getPath(),
                    uri.getQuery(),
                    null
            );
            return normalizedUri.toString();
        } catch (Exception e) {
            return url;
        }
    }

    public static String getTopDomain(String url) {
        try {
            URI uri = new URI(url);
            String host = uri.getHost();
            InternetDomainName internetDomainName = InternetDomainName.from(host).topPrivateDomain();
            return internetDomainName.toString();
        } catch (Exception e) {
            logger.error("Could not fetch TLD due to exception: {}", e.getMessage());
            return "";
        }
    }

}
