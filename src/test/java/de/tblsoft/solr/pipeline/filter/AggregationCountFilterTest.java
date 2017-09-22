package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 22.09.17.
 */
public class AggregationCountFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(AggregationCountFilter.class);
    }

    @Override
    public void testPassthrough() {
    }

    @Override
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(0);
    }

    @Test
    public void testAggregationFilter() {
        configure();
        Document document1 = DocumentBuilder.document().field("field1", "value1").create();
        Document document2 = DocumentBuilder.document().field("field1", "value1").field("field2","value2").create();
        document(document1, document2);
        print();
        assertFiled(0, "value", "value2");
        assertFiled(0, "count", "1");
        assertFiled(0, "type", "field2");

        assertFiled(1, "value", "value1");
        assertFiled(1, "count", "2");
        assertFiled(1, "type", "field1");

        assertNumberOfDocuments(2);
        assertNumberOfFields(3);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
