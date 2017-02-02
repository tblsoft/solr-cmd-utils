package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class FieldSplitterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("sourceField", "name");
        setClazz(FieldSplitter.class);
    }

    @Test
    public void testFieldSplitterFilter() {
        configure();
        createField("name", "John, Doe");
        runTest();
        assertFiledList("name", "John", " Doe");

    }

    @Test
    public void testFieldSplitterFilterWithTrimValues() {
        configure();
        putProperty("trimValues", "true");
        createField("name", "John, Doe");
        runTest();
        assertFiledList("name", "John", "Doe");

    }
}
