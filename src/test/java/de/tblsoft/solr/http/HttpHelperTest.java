package de.tblsoft.solr.http;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by tblsoft on 10.03.17.
 */
public class HttpHelperTest {


    @Test
    public void removeQueryParameterTest() {
        String url = "http://www.tblsoft.de/foo/bar?foo=bar";
        String actual = HTTPHelper.removeQueryParameter(url);
        Assert.assertEquals("http://www.tblsoft.de/foo/bar", actual);
    }

    @Test
    public void removeQueryParameterWithEndingSlashTest() {
        String url = "http://www.tblsoft.de/foo/bar/?foo=bar";
        String actual = HTTPHelper.removeQueryParameter(url);
        Assert.assertEquals("http://www.tblsoft.de/foo/bar/", actual);
    }

    @Test
    public void removeQueryParameterWithoutQueryStringTest() {
        String url = "http://www.tblsoft.de/foo/bar";
        String actual = HTTPHelper.removeQueryParameter(url);
        Assert.assertEquals("http://www.tblsoft.de/foo/bar", actual);
    }

    @Test
    public void removeQueryParameterWithoutQueryStringAndEndingSlashTest() {
        String url = "http://www.tblsoft.de/foo/bar/";
        String actual = HTTPHelper.removeQueryParameter(url);
        Assert.assertEquals("http://www.tblsoft.de/foo/bar/", actual);
    }

    @Test
    public void removeQueryParameterNullTest() {
        String url = null;
        String actual = HTTPHelper.removeQueryParameter(url);
        Assert.assertNull( actual);
    }

}
