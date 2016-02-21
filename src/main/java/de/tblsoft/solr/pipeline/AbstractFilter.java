package de.tblsoft.solr.pipeline;

import com.google.common.base.Strings;
import de.tblsoft.solr.pipeline.bean.Filter;

import java.util.List;

/**
 * Created by tblsoft on 23.01.16.
 */
public abstract class AbstractFilter implements FilterIF {

    private FilterIF nextFilter;

    private Filter filter;

    @Override
    public void init() {
        nextFilter.init();
    }

    @Override
    public void setFilterConfig(Filter filter) {
        this.filter = filter;
    }

    @Override
    public void field(String name, String value) {
        nextFilter.field(name, value);
    }

    @Override
    public void endDocument() {
        nextFilter.endDocument();
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
        String value = (String) filter.getProperty().get(name);
        if(value != null) {
            return value;
        }
        return defaultValue;
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
}
