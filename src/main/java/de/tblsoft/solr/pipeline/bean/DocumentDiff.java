package de.tblsoft.solr.pipeline.bean;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tblsoft on 19.05.16.
 */
public class DocumentDiff {

    private String idField = "id";
    private String id;
    private List<FieldDiff> fieldDiffs = new ArrayList<FieldDiff>();
    private Date timestamp = new Date();

    public String getIdField() {
        return idField;
    }

    public void setIdField(String idField) {
        this.idField = idField;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<FieldDiff> getFieldDiffs() {
        return fieldDiffs;
    }

    public void setFieldDiffs(List<FieldDiff> fieldDiffs) {
        this.fieldDiffs = fieldDiffs;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
