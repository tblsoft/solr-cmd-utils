package de.tblsoft.solr.pipeline.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 22.01.16.
 */
public class Pipeline {
    private String name;

    private Map<String,String> variables = new HashMap<String, String>();

    private Reader reader;
    private List<Filter> filter;

    private List<Processor> preProcessor;

    private List<Processor> postProcessor;

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

    public Map<String, String> getVariables() {
        return variables;
    }

    public void setVariables(Map<String, String> variables) {
        this.variables = variables;
    }


    public List<Processor> getPreProcessor() {
        return preProcessor;
    }

    public void setPreProcessor(List<Processor> preProcessor) {
        this.preProcessor = preProcessor;
    }

    public List<Processor> getPostProcessor() {
        return postProcessor;
    }

    public void setPostProcessor(List<Processor> postProcessor) {
        this.postProcessor = postProcessor;
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
