package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Custom {

    private String fieldName;
    private String value;
    private String jsoupSelector;
    private String type;
    private String attributeName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getJsoupSelector() {
        return jsoupSelector;
    }

    public void setJsoupSelector(String jsoupSelector) {
        this.jsoupSelector = jsoupSelector;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
