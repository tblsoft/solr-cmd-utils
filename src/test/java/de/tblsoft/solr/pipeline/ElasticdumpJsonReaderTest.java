package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;


public class ElasticdumpJsonReaderTest {
    @Test
    public void readFileAsLines() throws Exception {
        // given
        String filepath = "examples/unittest/elasticdump_export.txt";

        // when
        List<String> jsonLines = ElasticdumpJsonReader.readFileAsLines(filepath);

        // then
        assertEquals(3, jsonLines.size());
    }

    @Test
    public void parseJsonLineAsDoc() throws Exception {
        // given
        String jsonLine = "{\"_index\":\"example\",\"_type\":\"doc\",\"_id\":\"123-456-789\",\"_score\":1,\"_source\":{\"title\":\"Hello World!\",\"tags\":[\"tag1\", \"tag2\"],\"weight\":10.5}}";

        // when
        Document doc = ElasticdumpJsonReader.parseJsonLineAsDoc(jsonLine);

        // then
        assertNotNull(doc);
        assertEquals("example", doc.getFieldValue("_index"));
        assertEquals("doc", doc.getFieldValue("_type"));
        assertEquals("123-456-789", doc.getFieldValue("_id"));
        assertEquals("Hello World!", doc.getFieldValue("title"));
        assertEquals(Arrays.asList("tag1", "tag2"), doc.getFieldValues("tags"));
        assertEquals("10.5", doc.getFieldValue("weight"));
    }

    @Test
    public void parseJsonLineAsDocWithEmptyId() throws Exception {
        // given
        String jsonLine = "{\"_index\":\"example\",\"_type\":\"doc\",\"_score\":1,\"_source\":{\"title\":\"Hello World!\",\"tags\":[\"tag1\", \"tags2\"],\"weight\":10.5}}";

        // when
        Document doc = ElasticdumpJsonReader.parseJsonLineAsDoc(jsonLine);

        // then
        assertNotNull(doc);
        assertEquals("example", doc.getFieldValue("_index"));
        assertEquals("doc", doc.getFieldValue("_type"));
        assertEquals(null, doc.getFieldValue("_id"));
    }

    @Test
    public void parseJsonLineAsDocWithEmptySource() throws Exception {
        // given
        String jsonLine = "{\"_index\":\"example\",\"_type\":\"doc\",\"_score\":1}";

        // when
        Document doc = ElasticdumpJsonReader.parseJsonLineAsDoc(jsonLine);

        // then
        assertNotNull(doc);
        assertEquals("example", doc.getFieldValue("_index"));
        assertEquals("doc", doc.getFieldValue("_type"));
        assertEquals(null, doc.getFieldValue("_id"));
        assertEquals(3, doc.getFields().size());
    }
}