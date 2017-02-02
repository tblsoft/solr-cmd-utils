package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
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

    private String serverUrl;

    private int queueSize = 1;

    private int threads = 1;

    private String user;

    private String password;

    boolean deleteIndex = false;

    @Override
    public void init() {
        queueSize = getPropertyAsInt("queueSize", 1);
        threads = getPropertyAsInt("threads", 1);
        serverUrl = getProperty("serverUrl", null);
        ignoreFields = getPropertyAsList("ignoreFields", null);
        deleteIndex = getPropertyAsBoolean("deleteIndex", false);
        user = getProperty("user", null);
        password = getProperty("password", null);

        if(serverUrl == null) {
            throw new RuntimeException("You must configure a solr server url.");
        }

        BasicAuthConcurrentUpdateSolrClient basicAuthserver = new BasicAuthConcurrentUpdateSolrClient(serverUrl, queueSize, threads);
        basicAuthserver.setBasicAuthCredentials(user,password);
        this.server = basicAuthserver;
        try {
            if(deleteIndex) {
                System.out.println("Delete the index.");
                server.deleteByQuery("*:*");
            }
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
    public void document(Document document) {
        SolrInputDocument inputDoc = new SolrInputDocument();
        for(Field field: document.getFields()) {
            if (!isFieldIgnored(field.getName())) {
                inputDoc.addField(field.getName(), field.getValues());
            }
        }

        try {
            server.add(inputDoc);
        } catch (SolrServerException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        super.document(document);
    }


    private boolean isFieldIgnored(String name) {
        if(ignoreFields == null) {
            return false;
        }
        for (String pattern : ignoreFields) {
            if (name.matches(pattern)) {
                return true;
            }
        }
        return false;
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

    public void setDeleteIndex(boolean deleteIndex) {
        this.deleteIndex = deleteIndex;
    }
}
