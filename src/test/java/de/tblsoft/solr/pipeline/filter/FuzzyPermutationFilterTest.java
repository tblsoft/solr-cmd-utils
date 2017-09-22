package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;

/**
 * Created by tblsoft on 26.04.16.
 */
public class FuzzyPermutationFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("input", "token");
        putProperty("output", "permutations");
        putProperty("distance", "1");
        setClazz(FuzzyPermutationFilter.class);
    }

    @org.junit.Test
    public void testDateFilter() {
        configure();
        createField("token", "original");
        runTest();
        assertFiledList("permutations", "oriinal");

    }
}
