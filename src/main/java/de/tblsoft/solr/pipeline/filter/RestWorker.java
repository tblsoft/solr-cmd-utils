package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public class RestWorker implements Callable<Document> {
    public static class RestRequest {
        String url;
        String method;
        List<String> headers;
        Object payload;

        public RestRequest(String url, String method, List<String> headers, Object payload) {
            this.url = url;
            this.method = method;
            this.headers = headers;
            this.payload = payload;
        }
    }

    private Document doc;
    private CloseableHttpClient httpclient;
    private RestRequest request;
    private String responsePrefix;
    private FilterIF filter;

    public RestWorker(Document doc, CloseableHttpClient httpclient, RestRequest request, String responsePrefix, FilterIF filter) {
        this.doc = doc;
        this.httpclient = httpclient;
        this.request = request;
        this.responsePrefix = responsePrefix;
        this.filter = filter;
    }

    public Document call() throws Exception {
        HttpUriRequest httpUriRequest = buildRequest();
        CloseableHttpResponse response = null;
        try {
            long start = System.currentTimeMillis();
            response = httpclient.execute(httpUriRequest);

            if(responsePrefix != null) {
                enrichDocWithResponse(response);

                long duration = System.currentTimeMillis() - start;
                doc.setField(responsePrefix+"duration", String.valueOf(duration));
            }
        } catch (IOException e) {
            if(responsePrefix != null) {
                doc.addField(responsePrefix+"error_msg", e.getMessage());
            }
        } finally {
            if(response != null) {
                try {
                    response.close();
                } catch (IOException ignored) {
                }
            }
        }

        filter.document(doc);

        return doc;
    }

    protected void enrichDocWithResponse(HttpResponse response) throws IOException {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(EntityUtils.toString(response.getEntity()));

        for (Header header : response.getAllHeaders()) {
            doc.addField(responsePrefix+"header_" + header.getName(), header.getValue());
            doc.addField(responsePrefix+"headers", header.getName());
        }

        doc.setField(responsePrefix+"size", String.valueOf(responseBuilder.length()));
        doc.setField(responsePrefix+"code", String.valueOf(response.getStatusLine().getStatusCode()));
        doc.setField(responsePrefix+"payload", responseBuilder.toString());
        doc.setField(responsePrefix+"time", DateUtils.date2String(new Date()));
    }

    protected HttpUriRequest buildRequest() {
        RequestBuilder requestBuilder = RequestBuilder
                .create(request.method)
                .setUri(request.url);
        for (String header : request.headers) {
            String[] split = header.split(":");
            if(split.length == 2) {
                requestBuilder.addHeader(split[0], split[1]);
            }
        }

        if(request.payload != null && request.payload instanceof String) {
            ByteArrayEntity jsonEntity = null;
            try {
                jsonEntity = new ByteArrayEntity(request.payload.toString().getBytes("UTF-8"));
                requestBuilder.setEntity(jsonEntity);
            } catch (UnsupportedEncodingException ignored) {
            }
        }

        return requestBuilder.build();
    }
}
