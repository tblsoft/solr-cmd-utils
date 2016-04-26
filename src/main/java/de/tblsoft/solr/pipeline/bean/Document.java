package de.tblsoft.solr.pipeline.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tblsoft on 21.04.16.
 */
public class Document {

    private List<Field> fields = new ArrayList<Field>();


    public List<Field> getFields() {
        return fields;
    }


    public String getFieldValue(String name, String defaultValue) {
       String value = getFieldValue(name);
        if(value != null) {
            return value;
        }
        return defaultValue;
    }

    public String getFieldValue(String name) {
        List<String> fields = getFieldValues(name, new ArrayList<String>());
        if(fields.size()> 0) {
            return fields.get(0);
        }
        return null;
    }

    public List<String> getFieldValues(String name) {
        for(Field field:fields) {
            if(name.equals(field.getName())) {
                return field.getValues();
            }
        }
        return null;
    }

    public List<String> getFieldValues(String name, List<String> defaultValue) {
        List<String> fields = getFieldValues(name);
        if(fields != null) {
            return fields;
        }
        return defaultValue;
    }

    public void deleteField(String name) {
        List<Field> newFields = new ArrayList<Field>();
        for(Field field:fields) {
            if(!name.equals(field.getName())) {
                newFields.add(field);
            }
        }
        this.fields = newFields;
    }

    public void setField(String name, String value) {
        Field field = new Field(name,value);
        deleteField(name);
        this.fields.add(field);
    }

    public void addField(String name, String value) {
        Field field = new Field(name,value);
        this.fields.add(field);
    }

}
