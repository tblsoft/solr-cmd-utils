package de.tblsoft.solr.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

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
	    String indexUrl = getIndexUrl(url);

    	if(!indexUrl.endsWith("/")) {
            indexUrl = indexUrl + "/";
    	}
    	return indexUrl + "_mapping";
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


    public static String normalizeKey(String key) {
        if(key == null) {
            return null;
        }
        key = key.toLowerCase();
        key = key.replaceAll("ä", "ae");
        key = key.replaceAll("ö", "oe");
        key = key.replaceAll("ü", "ue");
        key = key.replaceAll("ß", "ss");
        key = key.replaceAll("[^a-z0-9_-]", "_");

        if(key.length() > 30) {
            key = key.substring(0, 30);
        }
        return key;
    }


    public static String guessDatatype(String value) {
        if (NumberUtils.isNumber(value)) {
            try {
                Long intValue = Long.valueOf(value);
                return "long";
            } catch (NumberFormatException e) {
                return "double";
            }
        }
        return "string";
    }



}
