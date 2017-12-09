package de.tblsoft.solr.pipeline.filter;

import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;


public class ElasticWriterTest {
    @Test
    public void createExpandedValue1Level() throws Exception {
        // given
        String flatName = "value.de";
        String value = "testval";

        // when
        Map expandedValue = ElasticWriter.createExpandedValue(flatName, value);

        // then
        assertEquals(expandedValue.get("de"), value);
    }

    @Test
    public void createExpandedValue3Level() throws Exception {
        // given
        String flatName = "value.abc.def.ghi";
        String value = "testval";

        // when
        Map expandedValue = ElasticWriter.createExpandedValue(flatName, value);

        // then
        assertEquals(((Map<String, Object>)((Map<String, Object>)expandedValue.get("abc")).get("def")).get("ghi"), value);
    }
}