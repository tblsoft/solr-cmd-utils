package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Processor;
import de.tblsoft.solr.pipeline.helper.PipelinePropertiesHelper;
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
        return PipelinePropertiesHelper.getPropertyAsList(processor.getProperty(), variables, name, defaultValue);
    }

    public String[] getPropertyAsArray(String name, String[] defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsArray(processor.getProperty(), variables, name, defaultValue);
    }

    public String getProperty(String name, String defaultValue) {
        return PipelinePropertiesHelper.getProperty(processor.getProperty(), variables, name, defaultValue);
    }

    public Long getPropertyAsInteger(String name, Long defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsLong(processor.getProperty(), variables, name, defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsBoolean(processor.getProperty(), variables, name, defaultValue);
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
