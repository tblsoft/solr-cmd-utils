package de.tblsoft.solr.pipeline.filter;

import de.tblsoft.solr.pipeline.AbstractFilter;
import de.tblsoft.solr.pipeline.bean.Document;

import java.util.List;

/**
 * Filter document when field is empty
 */
public class EmptyFieldDocumentFilter extends AbstractFilter {
    private String field;

    @Override
    public void init() {
        this.field = getProperty("field", null);
        verify(this.field, "For the RegexSplitFilter a field property must be defined!");

        super.init();
    }

    @Override
    public void document(Document document) {
        List<String> fieldValues = document.getFieldValues(field);
        if (fieldValues != null && fieldValues.size() > 0) {
            super.document(document);
        }
    }
}
