package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.util.DateUtils;

import org.apache.commons.lang3.text.StrSubstitutor;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
        } catch (NotImplementedException ignored) {
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
        throw new NotImplementedException();
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
        if(filter.getProperty() == null) {
            return defaultValue;
        }
        String value = (String) filter.getProperty().get(name);
        if(value != null) {
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            value = strSubstitutor.replace(value);
            return value;
        }
        return defaultValue;
    }

    public Boolean getPropertyAsBoolean(String name, Boolean defaultValue) {
        String value = getProperty(name,null);
        if(value == null) {
            return defaultValue;
        }
        return Boolean.valueOf(value);
    }

    public List<String> getPropertyAsList(String name, List<String> defaultValue) {
        if(filter.getProperty() == null) {
            return defaultValue;
        }
        List<String> value = (List<String>) filter.getProperty().get(name);
        if(value != null) {
            StrSubstitutor strSubstitutor = new StrSubstitutor(variables);
            for(int i = 0; i < value.size(); i++) {
                value.set(i, strSubstitutor.replace(value.get(i)));
            }

            return value;
        }
        return defaultValue;
    }


    public Map<String, String> getPropertyAsMapping(String name) {
        return getPropertyAsMapping(name, new HashMap<>(), "->");
    }

    public Map<String, String> getPropertyAsMapping(String name,  Map<String, String> defaultValue) {
        return getPropertyAsMapping(name,defaultValue, "->");
    }

    public Map<String, String> getPropertyAsMapping(String name,  Map<String, String> defaultValue, String splitter) {
        if(filter.getProperty() == null) {
            return defaultValue;
        }
        Map<String, String> mapping = new HashMap<>();
        List<String> rawValues = getPropertyAsList(name, new ArrayList<>());
        for (String rawValue : rawValues) {
            String[] splittedValue = rawValue.split(splitter);
            if(splittedValue.length < 2) {
                throw new RuntimeException("The mapping is not correct configured: " + rawValue);
            }
            mapping.put(splittedValue[0], splittedValue[1]);
        }
        return mapping;
    }

    public int getPropertyAsInt(String name, int defaultValue) {
        String value = getProperty(name,null);
        if(value != null) {
            return Integer.valueOf(value).intValue();
        }
        return defaultValue;
    }

    public float getPropertyAsFloat(String name, float defaultValue) {
        String value = getProperty(name,null);
        if(value != null) {
            return Float.valueOf(value);
        }
        return defaultValue;
    }
    
    public Date getPropertyAsDate(String name, Date defaultValue) {
        String value = getProperty(name,null);
        if(value != null) {
            return DateUtils.getDate(value);
        }
        return defaultValue;
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
        List<String> list = getPropertyAsList(name, null);
        if(list == null) {
            return defaultValue;
        }
        return list.toArray(new String[list.size()]);
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
