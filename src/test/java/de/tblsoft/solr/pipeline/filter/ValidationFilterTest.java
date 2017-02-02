package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 8.01.17.
 */
public class ValidationFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        addProperty("requiredFields","title");
        setClazz(ValidationFilter.class);
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

    @Test
    public void testValidationFilter() {
        configure();
        createField("firstname", "John");
        createField("lastname", "Doe");
        runTest();
        assertFiled("validation", "title_missingRequired");

    }
}
