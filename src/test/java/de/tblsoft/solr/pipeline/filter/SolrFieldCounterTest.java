package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class SolrFieldCounterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(SolrFieldCounter.class);
    }

    @Test
    public void testPassthrough() {

    }


    @Test
    public void testSolrFieldCounterFilter() {
        configure();
        Document document1 = DocumentBuilder.document().field("field1", "value1").create();
        Document document2 = DocumentBuilder.document().field("field1", "value1").field("field2","value2").create();
        document(document1, document2);
        assertFiled("field2", "1");
        assertFiled("field1", "2");
        assertNumberOfDocuments(1);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
