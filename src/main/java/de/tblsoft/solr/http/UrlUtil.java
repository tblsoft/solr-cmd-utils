package de.tblsoft.solr.http;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 26.04.16.
 */
public class UrlUtil {


    public static String getPath(String url) {
        try {
            URL tempUrl = new URL(url);
            return tempUrl.getPath();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url " + url + " is not valid.");
        }
    }


    public static String getHost(String url) {
        try {
            URL tempUrl = new URL(url);
            return tempUrl.getHost();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url " + url + " is not valid.");
        }
    }

    public static String getProtocol(String url) {
        try {
            URL tempUrl = new URL(url);
            return tempUrl.getProtocol();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url " + url + " is not valid.");
        }
    }

    public static List<String> getPathParts(String url) {
        return Arrays.asList(getPath(url).split(Pattern.quote("/")));

    }

    public static List<NameValuePair> getUrlParams(String url) {
        return getUrlParams(url, "UTF-8");
    }

    public static List<NameValuePair> getUrlParamsForQuery(String query) {
        return getUrlParams("http://localhost?" + query, "UTF-8");
    }
    
    public static Map<String, List<String>> getUrlParamsAsMap(String url) {
    	Map<String, List<String>> ret = new HashMap<String, List<String>>();
        List<NameValuePair> nameValuePairList = getUrlParams(url, "UTF-8");
        for(NameValuePair pair: nameValuePairList) {
        	String name = pair.getName();
        	if(ret.get(name) == null) {
        		ret.put(name, new ArrayList<String>());
        	}
        	ret.get(name).add(pair.getValue());
        }
        return ret;
    }


    public static String getUrlQuery(String url) {

        try {
            URL tempUrl = new URL(url);
            String query = tempUrl.getQuery();
            return query;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url " + url + " is not valid.");
        }
    }

    public static List<NameValuePair> getUrlParams(String url, String charset) {

        try {
            URL tempUrl = new URL(url);
            String query = tempUrl.getQuery();
            List<NameValuePair> urlParams = URLEncodedUtils.parse(query,
                    Charset.forName(charset));
            return urlParams;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("The url " + url + " is not valid.");
        }
    }
    
    public static String encode(String value) {
    	try {
			return URLEncoder.encode(value, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
    }

    public static String decode(String value) {
        try {
            return URLDecoder.decode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
