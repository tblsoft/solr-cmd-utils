package de.tblsoft.solr.pipeline.filter;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.tblsoft.solr.http.HTTPHelper;
import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.ArrayList;
import java.util.List;

public class GoogleCloudFunctionFilter extends AbstractFilter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String cloudFunctionUrl;
    private String authToken;

    @Override
    public void init() {
        this.cloudFunctionUrl = getProperty("cloudFunctionUrl", null);
        if (this.cloudFunctionUrl == null || this.cloudFunctionUrl.isEmpty()) {
            throw new RuntimeException("cloudRunUrl must be specified in the configuration");
        }

        this.authToken = getProperty("authToken", null);

        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        super.init();
    }

    @Override
    public void document(Document doc) {
        try {
            // Convert document fields to JSON
            String json = objectMapper.writeValueAsString(doc);

            // Prepare headers
            List<String> headers = new ArrayList<>();
            headers.add("Content-Type: application/json");
            if (authToken != null && !authToken.isEmpty()) {
                headers.add("Authorization: Bearer " + authToken);
            }

            // Call Cloud Run using HttpHelper
            String response = HTTPHelper.post(cloudFunctionUrl, json, headers);

            // Parse response JSON
            Document enrichedDoc = objectMapper.readValue(response, Document.class);

            super.document(enrichedDoc);
        } catch (Exception e) {
            throw new RuntimeException("Failed to call Google Cloud Function", e);
        }
    }

    public String getCloudFunctionUrl() {
        return cloudFunctionUrl;
    }

    public void setCloudFunctionUrl(String cloudFunctionUrl) {
        this.cloudFunctionUrl = cloudFunctionUrl;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }
}
