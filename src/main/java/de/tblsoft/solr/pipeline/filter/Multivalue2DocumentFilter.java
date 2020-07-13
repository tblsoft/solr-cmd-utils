package de.tblsoft.solr.pipeline.filter;


import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;

/**
 * This value converts multiple values of a field to documents.
 */
public class Multivalue2DocumentFilter extends AbstractFilter {

    private String fieldName;

    @Override
    public void init() {
        fieldName=getProperty("fieldName", null);
        verify(fieldName, "For the Multivalue2DocumentFilter a fieldName property must be defined.");
        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> values = document.getFieldValues(fieldName);
        if(values == null) {
            super.document(document);
        } else {
            for(String value: values) {
                Document newDocument = new Document(document);
                newDocument.setField(fieldName, value);
                super.document(newDocument);
            }
        }
    }

}

