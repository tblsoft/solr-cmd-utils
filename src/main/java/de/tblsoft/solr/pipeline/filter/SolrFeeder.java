package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SolrFeeder extends AbstractFilter {


    private List<String> ignoreFields = new ArrayList<String>();

    private SolrClient server;

    private SolrInputDocument inputDoc = new SolrInputDocument();

    private String serverUrl;

    private int queueSize = 1;

    private int threads = 1;

    @Override
    public void init() {
        queueSize = getPropertyAsInt("queueSize", 1);
        threads = getPropertyAsInt("threads", 1);
        serverUrl = getProperty("serverUrl", null);
        ignoreFields = getPropertyAsList("ignoreFields", null);
        if(serverUrl == null) {
            throw new RuntimeException("You must configure a solr server url.");
        }

        this.server = new ConcurrentUpdateSolrClient(serverUrl, queueSize, threads);
        try {
            server.deleteByQuery("*:*");
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    public void end() {
        try {
            server.commit();
            server.optimize();
            server.close();
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void field(String name, String value) {

        if (!isFieldIgnored(name)) {
            inputDoc.addField(name, value);
        }
    }

    private boolean isFieldIgnored(String name) {
        for (String pattern : ignoreFields) {
            if (name.matches(pattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void endDocument() {
        try {
            server.add(inputDoc);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        inputDoc = new SolrInputDocument();

    }

    public void setIgnoreFields(List<String> ignoreFields) {
        this.ignoreFields = ignoreFields;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public void setThreads(int threads) {
        this.threads = threads;
    }
}
