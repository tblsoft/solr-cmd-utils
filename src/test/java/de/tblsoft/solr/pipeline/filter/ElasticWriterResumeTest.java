package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.resume.ResumeStatusDTO;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElasticWriterResumeTest {

    private final ResumeStatusDTO status = new ResumeStatusDTO();

    private final PipelineExecuter executer = new PipelineExecuter("unused") {
        @Override
        public Boolean getResumeable() {
            return true;
        }

        @Override
        public ResumeStatusDTO getResumeStatus() {
            return status;
        }

        @Override
        public void saveResumeStatus() {
        }
    };

    public ElasticWriterResumeTest() {
        status.setContext(new HashMap<>());
    }

    @Test
    public void savedIndexIsRestoredOnResume() {
        status.getContext().put("ElasticWriter.indexUrl", "http://localhost:1/saved");

        Assert.assertEquals("http://localhost:1/saved", writer("http://localhost:1/first/_doc").getIndexUrl());
    }

    @Test
    public void flushSendsTheBufferOnce() {
        List<String> payloads = new ArrayList<>();
        ElasticWriter writer = new ElasticWriter() {
            @Override
            protected void sendBulkPayload(String bulkPayload) {
                payloads.add(bulkPayload);
            }
        };
        init(writer, "http://localhost:1/first/_doc");
        Document document = new Document();
        document.setId("1");
        document.setField("title", "value");
        writer.document(document);
        Assert.assertTrue(payloads.isEmpty());

        writer.flush();
        writer.flush();

        Assert.assertEquals(1, payloads.size());
    }

    private ElasticWriter writer(String location) {
        ElasticWriter writer = new ElasticWriter();
        init(writer, location);
        return writer;
    }

    private void init(ElasticWriter writer, String location) {
        Filter config = new Filter();
        config.setProperty(new HashMap<>(Map.of("location", location, "delete", "false", "idField", "id")));
        writer.setFilterConfig(config);
        writer.setNextFilter(new LastFilter());
        writer.setPipelineExecuter(executer);
        writer.init();
    }
}
