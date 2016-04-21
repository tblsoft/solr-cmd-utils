package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Reader;

import java.util.List;

/**
 * Created by tblsoft on 11.02.16.
 */
public abstract class AbstractReader implements ReaderIF {

    protected PipelineExecuter executer;

    protected Reader reader;

    protected String baseDir;



    @Override
    public void setSource(String source) {

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
        List<String> value = (List<String>) reader.getProperty().get(name);
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
        String value = (String) reader.getProperty().get(name);
        if(value != null) {
            return value;
        }
        return defaultValue;
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
