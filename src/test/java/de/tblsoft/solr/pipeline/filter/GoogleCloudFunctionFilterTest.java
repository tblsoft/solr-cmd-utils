package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class GoogleCloudFunctionFilterTest extends AbstractFilterTest {
    @Override
    public void configure() {
        putProperty("cloudFunctionUrl", "your-url-here");
        putProperty("authToken", "your-token-here");
        setClazz(GoogleCloudFunctionFilter.class);
    }

    @Ignore
    @Test
    public void testPassthrough() {
        configure();
        createField("foo", "bar");
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(2);
        assertFiled("enrichedField", "newValue");
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Ignore
    @Test
    public void testEmptyDocument() {
        configure();
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(1);
    }
}