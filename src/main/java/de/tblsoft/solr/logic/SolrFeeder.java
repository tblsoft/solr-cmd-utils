package de.tblsoft.solr.logic;

import de.tblsoft.solr.parser.SolrXmlParser;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.*;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Deprecated
public class SolrFeeder extends SolrXmlParser {

    private static Logger LOG = LoggerFactory.getLogger(SolrFeeder.class);



    private List<String> ignoreFields = new ArrayList<String>();

    private SolrClient server;

    private SolrInputDocument inputDoc = new SolrInputDocument();

    private String serverUrl;

    private int queueSize = 1;

    private int threads = 1;

    private boolean deleteIndex = false;

    public SolrFeeder(String server) {
        this.serverUrl = server;


    }

    public void doFeed() throws Exception {
        this.server = new ConcurrentUpdateHttp2SolrClient.Builder(serverUrl,
                new Http2SolrClient.Builder().build(), true)
                .withQueueSize(queueSize)
                .withThreadCount(threads)
                .build();
        if(threads==1 && queueSize == 1) {
            this.server = new HttpJdkSolrClient.Builder(serverUrl)
                    .build();
        }

        if(deleteIndex) {
            LOG.info("Delete the index.");
            server.deleteByQuery("*:*");
        }

        parse();
        server.commit();
        server.optimize();
        server.close();
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

    public void setDeleteIndex(boolean deleteIndex) {
        this.deleteIndex = deleteIndex;
    }
}
