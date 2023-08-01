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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by tblsoft 27.7.23.
 *
 * Send a document to a endpoint, where the document can be processed.
 * The endpoint returns the document.
 *
 * This filter act like a external filter in the pipeline.
 * You can implement a lamda function in aws to implement custom filter logic.
 */
public class ExternalFilter extends AbstractFilter {

    private static Logger LOG = LoggerFactory.getLogger(ExternalFilter.class);


    private CloseableHttpClient httpclient;

    private String userAgent;
    private String url;


    private int threads = 1;
    private int batchSize = 10;

    private List<Document> documentQueue = new ArrayList<Document>();

    private ExecutorService executor;

    @Override
    public void init() {
        url = getProperty("url", null);
        userAgent = getProperty("userAgent", "Solr Cmd Utils Http Agent/1.0");
        //httpclient = HttpClients.createDefault();
        threads = getPropertyAsInt("threads", 1);
        batchSize = getPropertyAsInt("batchSize", 10);
        boolean redirectsEnabled = getPropertyAsBoolean("redirectsEnabled", false);

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
                .setNameFormat("tblsoft-external-filter-thread-%d").build();
        executor  = Executors.newFixedThreadPool(threads,namedThreadFactory);


        LOG.info("start http filter with threads: " + threads);

        super.init();

    }

    @Override
    public void document(Document document) {
        documentQueue.add(document);

        if(documentQueue.size() >= threads * batchSize) {
            processQueue();
        }
    }

    void addDocsToDocumentFutures(List<Document> docs, List<Future<List<Document>>> documentFutures) {
        ExternalFilterWorker worker = new ExternalFilterWorker(docs,
                httpclient, userAgent, url);
        Future<List<Document>> future = executor.submit(worker);
        documentFutures.add(future);
    }

    void processQueue() {

        List<Future<List<Document>>> documentFutures = new ArrayList<>();
        //executor  = Executors.newFixedThreadPool(threads);
        List<Document> docs = new ArrayList<>();
        for(Document documentFromQueue: documentQueue) {
            docs.add(documentFromQueue);
            if(docs.size() >= batchSize) {
                addDocsToDocumentFutures(docs, documentFutures);
                docs = new ArrayList<>();
            }
        }
        addDocsToDocumentFutures(docs, documentFutures);

        for(Future<List<Document>> documentFuture: documentFutures) {
            try {
                List<Document> documentList = documentFuture.get();
                for(Document d : documentList) {
                    super.document(d);
                }
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
        LOG.info("end ExternalFilter");
        try {
            httpclient.close();
        } catch (IOException e) {
            LOG.info(e.getMessage());
        }

        executor.shutdown();
        super.end();
    }

}
