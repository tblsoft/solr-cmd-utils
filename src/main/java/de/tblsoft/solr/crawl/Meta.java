package de.tblsoft.solr.crawl;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Meta {

    private Map<String, String> name;
    private Map<String, String> property;
    private Map<String, String> itemprop;



    public Map<String, String> getName() {
        return name;
    }

    public void addName(String name, String value) {
        if(this.name == null) {
            this.name = new HashMap<>();
        }
        this.name.put(name, value);
    }

    public void setName(Map<String, String> name) {
        this.name = name;
    }

    public Map<String, String> getProperty() {
        return property;
    }

    public void addProperty(String property, String value) {
        if(this.property == null) {
            this.property = new HashMap<>();
        }
        this.property.put(property, value);
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    public Map<String, String> getItemprop() {
        return itemprop;
    }

    public void addItemprop(String itemprop, String value) {
        if(this.itemprop == null) {
            this.itemprop = new HashMap<>();
        }
        this.itemprop.put(itemprop, value);
    }

    public void setItemprop(Map<String, String> itemprop) {
        this.itemprop = itemprop;
    }
}
