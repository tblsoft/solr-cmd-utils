package com.quasiris.qsc.reader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import de.tblsoft.solr.pipeline.AbstractReader;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.filter.SimpleMapping;
import de.tblsoft.solr.util.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FeedingQueueReader extends AbstractReader {

    private static Logger LOG = LoggerFactory.getLogger(FeedingQueueReader.class);

    private Map<String, List<String>> mapping;

    @Override
    public void read() {

        SimpleMapping simpleMapping = new SimpleMapping(getPropertyAsList("mapping", new ArrayList<String>()), getPropertyAsList("config", new ArrayList<String>()));
        mapping = simpleMapping.getMapping();

        String url = getProperty("url", null);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            InputStream inputStream = IOUtils.getInputStream(url);
            FeedingQueue[] feedingQueueList = objectMapper.readValue(inputStream, FeedingQueue[].class);

            for(FeedingQueue feedingQueue : feedingQueueList ) {

                 try {
                    if ("delete".equals(feedingQueue.getOperation())) {
                        Document document = new Document();
                        document.setField("id", feedingQueue.getDocumentId());
                        document.setField("operation", "delete");
                        executer.document(document);
                    } else {
                        Document document = parseJsonDocument(feedingQueue.getPayload());
                        document.setField("id", feedingQueue.getDocumentId());
                        document.setField("batchId", feedingQueue.getBatchId());
                        document.setField("processId", executer.getProcessId());
                        executer.document(document);
                    }
                } catch (Exception e) {
                    LOG.error("could not map document with id " + feedingQueue.getId() +
                            " with operation: " + feedingQueue.getOperation() +
                            " payload: " + feedingQueue.getPayload());
                    throw e;
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private Document parseJsonDocument(String json) {
        DocumentContext documentContext = JsonPath.parse(json);
        Document document = new Document();
        for(Map.Entry<String, List<String>> mappingEntry : mapping.entrySet()) {
            try {
                Object parsedValue = documentContext.read(mappingEntry.getKey());
                for(String target: mappingEntry.getValue()) {
                    document.setField(target, parsedValue);
                }
            } catch (PathNotFoundException e) {
                //ignore
            }
        }
        return document;

    }


}
