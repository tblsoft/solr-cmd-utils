package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Processor;
import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 1.09.18.
 */
public abstract class AbstractProcessor implements ProcessorIF {

    protected PipelineExecuter executer;

    protected Processor processor;

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
    public void setProcessor(Processor processor) {
        this.processor = processor;
    }

    public List<String> getPropertyAsList(String name, List<String> defaultValue) {
        if(processor.getProperty() == null) {
            return defaultValue;
        }
        List<String> value = (List<String>) processor.getProperty().get(name);
        if(value != null) {
            return value;
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
        if(processor.getProperty() == null) {
            return defaultValue;
        }
        String value = (String) processor.getProperty().get(name);
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
