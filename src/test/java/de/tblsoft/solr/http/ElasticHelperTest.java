package de.tblsoft.solr.http;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by tblsoft on 30.04.16.
 */
public class ElasticHelperTest {

	
    @Test
    public void getScrollUrlTest() throws URISyntaxException {
        String expected = "http://localhost/_search/scroll";
        String actual = ElasticHelper.getScrollUrl("http://localhost/foo");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getScrollUrl("http://localhost/foo/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getScrollUrl("http://localhost/foo/bar");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getScrollUrl("http://localhost/foo/bar/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getScrollUrl("http://localhost/foo/bar/?");
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getIndexUrlTest() throws URISyntaxException {
        String expected = "http://localhost/foo";
        String actual = ElasticHelper.getIndexUrl("http://localhost/foo");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexUrl("http://localhost/foo/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexUrl("http://localhost/foo/bar");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexUrl("http://localhost/foo/bar/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexUrl("http://localhost/foo/bar/?");
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = URISyntaxException.class)
    public void getIndexUrlTestWithEmptyUrl() throws URISyntaxException {
        ElasticHelper.getIndexUrl("");
    }

    @Test(expected = URISyntaxException.class)
    public void getIndexUrlTestWithNullUrl() throws URISyntaxException {
        ElasticHelper.getIndexUrl(null);
    }

    @Test(expected = URISyntaxException.class)
    public void getIndexUrlTestInvalidElasticIndexUrl() throws URISyntaxException {
        ElasticHelper.getIndexUrl("http://localhost/");
    }
}
