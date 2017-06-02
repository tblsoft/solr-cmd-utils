package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import org.apache.http.NameValuePair;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.List;

/**
 * Created by tblsoft 04.06.17.
 *
 * Count the field names.
 */
public class SolrNumFoundFilter extends AbstractFilter {


    private boolean failOnError = true;
    private SolrClient solr;


    @Override
    public void init() {
        String solrUrl = getProperty("solrUrl", null);
        verify(solrUrl, "You must configure the property solrUrl: http://localhost:8983/solr/techproducts");
        solr = new HttpSolrClient(solrUrl);
        super.init();
    }


    @Override
    public void document(Document document) {
        SolrQuery query = new SolrQuery();
        List<NameValuePair> nameValuePairs = UrlUtil.getUrlParamsForQuery(document.getFieldValue("query"));
        for(NameValuePair nameValuePair : nameValuePairs) {
            query.add(nameValuePair.getName(), nameValuePair.getValue());
        }

        try {
            QueryResponse response = solr.query(query);
            long numFound = response.getResults().getNumFound();
            document.setField("numFound", String.valueOf(numFound));
        } catch (Exception e) {
            if(failOnError) {
                throw new RuntimeException(e);
            } else {
                document.addField("error", e.getMessage());
            }

        }
        super.document(document);

    }
}
