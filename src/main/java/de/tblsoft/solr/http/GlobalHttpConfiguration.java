package de.tblsoft.solr.http;


import org.apache.http.Header;

import java.util.*;

public class GlobalHttpConfiguration {
    private static final Map<String, List<Header>> headersForEndpoints = new HashMap<>();

    public static void registerEndpointHeaders(String regexp, Header... headers) {
        List<Header> headersForEndpoint = headersForEndpoints.get(regexp);
        if (headersForEndpoint == null) {
            headersForEndpoints.put(regexp, new ArrayList<>(Arrays.asList(headers)));
        } else {
            headersForEndpoint.addAll(Arrays.asList(headers));
        }
    }

    public static List<Header> getHeadersForEndpoint(String inputEndpoint) {
        List<Header> headers = new ArrayList<>();
        for (Map.Entry<String, List<Header>> entry : headersForEndpoints.entrySet()) {
            if (inputEndpoint.matches(entry.getKey())) {
                headers.addAll(entry.getValue());
            }
        }
        return headers;
    }

    public static Map<String, List<Header>> getHeadersForEndpoints() {
        return headersForEndpoints;
    }
}
