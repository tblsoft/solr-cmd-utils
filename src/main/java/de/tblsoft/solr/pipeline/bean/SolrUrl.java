package de.tblsoft.solr.pipeline.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 23.07.17.
 */
public class SolrUrl {


    public SolrUrl(Map<String, ?> properties) {
        this(properties, null);
    }

    public SolrUrl(Map<String, ?> properties, String key) {
        if(key == null){
            key = "solrUrl";
        }
        Map<String, ?> solrUrl = (Map<String, ?>) properties.get(key);
        this.baseUrl = (String) solrUrl.get("baseUrl");
        this.coreName = (String) solrUrl.get("coreName");
        this.handler = (String) solrUrl.get("handler");
        this.query = (List<Map<String, String>>) solrUrl.get("query");

    }

    private String baseUrl;

    private String coreName;

    private String handler;

    private List<Map<String,String>> query;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getCoreName() {
        return coreName;
    }

    public void setCoreName(String coreName) {
        this.coreName = coreName;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
    }

    public List<Map<String, String>> getQuery() {
        return query;
    }

    public void setQuery(List<Map<String, String>> query) {
        this.query = query;
    }

    public String getSolrClientBaseUrl() {
        return this.baseUrl + "/" + this.coreName;
    }
}
