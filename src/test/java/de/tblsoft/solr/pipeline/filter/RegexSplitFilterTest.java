package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

/**
 * Created by tblsoft on 26.04.16.
 */
public class RegexSplitFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("regex","(.*) (.*)");
        putProperty("sourceField", "name");
        addProperty("destFieldList", "firstname");
        addProperty("destFieldList", "lastname");
        addProperty("notMatchedDestFieldList", "firstname");
        addProperty("notMatchedDestFieldList", "lastname");
        setClazz(RegexSplitFilter.class);
    }

    @Test
    public void testPassthrough() {
        createField("foo", "bar");
        runTest();
        assertFiled("foo","bar");
        assertNumberOfDocuments(1);
        assertNumberOfFields(3);
        assertInitWasDelegated();
        assertEndWasDelegated();
    }

    @Test
    public void testEmptyDocument() {
        runTest();
        assertNumberOfDocuments(1);
        assertNumberOfFields(2);
    }

    @Test
    public void testJoinerFilter() {
        createField("name", "John Doe");
        runTest();
        assertFiled("firstname", "John");
        assertFiled("lastname", "Doe");

    }
}
