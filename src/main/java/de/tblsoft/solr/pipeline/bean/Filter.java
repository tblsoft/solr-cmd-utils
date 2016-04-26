package de.tblsoft.solr.pipeline.bean;

import java.util.Map;

/**
 * Created by oelbaer on 22.01.16.
 */
public class Filter {

    private String name;
    private String clazz;

    private Boolean disabled;

    private Map<String,?> property;

    private String id;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<String, ?> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Filter{" +
                "name='" + name + '\'' +
                '}';
    }
}
