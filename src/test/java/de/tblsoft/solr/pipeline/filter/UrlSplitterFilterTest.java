package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class UrlSplitterFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("urlField","url");
        putProperty("fieldPrefix", "url_");
        setClazz(UrlSplitter.class);
    }



    @Test
    public void testUrlSplitterFilter() {
        configure();
        createField("url", "http://localhost:8983/solr/mycore/select?q=*:*&fq=foo:bar&fq=collection:test");
        runTest();
        assertFiled("url", "http://localhost:8983/solr/mycore/select?q=*:*&fq=foo:bar&fq=collection:test");
        assertFiled("url__host", "localhost");
        assertFiled("url__path", "/solr/mycore/select");
        assertFiled("url_q", "*:*");
        assertFiledList("url_fq", "foo:bar","collection:test");

    }
}
