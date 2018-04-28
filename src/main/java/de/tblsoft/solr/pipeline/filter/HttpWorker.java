package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by tblsoft on 25.12.16.
 */
public class HttpWorker implements Callable<Document> {

    private CloseableHttpClient httpclient;

    private Document document;

    private String urlField;

    private String userAgent;

    public HttpWorker(Document document, CloseableHttpClient httpclient, String urlField, String userAgent) {
        this.document = document;
        this.httpclient = httpclient;
        this.urlField = urlField;
        this.userAgent = userAgent;
    }

    public Document call() throws Exception {
        String url = document.getFieldValue(urlField);
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent",userAgent);


        CloseableHttpResponse response = null;
        try {
            long start = System.currentTimeMillis();
            //CloseableHttpClient client = HttpClients.createDefault();
            //response = client.execute(httpGet);
            response = httpclient.execute(httpGet);
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append(EntityUtils.toString(response.getEntity()));
            response.close();
            long duration = System.currentTimeMillis() - start;

            for(Header header : response.getAllHeaders()) {
                document.addField("http_header_" + header.getName(), header.getValue());
                document.addField("headernames", header.getName());
            }

            document.setField("http_size", String.valueOf(responseBuilder.length()));
            document.setField("http_duration", String.valueOf(duration));
            document.setField("http_code", String.valueOf(response.getStatusLine().getStatusCode()));
            document.setField("http_payload", responseBuilder.toString());
            document.setField("http_time", DateUtils.date2String(new Date()));
        } catch (IOException e) {
            document.addField("errormessage", e.getMessage());
        }
        return document;

    }
}
