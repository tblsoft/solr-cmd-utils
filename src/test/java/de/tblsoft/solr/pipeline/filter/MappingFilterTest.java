package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class MappingFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(MappingFilter.class);
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
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(0);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testJoin() {
        configure();
        addProperty("mapping", "join:name=${firstname} ${lastname}");
        createField("firstname", "John");
        createField("lastname", "Doe");
        runTest();
        assertFiled("name", "John Doe");
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
    }

    @Test
    public void testMapping() {
        configure();
        addProperty("mapping", "firstname->forename");
        addProperty("mapping", "lastname->surname");
        createField("foo", "bar");
        createField("firstname", "John");
        createField("lastname", "Doe");
        runTest();
        assertFiled("forename", "John");
        assertFiled("surname", "Doe");
        assertNumberOfDocuments(1);
        assertNumberOfFields(2);
    }
}
