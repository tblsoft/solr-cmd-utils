package de.tblsoft.solr.pipeline.bean;

import java.util.Map;

/**
 * Created by oelbaer on 22.01.16.
 */
public class Writer {

    private String name;

    private String clazz;

    private Map<String,String> property;

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

    public Map<String, String> getProperty() {
        return property;
    }

    public void setProperty(Map<String, String> property) {
        this.property = property;
    }

    @Override
    public String toString() {
        return "Writer{" +
                "name='" + name + '\'' +
                '}';
    }
}
