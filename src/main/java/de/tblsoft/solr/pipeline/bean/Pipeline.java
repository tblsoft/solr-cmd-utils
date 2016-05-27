package de.tblsoft.solr.pipeline.bean;

import java.util.List;
import java.util.Map;

/**
 * Created by oelbaer on 22.01.16.
 */
public class Pipeline {
    private String name;

    private Map<String,?> variables;

    private Reader reader;
    private List<Filter> filter;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Reader getReader() {
        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }


    public Map<String, ?> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, ?> variables) {
        this.variables = variables;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "name='" + name + '\'' +
                ", variables=" + variables +
                ", reader=" + reader +
                ", filter=" + filter +
                '}';
    }
}
