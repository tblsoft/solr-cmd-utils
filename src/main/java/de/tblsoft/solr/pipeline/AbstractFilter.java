package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.pipeline.helper.PipelinePropertiesHelper;
import de.tblsoft.solr.util.DateUtils;

import java.util.*;

/**
 * Created by tblsoft on 23.01.16.
 */
public abstract class AbstractFilter implements FilterIF {

    protected FilterIF nextFilter;

    protected Filter filter;

    private String baseDir;

    protected Map<String,String> variables = new HashMap<String, String>();

    protected PipelineExecuter pipelineExecuter;

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
    public void init() {
        nextFilter.init();

    }

    @Override
    public void setFilterConfig(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void document(Document document) {
        List<Document> docs = null;
        try {
            docs = flatMap(document);
        } catch (UnsupportedOperationException ignored) {
            docs = new ArrayList<>();
            docs.add(document);
        }

        if(docs != null) {
            for (Document doc : docs) {
                nextFilter.document(document);
            }
        }
    }

    public List<Document> flatMap(Document document) {
        return new ArrayList<>(Arrays.asList(map(document)));
    }

    public Document map(Document document) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void end() {
        nextFilter.end();
    }

    @Override
    public void setNextFilter(FilterIF filter){
        this.nextFilter=filter;
    }

    public String getProperty(String name, String defaultValue) {
        return PipelinePropertiesHelper.getProperty(filter.getProperty(), variables, name, defaultValue);
    }

    public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsBoolean(filter.getProperty(), variables, name, defaultValue);
    }

    public List<String> getPropertyAsList(String name, List<String> defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsList(filter.getProperty(), variables, name, defaultValue);
    }


    public Map<String, String> getPropertyAsMapping(String name) {
        return getPropertyAsMapping(name, new HashMap<>(), "->");
    }

    public Map<String, String> getPropertyAsMapping(String name,  Map<String, String> defaultValue) {
        return getPropertyAsMapping(name,defaultValue, "->");
    }

    public Map<String, String> getPropertyAsMapping(String name,  Map<String, String> defaultValue, String splitter) {
        return PipelinePropertiesHelper.getPropertyAsMapping(filter.getProperty(), variables, name, defaultValue, splitter);
    }

    public int getPropertyAsInt(String name, int defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsInt(filter.getProperty(), variables, name, defaultValue);
    }

    public float getPropertyAsFloat(String name, float defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsFloat(filter.getProperty(), variables, name, defaultValue);
    }
    
    public Date getPropertyAsDate(String name, Date defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsDate(filter.getProperty(), variables, name, defaultValue);
    }

    public void verify(String value, String message) {
        if(Strings.isNullOrEmpty(value)) {
            throw new RuntimeException(message);
        }

    }

    public void verify(List<String> value, String message) {
        if(value == null) {
            throw new RuntimeException(message);
        }

    }

    public String[] getPropertyAsArray(String name, String[] defaultValue) {
        return PipelinePropertiesHelper.getPropertyAsArray(filter.getProperty(), variables, name, defaultValue);
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }

    public String getId() {
        return this.filter.getId();
    }

    @Override
    public void setPipelineExecuter(PipelineExecuter pipelineExecuter) {
        this.pipelineExecuter = pipelineExecuter;
    }

    public PipelineExecuter getPipelineExecuter() {
        return pipelineExecuter;
    }
}
