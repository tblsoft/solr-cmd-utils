package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;

/**
 * Created by tblsoft on 26.04.16.
 */
public class KeyValueSplitterFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("fieldName", "attributes");
        putProperty("delimiter", "|");
        putProperty("keyValueDelimiter", "=");
        putProperty("keyPrefix", "attr_");
        setClazz(KeyValueSplitterFilter.class);
    }

    @org.junit.Test
    public void testKeyValueFilter() {
        configure();
        createField("attributes", "|color=green|color=black|size of something %=10 cm|");
        runTest();
        assertFiledList("attr_color", "green", "black");
        assertFiled("attr_size_of_something_", "10 cm");

    }
}
