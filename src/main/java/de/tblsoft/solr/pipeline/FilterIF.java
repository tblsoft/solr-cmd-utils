package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Filter;

/**
 * Created by tblsoft on 23.01.16.
 */
public interface FilterIF {

    public void init();
     public void field(String name, String value);

     public void endDocument();

    public void end();

    public void setFilterConfig(Filter filter);

    public void setNextFilter(FilterIF filter);

    public void setBaseDir(String baseDir);
}
