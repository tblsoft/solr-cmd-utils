package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.util.DateUtils;
import de.tblsoft.solr.util.DocumentUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Created by tblsoft on 25.12.16.
 */
public class HttpWorker implements Callable<Document> {

    private CloseableHttpClient httpclient;

    private Document document;

    private String urlField;

    private String userAgent;

    private String cacheBasePath;

    public HttpWorker(Document document, CloseableHttpClient httpclient, String urlField, String userAgent, String cacheBasePath) {
        this.document = document;
        this.httpclient = httpclient;
        this.urlField = urlField;
        this.userAgent = userAgent;
        this.cacheBasePath = cacheBasePath;
    }

    public Document call() throws Exception {
        String url = document.getFieldValue(urlField);





        CloseableHttpResponse response = null;
        try {
            Document cachedDocument = readFromCache(url);
            if(cachedDocument != null) {
                return cachedDocument;
            }
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("User-Agent",userAgent);
            long start = System.currentTimeMillis();
            //CloseableHttpClient client = HttpClients.createDefault();
            //response = client.execute(httpGet);
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
            writeToCache(url,document);
        } catch (Exception e) {
            document.addField("errormessage", e.getMessage());
        }
        return document;

    }

    private Document readFromCache( String url)  {
        if(cacheBasePath == null) {
            return null;
        }
        try {
            File target = getTargetFile(url);
            if (!Files.exists(target.toPath())) {
                return null;
            }
            return DocumentUtils.readFromFile(target);
        } catch (Exception e) {
            System.out.println("error " + e.getMessage() + " reading from cache for url: " + url);
            return null;
        }
    }

    private void writeToCache(String url, Document document) throws Exception {
        if(cacheBasePath == null) {
            return;
        }
        File target = getTargetFile(url);
        DocumentUtils.writeToFile(target, document);
    }

    private File getTargetFile(String url) throws URISyntaxException {
        String hashedUrl = hash(url);
        URI uri = new URI(url);
        File target = new File(cacheBasePath + "/" + uri.getHost() + "/" + hashedUrl);
        return target;
    }

    private String hash(String url) {
        String base64 = Base64.getEncoder().encodeToString(url.getBytes());
        if(base64.length() < 255) {
            return base64;
        }
        return DigestUtils.md5Hex(url.getBytes());
    }
}
