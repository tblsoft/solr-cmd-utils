package de.tblsoft.solr.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by tblsoft on 18.03.16.
 */
public class HTTPHelper {



    public static String post(String url, String postString) {
       try {
           CloseableHttpClient httpclient = HttpClients.createDefault();
           HttpPost httpPost = new HttpPost(url);

           StringEntity entity = new StringEntity(postString, "UTF-8");
           httpPost.setEntity(entity);
           //httpPost.setHeader("Content-Type",contentType);


           CloseableHttpResponse response = httpclient.execute(httpPost);
           StringBuilder responseBuilder = new StringBuilder();

           responseBuilder.append(EntityUtils.toString(response.getEntity()));
           httpclient.close();
           return responseBuilder.toString();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }
    }
}
