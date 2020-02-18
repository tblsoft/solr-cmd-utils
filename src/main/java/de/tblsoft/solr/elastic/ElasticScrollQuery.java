package de.tblsoft.solr.elastic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import de.tblsoft.solr.http.ElasticHelper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.http.UrlUtil;
import de.tblsoft.solr.pipeline.bean.Document;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by oelbaer on 21.08.18.
 */
public class ElasticScrollQuery {


    private String url;

    private String request;

    private String scroll = "1m";

    private String scrollId = null;

    boolean hasHits = false;

    private String pagedUrl = null;

    private String scrollBaseUrl;
    private String searchBaseUrl;

    private Gson gson;

    private boolean usePostMethod = false;


    public ElasticScrollQuery(String url) throws URISyntaxException {
        this.url = url;
        pagedUrl = UrlUtil.appendParameter(url, "scroll=" + scroll);
        scrollBaseUrl = ElasticHelper.getScrollUrl(this.url);
        searchBaseUrl = ElasticHelper.getSearchUrl(this.url);
        GsonBuilder builder = new GsonBuilder();
        gson = builder.create();


    }

    public void setRequest(String request) {
        this.request = request;
        this.usePostMethod = true;
        this.pagedUrl = searchBaseUrl + "?scroll=" + scroll;
    }

    public void setScroll(String scroll) {
        this.scroll = scroll;
    }

    public List<Document> getAllDocuments() throws Exception {
        List<Document> allDocs = new ArrayList<>();
        List<Document> docs;
        while((docs = nextDocuments())  != null) {
            allDocs.addAll(docs);
        }
        return allDocs;
    }

    public List<Document> nextDocuments() throws Exception {


        List<Document> documents = new ArrayList<>();
        String response;
        if(usePostMethod) {
            response = HTTPHelper.post(pagedUrl, request, "application/json");
        } else {
            response = HTTPHelper.get(pagedUrl);
        }



        JsonElement jsonResponse = gson.fromJson(response,
                JsonElement.class);
        scrollId = jsonResponse.getAsJsonObject()
                .get("_scroll_id").getAsString();

        Iterator<JsonElement> hitsIterator = jsonResponse.getAsJsonObject()
                .get("hits").getAsJsonObject().get("hits").getAsJsonArray()
                .iterator();
        hasHits = false;
        while (hitsIterator.hasNext()) {
            hasHits=true;
            Document document = new Document();
            for (Map.Entry<String, JsonElement> entry : hitsIterator.next()
                    .getAsJsonObject().get("_source").getAsJsonObject()
                    .entrySet()) {
                if (entry.getValue().isJsonArray()) {
                    Iterator<JsonElement> iter = entry.getValue().getAsJsonArray().iterator();
                    List<String> values = new ArrayList<>();
                    while(iter.hasNext()) {
                        JsonElement nextElement = iter.next();
                        if(nextElement.isJsonPrimitive()) {
                            values.add(nextElement.getAsString());
                        } else {
                            values.add(nextElement.getAsJsonObject().toString());
                        }
                    }
                    document.addField(entry.getKey(), values);
                } else if (entry.getValue().isJsonPrimitive()) {
                    document.addField(entry.getKey(), entry.getValue().getAsString());
                }
            }
            documents.add(document);
        }

        if(usePostMethod) {
            pagedUrl = scrollBaseUrl;
            request = "{ \"scroll\" : \"" + scroll + "\", \"scroll_id\" : \"" + scrollId + "\"}";
        } else {
            pagedUrl = scrollBaseUrl + "?scroll=" + scroll + "&scroll_id=" + scrollId;
        }

        if(hasHits) {
            return documents;
        } else {
            return null;
        }
    }
}
