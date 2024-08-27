package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class RegexReplaceFilterTest  extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("regex","[^a-zA-Z0-9]");
        putProperty("replacement", "");
        addProperty("fields", "data");
        setClazz(RegexReplaceFilter.class);
    }

    @Test
    public void testRegexReplace() {
        configure();

        List<String> data = new ArrayList<>();
        data.add("foo ...... bar");
        data.add("alice ___bob");
        createField("data", data);
        runTest();
        assertFiledList("data",
                "foobar", "alicebob");
    }

}


