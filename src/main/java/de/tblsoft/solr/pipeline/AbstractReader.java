package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Reader;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 11.02.16.
 */
public abstract class AbstractReader implements ReaderIF {

    protected PipelineExecuter executer;

    protected Reader reader;

    protected String baseDir;

    protected Map<String,String> variables = new HashMap<String, String>();


    @Override
    public void setVariables(Map<String,String> variables) {
        if(variables == null) {
            return;
        }
        for(Map.Entry<String,String> entry: variables.entrySet()) {
            this.variables.put("variables." + entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setPipelineExecuter(PipelineExecuter executer) {
        this.executer = executer;
    }

    @Override
    public void end() {

    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public List<String> getPropertyAsList(String name, List<String> defaultValue) {
        if(reader.getProperty() == null) {
            return defaultValue;
        }
        List<String> values = (List<String>) reader.getProperty().get(name);
        if(values != null) {
            List<String> substitutedValues = new ArrayList<>();
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            for(String value : values) {
                value = strSubstitutor.replace(value);
                substitutedValues.add(value);
            }

            return substitutedValues;
        }
        return defaultValue;
    }

    public String[] getPropertyAsArray(String name, String[] defaultValue) {
        List<String> list = getPropertyAsList(name, null);
        if(list == null) {
            return defaultValue;
        }
        return list.toArray(new String[list.size()]);
    }

    public String getProperty(String name, String defaultValue) {
        String value = (String) reader.getProperty().get(name);
        if(value != null) {
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            value = strSubstitutor.replace(value);
            return value;
        }
        return defaultValue;
    }

    public Long getPropertyAsInteger(String name, Long defaultValue) {
        String value = getProperty(name, null);
        if(value == null) {
            return defaultValue;
        }

        return Long.valueOf(value);

    }

    public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {
        String value = getProperty(name, null);
        if(value == null) {
            return defaultValue;
        }

        return Boolean.valueOf(value);

    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
