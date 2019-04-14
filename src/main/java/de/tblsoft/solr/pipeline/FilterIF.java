package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Filter;

import java.util.Map;

/**
 * Created by tblsoft on 23.01.16.
 */
public interface FilterIF {

    public void init();

    public void document(Document document);

    public void end();

    public void setFilterConfig(Filter filter);

    public void setNextFilter(FilterIF filter);

    public void setBaseDir(String baseDir);

    public String getId();

    public void setVariables(Map<String,String> variables);

    void setPipelineExecuter(PipelineExecuter pipelineExecuter);
}
