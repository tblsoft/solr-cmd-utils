package de.tblsoft.solr.pipeline.filter;

import com.google.common.base.Strings;
import org.apache.solr.client.solrj.SolrRequest;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateHttp2SolrClient;
import org.apache.solr.client.solrj.impl.Http2SolrClient;
import org.apache.solr.common.util.NamedList;

import java.io.IOException;

/**
 * Created by tblsoft on 26.01.16.
 */
public class BasicAuthConcurrentUpdateSolrClient extends ConcurrentUpdateHttp2SolrClient {

    private String user;

    private String password;

    public BasicAuthConcurrentUpdateSolrClient(String solrServerUrl, int queueSize, int threadCount) {
        super(new Builder(solrServerUrl, new Http2SolrClient.Builder().build(), true)
                .withQueueSize(queueSize)
                .withThreadCount(threadCount));
    }

    @Override
    public NamedList<Object> request(final SolrRequest request, String collection)
            throws SolrServerException, IOException {
        if(!Strings.isNullOrEmpty(user)) {
            request.setBasicAuthCredentials(user,password);
        }
        return super.request(request, collection);
    }

    public void setBasicAuthCredentials(String user, String password) {
        this.user = user;
        this.password = password;
    }
}
