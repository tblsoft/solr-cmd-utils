package de.tblsoft.solr.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.tblsoft.solr.parser.SolrXmlParser;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

/**
 *
 */
public class SolrFeeder extends SolrXmlParser {


    private List<String> ignoreFields = new ArrayList<String>();

    private HttpSolrServer server;

    private SolrInputDocument inputDoc = new SolrInputDocument();



    public SolrFeeder(String server) {
        this.server = new HttpSolrServer(server);

    }

    public void doFeed() throws Exception {
        server.deleteByQuery("*:*");
        parse();
        server.commit();
        server.optimize();
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
}
