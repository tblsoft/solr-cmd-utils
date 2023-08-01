package de.tblsoft.solr.pipeline.filter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by tblsoft on 25.12.16.
 */
public class ExternalFilterWorker implements Callable<List<Document>> {

    private static Logger LOG = LoggerFactory.getLogger(ExternalFilterWorker.class);

    private CloseableHttpClient httpclient;

    private List<Document> documents;

    private String userAgent;

    private String url;

    private ObjectMapper objectMapper = new ObjectMapper();


    public ExternalFilterWorker(List<Document> documents,
                                CloseableHttpClient httpclient,
                                String userAgent,
                                String url
                   ) {
        this.documents = documents;
        this.httpclient = httpclient;
        this.userAgent = userAgent;
        this.url = url;

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    }

    public List<Document> call() throws Exception {
        CloseableHttpResponse response = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("User-Agent",userAgent);

            String jsonPayload = objectMapper.writeValueAsString(documents);
            StringEntity requestEntity = new StringEntity(jsonPayload, ContentType.APPLICATION_JSON);
            httpPost.setEntity(requestEntity);

            long start = System.currentTimeMillis();
            response = httpclient.execute(httpPost);

            documents = objectMapper.readValue(EntityUtils.toString(response.getEntity()), new TypeReference<List<Document>>() {});
            response.close();
            long duration = System.currentTimeMillis() - start;

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            //document.addField("errormessage", e.getMessage());
        }
        return documents;

    }
}
