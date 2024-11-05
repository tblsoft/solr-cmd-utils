package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Reader;
import de.tblsoft.solr.pipeline.helper.PipelinePropertiesHelper;
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

    protected Map<String,String> variables = new HashMap<>();

    @Override
    public void init() {

    }

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

    public void document(Document document) {
        executer.document(document);
    }

    @Override
    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public List<String> getPropertyAsList(String name, List<String> defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsList(reader.getProperty(), variables, name, defaultValue);
    }

    public String[] getPropertyAsArray(String name, String[] defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsArray(reader.getProperty(), variables, name, defaultValue);
    }

    public String getProperty(String name, String defaultValue) {
        return PipelinePropertiesHelper.getProperty(reader.getProperty(), variables, name, defaultValue);
    }

    public Long getPropertyAsInteger(String name, Long defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsLong(reader.getProperty(), variables, name, defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsBoolean(reader.getProperty(), variables, name, defaultValue);
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
