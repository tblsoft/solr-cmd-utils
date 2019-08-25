package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Custom {

    private String fieldName;
    private String text;
    private String html;
    private String jsoupSelector;
    private Map<String, String> attributes;


    /**
     * Getter for property 'fieldName'.
     *
     * @return Value for property 'fieldName'.
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Setter for property 'fieldName'.
     *
     * @param fieldName Value to set for property 'fieldName'.
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Getter for property 'text'.
     *
     * @return Value for property 'text'.
     */
    public String getText() {
        return text;
    }

    /**
     * Setter for property 'text'.
     *
     * @param text Value to set for property 'text'.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter for property 'html'.
     *
     * @return Value for property 'html'.
     */
    public String getHtml() {
        return html;
    }

    /**
     * Setter for property 'html'.
     *
     * @param html Value to set for property 'html'.
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * Getter for property 'jsoupSelector'.
     *
     * @return Value for property 'jsoupSelector'.
     */
    public String getJsoupSelector() {
        return jsoupSelector;
    }

    /**
     * Setter for property 'jsoupSelector'.
     *
     * @param jsoupSelector Value to set for property 'jsoupSelector'.
     */
    public void setJsoupSelector(String jsoupSelector) {
        this.jsoupSelector = jsoupSelector;
    }

    /**
     * Getter for property 'attributes'.
     *
     * @return Value for property 'attributes'.
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * Setter for property 'attributes'.
     *
     * @param attributes Value to set for property 'attributes'.
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void putAttribute(String key, String value) {
        if(this.attributes == null) {
            attributes = new HashMap<>();
        }
        this.attributes.put(key, value);
    }
}