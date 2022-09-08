package com.quasiris.qsc.writer;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quasiris.qsc.writer.dto.Header;
import com.quasiris.qsc.writer.dto.QscFeedingDocument;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DocumentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by tblsoft on 27.02.21.
 * Push documents to the qsc push API.
 */
public class QscDataPushWriter extends AbstractFilter {
    private static Logger LOG = LoggerFactory.getLogger(QscDataPushWriter.class);

    private ObjectMapper objectMapper = new ObjectMapper();

    private int batchSize = 100;
    private String idField = "id";
    private String url;
    private List<String> header;

    @Override
    public void init(){

        url = getProperty("url", null);
        header = getPropertyAsList("header", new ArrayList<>());
        batchSize = getPropertyAsInt("batchSize", 100);
        idField = getProperty("idField", "id");

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        String fullFeedStartUrl = url.replace("bulk/qsc", "fullfeed/start");
        HTTPHelper.post(fullFeedStartUrl, "{}", header);
        LOG.info("Full feed start: {}", fullFeedStartUrl);

        super.init();
    }


    private List<QscFeedingDocument> batch = new ArrayList<>();

    @Override
    public void document(Document document) {
        QscFeedingDocument qscFeedingDocument = convert(document);
        batch.add(qscFeedingDocument);
        if(batch.size() > batchSize) {
            sendBatch(batch);
            batch = new ArrayList<>();
        }
    }


    public QscFeedingDocument convert(Document document) {
        QscFeedingDocument feedingDocument = new QscFeedingDocument();
        Map<String, Object> outputDocument = DocumentMapper.toMap(document);
        feedingDocument.setPayload(outputDocument);
        Header header = new Header();
        header.setAction("update");

        String id = document.getFieldValue(idField);
        if(id == null) {
            id = UUID.randomUUID().toString();
        }
        header.setId(id);
        feedingDocument.setHeader(header);
        return feedingDocument;
    }

    public void sendBatch(List<QscFeedingDocument> data) {
        try {
            String dataAsString = objectMapper.writeValueAsString(data);
            HTTPHelper.post(url, dataAsString, header);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void end() {
        sendBatch(batch);
        batch = new ArrayList<>();

        String fullFeedEndUrl = url.replace("bulk/qsc", "fullfeed/end");
        HTTPHelper.post(fullFeedEndUrl, "{}", header);
        LOG.info("Full feed end: {}", fullFeedEndUrl);
        super.end();
    }

}
