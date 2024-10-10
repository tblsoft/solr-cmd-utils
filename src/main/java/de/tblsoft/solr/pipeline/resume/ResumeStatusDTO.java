package de.tblsoft.solr.pipeline.resume;

import java.util.Map;

public class ResumeStatusDTO {

    private Boolean completed;

    private Integer lastBatch;

    private Map<String, Object> context;

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getLastBatch() {
        return lastBatch;
    }

    public void setLastBatch(Integer lastBatch) {
        this.lastBatch = lastBatch;
    }

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }
}
