package de.tblsoft.solr.pipeline.bean;

import com.google.common.base.Strings;

import java.util.*;

/**
 * Created by tblsoft on 21.04.16.
 */
public class Document {

    private List<Field> fields = new ArrayList<Field>();

    private Long size;


    public List<Field> getFields() {
        return fields;
    }

    public Document() {

    }

    public Document(Document copy) {
        for (Field field : copy.getFields()) {
            this.fields.add(new Field(field));
        }
        fieldChanged();
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
        fieldChanged();
    }

    public void setField(String name, Collection<String> value) {
        setField(name, new ArrayList<String>(value));
    }

    public void setField(String name, List<String> value) {
        Field field = new Field(name,value);
        deleteField(name);
        this.fields.add(field);
        fieldChanged();
    }

    public void setField(String name, Object value) {
        if(value instanceof List) {
            deleteField(name);
            addField(name, value);
        } else {
            setField(name, String.valueOf(value));
        }
    }

    public void addField(String name, Object value) {
        if(value instanceof List) {
            List valueList = (List) value;
            for(Object v: valueList) {
                addField(name, String.valueOf(v));
            }
        } else {
            addField(name, String.valueOf(value));
        }
    }


    public void setField(String name, String value) {
        Field field = new Field(name,value);
        deleteField(name);
        this.fields.add(field);
        fieldChanged();
    }

    public Field getField(String name) {
        for(Field field:fields) {
            if(name.equals(field.getName())) {
                return field;
            }
        }
        return null;
    }

    public void addField(Field field) {
        addField(field.getName(), field.getValues(), field.getDatatype());
    }

    public void addField(String name, String value) {
        addField(name, value, null);
    }

    public void addField(String name, String value, String dataType) {
        List<String> values = new ArrayList<>();
        values.add(value);
        addField(name, values, dataType);
    }

    public void addField(String name, List<String> values, String dataType) {
        Field existingField = getField(name);
        if(existingField == null) {
            Field field = new Field(name,values);
            field.setDatatype(dataType);
            this.fields.add(field);
        } else {
            existingField.getValues().addAll(values);
        }
        fieldChanged();
    }

    public void addField(String name, List<String> values) {
       addField(name, values, null);
    }

    public void addFieldIfNotNullOrEmpty(String name, String value) {
        if(Strings.isNullOrEmpty(value)) {
            return;
        }
        addField(name,value);
    }

    public void uniqueFieldValues(String name) {
        List<String> values = getFieldValues(name);
        if(values == null) {
            return;
        }
        Set<String> uniqueValues = new HashSet<>(values);
        setField(name, uniqueValues);
    }


    @Override
    public String toString() {
        return "Document{" +
                "fields=" + fields +
                '}';
    }

    private void fieldChanged() {
        this.size = null;
    }

    public long getSize() {
        if(size == null) {
            long totalSize = 0;
            for(Field field: getFields()) {
                totalSize = totalSize + field.getSize();
            }
            size = totalSize;
        }
        return size;
    }
}
