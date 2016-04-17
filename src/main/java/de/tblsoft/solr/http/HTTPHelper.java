package de.tblsoft.solr.http;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Created by tblsoft on 18.03.16.
 */
public class HTTPHelper {

	public static String delete(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpDelete httpPost = new HttpDelete(url);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));
			httpclient.close();
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String put(String url, String postString) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPut httpPost = new HttpPut(url);

			if (postString != null) {
				StringEntity entity = new StringEntity(postString, "UTF-8");
				httpPost.setEntity(entity);
				// httpPost.setHeader("Content-Type",contentType);
			}

			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));
			httpclient.close();
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String post(String url, String postString) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			StringEntity entity = new StringEntity(postString, "UTF-8");
			httpPost.setEntity(entity);
			// httpPost.setHeader("Content-Type",contentType);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));
			httpclient.close();
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String get(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpPost = new HttpGet(url);

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
