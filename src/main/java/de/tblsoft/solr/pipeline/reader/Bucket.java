package de.tblsoft.solr.pipeline.reader;

public class Bucket {

    private String key;
    private Long doc_count;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getDoc_count() {
        return doc_count;
    }

    public void setDoc_count(Long doc_count) {
        this.doc_count = doc_count;
    }
}
