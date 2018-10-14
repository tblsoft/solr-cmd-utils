package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Processor;
import de.tblsoft.solr.pipeline.bean.Reader;

import java.util.Map;

/**
 * Created by tblsoft on 1.9.18.
 */
public interface ProcessorIF {

    void process();

    void setProcessor(Processor processor);

    void setBaseDir(String baseDir);

    void setVariables(Map<String, String> variables);
}
