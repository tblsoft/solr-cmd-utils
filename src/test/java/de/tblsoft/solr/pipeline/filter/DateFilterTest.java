package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.test.AbstractFilterTest;

/**
 * Created by tblsoft on 26.04.16.
 */
public class DateFilterTest extends AbstractFilterTest {

    @Override
    public void configure() {
        putProperty("inputDateFormat", "yyyy.MM.dd");
        putProperty("outputDateFormat", "yyyy-MM-dd");
        putProperty("dateField", "date");
        setClazz(DateFilter.class);
    }

    @org.junit.Test
    public void testDateFilter() {
        configure();
        createField("date", "2016.04.26");
        runTest();
        assertFiled("date", "2016-04-26");

    }
}
