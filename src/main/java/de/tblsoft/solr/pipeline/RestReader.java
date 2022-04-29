package de.tblsoft.solr.pipeline;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.tblsoft.solr.http.GlobalHttpConfiguration;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.filter.RestWorker;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

/**
 * Read from restendpoint and store response in fields:
 * _rest_headers, _rest_payload, _rest_code
 */
public class RestReader extends AbstractReader {
    private static Logger LOG = LoggerFactory.getLogger(RestReader.class);

    private final static String responsePrefix = "_rest_";

    private String url;
    private String method;
    private String payload;
    private List<String> headers;
    private int timeout; // ms

    private boolean useGlobalHeaders;

    private CloseableHttpClient httpclient;
    private ExecutorService executor;

    @Override
    public void read() {
        // read params
        url = getProperty("url", null);
        method = getProperty("method", "GET");
        List<String> defaultHeaders = new ArrayList<String>();
        if(payload != null) {
            defaultHeaders.add("Content-type: application/json");
        }
        headers = getPropertyAsList("headers", defaultHeaders);
        timeout = getPropertyAsInteger("timeout", 10000L).intValue();
        useGlobalHeaders = getPropertyAsBoolean("useGlobalHeaders", false);

        if(useGlobalHeaders) {
            List<Header> globalHeaders = GlobalHttpConfiguration.getHeadersForEndpoint(url);
            for (Header globalHeader : globalHeaders) {
                String h = globalHeader.getName() + ":" + globalHeader.getValue();
                headers.add(h);
            }
        }

        int threads = 1;

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
        executor = Executors.newFixedThreadPool(threads, namedThreadFactory);

        LOG.info("start http filter with threads: " + threads);

        RestWorker.RestRequest request = new RestWorker.RestRequest(url, method, headers, payload);

        Document doc = new Document();
        RestWorker worker = new RestWorker(doc, httpclient, request, responsePrefix, null);
        Future<Document> future = executor.submit(worker);

        try {
            executer.document(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error during REST-call!", e);
        }
    }

    @Override
    public void end() {
        try {
            httpclient.close();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }

        executor.shutdown();
        super.end();
    }
}
