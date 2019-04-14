package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class RestFilter extends AbstractFilter {

    private String url;
    private String method;
    private List<String> headers;
    private List<String> payload;
    private List<String> filters;
    private int timeout; // ms
    private int threads;
    private String responsePrefix;

    private CloseableHttpClient httpclient;
    private ExecutorService executor;
    private Gson gson;

    @Override
    public void init() {
        url = getProperty("url", null);
        verify(url, "For the RestFilter a url must be defined!");

        method = getProperty("method", "GET");
        payload = getPropertyAsList("payload", null);

        List<String> defaultHeaders = new ArrayList<String>();
        if(payload != null) {
            defaultHeaders.add("Content-type: application/json");
        }
        headers = getPropertyAsList("headers", defaultHeaders);
        filters = getPropertyAsList("filters", null);
        timeout = getPropertyAsInt("timeout", 10000);
        threads = getPropertyAsInt("threads", 1);
        responsePrefix = getProperty("responsePrefix", null);

        // Create httpclient
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(threads);
        cm.setDefaultMaxPerRoute(threads);

        httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .setMaxConnPerRoute(threads)
                .setDefaultRequestConfig(
                    RequestConfig.custom()
                            .setSocketTimeout(timeout)
                            .setConnectTimeout(timeout)
                            .setConnectionRequestTimeout(timeout)
                            .build()
                )
                .build();

        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("tblsoft-rest-filter-thread-%d").build();
        executor  = Executors.newFixedThreadPool(threads, namedThreadFactory);

        System.out.println("start http filter with threads: " + threads);

        gson = new GsonBuilder().create();

        super.init();
    }

    @Override
    public void document(Document doc) {
        if(filterMatch(doc)) {
            RestWorker.RestRequest request = buildRequest(doc);
            RestWorker worker = new RestWorker(doc, httpclient, request, responsePrefix, this.nextFilter);
            Future<Document> future = executor.submit(worker);
        } else {
            super.document(doc);
        }
    }

    protected boolean filterMatch(Document doc) {
        boolean match = true;
        for (String filter : filters) {
            String[] split = filter.split(":");
            if(split.length == 2) {
                String field = split[0];
                String value = split[1];
                String fieldValue = doc.getFieldValue(field);

                match &= StringUtils.equals(fieldValue, value); // filter support currently only AND operator
            }
        }

        return match;
    }

    protected RestWorker.RestRequest buildRequest(Document doc) {
        String urlWithParams = buildUrlWithParams(doc);
        Object payload = buildPayload(doc);
        RestWorker.RestRequest request = new RestWorker.RestRequest(urlWithParams, method, headers, payload);
        return request;
    }

    /**
     * Build url with params and path params
     */
    protected String buildUrlWithParams(Document doc) {
        String url = getUrl(doc);

        for (Field field : doc.getFields()) {
            String fieldPattern = "\\{"+field.getName()+"\\}";
            String urlEncodedValue = UrlUtil.encode(field.getValue());
            url = url.replaceAll(fieldPattern, urlEncodedValue);
        }

        return url;
    }

    /**
     * Return url from fieldname if exist or take configured url
     * @param doc with contain url
     * @return url from doc or config
     */
    protected String getUrl(Document doc) {
        return doc.getFieldValue(url, url);
    }

    protected String buildPayload(Document doc) {
        String jsonPayload = null;

        if(this.payload != null) {
            Map<String, Object> payloadMap = new HashMap();
            for (String fieldName : this.payload) {

                List<String> fieldValues = doc.getFieldValues(fieldName);
                if(fieldValues != null && fieldValues.size() > 1) {
                    payloadMap.put(fieldName, fieldValues);
                } else {
                    payloadMap.put(fieldName, fieldValues.get(0));
                }
            }
            jsonPayload = gson.toJson(payloadMap);
        }

        return jsonPayload;
    }

    @Override
    public void end() {
        try {
            httpclient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        executor.shutdown();
        super.end();
    }
}
