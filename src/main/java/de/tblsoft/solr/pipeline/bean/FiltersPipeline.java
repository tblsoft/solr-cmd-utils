package de.tblsoft.solr.pipeline.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * FiltersPipeline can be included through separate file
 */
public class FiltersPipeline {
    List<Filter> filter = new ArrayList<Filter>();

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }
}
