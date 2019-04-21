package de.tblsoft.solr.pipeline;

import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.filter.LookupFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Created by tblsoft on 09.08.17.
 */
public class Lookup {

    private static Logger LOG = LoggerFactory.getLogger(Lookup.class);



    public Lookup(String pipeline) {
        this.pipeline = pipeline;
    }

    private Map<String, Document> lookupMap;
    private String pipeline;

    private LookupFilter lookupFilter;

    void init() {
        PipelineExecuter pipelineExecuter = new PipelineExecuter(pipeline);
        pipelineExecuter.execute();
        lookupFilter = (LookupFilter) pipelineExecuter.getFilterById("lookup");
        lookupMap = lookupFilter.getLookup();
    }

    public Document get(String key) {
        if(lookupMap == null) {
            init();
        }

        String normalizedKey = lookupFilter.normalizeKey(key);
        LOG.info("--------------- " + normalizedKey);
        return lookupMap.get(normalizedKey);
    }
}
