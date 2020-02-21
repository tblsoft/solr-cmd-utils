package de.tblsoft.solr.pipeline.bean;

public class ElasticResponse {

    private Integer status;

    private Boolean acknowledged;

    private Boolean shardsAcknowledged;

    private Object error;

    /**
     * Getter for property 'status'.
     *
     * @return Value for property 'status'.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Setter for property 'status'.
     *
     * @param status Value to set for property 'status'.
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * Getter for property 'acknowledged'.
     *
     * @return Value for property 'acknowledged'.
     */
    public Boolean getAcknowledged() {
        return acknowledged;
    }

    /**
     * Setter for property 'acknowledged'.
     *
     * @param acknowledged Value to set for property 'acknowledged'.
     */
    public void setAcknowledged(Boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    /**
     * Getter for property 'shardsAcknowledged'.
     *
     * @return Value for property 'shardsAcknowledged'.
     */
    public Boolean getShardsAcknowledged() {
        return shardsAcknowledged;
    }

    /**
     * Setter for property 'shardsAcknowledged'.
     *
     * @param shardsAcknowledged Value to set for property 'shardsAcknowledged'.
     */
    public void setShardsAcknowledged(Boolean shardsAcknowledged) {
        this.shardsAcknowledged = shardsAcknowledged;
    }

    /**
     * Getter for property 'error'.
     *
     * @return Value for property 'error'.
     */
    public Object getError() {
        return error;
    }

    /**
     * Setter for property 'error'.
     *
     * @param error Value to set for property 'error'.
     */
    public void setError(Object error) {
        this.error = error;
    }
}
