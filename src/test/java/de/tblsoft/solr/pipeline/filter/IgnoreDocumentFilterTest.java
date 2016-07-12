package de.tblsoft.solr.pipeline.filter;

import org.junit.Test;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;

/**
 * Created by tblsoft on 02.06.16.
 */
public class IgnoreDocumentFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        setClazz(IgnoreDocumentFilter.class);
    }
    
    void configureDateTests() {
    	//yyyy-MM-dd'T'HH:mm:ssZ
        putProperty("fromDate", "2016-06-02T13:00:00+0200");
        putProperty("toDate", "2016-06-02T14:00:00+0200");
        putProperty("dateField", "datetest");
        configure();
    	
    }

    @Test
    public void testIgnoreDocumentFilterForDateAfter() {
    	configureDateTests();
        createField("datetest", "2016-06-02T14:30:00+0200");
        createField("foo", "bar");
        runTest();

        assertNumberOfDocuments(0);
    }
    

    @Test
    public void testIgnoreDocumentFilterForDateBefore() {
    	configureDateTests();
        createField("datetest", "2016-06-02T12:30:00+0200");
        createField("foo", "bar");
        runTest();

        assertNumberOfDocuments(0);
    }
    
    @Test
    public void testIgnoreDocumentFilterForDate() {
    	configureDateTests();
        createField("datetest", "2016-06-02T13:30:00+0200");
        createField("foo", "bar");
        runTest();
        assertFiled("foo", "bar");
        assertFiled("datetest", "2016-06-02T13:30:00+0200");

        assertNumberOfDocuments(1);
        assertNumberOfFields(2);

        assertInitWasDelegated();
        assertEndWasDelegated();
    }
}
