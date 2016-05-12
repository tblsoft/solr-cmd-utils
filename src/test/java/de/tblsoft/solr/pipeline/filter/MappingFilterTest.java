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
    
    @Test
    public void testMappingTrimLowercaseMd5() {
        configure();
        addProperty("mapping", "noun->noun|trim|lowercase|md5");
        createField("noun", " Substantiv ");
        runTest();
        assertFiled("noun", "0f194a56105d09847fffcdf8c9eaf979");
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
    }
    
    @Test
    public void testMappingForMultipleFields() {
        configure();
        addProperty("mapping", "noun->title");
        addProperty("mapping", "noun->id|lowercase");
        addProperty("mapping", "noun->noun|trim|lowercase|md5");
        createField("noun", " Substantiv ");
        runTest();
        assertFiled("title", " Substantiv ");
        assertFiled("id", " substantiv ");
        assertFiled("noun", "0f194a56105d09847fffcdf8c9eaf979");
        assertNumberOfDocuments(1);
        assertNumberOfFields(3);
    }
    
    
    @Test(expected=IllegalArgumentException.class)
    public void testMappingFunctionNotImplemented() {
        configure();
        addProperty("mapping", "noun->noun|lowercase|md5|unknown");
        createField("noun", "Substantiv");
        runTest();
    }
    
    
}
