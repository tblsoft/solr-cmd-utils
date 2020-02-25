package de.tblsoft.solr.pipeline.nlp.squad.dto;

import java.util.List;

public class Squad {
    private List<Data> data;
    private String version;

    /**
     * Getter for property 'data'.
     *
     * @return Value for property 'data'.
     */
    public List<Data> getData() {
        return data;
    }

    /**
     * Setter for property 'data'.
     *
     * @param data Value to set for property 'data'.
     */
    public void setData(List<Data> data) {
        this.data = data;
    }

    /**
     * Getter for property 'version'.
     *
     * @return Value for property 'version'.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Setter for property 'version'.
     *
     * @param version Value to set for property 'version'.
     */
    public void setVersion(String version) {
        this.version = version;
    }
}
