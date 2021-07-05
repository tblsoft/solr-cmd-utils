package com.quasiris.qsc.writer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class QscFeedingDocument {

    private Header header;
    private Map<String, Object> payload;

    /**
     * Getter for property 'header'.
     *
     * @return Value for property 'header'.
     */
    public Header getHeader() {
        return header;
    }

    /**
     * Setter for property 'header'.
     *
     * @param header Value to set for property 'header'.
     */
    public void setHeader(Header header) {
        this.header = header;
    }

    /**
     * Getter for property 'payload'.
     *
     * @return Value for property 'payload'.
     */
    public Map<String, Object> getPayload() {
        return payload;
    }

    /**
     * Setter for property 'payload'.
     *
     * @param payload Value to set for property 'payload'.
     */
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}
