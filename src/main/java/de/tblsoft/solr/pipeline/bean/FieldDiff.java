package de.tblsoft.solr.pipeline.bean;

import java.util.List;

/**
 * Created by tblsoft on 24.05.16.
 */
public class FieldDiff {
    private String fieldName;
    private List<String> oldValues;
    private List<String> newValue;
    private DiffType diffType;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public List<String> getOldValues() {
        return oldValues;
    }

    public void setOldValues(List<String> oldValues) {
        this.oldValues = oldValues;
    }

    public List<String> getNewValue() {
        return newValue;
    }

    public void setNewValue(List<String> newValue) {
        this.newValue = newValue;
    }

    public DiffType getDiffType() {
        return diffType;
    }

    public void setDiffType(DiffType diffType) {
        this.diffType = diffType;
    }

    public enum DiffType {
        DIFF, EQUAL, CREATE, DELETE
    }
}
