package de.tblsoft.solr.http;

import com.google.common.base.Strings;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 18.03.16.
 */
public class HTTPHelper {

	private static Logger LOG = LoggerFactory.getLogger(HTTPHelper.class);


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

	public static String put(String url, String postString, String contentType) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPut httpPost = new HttpPut(url);

			if (postString != null) {
				StringEntity entity = new StringEntity(postString, "UTF-8");
				httpPost.setEntity(entity);
			}
			if(!Strings.isNullOrEmpty(contentType)) {
				httpPost.setHeader("Content-Type",contentType);
			}


			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));

			if(response.getStatusLine().getStatusCode() != 200) {
				LOG.info(responseBuilder.toString());
			}
			httpclient.close();
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static String post(String url, String postString) {
		return post(url, postString, new ArrayList<>());
	}

	public static String post(String url, String postString, String contentType) {
		return post(url, postString, Arrays.asList("Content-Type: " + contentType));
	}
	public static String post(String url, String postString, List<String> headers) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpPost httpPost = new HttpPost(url);

			StringEntity entity = new StringEntity(postString, "UTF-8");
			httpPost.setEntity(entity);

			for(String header : headers ) {
				String[] splitted = header.split(":");
				if(splitted.length != 2) {
					throw new RuntimeException("Invalid header definition");
				}
				httpPost.setHeader(splitted[0].trim() ,splitted[1].trim());
			}

			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));
			httpclient.close();
			if(response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("Problem: " + response.getStatusLine().getStatusCode() + " payload: " + responseBuilder.toString());
			}
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

    public static void webHook(String url, String... parameters) {
		if(Strings.isNullOrEmpty(url)) {
			return;
		}
        Map<String, String> parametersMap = new HashMap<>();
        for (int i = 0; i < parameters.length; i=i+2) {
            parametersMap.put(parameters[i], parameters[i+1]);
        }
        StrSubstitutor strSubstitutor = new StrSubstitutor(parametersMap);
        String webhook = strSubstitutor.replace(url);
        // TODO this should be async
        get(webhook);

    }

	public static String get(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpPost = new HttpGet(url);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			StringBuilder responseBuilder = new StringBuilder();

			responseBuilder.append(EntityUtils.toString(response.getEntity()));
			httpclient.close();
			if(response.getStatusLine().getStatusCode() >= 300) {
				throw new RuntimeException("Problem httpCode: " + response.getStatusLine().getStatusCode() + " url: " + url + " payload: " + responseBuilder.toString());
			}
			return responseBuilder.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static InputStream getInputStream(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpPost = new HttpGet(url);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			return response.getEntity().getContent();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static InputStream getAsInputStream(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.createDefault();
			HttpGet httpPost = new HttpGet(url);

			CloseableHttpResponse response = httpclient.execute(httpPost);
			return response.getEntity().getContent();
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
			CloseableHttpClient httpclient = HttpClients.custom().disableRedirectHandling().build();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			httpclient.close();
			return response.getStatusLine().getStatusCode();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}


	public static String getRedirectLocation(String url) {
		try {
			CloseableHttpClient httpclient = HttpClients.custom().disableRedirectHandling().build();
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpclient.execute(httpGet);
			httpclient.close();
			Header location = response.getFirstHeader("Location");
			if(location == null) {
				return url;
			}
			return location.getValue();
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

	public static String removeQueryParameter(String url) {
		if(Strings.isNullOrEmpty(url)) {
			return url;
		}
		int index = url.indexOf("?");
		if(index > 0) {
			return url.substring(0, index);
		}
		return url;
	}
}
