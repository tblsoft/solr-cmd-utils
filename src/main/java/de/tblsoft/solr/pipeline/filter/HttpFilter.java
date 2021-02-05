package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by tblsoft 25.12.16.
 */
public class HttpFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(HttpFilter.class);


    private String urlField;

    private CloseableHttpClient httpclient;

    private String userAgent;
    private String cacheBasePath;
    private String fileExtension;

    Duration cacheMaxAge = null;

    private int threads = 1;

    private List<Document> documentQueue = new ArrayList<Document>();

    private ExecutorService executor;

    @Override
    public void init() {
        urlField = getProperty("urlField", "url");
        userAgent = getProperty("userAgent", "Solr Cmd Utils Http Agent/1.0");
        cacheBasePath = getProperty("cacheBasePath", null);
        fileExtension = getProperty("fileExtension", ".gz");
        //httpclient = HttpClients.createDefault();
        threads = getPropertyAsInt("threads", 1);
        boolean redirectsEnabled = getPropertyAsBoolean("redirectsEnabled", false);

        // "PT1H30M" - https://en.wikipedia.org/wiki/ISO_8601#Durations
        String cacheMaxAgeString = getProperty("cacheMaxAge", null);
        if(cacheMaxAgeString != null) {
            cacheMaxAge = Duration.parse(cacheMaxAgeString);
        }


        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(threads);
        cm.setDefaultMaxPerRoute(threads);

        HttpClientBuilder httpClientBuilder = HttpClients.custom()
                .setConnectionManager(cm)
                .setMaxConnPerRoute(threads);


        if(!redirectsEnabled) {
            httpClientBuilder = httpClientBuilder.disableRedirectHandling();
        }

        httpclient = httpClientBuilder.build();


        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("tblsoft-http-filter-thread-%d").build();
        executor  = Executors.newFixedThreadPool(threads,namedThreadFactory);


        LOG.info("start http filter with threads: " + threads);

        super.init();

    }

    @Override
    public void document(Document document) {
        documentQueue.add(document);

        if(documentQueue.size() >= threads) {
            processQueue();
        }
    }

    void processQueue() {

        List<Future<Document>> documentFutures = new ArrayList<Future<Document>>();
        //executor  = Executors.newFixedThreadPool(threads);

        for(Document documentFromQueue: documentQueue) {

            HttpWorker worker = new HttpWorker(documentFromQueue,
                    httpclient, urlField, userAgent, cacheBasePath, fileExtension, cacheMaxAge);
            Future<Document> future = executor.submit(worker);
            documentFutures.add(future);


        }

        for(Future<Document> documentFuture: documentFutures) {
            try {
                Document d = documentFuture.get();
                super.document(d);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        documentQueue.clear();
    }

    @Override
    public void end() {
        processQueue();
        LOG.info("end HttpFilter");
        try {
            httpclient.close();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }

        executor.shutdown();
        super.end();
    }

}
