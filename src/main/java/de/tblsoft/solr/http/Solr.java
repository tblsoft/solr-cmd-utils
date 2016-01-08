package de.tblsoft.solr.http;

import com.google.common.io.ByteStreams;
import de.tblsoft.solr.util.IOUtils;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by tblsoft
 */
public class Solr {


    public Solr(boolean showHeaders) {
        this.showHeaders = showHeaders;
    }

    private boolean showHeaders = false;

    public String feedFileToSolr(String url, String inputFileName) throws Exception{
        url = url + "/update?commit=true";
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);


        InputStream in = IOUtils.getInputStream(inputFileName);

        InputStreamEntity entity = new InputStreamEntity(in);
        httpPost.setEntity(entity);

        CloseableHttpResponse response = httpclient.execute(httpPost);
        StringBuilder responseBuilder = new StringBuilder();
        if(showHeaders) {
            responseBuilder.append(response.getStatusLine());
            responseBuilder.append("\n");
            for(Header header :response.getAllHeaders()) {
                responseBuilder.append(header.getName());
                responseBuilder.append(": ");
                responseBuilder.append(header.getValue());
                responseBuilder.append("\n");

            }
        }

        responseBuilder.append(EntityUtils.toString(response.getEntity()));
        in.close();
        httpclient.close();
        return responseBuilder.toString();
    }

    public void retrieveFromSolr(String url, String outputFileName) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        OutputStream out = IOUtils.getOutputStream(outputFileName);

        CloseableHttpResponse response = httpclient.execute(httpGet);
        InputStream in = response.getEntity().getContent();
        ByteStreams.copy(in,out);

        in.close();
        out.close();
        httpclient.close();
    }


    public String post(String url, String postString) throws Exception{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        StringEntity entity = new StringEntity(postString);
        httpPost.setEntity(entity);
        httpPost.setHeader("Content-Type","application/xml");


        CloseableHttpResponse response = httpclient.execute(httpPost);
        StringBuilder responseBuilder = new StringBuilder();
        if(showHeaders) {
            responseBuilder.append(response.getStatusLine());
            responseBuilder.append("\n");
            responseBuilder.append("\n");
            responseBuilder.append("Request Header\n");
            printHeader(httpPost.getAllHeaders(), responseBuilder);

            responseBuilder.append("\n");
            responseBuilder.append("Response Header\n");
            printHeader(response.getAllHeaders(), responseBuilder);

        }
        responseBuilder.append(EntityUtils.toString(response.getEntity()));
        httpclient.close();
        return responseBuilder.toString();
    }

    void printHeader(Header[] headers, StringBuilder responseBuilder) {
        for(Header header :headers) {
            responseBuilder.append(header.getName());
            responseBuilder.append(": ");
            responseBuilder.append(header.getValue());
            responseBuilder.append("\n");

        }

    }

    public String deleteByQuery(String url, String query) throws Exception {
        url = url + "/update?commit=true";
        String deleteQuery= "<delete><query>" + query + "</query></delete>";
        return post(url,deleteQuery);
    }

    public String deleteAll(String url) throws Exception {
        return deleteByQuery(url,"*:*");
    }
}
