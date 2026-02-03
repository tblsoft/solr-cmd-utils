package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.FilterIF;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;

import java.util.Map;

/**
 * Created by tblsoft on 23.01.16.
 */
public class LastFilter implements FilterIF {

    private String id;

    public LastFilter() {
        this.id = "LastFilter";
    }

    @Override
    public void init() {

    }


    @Override
    public void document(Document document) {

    }

    @Override
    public void end() {
    }

    @Override
    public void setFilterConfig(Filter filter) {

    }

    @Override
    public void setNextFilter(FilterIF filter){

    }

    @Override
    public void setBaseDir(String baseDir) {

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setVariables(Map<String, String> variables) {

    }

    @Override
    public void setPipelineExecuter(PipelineExecuter pipelineExecuter) {

    }
}
