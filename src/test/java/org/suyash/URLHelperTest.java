package org.suyash;

import org.junit.jupiter.api.Test;

public class URLHelperTest {

    @Test
    public void testIsValidURL1() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.com/privacy/policy/?entry_point=facebook_page_footer");
        assert isValid;
    }

    @Test
    public void testIsValidURL2() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.host");
        assert !isValid;
    }

    @Test
    public void testIsValidURL3() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.live");
        assert !isValid;
    }

    @Test
    public void testIsValidURL4() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.wang");
        assert !isValid;
    }

    @Test
    public void testIsValidURL5() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.icu");
        assert !isValid;
    }

    @Test
    public void testIsValidURL6() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.gq");
        assert !isValid;
    }

    @Test
    public void testIsValidURL7() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.buzz");
        assert !isValid;
    }

    @Test
    public void testIsValidURL8() {
        boolean isValid = URLHelper.isValidUrl("https://www.facebook.tk");
        assert !isValid;
    }

    @Test
    public void testIsMaliciousUrl1() {
        boolean isValid = URLHelper.isMaliciousUrl("https://www.google.com/");
        assert !isValid;
    }

    @Test
    public void testIsMaliciousUrl2() {
        boolean isValid = URLHelper.isMaliciousUrl("https://1.2.3.4/");
        assert isValid;
    }

    @Test
    public void testIsMaliciousUrl3() {
        boolean isValid = URLHelper.isMaliciousUrl("https://www.1.2.3.4/");
        assert isValid;
    }

    @Test
    public void testIsMaliciousUrl4() {
        boolean isValid = URLHelper.isMaliciousUrl("https://www.googlegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegooglegoogle.com/");
        assert isValid;
    }

    @Test
    public void testGetTopLevelDomain1() {
        String tld = URLHelper.getTopDomain("https://www.google.com/");
        assert tld.equals("google.com");
    }

    @Test
    public void testGetTopLevelDomain2() {
        String tld = URLHelper.getTopDomain("https://www.maps.google.com/");
        assert tld.equals("google.com");
    }

}
