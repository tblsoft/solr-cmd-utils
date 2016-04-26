package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class FieldJoinerTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("outputField","name");
        putProperty("output", "${firstname} ${lastname}");
        setClazz(FieldJoiner.class);
    }

    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertFiled("foo","bar");
        assertNumberOfDocuments(1);
        assertNumberOfFields(2);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
    }

    @org.junit.Test
    public void testJoinerFilter() {
        createField("firstname", "John");
        createField("lastname", "Doe");
        runTest();
        assertFiled("name", "John Doe");

    }
}
