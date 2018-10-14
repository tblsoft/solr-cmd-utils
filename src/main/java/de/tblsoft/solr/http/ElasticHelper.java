package de.tblsoft.solr.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by tblsoft on 18.03.16.
 */
public class ElasticHelper {

	
	public static String getIndexUrlWithId(String url, String id) {
		if(StringUtils.isEmpty(id)) {
			return url;
		}
		if(StringUtils.endsWith(url, "/")) {
			return url + id;
		}
		return url + "/" + id;
	}

    public static String getSearchUrl(String url) throws URISyntaxException {
        String updateUrl = getIndexUrl(url) + "/_search";
        return updateUrl;
    }
	
	public static String getUpdateUrl(String url, String id) {
		String updateUrl = getIndexUrlWithId(url, id) + "/_update";
		return updateUrl;
	}

    public static String getCatlUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String path = "/_cat/indices";
        uri = uri.resolve(path);
        return uri.toString();
    }

    public static String getAliaslUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String path = "/_aliases";
        uri = uri.resolve(path);
        return uri.toString();
    }

    public static String getScrollUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String path = "/_search/scroll";
        uri = uri.resolve(path);
        return uri.toString();
    }

    public static String getIndexUrl(String url, String index) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String[] parts = uri.getPath().split(Pattern.quote("/"));
        if(parts.length < 2) {
            throw new URISyntaxException(url, "The url " + url + " must contain at least one path part.");
        }
        uri = uri.resolve("/" + index);
        return uri.toString();
    }

    public static String getIndexUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String[] parts = uri.getPath().split(Pattern.quote("/"));
        if(parts.length < 2) {
            throw new URISyntaxException(url, "The url " + url + " must contain at least one path part.");
        }
        String path = parts[1];
        uri = uri.resolve("/" + path);
        return uri.toString();
    }
    
    public static String getIndexFromUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String[] parts = uri.getPath().split(Pattern.quote("/"));
        if(parts.length < 2) {
            throw new URISyntaxException(url, "The url " + url + " must contain at least one path part.");
        }
        String index = parts[1];
        return index;
    }
    public static String getTypeFromUrl(String url) throws URISyntaxException {
    	if(url==null) {
    		throw new URISyntaxException("", "The url is null.");
    	}
    	URI uri = new URI(url);
    	String[] parts = uri.getPath().split(Pattern.quote("/"));
    	if(parts.length < 3) {
    		throw new URISyntaxException(url, "The url " + url + " must contain at least two path part.");
    	}
    	String type = parts[2];
    	return type;
    }
    
    public static String getMappingUrl(String url) throws URISyntaxException {
    	if(!url.endsWith("/")) {
    		url = url + "/";
    	}
    	return url + "_mapping";
    }
    
    public static String getBulkUrl(String url) throws URISyntaxException {
        if(url==null) {
            throw new URISyntaxException("", "The url is null.");
        }
        URI uri = new URI(url);
        String path = "/_bulk";
        uri = uri.resolve(path);
        return uri.toString();
    }



}
