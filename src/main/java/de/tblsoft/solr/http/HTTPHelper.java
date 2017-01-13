package de.tblsoft.solr.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
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
	
	/**
	 * Store the body of the url in the specified fileName.
	 * 
	 * @param url The url where the content is fetched.
	 * @param fileName The fileName where the content is stored.
	 */
	public static void get2File(String url, File fileName) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpPost = new HttpGet(url);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			InputStream is = response.getEntity().getContent();
			FileOutputStream fos = new FileOutputStream(fileName);
			org.apache.commons.io.IOUtils.copy(is, fos);
			httpclient.close();
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public static int getStatusCode(String url) {
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            httpclient.close();
            return response.getStatusLine().getStatusCode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
 
    
    public static String getCookieValueFromHeader(String cookieName, HttpResponse response) {
    	Header[] headers = response.getHeaders("Set-Cookie");
    	if(headers == null) {
    		return null;
    	}
    	
    	for (int i = 0; i < headers.length; i++) {
    		Header header = headers[i];
        	List<HttpCookie> cookies = HttpCookie.parse(header.getValue());
        	for(HttpCookie cookie : cookies) {
        		if(cookie.getName().equals(cookieName)) {
        			return cookie.getValue();
        		}
        	}
		}

    	return null;

    }
}
