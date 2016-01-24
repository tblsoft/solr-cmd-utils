package de.tblsoft.solr.pipeline.bean;

import java.util.List;

/**
 * Created by oelbaer on 22.01.16.
 */
public class Pipeline {
    private String name;

    private Reader reader;
    private List<Filter> filter;
    private Writer writer;


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

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    @Override
    public String toString() {
        return "Pipeline{" +
                "name='" + name + '\'' +
                ", reader=" + reader +
                ", filter=" + filter +
                ", writer=" + writer +
                '}';
    }
}
