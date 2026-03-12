package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.PipelineExecuter;
import de.tblsoft.solr.pipeline.bean.Document;
import de.tblsoft.solr.pipeline.bean.Field;
import de.tblsoft.solr.pipeline.bean.Pipeline;
import de.tblsoft.solr.util.DocumentIdHelper;

import java.util.Map;


public class DocumentJoinerFilter extends AbstractFilter {


    private Map<String, Document> lookupMap;

    private String key;
    private boolean useExplicitIdField;


    @Override
    public void init() {
        String pipelineId = getProperty("pipelineId", null);
        key = getProperty("key", "id");
        useExplicitIdField = getPropertyAsBoolean("useExplicitIdField", false);
        Pipeline pipeline = pipelineExecuter.getPipeline(pipelineId);

        PipelineExecuter pipelineExecuter = new PipelineExecuter(pipeline, getBaseDir());
        pipelineExecuter.execute();
        LookupFilter lookupFilter = (LookupFilter) pipelineExecuter.getFilterById("lookup");
        if(lookupFilter == null) {
            throw new RuntimeException("There is no lookup filter defined.");
        }
        lookupMap = lookupFilter.getLookup();
        super.init();
    }


    @Override
    public void document(Document document) {
        String keyValue = DocumentIdHelper.resolveId(document, key, useExplicitIdField);
        Document joinDocument = lookupMap.get(keyValue);
        if(joinDocument != null) {
            for (Field fieldToJoin : joinDocument.getFields()) {
                if(!fieldToJoin.getName().equals(key)) {
                    document.addField(fieldToJoin);
                }
            }
        }
        super.document(document);
    }

}

