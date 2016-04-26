package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class GrepFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("fieldName", "name");
        putProperty("pattern", ".*John.*");
        putProperty("shouldMatch", "true");
        setClazz(GrepFilter.class);
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

    @Test
    public void testShouldMatch() {
        configure();
        putProperty("shouldMatch", "true");
        createField("foo", "bar");
        createField("name", "John Doe");
        runTest();
        assertFiled("name", "John Doe");
        assertFiled("foo", "bar");

        assertNumberOfDocuments(1);
        assertNumberOfFields(2);

    }

    @Test
    public void testShouldNotMatch() {
        configure();
        putProperty("shouldMatch", "false");
        createField("foo", "bar");
        createField("name", "John Doe");
        runTest();

        assertNumberOfDocuments(0);

    }

}
