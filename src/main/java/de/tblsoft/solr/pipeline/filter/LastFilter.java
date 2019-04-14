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
        return null;
    }

    @Override
    public void setVariables(Map<String, String> variables) {

    }

    @Override
    public void setPipelineExecuter(PipelineExecuter pipelineExecuter) {

    }
}
