package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Reader;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by tblsoft on 23.01.16.
 */
public interface ReaderIF extends Serializable {

    public void read();

    public void setPipelineExecuter(PipelineExecuter executer);

    public void end();

    public void setReader(Reader reader);

    public void setBaseDir(String baseDir);

    public void setVariables(Map<String,String> variables);
}
