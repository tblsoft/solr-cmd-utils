package de.tblsoft.solr.pipeline.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 21.04.16.
 */
public class Field {

    public Field(String name, String value) {
        this.name = name;
        this.values = new ArrayList<String>();
        this.values.add(value);
    }

    public Field(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public Field(Field copy) {
        List<String> values = new ArrayList<>();
        if(copy.getValues() != null) {
            values = new ArrayList<>(copy.getValues());
        }

        this.name = copy.getName();
        this.values = values;
        this.datatype = copy.getDatatype();
    }

    private String name;

    private List<String> values;

    private String datatype;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getValue() {
        for(String value :getValues()) {
            return value;
        }
        return null;
    }

    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    @Override
    public String toString() {
        return "Field{" +
                "name='" + name + '\'' +
                ", values=" + values +
                ", datatype='" + datatype + '\'' +
                '}';
    }
}
