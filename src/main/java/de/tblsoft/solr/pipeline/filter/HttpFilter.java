package de.tblsoft.solr.pipeline.filter;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by tblsoft 25.12.16.
 */
public class HttpFilter extends AbstractFilter {

    private String urlField;

    private CloseableHttpClient httpclient;

    private String userAgent;

    private int threads = 1;

    private List<Document> documentQueue = new ArrayList<Document>();

    private ExecutorService executor;

    @Override
    public void init() {
        urlField = getProperty("urlField", "url");
        userAgent = getProperty("userAgent", "Solr Cmd Utils Http Agent/1.0");
        //httpclient = HttpClients.createDefault();
        threads = getPropertyAsInt("threads", 1);

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(threads);
        cm.setDefaultMaxPerRoute(threads);

        httpclient = HttpClients.custom()
                .setConnectionManager(cm)
                .setMaxConnPerRoute(threads)
                .disableRedirectHandling()
                .build();


        ThreadFactory namedThreadFactory = new ThreadFactoryBuilder()
                .setNameFormat("tblsoft-http-filter-thread-%d").build();
        executor  = Executors.newFixedThreadPool(threads,namedThreadFactory);


        System.out.println("start http filter with threads: " + threads);

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

            HttpWorker worker = new HttpWorker(documentFromQueue, httpclient, urlField, userAgent);
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
        System.out.println("end HttpFilter");
        try {
            httpclient.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        executor.shutdown();
        super.end();
    }

}
