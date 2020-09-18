package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.cache.FileCache;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by tblsoft on 25.12.16.
 */
public class HttpWorker implements Callable<Document> {

    private static Logger LOG = LoggerFactory.getLogger(HttpWorker.class);

    private CloseableHttpClient httpclient;

    private Document document;

    private String urlField;

    private String userAgent;

    private FileCache cache;

    public HttpWorker(Document document,
                      CloseableHttpClient httpclient,
                      String urlField,
                      String userAgent,
                      String cacheBasePath,
                      String fileExtension) {
        this.document = document;
        this.httpclient = httpclient;
        this.urlField = urlField;
        this.userAgent = userAgent;
        cache = new FileCache(cacheBasePath, fileExtension);
    }

    public Document call() throws Exception {
        String url = document.getFieldValue(urlField);
        if(url == null) {
            return document;
        }
        url = url.trim();





        CloseableHttpResponse response = null;
        try {
            Document cachedDocument = cache.readFromCache(url);
            if(cachedDocument != null) {
                return cachedDocument;
            }
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent",userAgent);
            long start = System.currentTimeMillis();
            response = httpclient.execute(httpGet);
            StringBuilder responseBuilder = new StringBuilder();
            responseBuilder.append(EntityUtils.toString(response.getEntity()));
            response.close();
            long duration = System.currentTimeMillis() - start;

            for(Header header : response.getAllHeaders()) {
                document.addField("http_header_" + header.getName(), header.getValue());
                document.addField("headernames", header.getName());
            }

            document.setField("http_size", String.valueOf(responseBuilder.length()));
            document.setField("http_duration", String.valueOf(duration));
            document.setField("http_code", String.valueOf(response.getStatusLine().getStatusCode()));
            document.setField("http_payload", responseBuilder.toString());
            document.setField("http_time", DateUtils.date2String(new Date()));
            cache.writeToCache(url,document);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            document.addField("errormessage", e.getMessage());
        }
        return document;

    }
}
