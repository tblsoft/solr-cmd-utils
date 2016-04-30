package de.tblsoft.solr.http;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

/**
 * Created by tblsoft on 18.03.16.
 */
public class ElasticHelper {


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
    
    public static String getMappingUrl(String url) throws URISyntaxException {
    	if(!url.endsWith("/")) {
    		url = url + "/";
    	}
    	return url + "_mapping";
    }



}
