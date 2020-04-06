package de.tblsoft.solr.pipeline.bean;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tblsoft on 22.01.16.
 */
public class Pipeline {
    private String name;

    private String processId;

    private String webHookStart;

    private String webHookEnd;

    private String webHookError;

    private Map<String,String> variables = new HashMap<>();

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

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getWebHookStart() {
        return webHookStart;
    }

    public void setWebHookStart(String webHookStart) {
        this.webHookStart = webHookStart;
    }

    public String getWebHookEnd() {
        return webHookEnd;
    }

    public void setWebHookEnd(String webHookEnd) {
        this.webHookEnd = webHookEnd;
    }

    /**
     * Getter for property 'webHookError'.
     *
     * @return Value for property 'webHookError'.
     */
    public String getWebHookError() {
        return webHookError;
    }

    /**
     * Setter for property 'webHookError'.
     *
     * @param webHookError Value to set for property 'webHookError'.
     */
    public void setWebHookError(String webHookError) {
        this.webHookError = webHookError;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "name='" + name + '\'' +
                ", processId='" + processId + '\'' +
                ", webHookStart='" + webHookStart + '\'' +
                ", webHookEnd='" + webHookEnd + '\'' +
                ", webHookError='" + webHookError + '\'' +
                ", variables=" + variables +
                ", reader=" + reader +
                ", filter=" + filter +
                ", preProcessor=" + preProcessor +
                ", postProcessor=" + postProcessor +
                '}';
    }
}
