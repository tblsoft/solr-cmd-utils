package de.tblsoft.solr.compare;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.SolrUrl;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by tbl on 23.07.17.
 */
public class SolrCompareFilter extends AbstractFilter {


    private SolrUrl solrReferenceUrl;

    private SolrUrl solrTestUrl;

    private SolrClient solrReferenceClient;
    private SolrClient solrTestClient;

    private boolean failOnError = true;

    private String idField;

    @Override
    public void init() {
        super.init();
        solrReferenceUrl = new SolrUrl(filter.getProperty(), "solrReferenceUrl");
        solrTestUrl = new SolrUrl(filter.getProperty(), "solrTestUrl");

        solrReferenceClient = new HttpSolrClient(solrReferenceUrl.getSolrClientBaseUrl());
        solrTestClient = new HttpSolrClient(solrTestUrl.getSolrClientBaseUrl());

        idField = getProperty("idField", "id");
    }


    @Override
    public void document(Document document) {
        SearchResult reference = query(document, solrReferenceUrl, solrReferenceClient);
        SearchResult test = query(document, solrTestUrl, solrTestClient);


        SearchResultDiff searchResultDiff = new SearchResultDiff(reference, test);

        document.setField("numFoundRelativeDiff", searchResultDiff.getNumFoundRelative());
        document.setField("numFoundTotalDiff", searchResultDiff.getNumFoundTotal());
        document.setField("positionChangedCount", searchResultDiff.getPositionChangedCount());
        document.setField("positionsDiff", searchResultDiff.getPositions());
        document.setField("responseTimeRelativeDiff", searchResultDiff.getResponseTimeRelative());
        document.setField("responseTimeTotalDiff", searchResultDiff.getResponseTimeTotal());
        document.setField("totalDiff", searchResultDiff.getTotalDiff());


        super.document(document);
    }

    SearchResult query(Document document, SolrUrl solrUrl, SolrClient solrClient) {
        SolrQuery solrQuery = new SolrQuery();

        Map<String, String> valueMap = new HashMap<String, String>();
        String queryString = document.getFieldValue("query");
        valueMap.put("query", queryString);

        StrSubstitutor strSubstitutor = new StrSubstitutor(valueMap);
        for(Map<String, String> map : solrUrl.getQuery()) {
            for(String key: map.keySet()) {
                String value = map.get(key);
                value = strSubstitutor.replace(value);
                solrQuery.add(key, value);
            }
        }
        SearchResult searchResult = new SearchResult();
        try {
            QueryResponse response = solrClient.query(solrQuery);

            searchResult.setResponseTime(response.getQTime());
            searchResult.setNumFound(response.getResults().getNumFound());
            searchResult.setQuery(queryString);

            Iterator<SolrDocument> resultIterator = response.getResults().iterator();
            int position = 0;
            while(resultIterator.hasNext()) {
                SolrDocument solrDocument = resultIterator.next();
                String id = (String) solrDocument.getFieldValue("id");
                Result result = new Result(id,position++);
                searchResult.getResultList().add(result);
            }
        } catch (Exception e) {
            if(failOnError) {
                throw new RuntimeException(e);
            } else {
                searchResult.setValid(false);
                searchResult.setErrorMessage(e.getMessage());
            }

        }
        return searchResult;
    }


}
