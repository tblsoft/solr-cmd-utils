package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Reader;

/**
 * Created by tblsoft on 23.01.16.
 */
public interface ReaderIF {

    public void read();

    public void setPipelineExecuter(PipelineExecuter executer);

    public void end();

    public void setReader(Reader reader);

    public void setBaseDir(String baseDir);
}
