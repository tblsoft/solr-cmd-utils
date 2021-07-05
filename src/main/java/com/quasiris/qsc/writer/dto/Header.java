package com.quasiris.qsc.writer.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Header {

    private String id;
    private String action;

    /**
     * Getter for property 'id'.
     *
     * @return Value for property 'id'.
     */
    public String getId() {
        return id;
    }

    /**
     * Setter for property 'id'.
     *
     * @param id Value to set for property 'id'.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter for property 'action'.
     *
     * @return Value for property 'action'.
     */
    public String getAction() {
        return action;
    }

    /**
     * Setter for property 'action'.
     *
     * @param action Value to set for property 'action'.
     */
    public void setAction(String action) {
        this.action = action;
    }
}
