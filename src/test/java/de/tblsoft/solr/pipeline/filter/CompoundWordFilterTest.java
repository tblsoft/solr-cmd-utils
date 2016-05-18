package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.DocumentBuilder;
import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 17.05.16.
 */
public class CompoundWordFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(CompoundWordFilter.class);
    }

    @Test
    public void testCompoundWordFilter() {
        configure();
        document(
                DocumentBuilder.document().field("noun","Maurer").create(),
                DocumentBuilder.document().field("noun","Hammer").create(),
                DocumentBuilder.document().field("noun","Maurerhammer").create()
        );

        assertFiled("noun", "maurerhammer");
        assertFiledList("compound", "maurer", "hammer");

    }

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(0);
    }


    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertNumberOfDocuments(0);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
