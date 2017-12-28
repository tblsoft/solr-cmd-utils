package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tblsoft on 22.10.17.
 * 
 * Remove all duplicates depended on a specified field.
 * The field must be a single value field.
 * This filter is not streaming compatible.
 *
 * TODO: allow duplicate removal for multivalue fields.
 * Idea: concat the values and use the concat values as key.
 */
public class DuplicateRemovalFilter extends AbstractFilter {

	private String fieldName;
	private boolean passThroughNullValues;

	private Map<String, Document> duplicateRemovalMap = new HashMap<String, Document>();

    @Override
    public void init() {
        fieldName = getProperty("fieldName", null);
        passThroughNullValues = getPropertyAsBoolean("passThroughNullValues", true);
        verify(fieldName, "For the DuplicateRemovalFilter a fieldName property must be defined.");
        super.init();
    }


    @Override
    public void document(Document document) {
        String key = document.getFieldValue(fieldName);
        if(key != null) {
            duplicateRemovalMap.put(key, document);
        }
        else if(passThroughNullValues) {
            super.document(document);
        }
    }

    @Override
    public void end() {
        for(Document document :duplicateRemovalMap.values()) {
            super.document(document);
        };
        super.end();
    }
}
