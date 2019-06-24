package de.tblsoft.solr.http;

import org.junit.Assert;
import org.junit.Test;

import java.net.URISyntaxException;

/**
 * Created by tblsoft on 30.04.16.
 */
public class ElasticHelperTest {

    @Test
    public void guessLongTest() {
        String dataType = ElasticHelper.guessDatatype("100");
        Assert.assertEquals("long", dataType);
    }

    @Test
    public void guessDoubleTest() {
        String dataType = ElasticHelper.guessDatatype("100.1");
        Assert.assertEquals("double", dataType);
    }

    @Test
    public void guessDataTypeNullInputTest() {
        String dataType = ElasticHelper.guessDatatype(null);
        Assert.assertEquals("string", dataType);

        dataType = ElasticHelper.guessDatatype("");
        Assert.assertEquals("string", dataType);

        dataType = ElasticHelper.guessDatatype("str");
        Assert.assertEquals("string", dataType);
    }



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
    public void getTypeFromUrlTest() throws URISyntaxException {
        String expected = "bar";

        String actual = ElasticHelper.getTypeFromUrl("http://localhost/foo/bar");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getTypeFromUrl("http://localhost/foo/bar/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getTypeFromUrl("http://localhost/foo/bar/?");
        Assert.assertEquals(expected, actual);
    }

    
    @Test(expected=URISyntaxException.class)
    public void getTypeFromUrlTestWithNoType() throws URISyntaxException {
      ElasticHelper.getTypeFromUrl("http://localhost/foo");
    }
    
    @Test(expected=URISyntaxException.class)
    public void getTypeFromUrlTestWithSlashAndNoType() throws URISyntaxException {
    	ElasticHelper.getTypeFromUrl("http://localhost/foo/");
    }
    
    @Test
    public void getIndexFromUrlTest() throws URISyntaxException {
        String expected = "foo";
        String actual = ElasticHelper.getIndexFromUrl("http://localhost/foo");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexFromUrl("http://localhost/foo/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexFromUrl("http://localhost/foo/bar");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexFromUrl("http://localhost/foo/bar/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getIndexFromUrl("http://localhost/foo/bar/?");
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
    
    @Test
    public void getBulkUrlTest() throws URISyntaxException {
        String expected = "http://localhost/_bulk";
        String actual = ElasticHelper.getBulkUrl("http://localhost/foo");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getBulkUrl("http://localhost/foo/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getBulkUrl("http://localhost/foo/bar");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getBulkUrl("http://localhost/foo/bar/");
        Assert.assertEquals(expected, actual);

        actual = ElasticHelper.getBulkUrl("http://localhost/foo/bar/?");
        Assert.assertEquals(expected, actual);
    }
}
