package de.tblsoft.solr.pipeline.bean;

import org.apache.commons.lang3.BooleanUtils;

import java.util.Map;

/**
 * Created by tblsoft on 1.09.18.
 */
public class Processor {

    private String name;

    private String clazz;

    private Map<String,?> property;

    private Boolean disabled = Boolean.FALSE;

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

    @Override
    public String toString() {
        return "Processor{" +
                "name='" + name + '\'' +
                '}';
    }
}
