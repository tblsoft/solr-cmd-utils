package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 17.05.16.
 */
public class LinkCheckerTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(LinkCheckerFilter.class);
    }

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(3);
        assertFiled("status","NOURL");
        assertFiled("httpCode","0");

    }



    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertFiled("foo","bar");
        assertFiled("status","NOURL");
        assertFiled("httpCode","0");
        assertNumberOfDocuments(1);
        assertNumberOfFields(4);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @org.junit.Test
    public void testLinkCheckerFilter() {
        configure();
        createField("url", "http://example.org");
        runTest();
        assertFiled("url", "http://example.org");
        assertFiled("status", "OK");
        assertFiled("httpCode", "200");
    }

    @org.junit.Test
    public void testLinkCheckerFilterForRedirect() {
        configure();
        createField("url", "https://quasiris.de");
        runTest();
        assertFiled("url", "https://quasiris.de");
        assertFiled("status", "REDIRECT");
        assertFiled("httpCode", "301");

    }
}
