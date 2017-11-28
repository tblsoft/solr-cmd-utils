package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;
import de.tblsoft.solr.util.DateUtils;

import org.apache.commons.lang3.text.StrSubstitutor;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 23.01.16.
 */
public abstract class AbstractFilter implements FilterIF {

    protected FilterIF nextFilter;

    protected Filter filter;

    private String baseDir;

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
    public void init() {
        nextFilter.init();

    }

    @Override
    public void setFilterConfig(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void document(Document document) {
        nextFilter.document(document);
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
            return value;
        }
        return defaultValue;
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
}
