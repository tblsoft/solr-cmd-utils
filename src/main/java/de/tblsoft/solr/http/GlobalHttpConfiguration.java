package de.tblsoft.solr.http;


import org.apache.http.Header;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GlobalHttpConfiguration {
    private static final Map<String, List<Header>> headersForEndpoints = new HashMap<>();

    public static void registerEndpointHeaders(String endpoint, List<Header> headers) {
        List<Header> headersForEndpoint = headersForEndpoints.get(endpoint);
        if (headersForEndpoint == null) {
            headersForEndpoints.put(endpoint, new ArrayList<>(headers));
        } else {
            headersForEndpoint.addAll(headers);
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
